package com.github.kikisito.bungee.edorassoul.commands;

import com.github.kikisito.bungee.edorassoul.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class IgnoreTelegramCommand extends Command {
    final private Main plugin;

    public IgnoreTelegramCommand(Main plugin) {
        super("mutetelegram", "edorassoul.receive.telegram");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof ProxiedPlayer)) return;
        ProxiedPlayer p = (ProxiedPlayer) sender;
        if(!plugin.ignoreTelegram.contains(p)) {
            plugin.ignoreTelegram.add(p);
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.muted-chat"))));
        } else {
            plugin.ignoreTelegram.remove(p);
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.unmuted-chat"))));

        }
    }
}
