package com.winthier.wild;

import com.winthier.claims.bukkit.BukkitClaimsPlugin;
import org.bukkit.Bukkit;

public class ClaimCheck {
    public static boolean check(org.bukkit.Location location, int distance) {
        BukkitClaimsPlugin claims = (BukkitClaimsPlugin)Bukkit.getServer().getPluginManager().getPlugin("Claims");
        return claims.findClaimsNear(location, distance).isEmpty();
    }
}
