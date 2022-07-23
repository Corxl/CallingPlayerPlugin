package me.corxl.CorxlTeleport.Commands;

import me.corxl.CorxlTeleport.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equals("ecotpreload")) return false;
        if (!sender.hasPermission(command.getPermission())) return false;
        Main.loadConfig();
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + " &7Plugin reloaded."));
        return false;
    }
}
