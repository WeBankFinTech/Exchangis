package com.webank.wedatasphere.exchangis.engine.resource.loader.datax;

import com.webank.wedatasphere.exchangis.engine.resource.loader.AbstractEngineLocalPathResourceLoader;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;

import java.util.regex.Pattern;

/**
 * Datax engine resource loader
 */
public class DataxEngineResourceLoader extends AbstractEngineLocalPathResourceLoader {

    private static final CommonVars<String> ENGINE_DATAX_LOADER_PATH_PATTERN = CommonVars.apply("engine.datax.resource.loader.path-pattern",
            StringUtils.join(new String[]{
                    DataxEngineResourceConf.RESOURCE_PATH_PREFIX.getValue() + "/reader/.*[/]?",
                    DataxEngineResourceConf.RESOURCE_PATH_PREFIX.getValue() + "/writer/.*[/]?"
            }, ","));
    @Override
    protected String[] pathPatterns() {
        return ENGINE_DATAX_LOADER_PATH_PATTERN.getValue().split(",");
    }


    @Override
    public String engineType() {
        return "datax";
    }

}
