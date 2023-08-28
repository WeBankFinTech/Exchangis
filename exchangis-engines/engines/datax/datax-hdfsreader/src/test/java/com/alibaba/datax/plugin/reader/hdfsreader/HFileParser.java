package com.alibaba.datax.plugin.reader.hdfsreader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.io.hfile.CacheConfig;
import org.apache.hadoop.hbase.io.hfile.HFile;
import org.apache.hadoop.hbase.io.hfile.HFileScanner;

import java.io.IOException;

public class HFileParser {

    public void parse(String pathString, Configuration hadoopConf) throws IOException {
        FileSystem fs = new Path(pathString).getFileSystem(hadoopConf);
        HFile.Reader reader = HFile.createReader(fs, new Path(pathString), new CacheConfig(hadoopConf), false, hadoopConf);
        HFileScanner scanner = reader.getScanner(hadoopConf, true, true);
        scanner.seekTo();
        Cell cell = scanner.getCell();
        scanner.next();
    }
}
