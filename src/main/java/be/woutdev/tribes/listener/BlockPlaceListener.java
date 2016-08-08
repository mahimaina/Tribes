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
import org.bukkit.event.block.BlockPlaceEvent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Wout on 3-8-2016.
 */
public class BlockPlaceListener implements Listener
{
    private final Tribes plugin;
    private final RegionBlockHandler regionBlockHandler;
    private final PlayerRegionHandler playerRegionHandler;

    public BlockPlaceListener(Tribes plugin)
    {
        this.plugin = plugin;
        this.regionBlockHandler = plugin.getRegionBlockHandler();
        this.playerRegionHandler = plugin.getPlayerRegionHandler();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e)
    {
        RegionBlock block = regionBlockHandler.findByFlag(e.getItemInHand());

        if (block != null)
        {
            if (!PermissionHelper.hasPermission(e.getPlayer(), "tribes.region." + block.getName()))
            {
                e.getPlayer()
                 .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                .getCreateRegionNoPermission()));
                e.setCancelled(true);
                return;
            }

            PlayerRegion pRegion = new PlayerRegion(e.getPlayer().getUniqueId(), block, e.getBlockPlaced().getLocation());

            if (playerRegionHandler.isOverlapping(pRegion))
            {
                e.getPlayer()
                 .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                .getCreateRegionOverlapping()));
                e.setCancelled(true);
                return;
            }

            playerRegionHandler.addPlayerRegion(pRegion);

            e.getPlayer()
             .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                            .getCreateRegion()
                                                                            .replace("{region}", String.valueOf(
                                                                                    pRegion.getId()))));

            return;
        }

        PlayerRegion region = playerRegionHandler.findByLocation(e.getBlock().getLocation());

        if (region != null)
        {
            Level level = regionBlockHandler.findLevelByExp(region.getExp(), region.getRegionBlockId());

            if (region.getOwner().equals(e.getPlayer().getUniqueId()) || PermissionHelper.hasPermission(e.getPlayer(),
                                                                                                        "tribes.build"))
            {
                if (!playerRegionHandler.isInRadius(region, level.getInternalRadius(),
                                                    e.getBlockPlaced().getLocation()))
                {
                    e.setCancelled(true);
                    e.getPlayer()
                     .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getBuildExternalRadius()));
                    return;
                }

                if (level.getRestrictedBlocks().contains(e.getBlockPlaced().getTypeId()))
                {
                    e.setCancelled(true);
                    e.getPlayer()
                     .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getBuildRestrictedBlock()));
                    return;
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
                         ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getBuildNonMember()));
                return;
            }

            if (!playerRegionHandler.isInRadius(region, level.getInternalRadius(),
                                                e.getBlockPlaced().getLocation()))
            {
                e.setCancelled(true);
                e.getPlayer()
                 .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                .getBuildExternalRadius()));
                return;
            }

            if (level.getRestrictedBlocks().contains(e.getBlockPlaced().getTypeId()))
            {
                e.setCancelled(true);
                e.getPlayer()
                 .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                .getBuildRestrictedBlock()));
                return;
            }

            Rank rank = regionBlockHandler.findById(region.getRegionBlockId())
                                          .getRanks()
                                          .stream()
                                          .filter(r -> r.getId() == member.getRankId())
                                          .findAny()
                                          .orElse(null);

            Material material = e.getBlockPlaced().getType();

            switch (material)
            {
                case CROPS:
                case BEETROOT_BLOCK:
                case MELON_STEM:
                case PUMPKIN_STEM:
                case POTATO:
                case CARROT:
                    if (!rank.isGardener())
                    {
                        e.setCancelled(true);
                        e.getPlayer()
                         .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getBuildNonGardener()));
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
                         ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getBuildNonBuilder()));
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlaceMonitor(BlockPlaceEvent e)
    {
        if (e.isCancelled())
        {
            e.getPlayer().updateInventory();
            return;
        }

        PlayerRegion region = playerRegionHandler.findByLocation(e.getBlockPlaced().getLocation());

        if (region != null)
        {
            region.setLastAction(new Timestamp(new Date().getTime()));
            playerRegionHandler.updatePlayerRegion(region);
        }
    }
}
