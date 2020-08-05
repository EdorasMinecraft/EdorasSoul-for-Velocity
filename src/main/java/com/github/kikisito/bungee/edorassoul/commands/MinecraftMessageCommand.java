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

public class MinecraftMessageCommand extends Command {
    final private Main plugin;
    final private Configuration config;

    public MinecraftMessageCommand(Main plugin) {
        super("tel", "edorassoul.send.telegram", "sendtelegram", "stelegram", "ste");
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            String message = String.join(" ", args);
            Main.telegramBot.getTelegramBot().sendMessage(config.getString("telegram-channel"), config.getString("chat.minecraft-to-telegram").replace("{user}", sender.getName()).replace("{message}", message), ParseMode.MARKDOWN, false, false, null, null);

            for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
                if(player.hasPermission("edorassoul.receive.telegram") && !plugin.ignoreTelegram.contains(player)){
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.telegram-to-minecraft")).replace("{user}", sender.getName()).replace("{message}", message)));
                }
            }
        } catch (TelegramException e) {
            e.printStackTrace();
        }
    }
}
