package be.woutdev.tribes.ebean;

import be.woutdev.tribes.model.Member;
import be.woutdev.tribes.model.PlayerRegion;
import com.avaje.ebean.EbeanServer;
import com.avaje.ebeaninternal.server.core.DefaultServer;
import com.avaje.ebeaninternal.server.ddl.DdlGenerator;

import javax.persistence.PersistenceException;
import java.util.List;

/**
 * Created by Wout on 3-8-2016.
 */
public class TribesDao
{
    private final EbeanServer server;

    public TribesDao(EbeanServer server)
    {
        this.server = server;

        try
        {
            server.find(Member.class).findRowCount();
            server.find(PlayerRegion.class).findRowCount();
        }
        catch (PersistenceException e)
        {
            DdlGenerator gen = ((DefaultServer) server).getDdlGenerator();
            gen.runScript(true, gen.generateCreateDdl());
        }
    }

    /**
     * Insert a new PlayerRegion into the database.
     *
     * @param region The region to insert.
     */
    public void addPlayerRegion(PlayerRegion region)
    {
        server.insert(region);
    }

    /**
     * Get all the PlayerRegions inside the database.
     *
     * @return A list with all the PlayerRegions inside the database.
     */
    public List<PlayerRegion> findAllPlayerRegions()
    {
        return server.find(PlayerRegion.class).findList();
    }

    /**
     * Update the PlayerRegion
     *
     * @param region The region to update.
     */
    public void updatePlayerRegion(PlayerRegion region)
    {
        server.update(region);
    }

    /**
     * Remove the PlayerRegion and its region and members from the database.
     *
     * @param region The PlayerRegion to remove.
     */
    public void removePlayerRegion(PlayerRegion region)
    {
        server.delete(region);
    }

    /**
     * Add the Member to the database.
     *
     * @param member The Member to add.
     */
    public void addMember(Member member)
    {
        server.insert(member);
    }

    /**
     * Remove the Member from the database.
     *
     * @param member The Member to remove.
     */
    public void removeMember(Member member)
    {
        server.delete(member);
    }

    /**
     * Update the Member to the database.
     *
     * @param member The Member to update.
     */
    public void updateMember(Member member)
    {
        server.update(member);
    }
}
