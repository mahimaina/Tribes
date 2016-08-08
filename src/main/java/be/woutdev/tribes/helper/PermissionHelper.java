package be.woutdev.tribes.helper;

import org.bukkit.entity.Player;

/**
 * Created by Wout on 4/08/2016.
 */
public class PermissionHelper
{
    /**
     * Check if the player has a specified permission.
     *
     * @param p The player to check.
     * @param permission The permission to check.
     *
     * @return Returns if the specified player is either op, has all permissions or has the specified permission.
     */
    public static boolean hasPermission(Player p, String permission)
    {
        return p.isOp() || p.hasPermission("tribes.admin") || p.hasPermission("tribes.*") ||
               p.hasPermission(permission);
    }
}
