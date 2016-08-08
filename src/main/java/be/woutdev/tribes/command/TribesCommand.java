package be.woutdev.tribes.command;

import be.woutdev.tribes.Tribes;
import be.woutdev.tribes.config.RegionsConfig;
import be.woutdev.tribes.handler.PlayerRegionHandler;
import be.woutdev.tribes.handler.RegionBlockHandler;
import be.woutdev.tribes.helper.PermissionHelper;
import be.woutdev.tribes.model.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Wout on 3-8-2016.
 */
public class TribesCommand implements CommandExecutor
{
    private final Tribes plugin;
    private final RegionBlockHandler regionBlockHandler;
    private final PlayerRegionHandler handler;

    public TribesCommand(Tribes plugin)
    {
        this.plugin = plugin;
        this.regionBlockHandler = plugin.getRegionBlockHandler();
        this.handler = plugin.getPlayerRegionHandler();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(ChatColor.RED + "Tribe commands are player only.");
            return false;
        }

        Player p = (Player) sender;

        if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("help"))
        {
            handleHelp(p);

            return true;
        }

        String subCommand = args[0];

        switch (subCommand.toLowerCase())
        {
            case "add":
                handleAdd(p, args);
                break;
            case "rem":
                handleRemove(p, args);
                break;
            case "setspawn":
                handleSetSpawn(p);
                break;
            case "call":
                handleCall(p);
                break;
            case "spawn":
                handleSpawn(p);
                break;
            case "leave":
                handleLeave(p);
                break;
            case "info":
                handleInfo(p);
                break;
            case "admin":
                handleAdmin(p, args);
                break;
            default:
                handleUnknown(p);
                break;
        }

        return true;
    }

    // Handle command /tribes info
    private void handleInfo(Player p)
    {
        PlayerRegion region = handler.findByLocation(p.getLocation());

        if (region == null)
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotInRegion()));
            return;
        }

        // Going async
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
        {
            @Override
            public void run()
            {
                String type = regionBlockHandler.findById(region.getRegionBlockId()).getName();
                String owner = null;
                String level = null;
                StringBuilder members = new StringBuilder();

                if (Bukkit.getPlayer(region.getOwner()) != null)
                {
                    owner = Bukkit.getPlayer(region.getOwner()).getName();
                }
                else
                {
                    owner = plugin.getCacheHandler().findByUUID(region.getOwner());
                }

                Level l = regionBlockHandler.findLevelByExp(region.getExp(), region.getRegionBlockId());
                level = l.getName() + " (" + region.getExp() + ")";

                for (Member member : region.getMembers())
                {
                    if (Bukkit.getPlayer(member.getUniqueId()) != null)
                    {
                        members.append(Bukkit.getPlayer(member.getUniqueId()).getName()).append(", ");
                    }
                    else
                    {
                        String name = plugin.getCacheHandler().findByUUID(member.getUniqueId());

                        if (name == null)
                        {
                            continue;
                        }

                        members.append(name).append(", ");
                    }
                }

                if (members.toString().length() >= 3)
                {
                    if (members.toString().charAt(members.length() - 1) == ' ')
                    {
                        members = new StringBuilder(members.substring(0, members.length() - 2));
                    }
                }
                else
                {
                    members = new StringBuilder(plugin.getMessagesConfig().getCommandInfoNone());
                }

                for (String s : plugin.getMessagesConfig().getCommandInfo())
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', s.replace("{region}",
                                                                                        String.valueOf(region.getId()))
                                                                               .replace("{type}", type)
                                                                               .replace("{owner}", owner)
                                                                               .replace("{level}", level)
                                                                               .replace("{members}", members)));
                }
            }
        });
    }

    // Handle commands for /tribes admin
    private void handleAdmin(Player p, String[] args)
    {
        if (PermissionHelper.hasPermission(p, "tribes.admin"))
        {
            if (args.length != 2 && args.length != 3)
            {
                handleUnknown(p);
                return;
            }

            if (args[1].equalsIgnoreCase("reload"))
            {
                RegionsConfig config = plugin.getRegionsConfig();
                config.load();

                plugin.getMessagesConfig().load();

                regionBlockHandler.getRegionBlocks().clear();
                regionBlockHandler.getRegionBlocks().addAll(config.getRegionBlocks());

                p.sendMessage(ChatColor.GREEN + "Configuration file reloaded.");
            }
            else if (args[1].equalsIgnoreCase("killall"))
            {
                PlayerRegion region = handler.findByLocation(p.getLocation());

                if (region == null)
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandNotInRegion()));
                    return;
                }

                region.getSpawn()
                      .getWorld()
                      .getEntities()
                      .stream()
                      .filter(e -> handler.findByLocation(e.getLocation()) != null &&
                                   handler.findByLocation(e.getLocation()).getId() == region.getId() &&
                                   e instanceof Monster)
                      .forEach(Entity::remove);

                p.sendMessage(ChatColor.GREEN + "Killed all hostile mobs in region " + region.getId() + ".");
            }
            else if (args[1].equalsIgnoreCase("getflag"))
            {
                try
                {
                    int id = Integer.parseInt(args[2]);

                    RegionBlock block = regionBlockHandler.findById(id);

                    if (block == null)
                    {
                        p.sendMessage(ChatColor.RED + "Regionblock with id " + id + " not found.");
                        return;
                    }

                    p.getInventory().addItem(block.getFlag());
                    p.sendMessage(ChatColor.GREEN + "You received the flag for regionblock " + block.getName() + ".");
                }
                catch (Exception e)
                {
                    handleUnknown(p);
                }
            }
            else if (args[1].equalsIgnoreCase("addexp"))
            {
                PlayerRegion region = handler.findByLocation(p.getLocation());

                if (region == null)
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandNotInRegion()));
                    return;
                }

                try
                {
                    int amount = Integer.valueOf(args[2]);

                    handler.addExp(region, amount);

                    p.sendMessage(
                            ChatColor.GREEN + "You gave " + amount + " experience to region " + region.getId() + ".");
                }
                catch (Exception e)
                {
                    handleUnknown(p);
                }
            }
            else if (args[1].equalsIgnoreCase("remexp"))
            {
                PlayerRegion region = handler.findByLocation(p.getLocation());

                if (region == null)
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandNotInRegion()));
                    return;
                }

                try
                {
                    int amount = Integer.valueOf(args[2]);

                    handler.removeExp(region, amount);

                    p.sendMessage(
                            ChatColor.GREEN + "You took " + amount + " experience from region " + region.getId() + ".");
                }
                catch (Exception e)
                {
                    handleUnknown(p);
                }
            }
            else if (args[1].equalsIgnoreCase("setlevel"))
            {
                PlayerRegion region = handler.findByLocation(p.getLocation());

                if (region == null)
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandNotInRegion()));
                    return;
                }

                try
                {
                    int level = Integer.valueOf(args[2]);

                    Level l = regionBlockHandler.findById(region.getRegionBlockId())
                                                .getLevels()
                                                .stream()
                                                .filter(le -> le.getId() == level)
                                                .findAny()
                                                .orElse(null);

                    if (l == null)
                    {
                        p.sendMessage(ChatColor.RED + "No level found with id " + level + ".");
                        return;
                    }

                    int difference = region.getExp() - l.getExp();

                    if (difference < 0)
                    {
                        handler.addExp(region, Math.abs(difference));
                        p.sendMessage(
                                ChatColor.GREEN + "You gave " + Math.abs(difference) + " experience to region " +
                                region.getId() + ".");
                    }
                    else
                    {
                        handler.removeExp(region, difference);
                        p.sendMessage(
                                ChatColor.GREEN + "You took " + difference + " experience from region " +
                                region.getId() +
                                ".");
                    }
                }
                catch (Exception e)
                {
                    handleUnknown(p);
                }
            }
            else
            {
                handleUnknown(p);
            }
        }
        else
        {
            p.sendMessage(ChatColor.RED + "You are not allowed to do this!");
        }
    }

    // Handle command /tribe leave
    private void handleLeave(Player p)
    {
        PlayerRegion region = handler.findByLocation(p.getLocation());

        if (region == null)
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotInRegion()));
            return;
        }

        if (region.getOwner().equals(p.getUniqueId()))
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                            .getCommandOwnerCannotLeave()));
            return;
        }

        Member member = region.getMembers()
                              .stream()
                              .filter(m -> m.getUniqueId().equals(p.getUniqueId()))
                              .findAny()
                              .orElse(null);

        if (member == null)
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandCannotLeave()));
            return;
        }

        handler.removeMember(member);

        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                        .getCommandLeftRegion()
                                                                        .replace("{region}",
                                                                                 String.valueOf(region.getId()))));

        region.setLastAction(new Timestamp(new Date().getTime()));
        handler.updatePlayerRegion(region);
    }

    // Handle command /tribes spawn
    private void handleSpawn(Player p)
    {
        PlayerRegion region = handler.findByLocation(p.getLocation());

        if (region == null)
        {
            region = handler.findClosestMemberRegionForPlayer(p);

            if (region == null)
            {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                .getCommandNotInRegion()));
                return;
            }
        }
        else
        {
            Member member = region.getMembers()
                                  .stream()
                                  .filter(m -> m.getUniqueId().equals(p.getUniqueId()))
                                  .findAny()
                                  .orElse(null);

            if (member == null && !region.getOwner().equals(p.getUniqueId()))
            {
                region = handler.findClosestMemberRegionForPlayer(p);

                if (region == null)
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandNotInRegion()));
                    return;
                }
            }
        }

        p.teleport(region.getSpawn());
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                        .getCommandTeleportedToSpawn()
                                                                        .replace("{region}",
                                                                                 String.valueOf(region.getId()))));

        region.setLastAction(new Timestamp(new Date().getTime()));
        handler.updatePlayerRegion(region);
    }

    // Handle command /tribes call
    private void handleCall(Player p)
    {
        PlayerRegion region = handler.findByLocation(p.getLocation());

        if (region == null)
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotInRegion()));
            return;
        }

        if (!region.getOwner().equals(p.getUniqueId()))
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                            .getCommandRegionNotAllowed()));
            return;
        }

        Bukkit.getOnlinePlayers().stream().filter(pl -> {
            if (handler.findByLocation(pl.getLocation()) != null &&
                handler.findByLocation(pl.getLocation()).getId() == region.getId())
            {
                if (region.getMembers()
                          .stream()
                          .filter(m -> m.getUniqueId().equals(pl.getUniqueId()))
                          .findAny()
                          .isPresent() || pl.getUniqueId().equals(region.getOwner()))
                {
                    pl.sendMessage(
                            ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandCalledBy())
                                     .replace("{player}", p.getName()));
                    return true;
                }
            }

            return false;
        }).forEach(pl -> pl.teleport(region.getSpawn()));

        region.setLastAction(new Timestamp(new Date().getTime()));
        handler.updatePlayerRegion(region);
    }

    // Handle command /tribes setspawn
    private void handleSetSpawn(Player p)
    {
        PlayerRegion region = handler.findByLocation(p.getLocation());

        if (region == null)
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotInRegion()));
            return;
        }

        if (!region.getOwner().equals(p.getUniqueId()))
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotInRegion()));
            return;
        }

        region.setSpawn(p.getLocation());
        region.setLastAction(new Timestamp(new Date().getTime()));
        handler.updatePlayerRegion(region);

        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getSpawnChanged()));
    }

    // Handle command /tribes rem <player>
    private void handleRemove(Player p, String[] args)
    {
        if (args.length != 2)
        {
            handleUnknown(p);
            return;
        }

        PlayerRegion region = handler.findByLocation(p.getLocation());

        if (region == null)
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotInRegion()));
            return;
        }

        if (!region.getOwner().equals(p.getUniqueId()))
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                            .getCommandRegionNotAllowed()));
            return;
        }

        // Going async
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
        {
            private UUID uuid = null;

            @Override
            public void run()
            {
                if (Bukkit.getPlayer(args[1]) == null)
                {
                    UUID lookedUpUUID = plugin.getCacheHandler().findByName(args[1]);

                    if (lookedUpUUID == null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getCommandPlayerNotFound()));
                        return;
                    }

                    uuid = lookedUpUUID;
                }
                else
                {
                    uuid = Bukkit.getPlayer(args[1]).getUniqueId();
                }

                if (p.getUniqueId().equals(uuid))
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandOnYourselfDisallowed()));
                    return;
                }

                if (uuid.equals(region.getOwner()))
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandOnOwnerDisallowed()));
                    return;
                }

                Member member = region.getMembers().stream().filter(m -> m.getUniqueId().equals(uuid)).findAny().orElse(
                        null);

                if (member == null)
                {
                    p.sendMessage(plugin.getMessagesConfig()
                                        .getCommandPlayerNotMember()
                                        .replace("{region}", String.valueOf(region.getId())));
                    return;
                }

                handler.removeMember(member);

                region.setLastAction(new Timestamp(new Date().getTime()));
                handler.updatePlayerRegion(region);

                p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                .getCommandSenderMemberRemoved()
                                                                                .replace("{player}", args[1])
                                                                                .replace("{region}", String.valueOf(
                                                                                        region.getId()))));

                if (Bukkit.getPlayer(args[1]) != null)
                {
                    Bukkit.getPlayer(args[1])
                          .sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                         .getCommandReceiverMemberRemoved()
                                                                                         .replace("{region}",
                                                                                                  String.valueOf(
                                                                                                          region.getId()))));
                }
            }
        });
    }

    // Handle command /tribes add <player> [rank]
    private void handleAdd(Player p, String[] args)
    {
        if (args.length != 2 && args.length != 3)
        {
            handleUnknown(p);
            return;
        }

        PlayerRegion region = handler.findByLocation(p.getLocation());

        if (region == null)
        {
            p.sendMessage(
                    ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotInRegion()));
            return;
        }

        Member member = region.getMembers()
                              .stream()
                              .filter(m -> m.getUniqueId().equals(p.getUniqueId()))
                              .findAny()
                              .orElse(null);

        if (member == null && !region.getOwner().equals(p.getUniqueId()))
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                            .getCommandRegionNotAllowed()));
            return;
        }

        Rank rank = null;
        if (member != null)
        {
            rank = regionBlockHandler.findById(region.getRegionBlockId())
                                     .getRanks()
                                     .stream()
                                     .filter(r -> r.getId() == member.getRankId())
                                     .findAny()
                                     .orElse(null);
        }

        if (rank != null && !rank.isChiefsAide())
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                            .getCommandRegionNotAllowed()));
            return;
        }

        final boolean chiefsAide = rank != null;

        // Going async
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable()
        {
            private UUID uuid = null;

            @Override
            public void run()
            {
                if (Bukkit.getPlayer(args[1]) == null)
                {
                    UUID lookedUpUUID = plugin.getCacheHandler().findByName(args[1]);

                    if (lookedUpUUID == null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getCommandPlayerNotFound()));
                        return;
                    }

                    uuid = lookedUpUUID;
                }
                else
                {
                    uuid = Bukkit.getPlayer(args[1]).getUniqueId();
                }

                if (uuid.equals(p.getUniqueId()))
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandOnYourselfDisallowed()));
                    return;
                }

                if (uuid.equals(region.getOwner()))
                {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandOnOwnerDisallowed()));
                    return;
                }

                Member member = region.getMembers()
                                      .stream()
                                      .filter(m -> m.getUniqueId().equals(uuid))
                                      .findAny()
                                      .orElse(null);

                if (args.length == 2)
                {
                    if (member != null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getCommandMemberAlreadyInRegion()));
                        return;
                    }

                    handler.addMember(new Member(uuid,
                                                 regionBlockHandler.findById(region.getRegionBlockId())
                                                                   .getRanks()
                                                                   .get(0),
                                                 region));

                    region.setLastAction(new Timestamp(new Date().getTime()));
                    handler.updatePlayerRegion(region);

                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandSenderPlayerAdded()
                                                                                    .replace("{player}", args[1])));

                    if (Bukkit.getPlayer(args[1]) != null)
                    {
                        Bukkit.getPlayer(args[1]).sendMessage(ChatColor.translateAlternateColorCodes('&',
                                                                                                     plugin.getMessagesConfig()
                                                                                                           .getCommandReceiverPlayerAdded()
                                                                                                           .replace(
                                                                                                                   "{region}",
                                                                                                                   String.valueOf(
                                                                                                                           region.getId()))));
                    }
                }
                else
                {
                    if (chiefsAide)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getCommandRegionNotAllowed()));
                        return;
                    }

                    Rank rank = regionBlockHandler.findById(region.getRegionBlockId())
                                                  .getRanks()
                                                  .stream()
                                                  .filter(r -> r.getName().equalsIgnoreCase(args[2]))
                                                  .findAny()
                                                  .orElse(null);

                    if (rank == null)
                    {
                        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                        .getCommandRankNotFound()));
                        return;
                    }

                    if (member == null)
                    {
                        member = new Member(uuid,
                                            rank,
                                            region);
                        handler.addMember(member);
                    }
                    else
                    {
                        member.setRankId(rank.getId());
                        handler.updateMember(member);
                    }

                    region.setLastAction(new Timestamp(new Date().getTime()));
                    handler.updatePlayerRegion(region);

                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                    .getCommandSenderPlayerPromoted()
                                                                                    .replace("{player}", args[1])
                                                                                    .replace("{rank}",
                                                                                             rank.getName())));

                    if (Bukkit.getPlayer(args[1]) != null)
                    {
                        Bukkit.getPlayer(args[1]).sendMessage(
                                ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig()
                                                                                  .getCommandReceiverPlayerPromoted()
                                                                                  .replace("{rank}", rank.getName())
                                                                                  .replace("{region}", String.valueOf(
                                                                                          region.getId()))));
                    }
                }
            }
        });
    }

    // Handle command /tribes help
    private void handleHelp(Player p)
    {
        for (String s : plugin.getMessagesConfig().getHelpCommand())
        {
            p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }

        if (PermissionHelper.hasPermission(p, "tribes.admin"))
        {
            for (String s : plugin.getMessagesConfig().getAdminHelpCommand())
            {
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
            }
        }
    }

    // Handle wrong syntax / wrong command
    private void handleUnknown(Player p)
    {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getMessagesConfig().getCommandNotFound()));
    }
}
