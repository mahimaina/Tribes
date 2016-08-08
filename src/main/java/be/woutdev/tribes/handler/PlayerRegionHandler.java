package be.woutdev.tribes.handler;

import be.woutdev.tribes.Tribes;
import be.woutdev.tribes.ebean.TribesDao;
import be.woutdev.tribes.enums.RegionShape;
import be.woutdev.tribes.model.*;
import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Wout on 3-8-2016.
 */
public class PlayerRegionHandler
{
    private final Tribes plugin;
    private final TribesDao dao;
    private final CopyOnWriteArrayList<PlayerRegion> regions;

    public PlayerRegionHandler(Tribes plugin)
    {
        this.plugin = plugin;
        this.dao = new TribesDao(plugin.getDatabase());
        this.regions = new CopyOnWriteArrayList<PlayerRegion>(dao.findAllPlayerRegions());
    }

    /**
     * Add a new region to the database and the cached list.
     *
     * @param region The region to add.
     */
    public void addPlayerRegion(PlayerRegion region)
    {
        regions.add(region);
        dao.addPlayerRegion(region);
    }

    /**
     * Remove a region from the database and the cached list.
     *
     * @param region The region to remove.
     */
    public void removePlayerRegion(PlayerRegion region)
    {
        regions.remove(region);
        dao.removePlayerRegion(region);
    }

    /**
     * Update a region to the database.
     *
     * @param region The region to update.
     */
    public void updatePlayerRegion(PlayerRegion region)
    {
        dao.updatePlayerRegion(region);
    }

    /**
     * Add a new member to the database and the cached list.
     *
     * @param member The member to add.
     */
    public void addMember(Member member)
    {
        regions.stream()
               .filter(r -> r.getId() == member.getPlayerRegion().getId())
               .findAny()
               .ifPresent(r -> r.getMembers().add(member));

        dao.addMember(member);
    }

    /**
     * Remove the member from the database and the cached list.
     *
     * @param member The member to remove.
     */
    public void removeMember(Member member)
    {
        regions.stream()
               .filter(r -> r.getId() == member.getPlayerRegion().getId())
               .findAny()
               .ifPresent(r -> r.getMembers().remove(member));

        dao.removeMember(member);
    }

    /**
     * Update the member to the database.
     *
     * @param member The member to update.
     */
    public void updateMember(Member member)
    {
        dao.updateMember(member);
    }

    /**
     * Check if the given region overlaps any other region.
     *
     * @param region The region to check.
     *
     * @return If this region overlaps any other region.
     */
    public boolean isOverlapping(PlayerRegion region)
    {
        boolean result = false;

        RegionBlockHandler handler = ((Tribes) Bukkit.getPluginManager().getPlugin("Tribes")).getRegionBlockHandler();
        RegionBlock block = handler.findById(region.getRegionBlockId());

        Location location = new Location(region.getCenter().getWorld(), 0, 0, 0);

        if (block.getShape().equals(RegionShape.CYLINDER))
        {
            for (PlayerRegion r : regions)
            {
                RegionBlock rBlock = handler.findById(r.getRegionBlockId());

                if (block.getBottomY() > rBlock.getTopY() || block.getTopY() < rBlock.getBottomY())
                {
                    continue;
                }

                Location regionLocation = region.getCenter().clone();
                regionLocation.setY(0);

                Location rLocation = r.getCenter().clone();
                rLocation.setY(0);

                for (int x = regionLocation.getBlockX() - block.getExternalRadius();
                     x <= regionLocation.getBlockX(); x++)
                {
                    for (int z = regionLocation.getBlockZ() - block.getExternalRadius();
                         z <= regionLocation.getBlockZ(); z++)
                    {
                        if ((x - regionLocation.getBlockX()) * (x - regionLocation.getBlockX()) +
                            (z - regionLocation.getBlockZ()) * (z - regionLocation.getBlockZ()) <=
                            block.getExternalRadius() * block.getExternalRadius())
                        {
                            int xSym = regionLocation.getBlockX() - (x - regionLocation.getBlockX());
                            int zSym = regionLocation.getBlockZ() - (z - regionLocation.getBlockZ());

                            if (findByLocation(location.clone().add(x, 0, z)) != null ||
                                findByLocation(location.clone().add(x, 0, zSym)) != null ||
                                findByLocation(location.clone().add(xSym, 0, z)) != null ||
                                findByLocation(location.clone().add(xSym, 0, zSym)) != null)
                            {
                                result = true;
                            }
                        }
                    }
                }
            }
        }
        else
        {
            if (findByLocation(region.getCenter()) != null)
            {
                result = true;
            }
            else
            {
                Location cloneOne = region.getCenter().clone();
                cloneOne.setY(0);
                cloneOne.add(block.getExternalRadius(), 0, block.getExternalRadius());

                Location cloneTwo = region.getCenter().clone();
                cloneTwo.setY(0);
                cloneTwo.add(block.getExternalRadius(), 0, -block.getExternalRadius());

                Location cloneThree = region.getCenter().clone();
                cloneThree.setY(0);
                cloneThree.add(-block.getExternalRadius(), 0, -block.getExternalRadius());

                Location cloneFour = region.getCenter().clone();
                cloneFour.setY(0);
                cloneFour.add(-block.getExternalRadius(), 0, block.getExternalRadius());

                if (findByLocation(cloneOne) != null || findByLocation(cloneTwo) != null ||
                    findByLocation(cloneThree) != null || findByLocation(cloneFour) != null)
                {
                    result = true;
                }
            }
        }

        return result;
    }

    /**
     * Find a PlayerRegion by location.
     *
     * @param location The location to check.
     *
     * @return The PlayerRegion or null where this Location is in.
     */
    public PlayerRegion findByLocation(Location location)
    {
        PlayerRegion r = null;

        RegionBlockHandler handler = ((Tribes) Bukkit.getPluginManager().getPlugin("Tribes")).getRegionBlockHandler();

        for (PlayerRegion region : regions)
        {
            if (isInRadius(region, handler.findById(region.getRegionBlockId()).getExternalRadius(), location))
            {
                r = region;
            }
        }

        return r;
    }

    /**
     * Check if the location is in the radius from the region's center.
     *
     * @param region The region to check.
     * @param radius The radius to check.
     * @param location The location to check.
     *
     * @return If the location is in radius from the center of the region.
     */
    public boolean isInRadius(PlayerRegion region, int radius, Location location)
    {
        RegionBlockHandler handler = ((Tribes) Bukkit.getPluginManager().getPlugin("Tribes")).getRegionBlockHandler();

        RegionBlock block = handler.findById(region.getRegionBlockId());

        if (location.getY() > block.getTopY() || location.getY() < block.getBottomY())
        {
            return false;
        }

        Location locationCloned = location.clone();
        locationCloned.setY(0);
        locationCloned.setX(locationCloned.getBlockX());
        locationCloned.setZ(locationCloned.getBlockZ());

        Location centerCloned = region.getCenter().clone();
        centerCloned.setY(0);

        if (block.getShape().equals(RegionShape.CYLINDER))
        {
            return centerCloned.distance(locationCloned) <= radius;
        }
        else
        {
            Location centerClonedTwo = centerCloned.clone();
            centerClonedTwo.subtract(radius, 0, radius); // Point one

            Location centerClonedThree = centerCloned.clone();
            centerClonedThree.add(radius, 0, radius); // Point two

            return locationCloned.getZ() >= centerClonedTwo.getZ() &&
                   locationCloned.getX() <= centerClonedThree.getX() &&
                   locationCloned.getZ() <= centerClonedThree.getZ() && locationCloned.getX() >= centerClonedTwo.getX();
        }
    }

    /**
     * Find a PlayerRegion by center location.
     *
     * @param location The location to check.
     *
     * @return The PlayerRegion or null.
     */
    public PlayerRegion findByCenter(Location location)
    {
        return regions.stream().filter(r -> r.getCenter().equals(location)).findAny().orElse(null);
    }

    /**
     * Find the closest PlayerRegion where the player is member of.
     *
     * @param player The player to check.
     *
     * @return The closest PlayerRegion where the player is member of.
     */
    public PlayerRegion findClosestMemberRegionForPlayer(Player player)
    {
        Location location = player.getLocation();

        HashMap<PlayerRegion, Double> distanceMap = new HashMap<>();

        for (PlayerRegion region : regions)
        {
            if (region.getMembers()
                      .stream()
                      .filter(m -> player.getUniqueId().equals(m.getUniqueId()))
                      .findAny()
                      .isPresent() || region.getOwner().equals(player.getUniqueId()))
            {
                Location centerClone = region.getCenter().clone();
                centerClone.setY(0);

                Location locationClone = location.clone();
                locationClone.setY(0);

                distanceMap.put(region, centerClone.distance(locationClone));
            }
        }

        Map.Entry<PlayerRegion, Double> min = null;

        for (Map.Entry<PlayerRegion, Double> entry : distanceMap.entrySet())
        {
            if (min == null || min.getValue() > entry.getValue())
            {
                min = entry;
            }
        }

        return min == null ? null : min.getKey();
    }

    /**
     * Add some experience to a region. This will update the database and possibly do actions.
     *
     * @param region The region.
     * @param exp The experience to add.
     */
    public void addExp(PlayerRegion region, int exp)
    {
        int originalExp = region.getExp();
        region.setExp(originalExp + exp);
        updatePlayerRegion(region);

        Level level = newLevel(originalExp, originalExp + exp, region.getRegionBlockId());
        if (level != null)
        {
            List<UUID> membersAndOwner = Lists.newArrayList();
            region.getMembers()
                  .stream()
                  .filter(m -> Bukkit.getPlayer(m.getUniqueId()) != null)
                  .forEach(m -> membersAndOwner.add(m.getUniqueId()));

            if (Bukkit.getPlayer(region.getOwner()) != null)
            {
                membersAndOwner.add(region.getOwner());
            }

            FireworkEffect effect = FireworkEffect.builder()
                                                  .flicker(true)
                                                  .trail(true)
                                                  .withColor(Color.YELLOW)
                                                  .withColor(Color.ORANGE)
                                                  .build();

            membersAndOwner.forEach(m -> {
                Player p = Bukkit.getPlayer(m);

                if (findByLocation(p.getLocation()).getId() != region.getId())
                {
                    return;
                }

                Firework firework = p.getWorld().spawn(p.getLocation(), Firework.class);
                FireworkMeta meta = firework.getFireworkMeta();
                meta.addEffect(effect);
                meta.setPower(3);
                firework.setFireworkMeta(meta);

                p.sendMessage(plugin.getMessagesConfig().getRegionUpgraded().replace("{level}", level.getName()));
            });
        }
    }

    /**
     * Remove some experience from from a region. This will update the database and possibly do actions.
     *
     * @param region The region.
     * @param exp The experience to remove.
     */
    public void removeExp(PlayerRegion region, int exp)
    {
        int originalExp = region.getExp();
        region.setExp(originalExp - exp);
        updatePlayerRegion(region);

        Level level = newLevel(originalExp, originalExp - exp, region.getRegionBlockId());
        if (level != null)
        {
            List<UUID> membersAndOwner = Lists.newArrayList();
            region.getMembers()
                  .stream()
                  .filter(m -> Bukkit.getPlayer(m.getUniqueId()) != null)
                  .forEach(m -> membersAndOwner.add(m.getUniqueId()));

            if (Bukkit.getPlayer(region.getOwner()) != null)
            {
                membersAndOwner.add(region.getOwner());
            }

            membersAndOwner.forEach(m -> {
                Player p = Bukkit.getPlayer(m);

                if (findByLocation(p.getLocation()).getId() != region.getId())
                {
                    return;
                }

                p.sendMessage(plugin.getMessagesConfig().getRegionDowngraded().replace("{level}", level.getName()));
            });
        }
    }

    /**
     * Returns the new level if there is a difference, otherwise null.
     *
     * @param original The original amount of exp.
     * @param now The new amount of exp.
     * @param regionBlockId The id of the RegionBlock where the region belongs to.
     *
     * @return The new level or null.
     */
    public Level newLevel(int original, int now, int regionBlockId)
    {
        RegionBlockHandler handler = ((Tribes) Bukkit.getPluginManager().getPlugin("Tribes")).getRegionBlockHandler();

        Level originalLevel = handler.findLevelByExp(original, regionBlockId);
        Level nowLevel = handler.findLevelByExp(now, regionBlockId);

        return originalLevel.getId() == nowLevel.getId() ? null : nowLevel;
    }

    /**
     * Get a list of all PlayerRegions.
     *
     * @return A list of all PlayerRegions.
     */
    public List<PlayerRegion> getRegions()
    {
        return regions;
    }
}
