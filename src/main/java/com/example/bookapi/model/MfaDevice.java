
package com.example.bookapi.model;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class MfaDevice {
    private Long id;
    private Long userId;
    
    @NotBlank(message = "Device name is required")
    private String deviceName;
    
    private String deviceType; // TOTP, SMS, EMAIL
    private String secret;
    private String backupCodes;
    private boolean isVerified = false;
    private boolean isActive = true;
    private LocalDateTime createdAt;
    private LocalDateTime lastUsedAt;

    public MfaDevice() {
        this.createdAt = LocalDateTime.now();
    }

    public MfaDevice(Long userId, String deviceName, String deviceType) {
        this();
        this.userId = userId;
        this.deviceName = deviceName;
        this.deviceType = deviceType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getBackupCodes() { return backupCodes; }
    public void setBackupCodes(String backupCodes) { this.backupCodes = backupCodes; }

    public boolean isVerified() { return isVerified; }
    public void setVerified(boolean verified) { isVerified = verified; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastUsedAt() { return lastUsedAt; }
    public void setLastUsedAt(LocalDateTime lastUsedAt) { this.lastUsedAt = lastUsedAt; }
}
