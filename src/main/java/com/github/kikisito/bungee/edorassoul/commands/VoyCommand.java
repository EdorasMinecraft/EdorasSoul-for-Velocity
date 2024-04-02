package com.github.kikisito.bungee.edorassoul.commands;

import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.github.kikisito.bungee.edorassoul.Main;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.file.YamlFile;

public class VoyCommand implements SimpleCommand {
    private Main plugin;

    public VoyCommand(Main plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("a51.notificaciones") && invocation.source() instanceof Player;
    }

    @Override
    public void execute(final Invocation invocation) {
        try {
            Player sender = (Player) invocation.source();
            YamlFile config = plugin.getConfig();
            Main.telegramBot.getTelegramBot().sendMessage(config.get("staffchat-channel"), config.getString("formularios.telegram-voy").replace("{user}", sender.getUsername()), ParseMode.MARKDOWN, false, false, null, null, null);
            for(Player p : plugin.getServer().getAllPlayers()){
                if(p.hasPermission("a51.notificaciones")){
                    p.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("formularios.voy").replace("{user}", sender.getUsername())));
                }
            }
        } catch (TelegramException e) {
            e.printStackTrace();
        }
    }
}
