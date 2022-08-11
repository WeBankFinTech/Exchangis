package com.webank.wedatasphere.exchangis.engine.dao;

import com.webank.wedatasphere.exchangis.engine.domain.EngineSettings;

import java.util.List;

/**
 * Engine settings dao
 */
public interface EngineSettingsDao {
    /**
     * Settings
     * @return list
     */
    List<EngineSettings> getSettings();
}
