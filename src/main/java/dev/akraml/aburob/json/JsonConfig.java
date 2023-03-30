package dev.akraml.aburob.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
@SuppressWarnings("unchecked")

public class JsonConfig {

    public Gson gson = new GsonBuilder().setPrettyPrinting().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();

    private Map<Object, Object> map;
    private final File file;

    @SneakyThrows
    public JsonConfig(File file)  {
        this.file = file;
        readFile();
    }

    public <K, V> V get(K key) {
        return (V) map.get(key);
    }

    public <K, V> void set(K key, V value) {
        if (map.containsKey(key)) {
            map.replace(key, value);
        } else {
            map.put(key, value);
        }
    }

    private void readFile() throws IOException {
        try (Reader reader = Files.newBufferedReader(file.toPath())) {
            this.map = gson.fromJson(reader, Map.class);
        }
        if (this.map == null) // If there's nothing in the file
            this.map = new HashMap<>();
    }

    public void saveFile() {
        try (FileWriter writer = new FileWriter(file, false)) {
            writer.write(gson.toJson(map));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public Map<Object, Object> getKeys() {
        return map;
    }

}