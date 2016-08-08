package be.woutdev.tribes.listener;

import be.woutdev.tribes.Tribes;
import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.helper.PermissionHelper;
import be.woutdev.tribes.model.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Wout on 3-8-2016.
 */
public class BlockBreakListener implements Listener
{
    private final Tribes plugin;
    private final RegionBlockHandler regionBlockHandler;
    private final PlayerRegionHandler playerRegionHandler;

    public BlockBreakListener(Tribes plugin)
    {
        this.plugin = plugin;
        this.regionBlockHandler = plugin.getRegionBlockHandler();
        this.playerRegionHandler = plugin.getPlayerRegionHandler();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e)
    {
        RegionBlock block = regionBlockHandler.findByType(e.getBlock().getType());

        if (block != null)
        {
            PlayerRegion region = playerRegionHandler.findByCenter(e.getBlock().getLocation());

            if (region != null)
            {
                if (e.getPlayer().getUniqueId().equals(region.getOwner()) || e.getPlayer().isOp())
                {
                    playerRegionHandler.removePlayerRegion(region);
                    e.getPlayer()
                     .sendMessage(
                             ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                               .getBreakFlag()
                                                                               .replace("{region}", String.valueOf(
                                                                                       region.getId()))));
                }
                else
                {
                    e.setCancelled(true);
                    e.getPlayer()
                     .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getBreakFlagNoPermission()));
                }

                return;
            }
        }

        PlayerRegion region = playerRegionHandler.findByLocation(e.getBlock().getLocation());

        if (region != null)
        {
            Level level = regionBlockHandler.findLevelByExp(region.getExp(), region.getRegionBlockId());

            if (region.getOwner().equals(e.getPlayer().getUniqueId()) ||
                PermissionHelper.hasPermission(e.getPlayer(), "tribes.build"))
            {
                if (!playerRegionHandler.isInRadius(region, level.getInternalRadius(), e.getBlock().getLocation()))
                {
                    e.setCancelled(true);
                    e.getPlayer()
                     .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getBreakExternalRadius()));
                }

                return;
            }

            Member member = region.getMembers()
                                  .stream()
                                  .filter(m -> m.getUniqueId().equals(e.getPlayer().getUniqueId()))
                                  .findAny()
                                  .orElse(null);

            if (member == null)
            {
                e.setCancelled(true);
                e.getPlayer()
                 .sendMessage(
                         ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getBreakNonMember()));
                return;
            }

            if (!playerRegionHandler.isInRadius(region, level.getInternalRadius(), e.getBlock().getLocation()))
            {
                e.setCancelled(true);
                e.getPlayer()
                 .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                .getBreakExternalRadius()));
                return;
            }

            Rank rank = regionBlockHandler.findById(region.getRegionBlockId())
                                          .getRanks()
                                          .stream()
                                          .filter(r -> r.getId() == member.getRankId())
                                          .findAny()
                                          .orElse(null);

            Material material = e.getBlock().getType();

            switch (material)
            {
                case CROPS:
                case SOIL:
                case PUMPKIN_STEM:
                case MELON_STEM:
                case PUMPKIN:
                case MELON_BLOCK:
                case POTATO:
                case CARROT:
                case BEETROOT_BLOCK:
                    if (!rank.isGardener())
                    {
                        e.setCancelled(true);
                        e.getPlayer()
                         .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getBreakNonGardener()));
                    }

                    return;
                default:
                    break;
            }

            if (!rank.isBuilder())
            {
                e.setCancelled(true);
                e.getPlayer()
                 .sendMessage(
                         ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getBreakNonBuilder()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreakMonitor(BlockBreakEvent e)
    {
        if (e.isCancelled())
        {
            return;
        }

        PlayerRegion region = playerRegionHandler.findByLocation(e.getBlock().getLocation());

        if (region != null)
        {
            region.setLastAction(new Timestamp(new Date().getTime()));
            playerRegionHandler.updatePlayerRegion(region);
        }
    }
}
