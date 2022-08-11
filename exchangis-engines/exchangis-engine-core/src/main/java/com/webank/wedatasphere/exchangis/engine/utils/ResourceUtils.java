package com.webank.wedatasphere.exchangis.engine.utils;

import com.webank.wedatasphere.exchangis.engine.domain.EngineLocalPathResource;
import com.webank.wedatasphere.exchangis.engine.domain.EngineResource;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Resource utils
 */
public class ResourceUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ResourceUtils.class);

    private static final Integer BUFFER_SIZE = 2 * 1024;

    public static String normalizeFilePath(String path){
        return FilenameUtils.normalize(path);
    }

    /**
     * Combine the resources and packet
     * @param resources resources
     * @param outputStream output stream
     * @throws IOException
     */
    public static void combinePacket(EngineResource[] resources, OutputStream outputStream) throws IOException {
        LOG.info("Start to combine the resources to packet file...");
        long startTime = System.currentTimeMillis();
        try(ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            for (EngineResource resource : resources) {
                if (resource instanceof EngineLocalPathResource) {
                    packet(resource.getName(), ((EngineLocalPathResource) resource).getLocalFile().toPath(),
                            zipOutputStream);
                } else {
                    packet(resource.getName(), resource.getInputStream(), zipOutputStream);
                }
            }
        }
        LOG.info("Success to combine the resources to packet file, taken: {}", System.currentTimeMillis() - startTime);
    }

    public static void packet(Path source, Path target, boolean includeBaseDir) throws IOException {

    }

    public static void unPacket(Path source, Path target) throws IOException{
        if (Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)){
            ZipFile zipFile = new ZipFile(source.toFile());
            InputStream inputStream = Files.newInputStream(source);
            try(ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                ZipEntry zipEntry = null;
                while (null != (zipEntry = zipInputStream.getNextEntry())) {
                    Path entryPath = target.resolve(zipEntry.getName());
                    if (zipEntry.isDirectory()) {
                        if (!Files.isDirectory(entryPath)) {
                            Files.createDirectories(entryPath);
                        }
                    } else {
                        try (InputStream entryStream = zipFile.getInputStream(zipEntry)) {
                            try (OutputStream outputStream = Files.newOutputStream(entryPath, StandardOpenOption.CREATE_NEW)) {
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int pos = -1;
                                while ((pos = entryStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, pos);
                                }
                                outputStream.flush();
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Packet path source
     * @param name name
     * @param source source path
     * @param outputStream stream
     * @throws IOException
     */
    private static void packet(String name, Path source, ZipOutputStream outputStream) throws IOException {
        if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)){
            name = name + IOUtils.DIR_SEPARATOR_UNIX;
            // Accept empty directory
            ZipEntry zipEntry = new ZipEntry(name);
            outputStream.putNextEntry(zipEntry);
            outputStream.closeEntry();
            for(Path child : Files.list(source).collect(Collectors.toList())) {
               packet(name + child.toFile().getName(), child, outputStream);
            }
        } else if (Files.isRegularFile(source, LinkOption.NOFOLLOW_LINKS)){
            packet(name, Files.newInputStream(source), outputStream);
        }
    }

    /**
     * Packet input  stream
     * @param name name
     * @param inputStream input stream
     * @param outputStream output stream
     * @throws IOException
     */
    private static void packet(String name, InputStream inputStream, ZipOutputStream outputStream) throws IOException{
        if (Objects.nonNull(inputStream)) {
            ZipEntry zipEntry = new ZipEntry(name);
            outputStream.putNextEntry(zipEntry);
            byte[] buffer = new byte[BUFFER_SIZE];
            int pos = -1;
            while ((pos = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, pos);
            }
            outputStream.closeEntry();
        }
    }
}
