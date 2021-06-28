package com.github.kikisito.bungee.edorassoul.commands;

import com.github.kikisito.bungee.edorassoul.Main;
import com.github.kikisito.bungee.edorassoul.tasks.Utils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WhitelistCommand extends Command implements TabExecutor {
    private Main plugin;

    public WhitelistCommand(Main plugin){
        super("whitelist", "edorassoul.staff.whitelist");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String subcommand;
        switch(args.length){
            case 1:
                subcommand = args[0];
                if(subcommand.equalsIgnoreCase("enable")){
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-enabled"))));
                    plugin.isWhitelist = true;
                    plugin.getWhitelist().set("whitelist", true);
                    plugin.saveWhitelist();
                    break;
                } else if (subcommand.equalsIgnoreCase("disable")){
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-disabled"))));
                    plugin.isWhitelist = false;
                    plugin.getWhitelist().set("whitelist", false);
                    plugin.saveWhitelist();
                    break;
                } else if(subcommand.equalsIgnoreCase("kickall")){
                    if(plugin.isWhitelist){
                        for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
                            if(!player.hasPermission("edorassoul.bypass.whitelist") && !player.hasPermission("edorassoul.staff.whitelist")){
                                player.disconnect(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-kickall"))));
                            } else {
                                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-kickall-executed"))));
                            }
                        }
                    } else {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-required"))));
                    }
                    break;
                } else if (subcommand.equalsIgnoreCase("check")){
                    List<String> allowed = plugin.getWhitelist().getStringList("allowed-players");
                    List<String> names = new ArrayList<>();
                    for(String uuid : allowed){
                        try {
                            names.add(Utils.requestName(uuid));
                        } catch (Exception e) {
                            e.printStackTrace();
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-error"))));
                        }
                    }
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-list").replace("{users}", String.join(",", names)))));
                    break;
                }
            case 2:
                subcommand = args[0];
                if(subcommand.equalsIgnoreCase("add")){
                    List<String> allowed = plugin.getWhitelist().getStringList("allowed-players");
                    try {
                        allowed.add(Utils.requestUUID(args[1]));
                        plugin.getWhitelist().set("allowed-players", allowed);
                        plugin.saveWhitelist();
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-added"))));
                    } catch (Exception e) {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-error"))));
                    }
                    break;
                } else if (subcommand.equalsIgnoreCase("remove")){
                    List<String> allowed = plugin.getWhitelist().getStringList("allowed-players");
                    try {
                        allowed.remove(Utils.requestUUID(args[1]));
                        plugin.getWhitelist().set("allowed-players", allowed);
                        plugin.saveWhitelist();
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-removed"))));
                    } catch (Exception e) {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-error"))));
                    }
                    break;
                } else if (subcommand.equalsIgnoreCase("check")){
                    List<String> allowed = plugin.getWhitelist().getStringList("allowed-players");
                    try {
                        if(allowed.contains(Utils.requestUUID(args[1]))){
                                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-found").replace("{user}", args[1]))));
                        } else {
                            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-not-found").replace("{user}", args[1]))));
                        }
                    } catch (Exception e) {
                        sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-error"))));
                    }
                    break;
                }
            default:
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.whitelist-usage"))));
                break;
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender commandSender, String[] args) {
        List<String> tabs = new ArrayList<>();
        if(args.length == 1){
            String[] available = {"add", "remove", "disable", "enable", "check", "kickall"};
            String subcommand = args[0];
            for(String s : available){
                if(s.toLowerCase().toLowerCase().startsWith(subcommand.toLowerCase())){
                    tabs.add(s);
                }
            }
        } else if (args.length == 2 && args[1].equalsIgnoreCase("add") || args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("check")){
            for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
                if(player.getName().toLowerCase().startsWith(args[1].toLowerCase())){
                    tabs.add(player.getName());
                }
            }
        }
        return tabs;
    }
}
