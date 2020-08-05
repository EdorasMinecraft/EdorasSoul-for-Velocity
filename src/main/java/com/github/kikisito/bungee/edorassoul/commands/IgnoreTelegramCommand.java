package com.github.kikisito.bungee.edorassoul.commands;

import com.github.kikisito.bungee.edorassoul.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.Random;

public class IgnoreTelegramCommand extends Command {
    final private Main plugin;
    final private Configuration config;

    public IgnoreTelegramCommand(Main plugin) {
        super("mutetelegram", "edorassoul.receive.telegram");
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if(!plugin.ignoreTelegram.contains(p)) {
            plugin.ignoreTelegram.add(p);
        } else {
            plugin.ignoreTelegram.remove(p);
        }
    }
}
