package com.platform.marketing.system.service;

import com.platform.marketing.system.dto.*;

public interface SystemSettingsService {
    SystemSettingsDto getSettings();
    void saveBasicSettings(BasicSettingsDto dto);
    void saveNotificationSettings(NotifySettingsDto dto);
    void saveSecuritySettings(SecuritySettingsDto dto);
}
