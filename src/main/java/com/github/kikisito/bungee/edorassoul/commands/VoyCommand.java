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

public class VoyCommand extends Command {
    private Main plugin;

    public VoyCommand(Main plugin){
        super("voy", "a51.notificaciones");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        try {
            Configuration config = plugin.getConfig();
            Main.telegramBot.getTelegramBot().sendMessage(config.get("staffchat-channel"), config.getString("formularios.telegram-voy").replace("{user}", sender.getName()), ParseMode.MARKDOWN, false, false, null, null);
            for(ProxiedPlayer p : plugin.getProxy().getPlayers()){
                if(p.hasPermission("a51.notificaciones")){
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("formularios.voy").replace("{user}", sender.getName()))));
                }
            }
        } catch (TelegramException e) {
            e.printStackTrace();
        }
    }
}
