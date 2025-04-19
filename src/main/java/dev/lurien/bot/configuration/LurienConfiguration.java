package dev.lurien.bot.configuration;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Set;

import static dev.lurien.staff.lurienStaff.utils.MessagesUtils.log;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class LurienConfiguration {

    private final File file;
    @Getter
    private FileConfiguration config;
    private final String name;
    private final File folder;
    private final Plugin plugin;

    public LurienConfiguration(String name, File folder, Plugin plugin) {
        this.name = name+".yml";
        this.folder = folder;
        this.plugin = plugin;
        this.file = new File(folder, this.name);
        load();
    }

    public void load() {
        if (!file.exists()) {
            try {
                folder.mkdirs();
                try (InputStream inputStream = plugin.getResource(name)) {
                    if (inputStream != null) {
                        Files.copy(inputStream, file.toPath());
                    } else {
                        file.createNewFile();
                    }
                }
            } catch (IOException e) {
                log("&a[Lurien Config] &cA IOException occurred while creating "+name+" file: "+e.getMessage());
                return;
            }
        }
        config = YamlConfiguration.loadConfiguration(file);
        mergeDefaults();
    }

    private void mergeDefaults() {
        try (InputStream inputStream = plugin.getResource(name)) {
            if (inputStream != null) {
                FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new java.io.InputStreamReader(inputStream));
                Set<String> defaultKeys = defaultConfig.getKeys(true);
                for (String key : defaultKeys) {
                    if (!config.contains(key)) {
                        config.set(key, defaultConfig.get(key));
                    }
                }
                save();
            }
        } catch (IOException e) {
            log("&a[Clans Config] &cA IOException occurred while merging default keys in "+name+" file: "+e.getMessage());
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
             log("&a[Clans Config] &cA IOException occurred while saving "+name+" file: "+e.getMessage());
        }
    }

    public void reload() {
        load();
    }

}
