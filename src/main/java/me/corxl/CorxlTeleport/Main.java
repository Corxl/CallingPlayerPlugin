package me.corxl.CorxlTeleport;

import java.util.HashMap;
import java.util.Map;

import me.corxl.CorxlTeleport.Commands.*;
import me.corxl.CorxlTeleport.Events.TeleportEventHandler;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public static String prefix = ChatColor.translateAlternateColorCodes('&', "&b&l[&a&lEcoTP&b&l]");

    public static Map<String, TeleportRequest> tpRequest = new HashMap<>();

    public static Map<String, Long> playerCooldowns = new HashMap<>();

    public static HashMap<String, TeleportRequest> teleportingPlayers = new HashMap<>();

    public static int cooldown, countdown;
    public static boolean enableMovement, pluginPrefix, useDisplayName;
    public static double teleportCost;

    public static Economy eco;

    public void onEnable() {
        if (!setupEconomy()) {
            this.getServer().getLogger().info(ChatColor.RED + "This plugin requires [Vault] and an Economy plugin to function.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        this.saveConfig();
        loadConfig();
        this.getServer().getPluginManager().registerEvents(new TeleportEventHandler(this), this);
        this.getCommand("tpa").setExecutor(new TpaCommand());
        this.getCommand("tpaccept").setExecutor(new TpacceptCommand());
        this.getCommand("tpadeny").setExecutor(new TpadenyCommand());
        this.getCommand("tpahere").setExecutor(new TpahereCommand());
        this.getCommand("ecotpreload").setExecutor(new ReloadCommand());

        this.getCommand("tpa").setTabCompleter(new PlayerNamesTab());
        this.getCommand("tpaccept").setTabCompleter(new PlayerNamesTab());
        this.getCommand("tpadeny").setTabCompleter(new PlayerNamesTab());
        this.getCommand("tpahere").setTabCompleter(new PlayerNamesTab());
        this.getCommand("ecotpreload").setTabCompleter(new PlayerNamesTab());
    }

    public void onDisable() {}

    private boolean setupEconomy() {
        RegisteredServiceProvider<Economy> economy = getServer().getServicesManager().getRegistration(Economy.class);
        if (economy != null)
            eco = (Economy)economy.getProvider();
        return (eco != null);
    }

    public static boolean isOnCooldown(Player player) {
        if (playerCooldowns.containsKey(player.getUniqueId().toString())) {
            int cooldown = (int)(System.currentTimeMillis() - Main.playerCooldowns.get(player.getUniqueId().toString()))/1000;
            if (cooldown < Main.cooldown) {
                player.sendMessage((Main.pluginPrefix ? Main.prefix + " " : "") + ChatColor.RED + "You have to wait " + ChatColor.GOLD + (Main.cooldown - cooldown) + ChatColor.RED + " second(s) before you can tp again!");
                return false;
            }
        }
        return true;
    }

    public static TextComponent sendRequest(Player player, Player player2, boolean isTpaHere) {
        player2.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &r&7" + player.getName() + " &r&3 has sent you a request to teleport " + (isTpaHere ? "&2you &3to &9them" : "&9them &3to &2you") + "!"));
        player2.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + " &r&8You have &615 &8seconds to accept/decline."));
        player2.sendMessage(ChatColor.GRAY + "----------------------------------------");
        player2.sendMessage("");
        TextComponent accSpace = new TextComponent("               ");
        accSpace.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
        accSpace.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("")).create()));
        TextComponent accept = new TextComponent(ChatColor.GREEN + "" + ChatColor.BOLD + "[Accept]");
        accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaccept"));
        accept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("Click to accept")).create()));
        TextComponent accSpace2 = new TextComponent("   ");
        accSpace2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ""));
        accSpace2.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("")).create()));
        TextComponent deny = new TextComponent(ChatColor.RED + "" + ChatColor.BOLD + "[Deny]");
        deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny"));
        deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (new ComponentBuilder("Click to deny")).create()));
        accSpace.addExtra((BaseComponent)accept);
        accSpace.addExtra((BaseComponent)accSpace2);
        accSpace.addExtra((BaseComponent)deny);
        Bukkit.getScheduler().runTaskLater(Main.getPlugin(Main.class), ()-> {
            if (tpRequest.containsKey(player2)) {
                tpRequest.remove(player2);
                String expireText = ChatColor.translateAlternateColorCodes('&', prefix + " &4The tp request has expired!");
                player.sendMessage(expireText);
                player2.sendMessage(expireText);
            }
        }, 300);
        return accSpace;
    }

    public static void loadConfig() {
        Main m = Main.getPlugin(Main.class);
        m.reloadConfig();
        cooldown = m.getConfig().getInt("teleport-cooldown");
        countdown = m.getConfig().getInt("teleport-countdown");
        enableMovement = m.getConfig().getBoolean("allow-tp-movement");
        teleportCost = m.getConfig().getDouble("teleport-cost");
        pluginPrefix = m.getConfig().getBoolean("show-plugin-prefix");
        useDisplayName = m.getConfig().getBoolean("use-display-name");
    }
}
