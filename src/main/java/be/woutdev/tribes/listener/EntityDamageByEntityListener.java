package be.woutdev.tribes.listener;

import be.woutdev.tribes.Tribes;
import be.woutdev.tribes.handler.PhysicalActionHandler;
import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.model.Member;
import be.woutdev.tribes.model.PlayerRegion;
import be.woutdev.tribes.model.Rank;
import be.woutdev.tribes.runnable.PhysicalActionRunnable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Wout on 5/08/2016.
 */
public class EntityDamageByEntityListener implements Listener
{
    private final Tribes plugin;
    private final PhysicalActionHandler actionHandler;
    private final PlayerRegionHandler playerRegionHandler;
    private final RegionBlockHandler regionBlockHandler;

    public EntityDamageByEntityListener(Tribes plugin)
    {
        this.plugin = plugin;
        this.actionHandler = plugin.getPhysicalActionHandler();
        this.playerRegionHandler = plugin.getPlayerRegionHandler();
        this.regionBlockHandler = plugin.getRegionBlockHandler();
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e)
    {
        if (e.getDamager() instanceof Player)
        {
            Player damager = (Player) e.getDamager();

            PlayerRegion region = playerRegionHandler.findByLocation(damager.getLocation());

            if (region != null)
            {
                Member member = region.getMembers()
                                      .stream()
                                      .filter(m -> m.getUniqueId().equals(damager.getUniqueId()))
                                      .findAny()
                                      .orElse(null);

                if (member == null && !region.getOwner().equals(damager.getUniqueId()))
                {
                    e.setCancelled(true);
                    damager.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                          .getDamageNonMember()));
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

                if (e.getEntity() instanceof Player)
                {
                    if (rank != null && !rank.isWarrior())
                    {
                        e.setCancelled(true);
                        damager.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                              .getDamageRequiresWarrior()));
                    }
                }
                else
                {
                    if (rank != null && !rank.isProtector())
                    {
                        e.setCancelled(true);
                        damager.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                              .getDamageRequiresProtector()));
                    }
                }
            }
        }
        else if (e.getDamager() instanceof Arrow)
        {
            Arrow arrow = (Arrow) e.getDamager();

            if (arrow.getShooter() instanceof Player)
            {
                Player damager = (Player) arrow.getShooter();

                PlayerRegion region = playerRegionHandler.findByLocation(e.getEntity().getLocation());

                if (region != null)
                {
                    Member member = region.getMembers()
                                          .stream()
                                          .filter(m -> m.getUniqueId().equals(damager.getUniqueId()))
                                          .findAny()
                                          .orElse(null);

                    if (member == null && !region.getOwner().equals(damager.getUniqueId()))
                    {
                        e.setCancelled(true);
                        damager.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                              .getDamageNonMember()));
                        return;
                    }

                    PhysicalActionRunnable rar = actionHandler.findByPlayer(damager);

                    if (region.getOwner().equals(damager.getUniqueId()) && rar != null &&
                        e.getEntity() instanceof Player && rar.getAction() ==
                                                           PhysicalActionHandler.PhysicalActionType.REMOVE)
                    {
                        Member shotMember = region.getMembers()
                                                  .stream()
                                                  .filter(m -> m.getUniqueId().equals(e.getEntity().getUniqueId()))
                                                  .findAny()
                                                  .orElse(null);

                        if (shotMember != null)
                        {
                            Bukkit.dispatchCommand(damager, "tribes rem " + e.getEntity().getName());
                            actionHandler.cancel(damager);
                        }

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

                    if (e.getEntity() instanceof Player)
                    {
                        if (rank != null && !rank.isWarrior())
                        {
                            e.setCancelled(true);
                            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                                  .getDamageRequiresWarrior()));
                        }
                    }
                    else
                    {
                        if (rank != null && !rank.isProtector())
                        {
                            e.setCancelled(true);
                            damager.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                                  .getDamageRequiresProtector()));
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent e)
    {
        if (e.isCancelled())
        {
            return;
        }

        PlayerRegion region = playerRegionHandler.findByLocation(e.getEntity().getLocation());

        if (region != null)
        {
            region.setLastAction(new Timestamp(new Date().getTime()));
            playerRegionHandler.updatePlayerRegion(region);
        }
    }
}
