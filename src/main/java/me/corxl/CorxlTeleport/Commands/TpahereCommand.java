package me.corxl.CorxlTeleport.Commands;

import me.corxl.CorxlTeleport.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import org.jetbrains.annotations.NotNull;

public class TpahereCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equals("tpahere")) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("The console cannot use /tpahere");
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission(command.getPermission())) {
            return false;
        }

        if (args.length!=1) {

            player.sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&4Invalid use of /tpahere."));
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&4Usage: /tpahere [player-name]."));
            return false;
        }

        Player target = player.getServer().getPlayer(args[0]);
        if (target==null || !target.isOnline()) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&7" + args[0] + " &4is either not online or does not exist."));
            return false;
        }
        if (!target.hasPermission("ecotp.cooldown.bypass"))
            if (Main.playerCooldowns.containsKey(target.getUniqueId().toString())) {
                int cooldown = (int)(System.currentTimeMillis() - Main.playerCooldowns.get(target.getUniqueId().toString()))/1000;
                if (cooldown < Main.cooldown) {
                    player.sendMessage((Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.GRAY + (Main.useDisplayName ? target.getDisplayName() : "&7" + target.getName()) + ChatColor.DARK_AQUA + " has to wait " + ChatColor.GOLD + (Main.cooldown - cooldown) + ChatColor.DARK_AQUA + " second(s) before they can tp again!");
                    return false;
                }
            }

        if (Main.tpRequest.containsKey(target.getUniqueId().toString()) || Main.tpRequest.containsKey(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', (Main.pluginPrefix ? Main.prefix + " " : "") + "&4You or the target player already has an active TP request!"));
            return false;
        }

        if (target.getUniqueId().toString().equals(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&4You cannot teleport to yourself!"));
            return false;
        }

        if (Main.teleportCost!=0 && Main.eco.getBalance(player) < Main.teleportCost) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&4You do not have &a$" + Main.teleportCost + "&4!"));
            return false;
        }
        TeleportRequest tp = new TeleportRequest(player, target, true);
        Main.tpRequest.put(target.getUniqueId().toString(), tp);
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), ()-> {
            if (Main.tpRequest.containsKey(target.getUniqueId().toString())){
                TeleportRequest request = Main.tpRequest.get(target.getUniqueId().toString());
                request.getRequestSender().sendMessage(ChatColor.translateAlternateColorCodes('&',(Main.pluginPrefix ? Main.prefix + " " : "") + "&7Your request has timed out."));
                Main.tpRequest.remove(target.getUniqueId().toString());
            }
        }, 300);
        Main.playerCooldowns.put(player.getUniqueId().toString(), System.currentTimeMillis());

        player.sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&r&3Your request was sent to &r&7" + target.getName() + "&r&3!"));
        target.spigot().sendMessage(Main.sendRequest(player, target, true));
        target.sendMessage("");
        target.sendMessage(ChatColor.GRAY + "----------------------------------------");
        return true;
    }
}
