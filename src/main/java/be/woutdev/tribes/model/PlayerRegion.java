package be.woutdev.tribes.model;

import org.bukkit.Location;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Wout on 3-8-2016.
 */
@Entity
@Table(name = "hapu_player_region")
public class PlayerRegion
{
    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    private UUID owner;
    @Column(nullable = false)
    private int regionBlockId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "playerRegion")
    private List<Member> members;
    @Column(nullable = false, unique = true)
    private Location center;
    @Column(nullable = false, unique = true)
    private Location spawn;
    @Column
    private int exp;
    @Column
    private Timestamp lastAction;
    @Version
    private Long lastUpdate;

    public PlayerRegion()
    {
    }

    public PlayerRegion(UUID owner, RegionBlock block, Location center)
    {
        this.owner = owner;
        this.regionBlockId = block.getId();
        this.center = center;
        this.members = new ArrayList<Member>();
        this.spawn = center.clone().add(0, 1, 0);
        this.exp = 0;
        this.lastAction = new Timestamp(new Date().getTime());
    }

    /**
     * Get the (automatically assigned) id of this PlayerRegion.
     *
     * @return The (automatically assigned) id of this PlayerRegion.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set the (automatically assigned) id of this PlayerRegion. Highly unrecommended. This will cause problems in the
     * database.
     *
     * @param id The id
     */
    @Deprecated
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get the owner of this region.
     *
     * @return The UUID of the owner of this region.
     */
    public UUID getOwner()
    {
        return owner;
    }

    /**
     * Set the uuid of the owner of this region.
     *
     * @param owner The UUID of the owner of this region.
     */
    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    /**
     * Get the id of the RegionBlock this region belongs to.
     *
     * @return The id of the RegionBlock this region belongs to.
     */
    public int getRegionBlockId()
    {
        return regionBlockId;
    }

    /**
     * Set the id of the RegionBlock this region belongs to.
     *
     * @param regionBlockId The id of the RegionBlock this region belongs to.
     */
    public void setRegionBlockId(int regionBlockId)
    {
        this.regionBlockId = regionBlockId;
    }

    /**
     * Get a List of members this region has. The owner is not included.
     *
     * @return The list of members this region has. The owner is not included.
     */
    public List<Member> getMembers()
    {
        return members;
    }

    /**
     * Set a List of members this region has. The owner should not be included.
     *
     * @param members A list of members for this region bar.
     */
    public void setMembers(List<Member> members)
    {
        this.members = members;
    }

    /**
     * Get the center of the PlayerRegion, the location where the flag should be.
     *
     * @return The location of the center of the PlayerRegion.
     */
    public Location getCenter()
    {
        return center;
    }

    /**
     * Set the center of the PlayerRegion, the location where the flag should be.
     *
     * @param center The center of the PlayerRegion.
     */
    public void setCenter(Location center)
    {
        this.center = center;
    }

    /**
     * Get the spawn location for this region. As default this is the center of the region (+ 1 Y).
     *
     * @return The spawn location for this region.
     */
    public Location getSpawn()
    {
        return spawn;
    }

    /**
     * Set the spawn location for this region.
     *
     * @param spawn The spawn location for this region.
     */
    public void setSpawn(Location spawn)
    {
        this.spawn = spawn;
    }

    /**
     * Get the amount of exp this region has.
     *
     * @return The amount of exp this region has.
     */
    public int getExp()
    {
        return exp;
    }

    /**
     * Set the amount of exp this region has. Please use PlayerRegionHandler#addExp and PlayerRegionHandler#removeExp
     * instead.
     *
     * @param exp The amount of exp this region has.
     */
    public void setExp(int exp)
    {
        this.exp = exp;
    }

    /**
     * Get when the last action on this region happend.
     *
     * @return The timestamp when the last action on this region happend.
     */
    public Timestamp getLastAction()
    {
        return lastAction;
    }

    /**
     * Set when the last action on this region happend.
     *
     * @param lastAction When the last action on this region happend.
     */
    public void setLastAction(Timestamp lastAction)
    {
        this.lastAction = lastAction;
    }

    /**
     * Get when this object was last updated in the database. This should only concern Ebeans.
     *
     * @return When this object was last updated in the database.
     */
    public Long getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * Set when this object was last updated in the database. This should only concern Ebeans. Highly unrecommended.
     * This can cause problems in the database.
     *
     * @param lastUpdate When this object was last updated in the database.
     */
    @Deprecated
    public void setLastUpdate(Long lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }
}