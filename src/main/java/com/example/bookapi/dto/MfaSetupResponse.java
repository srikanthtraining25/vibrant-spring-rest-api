
package com.example.bookapi.dto;

import java.util.List;

public class MfaSetupResponse {
    private String secret;
    private String qrCodeUrl;
    private List<String> backupCodes;
    private String deviceId;

    public MfaSetupResponse() {}

    public MfaSetupResponse(String secret, String qrCodeUrl, List<String> backupCodes, String deviceId) {
        this.secret = secret;
        this.qrCodeUrl = qrCodeUrl;
        this.backupCodes = backupCodes;
        this.deviceId = deviceId;
    }

    // Getters and Setters
    public String getSecret() { return secret; }
    public void setSecret(String secret) { this.secret = secret; }

    public String getQrCodeUrl() { return qrCodeUrl; }
    public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }

    public List<String> getBackupCodes() { return backupCodes; }
    public void setBackupCodes(List<String> backupCodes) { this.backupCodes = backupCodes; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
}
