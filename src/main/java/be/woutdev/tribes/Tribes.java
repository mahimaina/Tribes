package be.woutdev.tribes;

import be.woutdev.tribes.command.TribesCommand;
import be.woutdev.tribes.config.MessagesConfig;
import be.woutdev.tribes.config.RegionsConfig;
import be.woutdev.tribes.ebean.LocationScalarTypeConverter;
import be.woutdev.tribes.handler.CacheHandler;
import be.woutdev.tribes.handler.PhysicalActionHandler;
import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.listener.*;
import be.woutdev.tribes.model.Member;
import be.woutdev.tribes.model.PlayerRegion;
import be.woutdev.tribes.runnable.RegionExpireRunnable;
import be.woutdev.tribes.runnable.RegionRegenerationRunnable;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * Created by Wout on 3-8-2016.
 */
public class Tribes extends JavaPlugin
{
    private RegionsConfig config;
    private MessagesConfig messagesConfig;
    private RegionBlockHandler regionBlockHandler;
    private PlayerRegionHandler playerRegionHandler;
    private CacheHandler cacheHandler;
    private PhysicalActionHandler physicalActionHandler;

    @Override
    public List<Class<?>> getDatabaseClasses()
    {
        // Add all ebean required classes
        List<Class<?>> list = Lists.newArrayList();
        list.add(PlayerRegion.class);
        list.add(Member.class);
        list.add(LocationScalarTypeConverter.class);

        return list;
    }

    @Override
    public void onLoad()
    {
        // Instantiate and load configuration
        config = new RegionsConfig(this);
        config.load();

        messagesConfig = new MessagesConfig(this);
        messagesConfig.load();

        // Instantiate handler
        cacheHandler = new CacheHandler(this);
    }

    @Override
    public void onEnable()
    {
        // Instantiate handlers
        regionBlockHandler = new RegionBlockHandler(config);
        playerRegionHandler = new PlayerRegionHandler(this);
        physicalActionHandler = new PhysicalActionHandler(this);

        // Register listeners
        getServer().getPluginManager()
                   .registerEvents(new BlockPlaceListener(this), this);
        getServer().getPluginManager()
                   .registerEvents(new BlockBreakListener(this), this);
        getServer().getPluginManager().registerEvents(new EntityDamageByEntityListener(this), this);
        getServer().getPluginManager()
                   .registerEvents(
                           new PlayerInteractListener(this),
                           this);
        getServer().getPluginManager()
                   .registerEvents(new PlayerInteractEntityListener(playerRegionHandler, physicalActionHandler,
                                                                    regionBlockHandler), this);

        // Register command
        getCommand("tribes").setExecutor(new TribesCommand(this));

        // Start runnables
        Bukkit.getScheduler()
              .runTaskTimerAsynchronously(this, new RegionRegenerationRunnable(regionBlockHandler, playerRegionHandler),
                                          1L, 1L);
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, new RegionExpireRunnable(this), 72000,
                                                         72000);
    }

    /**
     * Get the RegionBlockHandler instance used in this entire project.
     *
     * @return The RegionBlockHandler instance
     */
    public RegionBlockHandler getRegionBlockHandler()
    {
        return regionBlockHandler;
    }

    /**
     * Get the PlayerRegionHandler instance used in this entire project.
     *
     * @return The PlayerRegionHandler instance
     */
    public PlayerRegionHandler getPlayerRegionHandler()
    {
        return playerRegionHandler;
    }

    /**
     * Get the RegionsConfig instance used in this entire project.
     *
     * @return The RegionsConfig instance
     */
    public RegionsConfig getRegionsConfig()
    {
        return config;
    }

    /**
     * Get the CacheHandler instance used in this entire project.
     *
     * @return The CacheHandler instance
     */
    public CacheHandler getCacheHandler()
    {
        return cacheHandler;
    }

    /**
     * Get the Physical Action Handler. Used in this project for physical actions such as adding players by clicking.
     *
     * @return The Physical Action Handler instance used in this project.
     */
    public PhysicalActionHandler getPhysicalActionHandler()
    {
        return physicalActionHandler;
    }

    /**
     * Get the MessagesConfig instance used in this entire project.
     *
     * @return The MessagesConfig instance used.
     */
    public MessagesConfig getMessagesConfig()
    {
        return messagesConfig;
    }
}
