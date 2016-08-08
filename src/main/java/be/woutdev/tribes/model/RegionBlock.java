package be.woutdev.tribes.model;

import be.woutdev.tribes.enums.RegionShape;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * Created by Wout on 3-8-2016.
 */
public class RegionBlock
{
    private final int id;
    private final String name;
    private final String parent;
    private final ItemStack flag;
    private final int regionReset;
    private final RegionShape shape;
    private final int bottomY;
    private final int topY;
    private final int externalRadius;
    private final List<Level> levels;
    private final List<Rank> ranks;

    public RegionBlock(int id, String name, String parent, ItemStack flag, int regionReset, RegionShape shape, int bottomY, int topY, int externalRadius, List<Level> levels, List<Rank> ranks)
    {
        this.id = id;
        this.name = name;
        this.parent = parent;
        this.flag = flag;
        this.regionReset = regionReset;
        this.shape = shape;
        this.bottomY = bottomY;
        this.topY = topY;
        this.externalRadius = externalRadius;
        this.levels = levels;
        this.ranks = ranks;
    }

    /**
     * Get the Id of this RegionBlock.
     *
     * @return The id of this RegionBlock.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the name of this RegionBlock.
     *
     * @return The name of this RegionBlock.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the parent (name) of this RegionBlock.
     *
     * @return The parent (name) of this RegionBlock.
     */
    public String getParent()
    {
        return parent;
    }

    /**
     * Get the ItemStack that is used as a flag to create a region.
     *
     * @return The ItemStack that is used as a flag to create a region.
     */
    public ItemStack getFlag()
    {
        return flag;
    }

    /**
     * Get (in days) when this region should be automatically removed.
     *
     * @return The amount of days before this region should be automatically removed.
     */
    public int getRegionReset()
    {
        return regionReset;
    }

    /**
     * Get the shape for the regions inside this RegionBlock.
     *
     * @return The shape for the regions inside this RegionBlock.
     */
    public RegionShape getShape()
    {
        return shape;
    }

    /**
     * Get the lowest Y-coordinate for all regions inside this RegionBlock.
     *
     * @return The lowest Y-coordinate for all regions inside this RegionBlock.
     */
    public int getBottomY()
    {
        return bottomY;
    }

    /**
     * Get the highest Y-coordinate for all regions inside this RegionBlock.
     *
     * @return The highest Y-coordinate for all regions inside this RegionBlock.
     */
    public int getTopY()
    {
        return topY;
    }

    /**
     * Get the external radius for all regions inside this RegionBlock.
     *
     * @return The external radius for all regions inside this RegionBlock.
     */
    public int getExternalRadius()
    {
        return externalRadius;
    }

    /**
     * Get a list of all Levels for this RegionBlock.
     *
     * @return A list of all Levels for this RegionBlock.
     */
    public List<Level> getLevels()
    {
        return levels;
    }

    /**
     * Get a list of all Ranks for this RegionBlock.
     *
     * @return A list of all Ranks for this RegionBlock.
     */
    public List<Rank> getRanks()
    {
        return ranks;
    }
}