
package com.example.bookapi.controller;

import com.example.bookapi.dto.ApiResponse;
import com.example.bookapi.dto.MfaSetupResponse;
import com.example.bookapi.model.MfaDevice;
import com.example.bookapi.model.User;
import com.example.bookapi.service.MfaService;
import com.example.bookapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mfa")
@CrossOrigin(origins = "*")
public class MfaController {
    
    @Autowired
    private MfaService mfaService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/setup/totp")
    public ResponseEntity<ApiResponse<MfaSetupResponse>> setupTotp(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "Authenticator App") String deviceName) {
        
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found")
            );
        }
        
        User user = userOpt.get();
        MfaDevice device = mfaService.setupTotpDevice(userId, deviceName);
        
        String qrCodeUrl = mfaService.generateQrCodeUrl(
            device.getSecret(), 
            user.getEmail(), 
            "BookAPI"
        );
        
        List<String> backupCodes = Arrays.asList(device.getBackupCodes().split(","));
        
        MfaSetupResponse response = new MfaSetupResponse(
            device.getSecret(),
            qrCodeUrl,
            backupCodes,
            device.getId().toString()
        );
        
        return ResponseEntity.ok(
            ApiResponse.success("TOTP device setup initiated", response)
        );
    }
    
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyMfaSetup(
            @RequestParam Long deviceId,
            @RequestParam String code) {
        
        boolean isValid = mfaService.verifyTotpCode(deviceId, code);
        
        if (isValid) {
            return ResponseEntity.ok(
                ApiResponse.success("MFA device verified successfully", "Device is now active")
            );
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.error("Invalid MFA code")
            );
        }
    }
    
    @GetMapping("/devices")
    public ResponseEntity<ApiResponse<List<MfaDevice>>> getUserMfaDevices(@RequestParam Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found")
            );
        }
        
        List<MfaDevice> devices = mfaService.getUserDevices(userId);
        
        // Don't return secrets in the list
        devices.forEach(device -> device.setSecret(null));
        
        return ResponseEntity.ok(
            ApiResponse.success("MFA devices retrieved", devices)
        );
    }
    
    @DeleteMapping("/devices/{deviceId}")
    public ResponseEntity<ApiResponse<String>> deleteMfaDevice(
            @PathVariable Long deviceId,
            @RequestParam Long userId) {
        
        boolean deleted = mfaService.deleteDevice(deviceId, userId);
        
        if (deleted) {
            return ResponseEntity.ok(
                ApiResponse.success("MFA device deleted successfully", "Device removed")
            );
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("Device not found or unauthorized")
            );
        }
    }
    
    @PutMapping("/devices/{deviceId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateMfaDevice(
            @PathVariable Long deviceId,
            @RequestParam Long userId) {
        
        mfaService.deactivateDevice(deviceId, userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("MFA device deactivated", "Device is now inactive")
        );
    }
    
    @PutMapping("/devices/{deviceId}/activate")
    public ResponseEntity<ApiResponse<String>> activateMfaDevice(
            @PathVariable Long deviceId,
            @RequestParam Long userId) {
        
        mfaService.activateDevice(deviceId, userId);
        
        return ResponseEntity.ok(
            ApiResponse.success("MFA device activated", "Device is now active")
        );
    }
    
    @PostMapping("/backup-codes/regenerate")
    public ResponseEntity<ApiResponse<List<String>>> regenerateBackupCodes(
            @RequestParam Long userId,
            @RequestParam Long deviceId) {
        
        Optional<MfaDevice> deviceOpt = mfaService.getDevice(deviceId);
        if (deviceOpt.isEmpty() || !deviceOpt.get().getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("Device not found or unauthorized")
            );
        }
        
        List<String> newBackupCodes = mfaService.generateBackupCodes(10);
        MfaDevice device = deviceOpt.get();
        device.setBackupCodes(String.join(",", newBackupCodes));
        
        return ResponseEntity.ok(
            ApiResponse.success("Backup codes regenerated", newBackupCodes)
        );
    }
    
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Object>> getMfaStatus(@RequestParam Long userId) {
        Optional<User> userOpt = userService.getUserById(userId);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.error("User not found")
            );
        }
        
        User user = userOpt.get();
        List<MfaDevice> devices = mfaService.getUserDevices(userId);
        
        long activeDevices = devices.stream().filter(MfaDevice::isActive).count();
        long verifiedDevices = devices.stream().filter(MfaDevice::isVerified).count();
        
        return ResponseEntity.ok(
            ApiResponse.success("MFA status retrieved", Map.of(
                "mfaEnabled", user.isMfaEnabled(),
                "totalDevices", devices.size(),
                "activeDevices", activeDevices,
                "verifiedDevices", verifiedDevices
            ))
        );
    }
}
