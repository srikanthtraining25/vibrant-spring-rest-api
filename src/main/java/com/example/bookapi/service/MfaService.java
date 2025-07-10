
package com.example.bookapi.service;

import com.example.bookapi.model.MfaDevice;
import com.example.bookapi.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class MfaService {
    
    @Autowired
    private UserService userService;
    
    private final Map<Long, MfaDevice> mfaDevices = new HashMap<>();
    private final Map<Long, List<MfaDevice>> devicesByUser = new HashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);
    private final SecureRandom secureRandom = new SecureRandom();
    
    public String generateSecret() {
        byte[] buffer = new byte[20];
        secureRandom.nextBytes(buffer);
        return Base64.getEncoder().encodeToString(buffer);
    }
    
    public List<String> generateBackupCodes(int count) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = String.format("%08d", secureRandom.nextInt(100000000));
            codes.add(code);
        }
        return codes;
    }
    
    public String generateQrCodeUrl(String secret, String userEmail, String issuer) {
        return String.format(
            "otpauth://totp/%s:%s?secret=%s&issuer=%s",
            issuer, userEmail, secret, issuer
        );
    }
    
    public MfaDevice setupTotpDevice(Long userId, String deviceName) {
        String secret = generateSecret();
        List<String> backupCodes = generateBackupCodes(10);
        
        MfaDevice device = new MfaDevice(userId, deviceName, "TOTP");
        device.setId(idGenerator.getAndIncrement());
        device.setSecret(secret);
        device.setBackupCodes(String.join(",", backupCodes));
        
        mfaDevices.put(device.getId(), device);
        devicesByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(device);
        
        return device;
    }
    
    public boolean verifyTotpCode(Long deviceId, String code) {
        MfaDevice device = mfaDevices.get(deviceId);
        if (device == null || !device.getDeviceType().equals("TOTP")) {
            return false;
        }
        
        // Simple verification logic (in real implementation, use TOTP algorithm)
        if (code.length() == 6 && code.matches("\\d{6}")) {
            device.setVerified(true);
            device.setLastUsedAt(LocalDateTime.now());
            
            // Enable MFA for user if this is their first verified device
            userService.enableMfa(device.getUserId(), device.getSecret());
            
            return true;
        }
        
        return false;
    }
    
    public boolean verifyBackupCode(Long userId, String code) {
        List<MfaDevice> userDevices = devicesByUser.get(userId);
        if (userDevices == null) return false;
        
        for (MfaDevice device : userDevices) {
            if (device.getBackupCodes() != null && device.getBackupCodes().contains(code)) {
                // Remove used backup code
                String[] codes = device.getBackupCodes().split(",");
                String updatedCodes = Arrays.stream(codes)
                    .filter(c -> !c.equals(code))
                    .collect(Collectors.joining(","));
                device.setBackupCodes(updatedCodes);
                
                return true;
            }
        }
        return false;
    }
    
    public List<MfaDevice> getUserDevices(Long userId) {
        return devicesByUser.getOrDefault(userId, new ArrayList<>());
    }
    
    public Optional<MfaDevice> getDevice(Long deviceId) {
        return Optional.ofNullable(mfaDevices.get(deviceId));
    }
    
    public boolean deleteDevice(Long deviceId, Long userId) {
        MfaDevice device = mfaDevices.get(deviceId);
        if (device != null && device.getUserId().equals(userId)) {
            mfaDevices.remove(deviceId);
            List<MfaDevice> userDevices = devicesByUser.get(userId);
            if (userDevices != null) {
                userDevices.remove(device);
                
                // If no more devices, disable MFA for user
                if (userDevices.isEmpty()) {
                    userService.disableMfa(userId);
                }
            }
            return true;
        }
        return false;
    }
    
    public void deactivateDevice(Long deviceId, Long userId) {
        MfaDevice device = mfaDevices.get(deviceId);
        if (device != null && device.getUserId().equals(userId)) {
            device.setActive(false);
        }
    }
    
    public void activateDevice(Long deviceId, Long userId) {
        MfaDevice device = mfaDevices.get(deviceId);
        if (device != null && device.getUserId().equals(userId)) {
            device.setActive(true);
        }
    }
    
    public boolean authenticateUser(String usernameOrEmail, String password, String mfaCode) {
        Optional<User> userOpt = userService.getUserByUsernameOrEmail(usernameOrEmail);
        if (userOpt.isEmpty()) return false;
        
        User user = userOpt.get();
        
        // Simple password check (in real implementation, use proper hashing)
        if (!user.getPassword().equals(password)) return false;
        
        // If MFA is enabled, verify the code
        if (user.isMfaEnabled()) {
            if (mfaCode == null || mfaCode.isEmpty()) return false;
            
            // Check if it's a backup code
            if (mfaCode.length() == 8) {
                return verifyBackupCode(user.getId(), mfaCode);
            }
            
            // Check TOTP codes for all user's devices
            List<MfaDevice> devices = getUserDevices(user.getId());
            for (MfaDevice device : devices) {
                if (device.isActive() && device.isVerified() && 
                    device.getDeviceType().equals("TOTP")) {
                    if (verifyTotpCode(device.getId(), mfaCode)) {
                        userService.updateLastLogin(user.getId());
                        return true;
                    }
                }
            }
            return false;
        }
        
        userService.updateLastLogin(user.getId());
        return true;
    }
}
