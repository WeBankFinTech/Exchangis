package com.webank.wedatasphere.exchangis.engine.resource;

import com.webank.wedatasphere.exchangis.engine.config.ExchangisEngineConfiguration;
import com.webank.wedatasphere.exchangis.engine.dao.EngineResourceDao;
import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineStoreResource;
import com.webank.wedatasphere.exchangis.engine.exception.ExchangisEngineResException;
import com.webank.wedatasphere.exchangis.engine.utils.ResourceUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Abstract engine resource container
 */
public abstract class AbstractEngineResourceContainer<T extends EngineResource, U extends EngineResource> implements EngineResourceContainer<T, U> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractEngineResourceContainer.class);

    /**
     * Resource root uri
     */
    protected final URI rootUri;
    /**
     * Engine type
     */
    private final String engineType;

    /**
     * Resource dao
     */
    protected final EngineResourceDao engineResourceDao;

    /**
     * Resource loader in container
     */
    protected final EngineResourceLoader<T> engineResourceLoader;

    /**
     * Resource uploader in container
     */
    protected final EngineResourceUploader<T, U> engineResourceUploader;

    /**
     * Root node
     */
    private final ResourcePathNode rootNode = new ResourcePathNode("/");

    public AbstractEngineResourceContainer(String engineType, String rootPath, EngineResourceDao resourceDao,
                                           EngineResourceLoader<T> resourceLoader,
                                           EngineResourceUploader<T, U> resourceUploader){
        this.engineType = engineType;
        this.rootUri = new File(ResourceUtils.normalizeFilePath(rootPath)).toURI();
        this.engineResourceDao = resourceDao;
        this.engineResourceLoader = resourceLoader;
        this.engineResourceUploader = resourceUploader;
    }

    @Override
    public String getEngineType() {
        return engineType;
    }

    @Override
    public List<T> getResources(String resourcePath) {
        String[] paths = pathSplit(resourcePath);
        if (Objects.nonNull(paths)){
            ResourcePathNode pathNode = searchResPathNode(paths);
            if (Objects.nonNull(pathNode)){
                return pathNode.getSubEngineResources();
            }
        }
        return null;
    }

    @Override
    public void addResource(String resourcePath, T engineResource) {
        String[] paths = pathSplit(resourcePath);
        if (Objects.nonNull(paths)){
            operateResPathNode(paths, pathNode -> pathNode.addSubEngineResource(engineResource));
        }
    }

    @Override
    public void updateResources(String resourcePath, T[] engineResources) {
        String[] paths = pathSplit(resourcePath);
        if (Objects.nonNull(paths)){
            ResourcePathNode pathNode = searchResPathNode(paths);
            if (Objects.nonNull(pathNode)){
                pathNode.updateSubEngineResource(engineResources);
            }
        }
    }

    @Override
    public T getResource(String resourcePath, String resourceId) {
        String[] paths = pathSplit(resourcePath);
        if (Objects.nonNull(paths)){
            ResourcePathNode pathNode = searchResPathNode(paths);
            if (Objects.nonNull(pathNode)){
                return pathNode.getSubEngineResource(resourceId);
            }
        }
        return null;
    }

    @Override
    public void flushResources(String resourcePath) throws ExchangisEngineResException{
        String[] paths = pathSplit(resourcePath);
        if (Objects.nonNull(paths)){
            flushResources(searchResPathNode(paths));
        }
    }

    @Override
    public void flushAllResources() throws ExchangisEngineResException{
        Queue<ResourcePathNode> queue = new LinkedList<>();
        queue.offer(this.rootNode);
        while(!queue.isEmpty()){
            ResourcePathNode currentNode = queue.poll();
            if (currentNode.hasSubEngineResources()){
                flushResources(currentNode);
            }
            currentNode.childNodes.values().forEach(queue::offer);
        }
    }

    @Override
    public U getRemoteResource(String resourcePath) {
        String[] paths = pathSplit(resourcePath);
        if (Objects.nonNull(paths)){
            ResourcePathNode pathNode = searchResPathNode(paths);
            if (Objects.nonNull(pathNode)){
                return pathNode.getRemoteResource();
            }
        }
        return null;
    }

    @Override
    public void removeResource(String resourcePath, String resourceId) {
        String[] paths = pathSplit(resourcePath);
        if (Objects.nonNull(paths)){
            ResourcePathNode pathNode = searchResPathNode(paths);
            if (Objects.nonNull(pathNode)){
                pathNode.removeSubEngineResource(resourceId);
            }
        }
    }

    @Override
    public EngineResourceLoader<T>  getResourceLoader() {
        return engineResourceLoader;
    }

    @Override
    public EngineResourceUploader<T, U> getResourceUploader() {
        return engineResourceUploader;
    }

    protected void operateResPathNode(String[] paths, Consumer<ResourcePathNode> operate){
        operateResPathNode(null, paths, 0, operate);
    }

    protected ResourcePathNode searchResPathNode(String[] paths){
        return searchResPathNode(null, paths, 0);
    }
    /**
     * Operate resource path node
     * @param parentNode parent node
     * @param paths paths
     * @param pos pos
     * @param operate operate function
     */
    private void operateResPathNode(ResourcePathNode parentNode, String[] paths, int pos,
                                    Consumer<ResourcePathNode> operate){
        int upper = Math.min(pos + 1, paths.length);
        String[] subPath = new String[upper];
        System.arraycopy(paths, 0, subPath, 0, upper);
        // path
        String path = subPath.length <= 1 ? "/" : StringUtils.join(subPath, "/");
        ResourcePathNode currentNode;
        if (null == parentNode){
            if (path.equals("/")) {
                currentNode = this.rootNode;
            } else {
                LOG.warn("Path: {} should start with '/'", StringUtils.join(paths, "/"));
                return;
            }
        } else {
            currentNode = parentNode.childNodes.computeIfAbsent(path, ResourcePathNode::new);
        }
        if (upper >= paths.length){
            operate.accept(currentNode);
        } else {
            operateResPathNode(currentNode, paths, pos + 1, operate);
        }
    }

    /**
     * Search resource path node
     * @param parentNode parent node
     * @param paths paths
     * @param pos pos
     * @return resource path node
     */
    private ResourcePathNode searchResPathNode(ResourcePathNode parentNode, String[] paths, int pos){
        int upper = Math.min(pos + 1, paths.length);
        String[] subPath = new String[upper];
        System.arraycopy(paths, 0, subPath, 0, upper);
        // path
        String path = subPath.length <= 1 ? "/" : StringUtils.join(subPath, "/");
        ResourcePathNode currentNode;
        if (null == parentNode){
            if (path.equals("/")) {
                currentNode = this.rootNode;
            } else {
                LOG.warn("Path: {} should start with '/'", StringUtils.join(paths, "/"));
                return null;
            }
        } else {
            currentNode = parentNode.childNodes.get(path);
        }
        if (upper >= paths.length || Objects.isNull(currentNode)){
            return currentNode;
        }
        return searchResPathNode(currentNode, paths, pos + 1);
    }

    private void flushResources(ResourcePathNode pathNode) throws ExchangisEngineResException {
        if(Objects.nonNull(pathNode)){
            LOG.info("Flush the {} engine resources in path: [{}]", getEngineType(), pathNode.getPath());
            T nodeEngineRes = mergeNodeEngineResource(pathNode);
            if (Objects.nonNull(nodeEngineRes)){
                // Mark the resource under the path
                nodeEngineRes.setPath(pathNode.path);
                // Try tp upload the node engine resource
                try {
                    U uploadedRes = this.engineResourceUploader.upload(nodeEngineRes, pathNode.getRemoteResource());
                    if (Objects.nonNull(uploadedRes)) {
                        // Store the uploaded remoted resource information
                        if (Objects.nonNull(pathNode.getRemoteResource())) {
                            this.engineResourceDao.updateResource(new EngineStoreResource(uploadedRes));
                        } else {
                            this.engineResourceDao.insertResource(new EngineStoreResource(uploadedRes));
                        }
                        pathNode.setRemoteResource(uploadedRes);
                    }
                }catch(Exception e){
                    // Not throw
                    LOG.warn(null, e);
                }
            }
        }
    }
    protected String[] pathSplit(String path){
        return path == null ? null : path.split("/");
    }

    /**
     * Merge the engine resources in path node
     * @param pathNode path node
     * @return
     */
    protected abstract T mergeNodeEngineResource(ResourcePathNode pathNode);
    /**
     * Resource path node (in tree)
     */
    protected class ResourcePathNode{

        /**
         * Resource path
         */
        protected final String path;
        /**
         * Node lock
         */
        protected final ReentrantReadWriteLock nodeLock;

        /**
         * Modify time
         */
        protected long lastModifyTime = -1;

        /**
         * Resource in data
         */
        protected final Map<String, T> subResources = new HashMap<>();

        /**
         * Remote resource
         */
        protected U remoteResource;

        /**
         * Children nodes
         */
        protected Map<String, ResourcePathNode> childNodes = new ConcurrentHashMap<>();

        public ResourcePathNode(String path){
            this.path = path;
            this.nodeLock = new ReentrantReadWriteLock();
            this.lastModifyTime = 0L;
        }

        public void updateSubEngineResource(T[] engineResources){
            nodeLock.writeLock().lock();
            try{
                subResources.clear();
                if (Objects.nonNull(engineResources)){
                    final AtomicLong modifyTime = new AtomicLong(0);
                    Arrays.asList(engineResources).forEach(engineResource -> {
                        Date resourceTime = engineResource.getModifyTime();
                        if (resourceTime.getTime() > modifyTime.get()){
                            modifyTime.set(resourceTime.getTime());
                        }
                        subResources.put(engineResource.getId(), engineResource);
                    });
                    this.lastModifyTime = modifyTime.get();
                }
            } finally {
                nodeLock.writeLock().unlock();
            }
        }
        public void addSubEngineResource(T engineResource){
            nodeLock.writeLock().lock();
            try{
                subResources.put(engineResource.getId(), engineResource);
                Date resourceTime = engineResource.getModifyTime();
                if (resourceTime.getTime() > lastModifyTime){
                    this.lastModifyTime = resourceTime.getTime();
                }
            }finally {
                nodeLock.writeLock().unlock();
            }
        }

        public List<T> getSubEngineResources(){
            nodeLock.readLock().lock();
            try{
                List<T> resources = new ArrayList<>();
                subResources.forEach((key, resource) -> resources.add(resource));
                return resources;
            }finally {
                nodeLock.readLock().unlock();
            }
        }
        public boolean hasSubEngineResources(){
            nodeLock.readLock().lock();
            try{
                return !subResources.isEmpty();
            }finally {
                nodeLock.readLock().unlock();
            }
        }
        public T getSubEngineResource(String resourceId){
            nodeLock.readLock().lock();
            try{
                return subResources.get(resourceId);
            }finally {
                nodeLock.readLock().unlock();
            }
        }

        public EngineResource removeSubEngineResource(String resourceId){
            nodeLock.writeLock().lock();
            try{
                return subResources.remove(resourceId);
            }finally {
                nodeLock.writeLock().unlock();
            }
        }
        public U getRemoteResource(){
            return remoteResource;
        }

        public void setRemoteResource(U engineResource){
            this.remoteResource = engineResource;
        }

        public String getPath() {
            return path;
        }
    }

}
