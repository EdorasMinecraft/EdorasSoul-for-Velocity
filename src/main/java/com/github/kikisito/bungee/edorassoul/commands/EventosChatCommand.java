package com.github.kikisito.bungee.edorassoul.commands;

import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.github.kikisito.bungee.edorassoul.Main;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.file.YamlFile;

public class EventosChatCommand implements SimpleCommand {
    final private Main plugin;
    final private YamlFile config;

    public EventosChatCommand(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("edorassoul.send.eventoschannel") && invocation.source() instanceof Player;
    }

    @Override
    public void execute(final Invocation invocation) {
        String[] args = invocation.arguments();
        final Player sender = (Player) invocation.source();

        if(args.length <= 0){
            sender.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("chat.minecraft-not-enough-args")));
            return;
        }
        try {
            String message = String.join(" ", args);
            Main.telegramBot.getTelegramBot().sendMessage(config.getString("eventoschat-channel"), config.getString("chat.telegram-eventoschannel").replace("{user}", sender.getUsername()).replace("{message}", message), ParseMode.MARKDOWN, false, false, null, null, null);

            for(Player player : plugin.getServer().getAllPlayers()){
                if(player.hasPermission("edorassoul.receive.eventoschannel") && !plugin.ignoreTelegram.contains(player)){
                    player.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("chat.minecraft-eventoschannel").replace("{user}", sender.getUsername()).replace("{message}", message)));
                }
            }
        } catch (TelegramException e) {
            e.printStackTrace();
        }
    }
}
