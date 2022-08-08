package com.webank.wedatasphere.exchangis.engine.utils;

import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * Resource utils
 */
public class ResourceUtils {

    public static String normalizeFilePath(String path){
        return FilenameUtils.normalize(path);
    }

    public static void combinePacket(EngineResource[] resources, OutputStream outputStream){

    }

    public static void packet(Path source, Path target, boolean includeBaseDir) throws IOException {

    }

    public static void unPacket(Path source, Path target) throws IOException{

    }
}
