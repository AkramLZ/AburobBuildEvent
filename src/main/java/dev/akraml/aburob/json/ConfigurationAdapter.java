package dev.akraml.aburob.json;

/**
 * @author AkramL
 * @param <H> Configuration Handler
 */
public interface ConfigurationAdapter<H> {

    /**
     * Gets a string from the configuration by its path
     * using {@link #get(String, Class)} method.
     *
     * @param key The key of string to get.
     * @return Provided value in the configuration.
     */
    default String getString(String key) {
        return get(key, String.class);
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
    <T> T get(String key, Class<? extends T> clazz);

    /**
     * Update the value in the configuration into the new
     * value by its configuration key and type.
     *
     * @param key The key of the value to update in the configuration file.
     * @param value The new value to set.
     * @param clazz The superclass of the value.
     * @param <T> Generic type.
     */
    <T> void set(String key, T value, Class<? extends T> clazz);

    /**
     * Just like {@link #getString(String)} but for integers.
     *
     * @param key The key of integer to get.
     * @return Provided value in the configuration.
     */
    default int getInt(String key) {
        return get(key, Integer.class);
    }

    /**
     * @return Configuration handler instance.
     */
    H getHandler();

}