package com.winthier.wild;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class WildTask extends BukkitRunnable {
    private final WildPlugin plugin;
    private final Player player;

    public WildTask(WildPlugin plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        Location location = plugin.randomLocation(player);
        if (location == null) return;
        player.sendMessage("" + ChatColor.AQUA + "Taking you into the wild...");
        player.teleport(location);
        player.sendTitle(ChatColor.GREEN+"Wilderness", ChatColor.GREEN+"Welcome to the Build World");
        stop();
        plugin.removeTask(player);
    }

    public void start() {
        runTaskTimer(plugin, 1L, 1L);
    }

    public void stop() {
        try {
            cancel();
        } catch (IllegalStateException ise) {}
    }
}
