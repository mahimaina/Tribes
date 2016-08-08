package be.woutdev.tribes.listener;

import be.woutdev.tribes.Tribes;
import be.woutdev.tribes.handler.PhysicalActionHandler;
import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.model.Member;
import be.woutdev.tribes.model.PlayerRegion;
import be.woutdev.tribes.model.Rank;
import be.woutdev.tribes.runnable.PhysicalActionRunnable;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Wout on 5/08/2016.
 */
public class PlayerInteractListener implements Listener
{
    private final Tribes plugin;
    private final PhysicalActionHandler actionHandler;
    private final PlayerRegionHandler playerRegionHandler;
    private final RegionBlockHandler regionBlockHandler;

    public PlayerInteractListener(Tribes plugin)
    {
        this.plugin = plugin;
        this.actionHandler = plugin.getPhysicalActionHandler();
        this.playerRegionHandler = plugin.getPlayerRegionHandler();
        this.regionBlockHandler = plugin.getRegionBlockHandler();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e)
    {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            PlayerRegion region = playerRegionHandler.findByLocation(e.getClickedBlock().getLocation());

            if (region != null)
            {
                if (playerRegionHandler.findByCenter(e.getClickedBlock().getLocation()) != null &&
                    region.getOwner().equals(e.getPlayer().getUniqueId()))
                {
                    if (actionHandler.findByPlayer(e.getPlayer()) != null)
                    {
                        return;
                    }

                    if (e.getPlayer().getItemInHand().getType().equals(Material.BOW))
                    {
                        actionHandler.createAction(e.getPlayer(), region,
                                                   PhysicalActionHandler.PhysicalActionType.REMOVE);
                    }
                    else
                    {
                        actionHandler.createAction(e.getPlayer(), region,
                                                   PhysicalActionHandler.PhysicalActionType.OTHER);
                    }

                    return;
                }

                Member member = region.getMembers()
                                      .stream()
                                      .filter(m -> m.getUniqueId().equals(e.getPlayer().getUniqueId()))
                                      .findAny()
                                      .orElse(null);

                if (member == null && !region.getOwner().equals(e.getPlayer().getUniqueId()))
                {
                    e.setCancelled(true);
                    e.getPlayer()
                     .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getInteractNonMember()));
                    return;
                }

                if (!playerRegionHandler.isInRadius(region, regionBlockHandler.findLevelByExp(region.getExp(),
                                                                                              region.getRegionBlockId())
                                                                              .getInternalRadius(),
                                                    e.getClickedBlock().getLocation()))
                {
                    e.setCancelled(true);
                    e.getPlayer()
                     .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getInteractExternalRadius()));
                    return;
                }

                Material type = e.getClickedBlock().getType();

                switch (type)
                {
                    case BURNING_FURNACE:
                    case FURNACE:
                    case ANVIL:
                    case BREWING_STAND:
                    case ENCHANTMENT_TABLE:
                        break;
                    default:
                        return;
                }

                Rank rank = null;
                if (member != null)
                {
                    rank = regionBlockHandler.findById(region.getRegionBlockId())
                                             .getRanks()
                                             .stream()
                                             .filter(r -> r.getId() == member.getRankId())
                                             .findAny()
                                             .orElse(null);
                }

                if (type.equals(Material.BURNING_FURNACE) || type.equals(Material.FURNACE))
                {
                    if (rank != null && !rank.isCook())
                    {
                        e.setCancelled(true);
                        e.getPlayer()
                         .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getInteractRequiresCook()));
                    }
                }
                else if (type.equals(Material.ANVIL))
                {
                    if (rank != null && !rank.isEngineer())
                    {
                        e.setCancelled(true);
                        e.getPlayer()
                         .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getInteractRequiresEngineer()));
                    }
                }
                else if (type.equals(Material.BREWING_STAND))
                {
                    if (rank != null && !rank.isChemist())
                    {
                        e.setCancelled(true);
                        e.getPlayer()
                         .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getInteractRequiresChemist()));
                    }
                }
                else if (type.equals(Material.ENCHANTMENT_TABLE))
                {
                    if (rank != null && !rank.isWizard())
                    {
                        e.setCancelled(true);
                        e.getPlayer()
                         .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getInteractRequiresWizard()));
                    }
                }
            }
            else if (e.getAction() == Action.LEFT_CLICK_BLOCK)
            {
                if (playerRegionHandler.findByLocation(e.getClickedBlock().getLocation()) == null ||
                    playerRegionHandler.findByLocation(e.getClickedBlock().getLocation()).getId() != region.getId() ||
                    !(region.getOwner().equals(e.getPlayer().getUniqueId())))
                {
                    return;
                }

                PhysicalActionRunnable r = actionHandler.findByPlayer(e.getPlayer());

                if (r != null)
                {
                    if (r.getAction() == PhysicalActionHandler.PhysicalActionType.OTHER)
                    {
                        region.setSpawn(e.getClickedBlock().getLocation().add(0, 1, 0));
                        playerRegionHandler.updatePlayerRegion(region);

                        e.getPlayer()
                         .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getSpawnChanged()));

                        actionHandler.cancel(e.getPlayer());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteractMonitor(PlayerInteractEvent e)
    {
        if (e.isCancelled())
        {
            return;
        }

        PlayerRegion region = playerRegionHandler.findByLocation(e.getPlayer().getLocation());

        if (region != null)
        {
            region.setLastAction(new Timestamp(new Date().getTime()));
            playerRegionHandler.updatePlayerRegion(region);
        }
    }
}
