package net.starcore.api;

import java.io.File;
import java.util.Optional;

public interface ConfigProvider {
    void load();

    void save();

    File getDataFolder();

    Optional<String> getString(String path);

    Optional<Integer> getInt(String path);
}
