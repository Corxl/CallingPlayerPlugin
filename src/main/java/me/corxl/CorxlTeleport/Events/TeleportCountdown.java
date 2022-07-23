package me.corxl.CorxlTeleport.Events;

import me.corxl.CorxlTeleport.Main;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeleportCountdown extends BukkitRunnable {
    Main plugin;

    Player player;

    int num;

    public TeleportCountdown(Main plugin, Player player, int num) {
        this.plugin = plugin;
        this.player = player;
        this.num = num;
    }

    public void run() {
        this.player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.plugin.prefix + " &r&aYou will be teleported in " + this.num));
    }
}
