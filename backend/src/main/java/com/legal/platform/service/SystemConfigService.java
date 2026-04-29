package com.legal.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.legal.platform.common.Result;
import com.legal.platform.entity.SystemConfig;

import java.util.List;
import java.util.Map;

public interface SystemConfigService extends IService<SystemConfig> {
    Result<Map<String, String>> getConfigMap();
    Result<SystemConfig> getConfigByKey(String key);
    Result<Boolean> updateConfig(String key, String value);
}
