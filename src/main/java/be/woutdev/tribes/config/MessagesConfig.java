package be.woutdev.tribes.config;

import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Created by Wout on 6/08/2016.
 */
public class MessagesConfig extends Config
{
    /**
     * Create a new Config instance.
     *
     * @param plugin The plugin where this config belongs to.
     */
    public MessagesConfig(Plugin plugin)
    {
        super(plugin, "messages.yml");
    }

    public String getRegionExpired()
    {
        return getFileConfiguration().getString("region-expired");
    }

    public String getSpawnChanged()
    {
        return getFileConfiguration().getString("spawn-changed");
    }

    public String getInteractRequiresWizard()
    {
        return getFileConfiguration().getString("interact-requires-wizard");
    }

    public String getInteractExternalRadius()
    {
        return getFileConfiguration().getString("interact-external-radius");
    }

    public String getInteractNonMember()
    {
        return getFileConfiguration().getString("interact-non-member");
    }

    public String getInteractRequiresCook()
    {
        return getFileConfiguration().getString("interact-requires-cook");
    }

    public String getInteractRequiresEngineer()
    {
        return getFileConfiguration().getString("interact-requires-engineer");
    }

    public String getInteractRequiresChemist()
    {
        return getFileConfiguration().getString("interact-requires-chemist");
    }

    public String getDamageRequiresProtector()
    {
        return getFileConfiguration().getString("damage-requires-protector");
    }

    public String getDamageRequiresWarrior()
    {
        return getFileConfiguration().getString("damage-requires-warrior");
    }

    public String getDamageNonMember()
    {
        return getFileConfiguration().getString("damage-non-member");
    }

    public String getBuildNonBuilder()
    {
        return getFileConfiguration().getString("build-non-builder");
    }

    public String getBuildNonGardener()
    {
        return getFileConfiguration().getString("build-non-gardener");
    }

    public String getBuildRestrictedBlock()
    {
        return getFileConfiguration().getString("build-restricted-block");
    }

    public String getBuildExternalRadius()
    {
        return getFileConfiguration().getString("build-external-radius");
    }

    public String getBuildNonMember()
    {
        return getFileConfiguration().getString("build-non-member");
    }

    public String getCreateRegion()
    {
        return getFileConfiguration().getString("creation-region");
    }

    public String getCreateRegionOverlapping()
    {
        return getFileConfiguration().getString("creation-region-overlapping");
    }

    public String getCreateRegionNoPermission()
    {
        return getFileConfiguration().getString("creation-no-permission");
    }

    public String getBreakNonBuilder()
    {
        return getFileConfiguration().getString("break-non-builder");
    }

    public String getBreakNonGardener()
    {
        return getFileConfiguration().getString("break-non-gardener");
    }

    public String getBreakExternalRadius()
    {
        return getFileConfiguration().getString("break-external-radius");
    }

    public String getBreakNonMember()
    {
        return getFileConfiguration().getString("break-non-member");
    }

    public String getBreakFlag()
    {
        return getFileConfiguration().getString("break-flag");
    }

    public String getBreakFlagNoPermission()
    {
        return getFileConfiguration().getString("break-flag-no-permission");
    }

    public String getRegionUpgraded()
    {
        return getFileConfiguration().getString("region-upgraded");
    }

    public String getRegionDowngraded()
    {
        return getFileConfiguration().getString("region-downgraded");
    }

    public String getCommandNotInRegion()
    {
        return getFileConfiguration().getString("command-not-in-region");
    }

    public String getCommandNotFound()
    {
        return getFileConfiguration().getString("command-unknown");
    }

    public List<String> getHelpCommand()
    {
        return getFileConfiguration().getStringList("command-help");
    }

    public List<String> getAdminHelpCommand()
    {
        return getFileConfiguration().getStringList("command-help-admin");
    }

    public String getCommandRegionNotAllowed()
    {
        return getFileConfiguration().getString("command-region-not-allowed");
    }

    public String getCommandPlayerNotFound()
    {
        return getFileConfiguration().getString("command-player-not-found");
    }

    public String getCommandOnYourselfDisallowed()
    {
        return getFileConfiguration().getString("command-on-yourself-disallowed");
    }

    public String getCommandOnOwnerDisallowed()
    {
        return getFileConfiguration().getString("command-on-owner-disallowed");
    }

    public String getCommandMemberAlreadyInRegion()
    {
        return getFileConfiguration().getString("command-already-member");
    }

    public String getCommandSenderPlayerAdded()
    {
        return getFileConfiguration().getString("command-sender-added-to-region");
    }

    public String getCommandReceiverPlayerAdded()
    {
        return getFileConfiguration().getString("command-receiver-added-to-region");
    }

    public String getCommandRankNotFound()
    {
        return getFileConfiguration().getString("command-rank-not-found");
    }

    public String getCommandReceiverPlayerPromoted()
    {
        return getFileConfiguration().getString("command-receiver-player-promoted");
    }

    public String getCommandSenderPlayerPromoted()
    {
        return getFileConfiguration().getString("command-sender-player-promoted");
    }

    public String getCommandPlayerNotMember()
    {
        return getFileConfiguration().getString("command-player-not-member");
    }

    public String getCommandSenderMemberRemoved()
    {
        return getFileConfiguration().getString("command-sender-member-removed");
    }

    public String getCommandReceiverMemberRemoved()
    {
        return getFileConfiguration().getString("command-receiver-member-removed");
    }

    public String getCommandCalledBy()
    {
        return getFileConfiguration().getString("command-you-have-been-called");
    }

    public String getCommandTeleportedToSpawn()
    {
        return getFileConfiguration().getString("command-teleported-to-spawn");
    }

    public String getCommandLeftRegion()
    {
        return getFileConfiguration().getString("command-player-left");
    }

    public String getCommandCannotLeave()
    {
        return getFileConfiguration().getString("command-player-cannot-leave");
    }

    public String getCommandOwnerCannotLeave()
    {
        return getFileConfiguration().getString("command-owner-cannot-leave");
    }

    public List<String> getCommandInfo()
    {
        return getFileConfiguration().getStringList("command-info");
    }

    public String getCommandInfoNone()
    {
        return getFileConfiguration().getString("command-info-no-members");
    }
}
