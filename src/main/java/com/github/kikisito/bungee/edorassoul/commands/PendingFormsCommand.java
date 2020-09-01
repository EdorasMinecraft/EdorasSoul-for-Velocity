package com.github.kikisito.bungee.edorassoul.commands;

import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.github.kikisito.bungee.edorassoul.Main;
import com.github.kikisito.bungee.edorassoul.PendingForm;
import com.google.gson.JsonArray;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

public class PendingFormsCommand extends Command {
    final private Main plugin;
    final private Configuration config;

    public PendingFormsCommand(Main plugin){
        super("checkforms", "a51.notificaciones");
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args){
        PendingForm pendingForm = plugin.isPendingForms();
        if(pendingForm.isPendingForms()){
            JsonArray jsonArray = pendingForm.getJsonArray();
            StringBuilder players = new StringBuilder();
            StringBuilder playersmc = new StringBuilder();
            for(int i = 0; i < jsonArray.size(); i++){
                String player = jsonArray.get(i).getAsJsonObject().get("nick").getAsString();
                String link = "https://edoras.es/area51/formulario.php?usuario=" + jsonArray.get(i).getAsJsonObject().get("id").getAsString();
                players.append("[" + player + "](" + link + ")");
                playersmc.append(player);
                if(i + 1 <= jsonArray.size() - 2){ players.append(", "); playersmc.append(", "); }
                if(i + 1 == jsonArray.size() - 1){ players.append(" y "); playersmc.append(" y "); }
            }
            for(ProxiedPlayer p : plugin.getProxy().getPlayers()){
                if(p.hasPermission("a51.notificaciones")){
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("formularios.new-form").replace("{players}", playersmc))));
                }
            }
            try {
                Main.telegramBot.getTelegramBot().sendMessage(config.get("staffchat-channel"), config.getString("formularios.telegram-new-form").replace("{players}", players), ParseMode.MARKDOWN, false, false, null, null);
            } catch (TelegramException e) {
                e.printStackTrace();
            }
        }
    }
}
