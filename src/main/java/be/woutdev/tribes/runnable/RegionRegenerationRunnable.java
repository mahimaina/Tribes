package be.woutdev.tribes.runnable;

import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.model.Member;
import be.woutdev.tribes.model.PlayerRegion;
import be.woutdev.tribes.model.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Wout on 5/08/2016.
 */
public class RegionRegenerationRunnable extends BukkitRunnable
{
    private final ConcurrentHashMap<UUID, Long> healedAt;
    private final ConcurrentHashMap<UUID, Long> feedAt;
    private final RegionBlockHandler regionBlockHandler;
    private final PlayerRegionHandler playerRegionHandler;

    public RegionRegenerationRunnable(RegionBlockHandler regionBlockHandler, PlayerRegionHandler playerRegionHandler)
    {
        this.regionBlockHandler = regionBlockHandler;
        this.playerRegionHandler = playerRegionHandler;
        this.healedAt = new ConcurrentHashMap<>();
        this.feedAt = new ConcurrentHashMap<>();
    }

    @Override
    public void run()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            PlayerRegion region = playerRegionHandler.findByLocation(player.getLocation());

            if (region == null)
            {
                continue;
            }

            Member member = region.getMembers()
                                  .stream()
                                  .filter(m -> m.getUniqueId().equals(player.getUniqueId()))
                                  .findAny()
                                  .orElse(null);

            if (member == null)
            {
                continue;
            }

            Rank rank = regionBlockHandler.findById(region.getRegionBlockId())
                                          .getRanks()
                                          .stream()
                                          .filter(r -> r.getId() == member.getRankId())
                                          .findAny()
                                          .orElse(null);

            if (rank == null || (rank.getFoodInterval() == 0 && rank.getHealInterval() == 0))
            {
                continue;
            }

            if (player.getHealth() + 1 <= 20)
            {
                if (healedAt.containsKey(player.getUniqueId()))
                {
                    double millis = rank.getHealInterval() * 1000;

                    if (System.currentTimeMillis() - healedAt.get(player.getUniqueId()) >= millis)
                    {
                        player.setHealth(player.getHealth() + 1);
                        healedAt.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }
                else
                {
                    player.setHealth(player.getHealth() + 1);
                    healedAt.put(player.getUniqueId(), System.currentTimeMillis());
                }
            }

            if (player.getFoodLevel() + 1 <= 20)
            {
                if (feedAt.containsKey(player.getUniqueId()))
                {
                    double millis = rank.getFoodInterval() * 1000;

                    if (System.currentTimeMillis() - feedAt.get(player.getUniqueId()) >= millis)
                    {
                        player.setFoodLevel(player.getFoodLevel() + 1);
                        feedAt.put(player.getUniqueId(), System.currentTimeMillis());
                    }
                }
                else
                {
                    player.setFoodLevel(player.getFoodLevel() + 1);
                    feedAt.put(player.getUniqueId(), System.currentTimeMillis());
                }
            }
        }
    }
}
