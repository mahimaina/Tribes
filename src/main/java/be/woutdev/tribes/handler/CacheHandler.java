package be.woutdev.tribes.handler;

import be.woutdev.tribes.squirrelid.Profile;
import be.woutdev.tribes.squirrelid.cache.ProfileCache;
import be.woutdev.tribes.squirrelid.cache.SQLiteCache;
import be.woutdev.tribes.squirrelid.resolver.CacheForwardingService;
import be.woutdev.tribes.squirrelid.resolver.HttpRepositoryService;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Created by Wout on 4/08/2016.
 */
public class CacheHandler
{
    private final CacheForwardingService service;
    private ProfileCache cache;

    public CacheHandler(Plugin plugin)
    {
        Path cachePath = Paths.get(plugin.getDataFolder().getAbsolutePath(), "cache.sqlite3");

        if (!Files.exists(cachePath))
        {
            try
            {
                Files.createFile(cachePath);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            cache = new SQLiteCache(cachePath.toFile());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        service = new CacheForwardingService(HttpRepositoryService.forMinecraft(), cache);
    }

    /**
     * Makes a request to Mojang to get the UUID of the given playername.
     *
     * @param name The name of the player.
     *
     * @return The UUID of the player or null.
     */
    public UUID findByName(String name)
    {
        UUID uuid = null;

        try
        {
            Profile profile = service.findByName(name);

            if (profile != null)
            {
                uuid = profile.getUniqueId();
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }

        return uuid;
    }

    /**
     * Looks into the cache to get the playername by UUID.
     *
     * @param uuid The UUID to check.
     *
     * @return The playername. If not found, then it tries to use Bukkit#getOfflinePlayer for a last resort. If then not
     * found, it returns null.
     */
    public String findByUUID(UUID uuid)
    {
        Profile profile = cache.getIfPresent(uuid);

        if (profile != null)
        {
            return profile.getName();
        }
        else
        {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);

            if (player == null || !player.hasPlayedBefore())
            {
                return null;
            }
            else
            {
                return player.getName();
            }
        }
    }
}
