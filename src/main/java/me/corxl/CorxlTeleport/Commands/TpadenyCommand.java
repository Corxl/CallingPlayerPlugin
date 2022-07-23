package me.corxl.CorxlTeleport.Commands;

import me.corxl.CorxlTeleport.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TpadenyCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equals("tpadeny")) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("The console cannot use this command.");
            return false;
        }
        Player p = (Player) sender;
        if (!sender.hasPermission(command.getPermission())) {
            return false;
        }
        if (!Main.tpRequest.containsKey(p.getUniqueId().toString())) {
            p.sendMessage((Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.RED + "You do not have an active request!");
            return false;
        }
        TeleportRequest tr = Main.tpRequest.get(p.getUniqueId().toString());
        tr.getRequestReceiver().sendMessage(ChatColor.translateAlternateColorCodes('&', (Main.pluginPrefix ? Main.prefix + " " : "") + "&7You &4&nDenied&r&7 "
                + (Main.useDisplayName ? tr.getRequestSender().getDisplayName() : "&7" + tr.getRequestSender().getName()) + "&7's&7 request!"));
        tr.getRequestSender().sendMessage(ChatColor.translateAlternateColorCodes('&', (Main.pluginPrefix ? Main.prefix + " " : "")
                + (Main.useDisplayName ? tr.getRequestReceiver().getDisplayName() : "&7" + tr.getRequestSender().getName()) + ChatColor.DARK_AQUA
                + " has &4&nDenied&r" + ChatColor.DARK_AQUA + " your request!"));
        Main.tpRequest.remove(p.getUniqueId().toString());
        return false;
    }
}
