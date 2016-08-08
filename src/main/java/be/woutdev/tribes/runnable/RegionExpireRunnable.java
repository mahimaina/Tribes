package be.woutdev.tribes.runnable;

import be.woutdev.tribes.Tribes;
import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.model.PlayerRegion;
import be.woutdev.tribes.model.RegionBlock;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.UUID;

/**
 * Created by Wout on 6/08/2016.
 */
public class RegionExpireRunnable extends BukkitRunnable
{
    private final Tribes plugin;
    private final PlayerRegionHandler handler;
    private final RegionBlockHandler regionBlockHandler;

    public RegionExpireRunnable(Tribes plugin)
    {
        this.plugin = plugin;
        this.handler = plugin.getPlayerRegionHandler();
        this.regionBlockHandler = plugin.getRegionBlockHandler();
    }

    @Override
    public void run()
    {
        for (PlayerRegion playerRegion : handler.getRegions())
        {
            RegionBlock block = regionBlockHandler.findById(playerRegion.getRegionBlockId());

            long difference = System.currentTimeMillis() - playerRegion.getLastAction().getTime();

            if (difference >= (block.getRegionReset() * 86_400_000))
            {
                handler.removePlayerRegion(playerRegion);

                List<UUID> members = Lists.newArrayList();

                playerRegion.getMembers()
                            .stream()
                            .filter(m -> Bukkit.getPlayer(m.getUniqueId()) != null)
                            .forEach(m -> members.add(m.getUniqueId()));

                if (Bukkit.getPlayer(playerRegion.getOwner()) != null)
                {
                    members.add(playerRegion.getOwner());
                }

                members.forEach(p -> {
                    Player pl = Bukkit.getPlayer(p);

                    pl.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                     .getRegionExpired()
                                                                                     .replace(
                                                                                             "{region}", String.valueOf(
                                                                                                     playerRegion.getId()))));
                });
            }
        }
    }
}
