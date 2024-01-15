package dk.martinersej.pint.manager.managertype;

import dk.martinersej.pint.manager.ManagerType;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class YamlManagerTypeImpl implements ManagerType.YamlManagerType {

    private String filePath;
    private FileConfiguration config = new YamlConfiguration();

    public YamlManagerTypeImpl(JavaPlugin instance, String fileName) {
        File file = new File(instance.getDataFolder(), fileName);
        setFilePath(file.getAbsolutePath());
        if (!file.exists()) {
            instance.saveResource(fileName, false);
        }
        setConfig(YamlConfiguration.loadConfiguration(file));
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void setConfig(FileConfiguration fileConfiguration) {
        this.config = fileConfiguration;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath1) {
        filePath = filePath1;
    }
}