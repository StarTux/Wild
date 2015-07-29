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
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format("minecraft:title %s subtitle {color:aqua,text:'Welcome to the Danger Zone B)'}", player.getName()));
        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), String.format("minecraft:title %s title {color:aqua,text:Wilderness}", player.getName()));
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
