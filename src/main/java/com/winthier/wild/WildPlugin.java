package com.winthier.wild;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class WildPlugin extends JavaPlugin implements Listener {
    private String world;
    private int x, z, radiusX, radiusZ;
    private final Random random = new Random(System.currentTimeMillis());
    private final Map<Player, WildTask> tasks = new HashMap<Player, WildTask>();
    private final Cooldown cooldown = new Cooldown(this);

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        reloadConfig();
        loadConfiguration();
    }

    @Override
    public void onDisable() {
    }

    private void loadConfiguration() {
        world = getConfig().getString("wild.World");
        x = getConfig().getIntegerList("wild.Center").get(0);
        z = getConfig().getIntegerList("wild.Center").get(1);
        radiusX = getConfig().getIntegerList("wild.Radius").get(0);
        radiusZ = getConfig().getIntegerList("wild.Radius").get(1);
    }

    public Location randomLocation(Player player) {
        final World world = getServer().getWorld(this.world);
        if (world == null) {
            getLogger().warning("Can't find world " + world + ".");
            return null;
        }
        final int x = this.x - this.radiusX + random.nextInt(this.radiusX + this.radiusX);
        final int z = this.z - this.radiusZ + random.nextInt(this.radiusZ + this.radiusZ);
        final int y = world.getHighestBlockYAt(x, z);
        Material floorMaterial = world.getBlockAt(x, y - 1, z).getType();
        if (!floorMaterial.isSolid()) return null;
        final Location playerLocation = player.getLocation();
        final Location location = new Location(world, (double)x + 0.5, (double)y, (double)z + 0.5, playerLocation.getYaw(), playerLocation.getPitch());
        if (getServer().getPluginManager().getPlugin("Claims") != null) {
            if (!ClaimCheck.check(location)) return null;
        }
        return location;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
        try {
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Player expected");
                    return true;
                }
                Player player = (Player)sender;
                sendPlayerToWild(player);
            } else if ("Reload".equalsIgnoreCase(args[0])) {
                if (!sender.hasPermission("wild.reload")) {
                    return false;
                }
                reloadConfig();
                loadConfiguration();
                cooldown.reload();
                sender.sendMessage("[Wild] Configuration reloaded.");
                return true;
            } else {
                return false;
            }
        } catch (CommandException ce) {
            sender.sendMessage("" + ChatColor.RED + ce.getMessage());
        }
        return true;
    }

    public boolean sendPlayerToWild(Player player) {
        WildTask task = tasks.get(player);
        if (task != null) return false;
        if (!player.hasPermission("wild.nocooldown")) {
            String cooldownMessage = cooldown.cooldownMessage(player.getUniqueId());
            if (cooldownMessage != null) {
                player.sendMessage(ChatColor.RED + cooldownMessage);
                return false;
            }
            cooldown.setCooldownNow(player.getUniqueId());
        }
        task = new WildTask(this, player);
        task.start();
        tasks.put(player, task);
        return true;
    }

    public void removeTask(Player player) {
        WildTask task = tasks.remove(player);
        if (task != null) task.stop();
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        removeTask(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeTask(event.getPlayer());
    }
}
