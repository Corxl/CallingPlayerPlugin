package me.corxl.CorxlTeleport.Commands;

import me.corxl.CorxlTeleport.Events.TeleportEventHandler;
import me.corxl.CorxlTeleport.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class TpacceptCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!command.getName().equals("tpaccept")) return false;
        if (!(sender instanceof Player)) {
            sender.sendMessage("The console cannot use /tpaccept");
            return false;
        }
        Player player = (Player) sender;
        if (!player.hasPermission(command.getPermission())) {
            return false;
        }

        if (!Main.tpRequest.containsKey(player.getUniqueId().toString())) {
            player.sendMessage((Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.RED + "You do not have an active request!");
            return false;
        }

        TeleportRequest tp = Main.tpRequest.get(player.getUniqueId().toString());

        if (Main.teleportCost!=0 && Main.eco.getBalance(tp.getFrom()) < Main.teleportCost) {
            tp.getFrom().sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&4You do not have &a$" + Main.teleportCost + "&4!"));
            if (!tp.getTo().equals(player)) {
                tp.getTo().sendMessage( (Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.GOLD + tp.getFrom() + ChatColor.RED + " did not have " + ChatColor.GREEN + "$" + Main.teleportCost + ChatColor.RED + "!");
            }
            Main.tpRequest.remove(player.getUniqueId().toString());
            return false;
        }
        Main.teleportingPlayers.put(tp.getFrom().getUniqueId().toString(), tp);
        tp.getRequestSender().sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "") + "&3Your request was &a&nAccepted&3!" + (tp.isTpaHere() ? "" : (Main.countdown > 0 ? " &cDon't move!" : ""))));
        tp.getRequestReceiver().sendMessage(ChatColor.translateAlternateColorCodes('&',  (Main.pluginPrefix ? Main.prefix + " " : "")
                + "&3You have &a&nAccepted&r&7 " + (Main.useDisplayName ? tp.getRequestSender().getDisplayName() : tp.getRequestSender().getName()) + "&3's &3request!"));

        if (Main.countdown > 0) {
            tp.setTask(new BukkitRunnable() {
                private int countdown = Main.countdown;
                @Override
                public void run() {
                    if (countdown <=0) {
                        Main.teleportingPlayers.remove(tp.getFrom().getUniqueId().toString());
                        if (!(Main.eco.getBalance(tp.getFrom())>=Main.teleportCost)) {
                            tp.getFrom().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have &a$" + Main.teleportCost + "&4!"));
                            tp.getTo().sendMessage(ChatColor.translateAlternateColorCodes('&', "&4The teleport was cancelled due to the &7" + (Main.useDisplayName ? tp.getFrom().getDisplayName() : tp.getFrom().getName()) + "'s &4insufficient funds."));
                            cancel();
                            return;
                        }

                        tp.getFrom().sendMessage((Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.DARK_AQUA + "Teleporting...");
                        TeleportEventHandler.chargeQueue.add(tp.getFrom().getUniqueId().toString());
                        tp.getFrom().teleport(tp.getTo().getLocation());
                        cancel();
                        return;
                    }
                    tp.getFrom().sendMessage((Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.DARK_AQUA + "Teleporting in " + ChatColor.GOLD + countdown);
                    countdown--;
                }
            }.runTaskTimer(Main.getPlugin(Main.class), 0, 20L));
        } else {
            tp.getFrom().sendMessage((Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.DARK_AQUA + "Teleporting...");
            TeleportEventHandler.chargeQueue.add(tp.getFrom().getUniqueId().toString());
            tp.getFrom().teleport(tp.getTo().getLocation());
        }
        Main.tpRequest.remove(player.getUniqueId().toString());
        return false;
    }
}
