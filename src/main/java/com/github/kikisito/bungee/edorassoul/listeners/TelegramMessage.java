package com.github.kikisito.bungee.edorassoul.listeners;

import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.Update;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.github.kikisito.bungee.edorassoul.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@ModuleInfo(name = "TelegramToMinecraft", description = "Envia los mensajes de Telegram a Minecraft")
public class TelegramMessage implements ZinciteModule {
    private final Main plugin;
    public TelegramMessage(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPostCommand(Update update, boolean success) {
        if(update.getMessage() == null) return; // Si no es un mensaje, cancela
        if(update.getMessage().getType() != Message.Type.TEXT) return; // Solo mensajes de texto de momento.
        Message msg = update.getMessage();

        for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
            if(player.hasPermission("edorassoul.receive.telegram") && !plugin.ignoreTelegram.contains(player)){
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("chat.telegram-to-minecraft")).replace("{user}", update.getMessage().getFrom().getUsername()).replace("{message}", msg.getText())));
            }
        }
    }



}
