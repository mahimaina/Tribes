package be.woutdev.tribes.model;

import javax.persistence.*;
import java.util.UUID;

/**
 * Created by Wout on 3-8-2016.
 */
@Entity
@Table(name = "hapu_member")
public class Member
{
    @Id
    @GeneratedValue
    private int id;
    @Column(nullable = false)
    private UUID uniqueId;
    @Column(nullable = false)
    private int rankId;
    @ManyToOne
    private PlayerRegion playerRegion;
    @Version
    private Long lastUpdate;

    public Member()
    {

    }

    public Member(UUID uniqueId, Rank rank, PlayerRegion playerRegion)
    {
        this.uniqueId = uniqueId;
        this.rankId = rank.getId();
        this.playerRegion = playerRegion;
    }

    /**
     * Get the (automatically assigned) id for this Member object. (Region specific)
     *
     * @return The automatically assigned id for this Member which is region specific.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Set the (automatically assigned) id for this Member object. (Region specific). Highly unrecommended! This will
     * cause problems in the database.
     *
     * @param id The id
     */
    @Deprecated
    public void setId(int id)
    {
        this.id = id;
    }

    /**
     * Get the unique id of the member.
     *
     * @return The unique id of the player.
     */
    public UUID getUniqueId()
    {
        return uniqueId;
    }

    /**
     * Set the unique id of the member.
     *
     * @param uniqueId The unique id of the member.
     */
    public void setUniqueId(UUID uniqueId)
    {
        this.uniqueId = uniqueId;
    }

    /**
     * Get the rank Id corresponding with a rank for the specified RegionBlock. See PlayerRegion#getRegionBlockId and
     * RegionBlockHandler#findById
     *
     * @return The id of the rank
     */
    public int getRankId()
    {
        return rankId;
    }

    /**
     * Set the rank Id corresponding with a rank for the specified RegionBlock. See PlayerRegion#getRegionBlockId and
     * RegionBlockHandler#findById
     *
     * @param rankId The id of the rank
     */
    public void setRankId(int rankId)
    {
        this.rankId = rankId;
    }

    /**
     * Get the PlayerRegion this member is member of.
     *
     * @return The PlayerRegion this member is member of.
     */
    public PlayerRegion getPlayerRegion()
    {
        return playerRegion;
    }

    /**
     * Set the PlayerRegion this member is member of.
     */
    public void setPlayerRegion(PlayerRegion playerRegion)
    {
        this.playerRegion = playerRegion;
    }

    /**
     * Get when this Member object was last updated in the database. This should only concern Ebeans.
     *
     * @return The Timestamp when this Member object was last updated in the database.
     */
    public Long getLastUpdate()
    {
        return lastUpdate;
    }

    /**
     * Set when this Member object was last updated in the database. This should only concern Ebeans. Highly
     * unrecommended! This can cause problems in the database.
     *
     * @param lastUpdate The Timestamp when this Member object was last updated in the database.
     */
    @Deprecated
    public void setLastUpdate(Long lastUpdate)
    {
        this.lastUpdate = lastUpdate;
    }
}
