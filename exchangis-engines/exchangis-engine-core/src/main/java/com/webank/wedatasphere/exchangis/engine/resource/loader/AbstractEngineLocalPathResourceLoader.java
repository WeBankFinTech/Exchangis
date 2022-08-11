package com.webank.wedatasphere.exchangis.engine.resource.loader;

import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResLoadException;
import com.webank.wedatasphere.exchangis.engine.resource.AbstractEngineResourceLoader;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Load the engine resources by local path
 */
public abstract class AbstractEngineLocalPathResourceLoader extends AbstractEngineResourceLoader<EngineLocalPathResource> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEngineLocalPathResourceLoader.class);
    private static final String DEFAULT_SUPPORT_SCHEMA = "file";

    /**
     * Pattern object
     */
    private Pattern[] patterns = new Pattern[0];

    public AbstractEngineLocalPathResourceLoader(){
        String[] pathPatterns = pathPatterns();
        if (Objects.nonNull(pathPatterns)){
            patterns = new Pattern[pathPatterns.length];
            for(int i = 0; i < pathPatterns.length; i++){
                Pattern pattern = Pattern.compile(pathPatterns[i]);
                patterns[i] = pattern;
            }
        }
    }

    @Override
    public boolean accept(URI baseUri, String path) {
        if (StringUtils.isBlank(baseUri.getScheme()) || DEFAULT_SUPPORT_SCHEMA.equals(baseUri.getScheme())){
            return Arrays.stream(patterns)
                    .anyMatch(pattern -> pattern.matcher(path).matches());
        }
        return false;
    }

    @Override
    public EngineLocalPathResource[] loadResource(URI baseUri, String path) throws ExchangisEngineResLoadException {
        LOG.info("Load local engine resource, path: {}", path);
        String scheme = baseUri.getScheme();
        if (StringUtils.isBlank(baseUri.getScheme()) || DEFAULT_SUPPORT_SCHEMA.equals(scheme)){
            return loadLocalResource(baseUri, path);
        } else {
            throw new ExchangisEngineResLoadException("Unsupported scheme: [" + scheme + "] in basic uri: [" + baseUri + "] for local resource loader.");
        }
    }

    /**
     * Path pattern list
     * @return pattern string array
     */
    protected abstract String[] pathPatterns();
    /**
     * Load local resource
     * @param path path
     * @return resource array
     */
    private EngineLocalPathResource[] loadLocalResource(URI baseUri, String path) throws ExchangisEngineResLoadException {
        File localFile = new File(baseUri.getPath(), path);
        EngineLocalPathResource[] resources = new EngineLocalPathResource[0];
        if (localFile.isDirectory()) {
            File[] resourceFiles = localFile.listFiles();
            if (Objects.nonNull(resourceFiles)) {
                resources = new EngineLocalPathResource[resourceFiles.length];
                for (int i = 0; i < resources.length; i++) {
                    resources[i] = createLocalResource(resourceFiles[i], baseUri, path);
                }
            }
        } else if (localFile.isFile()) {
            resources = new EngineLocalPathResource[]{createLocalResource(localFile, baseUri, path)};
        }
        // Important: make all the resources have the same value in 'path'
        for(EngineLocalPathResource resource : resources){
            resource.setPath(path);
        }
        return resources;
    }

    /**
     * Create local resource
     * @param localFile local file
     * @param baseUri base uri
     * @param path path
     * @return local resource
     */
    private EngineLocalPathResource createLocalResource(File localFile, URI baseUri, String path){
        EngineLocalPathResource localResource = new EngineLocalPathResource(engineType(), baseUri,
                path + IOUtils.DIR_SEPARATOR + localFile.getName());
        long lastModifyTime = traverseExtractTime(localFile, 0L);
        localResource.setCreateTime(new Date(lastModifyTime));
        localResource.setModifyTime(new Date(lastModifyTime));
        return localResource;
    }
    /**
     * Traverse the extract last time
     * @param localFile local  file
     * @param timestamp timestamp
     * @return
     */
    private long traverseExtractTime(File localFile, long timestamp){
        long lastTime = timestamp;
        if (localFile.lastModified() > lastTime){
            lastTime = localFile.lastModified();
        }
        if (localFile.isDirectory()){
            File[] subFiles = localFile.listFiles();
            if (Objects.nonNull(subFiles)) {
                for (File subFile : subFiles) {
                    lastTime = traverseExtractTime(subFile, lastTime);
                }
            }
        }
        return lastTime;
    }
}
