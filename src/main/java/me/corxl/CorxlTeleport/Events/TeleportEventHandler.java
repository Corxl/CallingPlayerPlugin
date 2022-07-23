package me.corxl.CorxlTeleport.Events;

import me.corxl.CorxlTeleport.Commands.TeleportRequest;
import me.corxl.CorxlTeleport.Main;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;

public class TeleportEventHandler implements Listener {
    public Main plugin;

    public static HashSet<String> chargeQueue = new HashSet<>();

    public TeleportEventHandler(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (Main.enableMovement) return;
        if (Main.countdown<=0) return;
        if (Main.teleportingPlayers.containsKey(event.getPlayer().getUniqueId().toString())) {
            Location l = event.getPlayer().getLocation();
            Location staticLocation = Main.teleportingPlayers.get(event.getPlayer().getUniqueId().toString()).getFromPlayerLocation();
            int x = l.getBlockX(), y = l.getBlockY(), z = l.getBlockZ();
            if (!(x!=staticLocation.getBlockX() || y!=staticLocation.getBlockY() || z!=staticLocation.getBlockZ())) return;
            TeleportRequest tpInstance= Main.teleportingPlayers.get(event.getPlayer().getUniqueId().toString());
            Main.tpRequest.remove(Main.teleportingPlayers.get(event.getPlayer().getUniqueId().toString()).getRequestReceiver().getUniqueId().toString());
            Main.playerCooldowns.remove(event.getPlayer().getUniqueId().toString());
            tpInstance.getTask().cancel();
            Main.teleportingPlayers.remove(event.getPlayer().getUniqueId().toString());
            tpInstance.getTo().sendMessage(ChatColor.translateAlternateColorCodes('&', (Main.pluginPrefix ? Main.prefix + " " : "") + "&6"
                    + (Main.useDisplayName ? tpInstance.getFrom().getDisplayName() : tpInstance.getFrom().getName()) + "&4 cancelled the teleport by moving!"));
            tpInstance.getFrom().sendMessage(ChatColor.translateAlternateColorCodes('&', (Main.pluginPrefix ? Main.prefix + " " : "") + "&4You cancelled the teleport by moving!"));
        }
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        if ((Main.teleportCost<=0)) return;
        if (!chargeQueue.contains(event.getPlayer().getUniqueId().toString())) return;
        if (event.isCancelled()) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', (Main.pluginPrefix ? Main.prefix + " " : "") + "&4You were unable to teleport. You have not been charged."));
            return;
        }
        Main.eco.withdrawPlayer(event.getPlayer(), Main.teleportCost);
        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&a$" + Main.teleportCost + " &3has been withdrawn from your account."));
        chargeQueue.remove(event.getPlayer().getUniqueId().toString());
        Main.playerCooldowns.put(event.getPlayer().getUniqueId().toString(), System.currentTimeMillis());
    }

}
