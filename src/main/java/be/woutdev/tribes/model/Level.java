package be.woutdev.tribes.model;

import java.util.Set;

/**
 * Created by Wout on 3-8-2016.
 */
public class Level
{
    private final int id;
    private final String name;
    private final int exp;
    private final int internalRadius;
    private final Set<Integer> restrictedBlocks;

    public Level(int id, String name, int exp, int internalRadius, Set<Integer> restrictedBlocks)
    {
        this.id = id;
        this.name = name;
        this.exp = exp;
        this.internalRadius = internalRadius;
        this.restrictedBlocks = restrictedBlocks;
    }

    /**
     * Get the id of the Level
     *
     * @return The id of the Level
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the name of the Level.
     *
     * @return The name of the Level.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the amount of experience required to upgrade to this level.
     *
     * @return The amount of experience required to upgrade to this level.
     */
    public int getExp()
    {
        return exp;
    }

    /**
     * Get the internal radius for this Level.
     *
     * @return The internal radius for this level.
     */
    public int getInternalRadius()
    {
        return internalRadius;
    }

    /**
     * Get all the restricted blocks (as integers) for this Level.
     *
     * @return A list with all restricted blocks (as integers) for this level.
     */
    public Set<Integer> getRestrictedBlocks()
    {
        return restrictedBlocks;
    }
}