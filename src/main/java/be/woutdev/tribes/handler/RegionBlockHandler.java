package be.woutdev.tribes.handler;

import be.woutdev.tribes.config.RegionsConfig;
import be.woutdev.tribes.model.Level;
import be.woutdev.tribes.model.RegionBlock;
import com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Wout on 3-8-2016.
 */
public class RegionBlockHandler
{
    private final List<RegionBlock> regionBlocks;

    public RegionBlockHandler(RegionsConfig config)
    {
        this.regionBlocks = config.getRegionBlocks();
    }

    /**
     * Get all registered RegionBlocks.
     *
     * @return A list with all RegionBlocks available.
     */
    public List<RegionBlock> getRegionBlocks()
    {
        return regionBlocks;
    }

    /**
     * Get a RegionBlock by its flag.
     *
     * @param flag The flag.
     *
     * @return The RegionBlock which has the flag.
     */
    public RegionBlock findByFlag(ItemStack flag)
    {
        return regionBlocks.stream().filter(rb -> rb.getFlag().isSimilar(flag)).findAny().orElse(null);
    }

    /**
     * Get a RegionBlock by its id.
     *
     * @param id The id.
     *
     * @return The RegionBlock with the given id.
     */
    public RegionBlock findById(int id)
    {
        return regionBlocks.stream().filter(rb -> rb.getId() == id).findAny().orElse(null);
    }

    /**
     * Get a RegionBlock by its flag type.
     *
     * @param type The type.
     *
     * @return The RegionBlock which has the type as flagtype.
     */
    public RegionBlock findByType(Material type)
    {
        return regionBlocks.stream().filter(rb -> rb.getFlag().getType().equals(type)).findAny().orElse(null);
    }

    /**
     * Get a Level by exp.
     *
     * @param exp The exp to check.
     * @param regionBlockId The id of RegionBlock where the region belongs to.
     *
     * @return The level.
     */
    public Level findLevelByExp(int exp, int regionBlockId)
    {
        List<Level> possibleLevels = Lists.newArrayList();
        findById(regionBlockId).getLevels().stream().filter(l -> l.getExp() <= exp).forEach(possibleLevels::add);

        Collections.sort(possibleLevels, new Comparator<Level>()
        {
            @Override
            public int compare(Level o1, Level o2)
            {
                return ((Integer) o1.getExp()).compareTo((Integer) o2.getExp());
            }
        });

        return possibleLevels.get(possibleLevels.size() - 1);
    }
}
