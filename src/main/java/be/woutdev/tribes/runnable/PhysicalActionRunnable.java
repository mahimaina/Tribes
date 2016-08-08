package be.woutdev.tribes.runnable;

import be.woutdev.tribes.handler.PhysicalActionHandler;
import be.woutdev.tribes.model.PlayerRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created by Wout on 5/08/2016.
 */
public class PhysicalActionRunnable extends BukkitRunnable
{
    private final PhysicalActionHandler handler;
    private final PhysicalActionHandler.PhysicalActionType action;
    private final PlayerRegion region;
    private final Player player;

    public PhysicalActionRunnable(PhysicalActionHandler handler, PhysicalActionHandler.PhysicalActionType action, PlayerRegion region, Player player)
    {
        this.handler = handler;
        this.action = action;
        this.region = region;
        this.player = player;
    }

    @Override
    public void run()
    {
        PhysicalActionRunnable instance = handler.getPhysicalActionRunnables()
                                                 .stream()
                                                 .filter(r -> r.getPlayer().getUniqueId().equals(player.getUniqueId()))
                                                 .findAny()
                                                 .orElse(null);

        if (instance != null)
        {
            handler.getPhysicalActionRunnables().remove(instance);

            if (player.isOnline())
            {
                if (player.getLocation().distance(region.getSpawn()) < 1)
                {
                    Bukkit.dispatchCommand(player, "tribes call");
                }
                else
                {
                    Bukkit.dispatchCommand(player, "tribes info");
                }
            }
        }
    }

    public PhysicalActionHandler getHandler()
    {
        return handler;
    }

    public PhysicalActionHandler.PhysicalActionType getAction()
    {
        return action;
    }

    public PlayerRegion getRegion()
    {
        return region;
    }

    public Player getPlayer()
    {
        return player;
    }
}
