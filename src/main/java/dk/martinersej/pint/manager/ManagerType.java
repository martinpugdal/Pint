package dk.martinersej.pint.manager;

import dk.martinersej.pint.Pint;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public interface ManagerType {

    interface YamlManagerType extends ManagerType {
        default void save() {
            try {
                getConfig().save(getFilePath());
            } catch (IOException ex) {
                Bukkit.getLogger().warning("Could not save " + this.getClass().getSimpleName());
            }
        }

        default void load() {
            File file = new File(this.getFilePath());
            if (!file.exists()) {
                Pint.getInstance().saveResource(file.getName(), false);
            }
            setConfig(YamlConfiguration.loadConfiguration(file));
        }

        FileConfiguration getConfig();

        void setConfig(FileConfiguration fileConfiguration);

        String getFilePath();

        void setFilePath(String filePath);

        default void setupConfig(JavaPlugin instance, String fileName) {
            File file = new File(instance.getDataFolder(), fileName);
            setFilePath(file.getAbsolutePath());
            if (!file.exists()) {
                instance.saveResource(fileName, false);
            }
            setConfig(YamlConfiguration.loadConfiguration(file));
        }
    }

    interface SQLiteManagerType extends ManagerType {
        DatabaseConnectionManager getDatabase();

        String[] getTables();

        void setTables(String[] tables);

        default void createTables(Runnable runnable) {
            getDatabase().connect(connection -> {
                try {
                    connection.createStatement().executeUpdate(String.join(" ", getTables()));
                    runnable.run();
                } catch (SQLException e) {
                    //noinspection CallToPrintStackTrace
                    e.printStackTrace();
                }
            });
        }
    }
}
