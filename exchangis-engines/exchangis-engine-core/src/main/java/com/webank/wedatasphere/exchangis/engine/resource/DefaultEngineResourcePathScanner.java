package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.config.ExchangisEngineConfiguration;
import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResException;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResLoadException;
import com.webank.wedatasphere.exchangis.engine.utils.ResourceUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Default path scanner
 */
public class DefaultEngineResourcePathScanner implements EngineResourcePathScanner{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEngineResourcePathScanner.class);
    /**
     * Resource loader list
     */
    private final Map<String, EngineResourceLoader<? extends EngineLocalPathResource>> resourceLoaders = new ConcurrentHashMap<>();

    @Override
    public void registerResourceLoader(EngineResourceLoader<? extends EngineLocalPathResource> resourceLoader) {
        LOG.info("Register the resource loader: '{}'", resourceLoader.getClass().getCanonicalName());
        this.resourceLoaders.put(resourceLoader.engineType(), resourceLoader);
    }

    @Override
    public Set<EngineLocalPathResource> doScan(String rootPath) throws ExchangisEngineResException {
        rootPath = FilenameUtils.normalize(rootPath);
        File rootFile = new File(rootPath);
        List<EngineLocalPathResource> resources = new ArrayList<>();
        if (!rootFile.exists()){
            throw new ExchangisEngineResLoadException("The engine resource root path: [" + rootPath +"] doesn't exist");
        }
        if (rootFile.isFile()){
            throw new ExchangisEngineResLoadException("The engine resource root path: [" + rootPath + "] should be a directory/link, but not a file");
        } else {
            LOG.info("Start to scan the resource root path: [{}]", rootPath);
            resourceLoaders.forEach((engine, resourceLoader) ->{
                File engineFile = new File(rootFile, engine.toLowerCase());
                if (engineFile.exists() && engineFile.isDirectory()){
                    LOG.info("Scan the resource path for engine: [{}] in [{}]", engine.toLowerCase(), engineFile.getPath());
                    resources.addAll(scanPathAndLoadResource(rootFile.toURI(),
                            IOUtils.DIR_SEPARATOR + engineFile.getName(), (baseUri, path) -> resourceLoader.accept(baseUri, path)? resourceLoader : null));
                } else {
                    LOG.warn("Cannot find the resource path for engine: [{}] in [{}], ignore it.", engine.toLowerCase(), engineFile.getPath());
                }
            });
        }
        return new HashSet<>(resources);
    }


    private List<EngineLocalPathResource> scanPathAndLoadResource(URI baseUri, String path,
                                    BiFunction<URI, String, EngineResourceLoader<? extends EngineLocalPathResource>> getResLoader) {
        List<EngineLocalPathResource> resources = new ArrayList<>();
        File rootFile = new File(baseUri.getPath(), path);
        if (rootFile.isDirectory()) {
            File[] childFiles = rootFile.listFiles((dir, name) -> {
                // skip the hidden file
                return !name.startsWith(".");
            });
            if (Objects.nonNull(childFiles)) {
                List<File> scanDirs = new ArrayList<>();
                List<String> skipNames = new ArrayList<>();
                List<File> directories = Arrays.stream(childFiles)
                        .filter(File::isDirectory).collect(Collectors.toList());
                directories.forEach(dir -> {
                    try {
                        String dirPath = path + IOUtils.DIR_SEPARATOR + dir.getName();
                        EngineResourceLoader<? extends EngineLocalPathResource> resourceLoader
                                = getResLoader.apply(baseUri, dirPath);
                        if (Objects.nonNull(resourceLoader)) {
                            resources.addAll(Arrays.asList(resourceLoader.loadResource(baseUri, dirPath)));
                            skipNames.add(dir.getName());
                        } else {
                            scanDirs.add(dir);
                        }
                    } catch (Exception e) {
                        LOG.warn("Exception in loading engine directory resource: [" + dir.getPath() + "]", e);
                    }
                });
                List<File> rawFiles = Arrays.stream(childFiles).filter(file ->
                        file.isFile() && skipNames.stream().noneMatch(skipName ->
                                file.getName().equals(skipName + ExchangisEngineConfiguration.ENGINE_RESOURCE_PACKET_SUFFIX.getValue())))
                        .collect(Collectors.toList());
                rawFiles.forEach(rawFile -> {
                    try {
                        String rawFilePath = path + IOUtils.DIR_SEPARATOR  + rawFile.getName();
                        EngineResourceLoader<? extends EngineLocalPathResource> resourceLoader =
                                getResLoader.apply(baseUri, rawFilePath);
                        if (Objects.nonNull(resourceLoader)){
                            EngineLocalPathResource[] resArray = resourceLoader.loadResource(baseUri, rawFilePath);
                            if (resArray.length == 1 && rawFile.getName()
                                    .endsWith(ExchangisEngineConfiguration.ENGINE_RESOURCE_PACKET_SUFFIX.getValue())) {
                                LOG.info("Mark the engine resource: [{}] as a packet({}) resource", rawFile.getPath(),
                                        ExchangisEngineConfiguration.ENGINE_RESOURCE_PACKET_SUFFIX.getValue());
                                resArray[0].setPacket(true);
                                Path source = rawFile.toPath();
                                Path dest = source.resolveSibling(StringUtils.substringBefore(rawFile.getName(), "."));
                                if (!Files.isDirectory(dest)) {
                                    Files.createDirectory(dest);
                                }
                                LOG.info("Un packet the engine resource: [{}] to [{}]", source, dest);
                                ResourceUtils.unPacket(source, dest);
                                // Update the path value
                                resArray[0].setPath(StringUtils.substringBeforeLast(rawFilePath, "."));
                            }
                            resources.addAll(Arrays.asList(resArray));
                        }
                    } catch (Exception e){
                        LOG.warn("Exception in loading engine file resource: [" + rawFile.getPath() + "]", e);
                    }
                });
                for(File scanDir : scanDirs) {
                    resources.addAll(scanPathAndLoadResource(baseUri, path + IOUtils.DIR_SEPARATOR + scanDir.getName(), getResLoader));
                }
            }
        }
        return resources;
    }
}
