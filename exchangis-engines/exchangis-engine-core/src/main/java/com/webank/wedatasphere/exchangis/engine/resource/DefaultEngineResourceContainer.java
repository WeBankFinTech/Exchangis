package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.config.ExchangisEngineConfiguration;
import com.webank.wedatasphere.exchangis.engine.dao.EngineResourceDao;
import com.webank.wedatasphere.exchangis.engine.domain.EngineBmlResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineStoreResource;
import com.webank.wedatasphere.exchangis.engine.utils.ResourceUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultEngineResourceContainer extends AbstractEngineResourceContainer<EngineLocalPathResource, EngineBmlResource> {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultEngineResourceContainer.class);

    public DefaultEngineResourceContainer(String engineType, String rootPath, EngineResourceDao resourceDao,
                                          EngineResourceLoader<EngineLocalPathResource> resourceLoader,
                                          EngineResourceUploader<EngineLocalPathResource, EngineBmlResource> resourceUploader) {
        super(engineType, rootPath, resourceDao, resourceLoader, resourceUploader);
    }

    @Override
    public void init() {
        List<EngineStoreResource> storeResources = this.engineResourceDao.getResources(getEngineType());
        storeResources.forEach(storeResource -> {
            String path = storeResource.getPath();
            if (StringUtils.isNotBlank(path)){
                operateResPathNode(pathSplit(path), resourcePathNode ->
                        resourcePathNode.setRemoteResource(new EngineBmlResource(storeResource)));
            }
        });
    }
    /**
     *
     * @param pathNode resource path node
     * @return engine resource
     */
    protected EngineLocalPathResource mergeNodeEngineResource(ResourcePathNode pathNode){
        if (Objects.isNull(pathNode.getRemoteResource()) || pathNode.getRemoteResource()
                .getModifyTime().getTime() <  pathNode.lastModifyTime) {
            ReentrantReadWriteLock nodeLock = pathNode.nodeLock;
            List<EngineLocalPathResource> resourcesFiltered;
            nodeLock.readLock().lock();
            try {
                resourcesFiltered = pathNode.subResources.values().stream().filter(Objects::nonNull)
                        .collect(Collectors.toList());
            }finally {
                nodeLock.readLock().unlock();
            }
            if (resourcesFiltered.size() == 1 && resourcesFiltered.get(0).isPacket()){
                //Ignore the packet resource
                return resourcesFiltered.get(0);
            }
            // Merged resource is a local resource, its name is equal to the path in pathNode
            String mergedResourcePath;
            boolean temp = false;
            if (ExchangisEngineConfiguration.ENGINE_RESOURCE_MERGE_LOCAL.getValue()) {
                // Need to store the merged resource into local path
                String rootPath = rootUri.getPath();
                mergedResourcePath = (rootPath.endsWith(IOUtils.DIR_SEPARATOR + "")? rootPath : rootPath + IOUtils.DIR_SEPARATOR )
                        + pathNode.getPath() + ExchangisEngineConfiguration.ENGINE_RESOURCE_PACKET_SUFFIX.getValue();
            } else {
                File temporaryPath = new File(ExchangisEngineConfiguration.ENGINE_RESOURCE_TMP_PATH.getValue());
                if (temporaryPath.mkdir()) {
                    LOG.info("Auto create the engine temporary directory [{}]", temporaryPath.getAbsolutePath());
                }
                mergedResourcePath = temporaryPath.getAbsolutePath() + IOUtils.DIR_SEPARATOR + UUID.randomUUID();
                temp = true;
            }
            synchronized ((getEngineType() + ":" + pathNode.getPath()).intern()){
                // 1. DELETE the exists local resource
                File resourceFile = new File(mergedResourcePath);
                if (resourceFile.exists()){
                    if (resourceFile.delete()){
                        LOG.info("Success to delete the existed local resource file [{}] before", resourceFile.getPath());
                    }else {
                        LOG.warn("Fail to delete the existed local resource file [{}], please examine the file permissions or occupation from the other program!", resourceFile.getPath());
                    }
                }
                try {
                    if (resourceFile.createNewFile()) {
                        ResourceUtils.combinePacket(resourcesFiltered.stream().toArray(value -> new EngineResource[resourcesFiltered.size()]), new FileOutputStream(resourceFile));
                        if (temp) {
                            resourceFile.deleteOnExit();
                        }
                        return new EngineLocalPathResource(getEngineType(), rootUri, pathNode.getPath() + ExchangisEngineConfiguration.ENGINE_RESOURCE_PACKET_SUFFIX.getValue());
                    }
                } catch (IOException e) {
                    LOG.warn("Exception in combing and packet resources in [{}]", pathNode.getPath(), e);
                }
            }
        }
        return null;
    }
}
