package com.legal.platform.controller;

import com.legal.platform.common.Result;
import com.legal.platform.entity.SystemConfig;
import com.legal.platform.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/map")
    public Result<Map<String, String>> getConfigMap() {
        return systemConfigService.getConfigMap();
    }

    @GetMapping("/{key}")
    public Result<SystemConfig> getConfigByKey(@PathVariable String key) {
        return systemConfigService.getConfigByKey(key);
    }

    @PutMapping
    public Result<Boolean> updateConfig(@RequestBody Map<String, String> params) {
        String key = params.get("key");
        String value = params.get("value");
        return systemConfigService.updateConfig(key, value);
    }
}
