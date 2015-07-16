package com.winthier.wild;

import com.winthier.claims.Claims;

public class ClaimCheck {
    public static boolean check(org.bukkit.Location location) {
        return Claims.getInstance().getClaimAt(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ()) == null;
    }
}
