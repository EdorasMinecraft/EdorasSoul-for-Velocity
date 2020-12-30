package com.github.kikisito.bungee.edorassoul.listeners;

import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.github.kikisito.bungee.edorassoul.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

@ModuleInfo(name = "TelegramToMinecraft", description = "Envia los mensajes de Telegram a Minecraft")
public class TelegramMessage implements ZinciteModule {
    private final Main plugin;
    final private Configuration config;
    public TelegramMessage(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void onPostCommand(Update update, boolean success) {
        if(update.getMessage() == null) return; // Si no es un mensaje, cancela
        if(update.getMessage().getType() != Message.Type.TEXT) return; // Solo mensajes de texto de momento.
        Message msg = update.getMessage();

        String name = msg.getFrom().getFirstName() == null ? "" : msg.getFrom().getFirstName();
        String lastname = msg.getFrom().getLastName() == null ? "" : msg.getFrom().getLastName();

        String username = msg.getFrom().getUsername() == null ? name + (lastname.equals("null") ? " " + lastname : "") : msg.getFrom().getUsername();

        if(msg.getChat().getId().equals(config.getString("staffchat-channel"))){
            for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
                if(player.hasPermission("edorassoul.receive.modchannel") && !plugin.ignoreTelegram.contains(player)){
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.minecraft-modchannel")).replace("{user}", username).replace("{message}", msg.getText())));
                }
            }
        }

        if(msg.getChat().getId().equals(config.getString("adminchat-channel"))){
            for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
                if(player.hasPermission("edorassoul.receive.adminchannel") && !plugin.ignoreTelegram.contains(player)){
                    player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.minecraft-adminchannel")).replace("{user}", username).replace("{message}", msg.getText())));
                }
            }
        }
    }



}
