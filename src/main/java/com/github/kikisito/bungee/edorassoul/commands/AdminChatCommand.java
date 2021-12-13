package com.github.kikisito.bungee.edorassoul.commands;

import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.github.kikisito.bungee.edorassoul.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class AdminChatCommand extends Command {
    final private Main plugin;
    final private Configuration config;

    public AdminChatCommand(Main plugin) {
        super("atel", "edorassoul.send.adminchannel", "sendadmin", "atelegram", "at");
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length <= 0){
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.minecraft-not-enough-args"))));
            return;
        }
        try {
            String message = String.join(" ", args);
            Main.telegramBot.getTelegramBot().sendMessage(config.getString("adminchat-channel"), config.getString("chat.telegram-adminchannel").replace("{user}", sender.getName()).replace("{message}", message), ParseMode.MARKDOWN, false, false, null, null);

            for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
                if(player.hasPermission("edorassoul.receive.adminchannel") && !plugin.ignoreTelegram.contains(player)){
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.minecraft-adminchannel")).replace("{user}", sender.getName()).replace("{message}", message)));
                }
            }
        } catch (TelegramException e) {
            e.printStackTrace();
        }
    }
}
