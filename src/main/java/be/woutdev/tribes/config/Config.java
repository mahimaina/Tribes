package be.woutdev.tribes.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Wout on 3-8-2016.
 */
public abstract class Config
{
    private final Plugin plugin;
    private final String name;
    private FileConfiguration fileConfiguration;

    /**
     * Create a new Config instance.
     *
     * @param plugin The plugin where this config belongs to.
     * @param name The name of the configuration file including any extension.
     */
    protected Config(Plugin plugin, String name)
    {
        this.plugin = plugin;
        this.name = name;
    }

    /**
     * Loads the configuration file into Config#fileConfiguration. It does create a new directory if it has to and it
     * does copy from the resources if it has to.
     */
    public void load()
    {
        Path mapPath = Paths.get(plugin.getDataFolder().getAbsolutePath());

        if (!Files.exists(mapPath))
        {
            try
            {
                Files.createDirectory(mapPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        Path configPath = Paths.get(mapPath.toAbsolutePath().toString(), name);

        if (!Files.exists(configPath))
        {
            try
            {
                Files.copy(plugin.getResource(name), configPath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        fileConfiguration = YamlConfiguration.loadConfiguration(configPath.toFile());
    }

    /**
     * Get the FileConfiguration. Please only use after Config#load.
     *
     * @return The FileConfiguration instance.
     */
    public FileConfiguration getFileConfiguration()
    {
        return fileConfiguration;
    }
}
