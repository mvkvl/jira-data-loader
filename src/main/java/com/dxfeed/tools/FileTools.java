package com.dxfeed.tools;

import lombok.SneakyThrows;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileTools {

    public static File getFileFromResources(String fileName) {
        ClassLoader classLoader = FileTools.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file " + fileName + " not found!");
        } else {
            return new File(resource.getFile());
        }
    }

    public static String readFile(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));//.append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }
    public static String readFile(File file) {
        StringBuilder contentBuilder = new StringBuilder();
        try (Stream<String> stream = Files.lines( file.toPath(), StandardCharsets.UTF_8)) {
            stream.forEach(s -> contentBuilder.append(s));//.append("\n"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contentBuilder.toString();
    }

    public static String readFileFromResources(String filePath) {
        InputStream input = FileTools.class.getResourceAsStream("/resources/" + filePath);
        if (input == null)
            input = FileTools.class.getClassLoader().getResourceAsStream(filePath);
        return readFileFromInputStream(input);
    }

    @SneakyThrows
    private static String readFileFromInputStream(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder contentBuilder = new StringBuilder();
        String line;
        while((line = reader.readLine()) != null)
            contentBuilder.append(line).append(System.lineSeparator());
        return contentBuilder.toString();
    }

}
