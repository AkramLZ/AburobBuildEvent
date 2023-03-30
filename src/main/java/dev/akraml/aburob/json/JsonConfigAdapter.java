package dev.akraml.aburob.json;

import java.io.File;
import java.io.IOException;

public class JsonConfigAdapter implements ConfigurationAdapter<JsonConfig> {

    private final JsonConfig handler;

    public JsonConfigAdapter(File file) throws IOException {
        this.handler = new JsonConfig(file);
    }


    /**
     * Gets an object from the configuration by its path
     * and generic type.
     *
     * @param key The key of the value to get from the configuration file.
     * @param clazz The class of the generic type.
     * @return Provided value in the configuration.
     * @param <T> Generic type.
     */
    @Override
    public <T> T get(String key, Class<? extends T> clazz) {
        Object value = handler.get(key);
        if (value == null)
            return null;
        if (clazz.isAssignableFrom(value.getClass()))
            return clazz.cast(value);
        else
            throw new ConfigurationKeyTypeException(
                    String.format(
                            "Different type (Found=%s, Provided=%s)",
                            value.getClass().getSimpleName(),
                            clazz.getSimpleName()
                    )
            );
    }

    /**
     * Update the value in the configuration into the new
     * value by its configuration key and type.
     *
     * @param key   The key of the value to update in the configuration file.
     * @param value The new value to set.
     * @param clazz The superclass of the value.
     */
    @Override
    public <T> void set(String key, T value, Class<? extends T> clazz) {
        handler.set(key, value);
        handler.saveFile();
    }

    /**
     * @return Configuration handler instance.
     */
    @Override
    public JsonConfig getHandler() {
        return handler;
    }

}
