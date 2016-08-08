package be.woutdev.tribes.model;

/**
 * Created by Wout on 3-8-2016.
 */
public class Rank
{
    private int id;
    private String name;
    private double healInterval;
    private double foodInterval;
    private boolean protector;
    private boolean cook;
    private boolean gardener;
    private boolean warrior;
    private boolean builder;
    private boolean engineer;
    private boolean chemist;
    private boolean wizard;
    private boolean chiefsAide;

    public Rank(int id, String name, double healInterval, double foodInterval, boolean protector, boolean cook, boolean gardener, boolean warrior, boolean builder, boolean engineer, boolean chemist, boolean wizard, boolean chiefsAide)
    {
        this.id = id;
        this.name = name;
        this.healInterval = healInterval;
        this.foodInterval = foodInterval;
        this.protector = protector;
        this.cook = cook;
        this.gardener = gardener;
        this.warrior = warrior;
        this.builder = builder;
        this.engineer = engineer;
        this.chemist = chemist;
        this.wizard = wizard;
        this.chiefsAide = chiefsAide;
    }

    /**
     * Get the Id of this rank.
     *
     * @return The Id of the rank.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the name of the rank.
     *
     * @return The name of the rank.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the heal interval in seconds.
     *
     * @return The heal interval in seconds.
     */
    public double getHealInterval()
    {
        return healInterval;
    }

    /**
     * Get the food interval in seconds.
     *
     * @return The food interval in seconds.
     */
    public double getFoodInterval()
    {
        return foodInterval;
    }

    /**
     * Check if this Rank is considered a protector.
     *
     * @return If this Rank is considered a Protector.
     */
    public boolean isProtector()
    {
        return protector;
    }

    /**
     * Check if this Rank is considered a cook.
     *
     * @return If this Rank is considered a cook.
     */
    public boolean isCook()
    {
        return cook;
    }

    /**
     * Check if this Rank is considered a gardener.
     *
     * @return If this Rank is considered a gardener.
     */
    public boolean isGardener()
    {
        return gardener;
    }

    /**
     * Check if this Rank is considered a warrior.
     *
     * @return If this Rank is considered a warrior.
     */
    public boolean isWarrior()
    {
        return warrior;
    }

    /**
     * Check if this Rank is considered a builder.
     *
     * @return If this Rank is considered a builder.
     */
    public boolean isBuilder()
    {
        return builder;
    }

    /**
     * Check if this Rank is considered a engineer.
     *
     * @return If this Rank is considered a engineer.
     */
    public boolean isEngineer()
    {
        return engineer;
    }

    /**
     * Check if this Rank is considered a chemist.
     *
     * @return If this Rank is considered a chemist.
     */
    public boolean isChemist()
    {
        return chemist;
    }

    /**
     * Check if this Rank is considered a wizard.
     *
     * @return If this Rank is considered a wizard.
     */
    public boolean isWizard()
    {
        return wizard;
    }

    /**
     * Check if this Rank is considered a chiefs aide.
     *
     * @return If this Rank is considered a chiefs aide.
     */
    public boolean isChiefsAide()
    {
        return chiefsAide;
    }
}