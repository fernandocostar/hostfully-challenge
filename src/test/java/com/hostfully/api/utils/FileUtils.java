package com.hostfully.api.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.groovy.json.internal.IO;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {

    private static final Log log = LogFactory.getLog(FileUtils.class);

    /**
     * Reads a JSON file from given path and returns in String format.
     * @param filePath JSON file path.
     * @return the read JSON object in String format.
     */
    public static String readJsonFile(String filePath) throws IOException {
        try {
            Path path = Paths.get(filePath);
            return Files.readString(path);
        } catch (IOException e) {
            log.error("Error reading file: " + filePath);
            throw new RuntimeException(e);
        }
    }
}
