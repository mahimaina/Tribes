package be.woutdev.tribes.handler;

import be.woutdev.tribes.Tribes;
import be.woutdev.tribes.model.PlayerRegion;
import be.woutdev.tribes.runnable.PhysicalActionRunnable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Wout on 5/08/2016.
 */
public class PhysicalActionHandler
{
    private final Tribes instance;
    private final Set<PhysicalActionRunnable> physicalActionRunnables;

    public PhysicalActionHandler(Tribes instance)
    {
        this.instance = instance;
        this.physicalActionRunnables = new HashSet<>();
    }

    public void createAction(Player p, PlayerRegion region, PhysicalActionType type)
    {
        PhysicalActionRunnable runnable = new PhysicalActionRunnable(this, type, region, p);

        physicalActionRunnables.add(runnable);

        if (runnable.getAction() == PhysicalActionType.REMOVE)
        {
            runnable.runTaskLater(instance, 2400L);
        }
        else
        {
            runnable.runTaskLater(instance, 200L);
        }
    }

    public void cancel(Player p)
    {
        PhysicalActionRunnable runnable = physicalActionRunnables.stream()
                                                                 .filter(r -> r.getPlayer()
                                                                               .getUniqueId()
                                                                               .equals(p.getUniqueId()))
                                                                 .findAny()
                                                                 .orElse(null);

        if (runnable != null)
        {
            physicalActionRunnables.remove(runnable);

            if (Bukkit.getScheduler().isCurrentlyRunning(runnable.getTaskId()) ||
                Bukkit.getScheduler().isQueued(runnable.getTaskId()))
            {
                Bukkit.getScheduler().cancelTask(runnable.getTaskId());
            }
        }
    }

    public PhysicalActionRunnable findByPlayer(Player p)
    {
        return physicalActionRunnables.stream()
                                      .filter(par -> par.getPlayer().getUniqueId().equals(p.getUniqueId()))
                                      .findAny()
                                      .orElse(null);
    }

    public Set<PhysicalActionRunnable> getPhysicalActionRunnables()
    {
        return physicalActionRunnables;
    }

    public static enum PhysicalActionType
    {
        OTHER, REMOVE
    }
}
