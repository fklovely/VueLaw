package com.legal.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.legal.platform.common.Result;
import com.legal.platform.entity.SystemConfig;
import com.legal.platform.mapper.SystemConfigMapper;
import com.legal.platform.service.SystemConfigService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    @Override
    public Result<Map<String, String>> getConfigMap() {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getStatus, 1);
        List<SystemConfig> configs = this.list(wrapper);
        Map<String, String> map = new HashMap<>();
        for (SystemConfig config : configs) {
            map.put(config.getConfigKey(), config.getConfigValue());
        }
        return Result.success(map);
    }

    @Override
    public Result<SystemConfig> getConfigByKey(String key) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        SystemConfig config = this.getOne(wrapper);
        return Result.success(config);
    }

    @Override
    public Result<Boolean> updateConfig(String key, String value) {
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SystemConfig::getConfigKey, key);
        SystemConfig config = this.getOne(wrapper);
        if (config == null) {
            return Result.error("配置不存在");
        }
        config.setConfigValue(value);
        this.updateById(config);
        return Result.success("更新成功", true);
    }
}
