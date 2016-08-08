package be.woutdev.tribes.listener;

import be.woutdev.tribes.handler.PhysicalActionHandler;
import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.model.Member;
import be.woutdev.tribes.model.PlayerRegion;
import be.woutdev.tribes.model.Rank;
import be.woutdev.tribes.runnable.PhysicalActionRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by Wout on 5/08/2016.
 */
public class PlayerInteractEntityListener implements Listener
{
    private final PlayerRegionHandler playerRegionHandler;
    private final PhysicalActionHandler physicalActionHandler;
    private final RegionBlockHandler regionBlockHandler;

    public PlayerInteractEntityListener(PlayerRegionHandler playerRegionHandler, PhysicalActionHandler physicalActionHandler, RegionBlockHandler regionBlockHandler)
    {
        this.playerRegionHandler = playerRegionHandler;
        this.physicalActionHandler = physicalActionHandler;
        this.regionBlockHandler = regionBlockHandler;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
    {
        if (e.getRightClicked() instanceof Player)
        {
            Player clicked = (Player) e.getRightClicked();

            if (playerRegionHandler.findByLocation(e.getPlayer().getLocation()) != null &&
                playerRegionHandler.findByLocation(e.getRightClicked().getLocation()) != null)
            {
                if (playerRegionHandler.findByLocation(e.getPlayer().getLocation()).getId() ==
                    playerRegionHandler.findByLocation(e.getRightClicked().getLocation()).getId())
                {
                    PlayerRegion region = playerRegionHandler.findByLocation(e.getPlayer().getLocation());

                    if (e.getPlayer().getUniqueId().equals(region.getOwner()))
                    {
                        PhysicalActionRunnable action = physicalActionHandler.findByPlayer(e.getPlayer());

                        if (action != null)
                        {
                            if (action.getAction() == PhysicalActionHandler.PhysicalActionType.OTHER)
                            {
                                Member memberClicked = region.getMembers()
                                                             .stream()
                                                             .filter(m -> m.getUniqueId().equals(clicked.getUniqueId()))
                                                             .findAny()
                                                             .orElse(null);

                                if (memberClicked != null)
                                {
                                    Rank rank = regionBlockHandler.findById(region.getRegionBlockId())
                                                                  .getRanks()
                                                                  .stream()
                                                                  .filter(
                                                                          r -> r.getId() ==
                                                                               memberClicked.getRankId() + 1)
                                                                  .findAny()
                                                                  .orElse(null);

                                    Bukkit.dispatchCommand(e.getPlayer(), "tribes add " + clicked.getName() + " " +
                                                                          (rank == null ? "null" : rank.getName()));
                                }
                                else
                                {
                                    Bukkit.dispatchCommand(e.getPlayer(), "tribes add " + clicked.getName());
                                }

                                physicalActionHandler.cancel(e.getPlayer());
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteractEntityMonitor(PlayerInteractEntityEvent e)
    {
        if (e.isCancelled())
        {
            return;
        }

        PlayerRegion region = playerRegionHandler.findByLocation(e.getRightClicked().getLocation());

        if (region != null)
        {
            region.setLastAction(new Timestamp(new Date().getTime()));
            playerRegionHandler.updatePlayerRegion(region);
        }
    }
}
