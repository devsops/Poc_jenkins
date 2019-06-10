package com.bosch.pai;

import com.bosch.pai.ipswrapper.Config;

import java.util.Map;

public interface IeroIPSPlatform {

    void register(Map<Config.Key, Object> configMap);

    void unregister(Map<Config.Key, Object> configMap);

}
