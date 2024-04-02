package com.github.kikisito.bungee.edorassoul.commands;

import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.github.kikisito.bungee.edorassoul.Main;
import com.github.kikisito.bungee.edorassoul.PendingForm;
import com.google.gson.JsonArray;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.file.YamlFile;

public class PendingFormsCommand implements SimpleCommand {
    final private Main plugin;
    final private YamlFile config;

    public PendingFormsCommand(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }
    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("a51.notificaciones") && invocation.source() instanceof Player;
    }

    @Override
    public void execute(final Invocation invocation) {
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
            for(Player p : plugin.getServer().getAllPlayers()){
                if(p.hasPermission("a51.notificaciones")){
                    p.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("formularios.new-form").replace("{players}", playersmc)));
                }
            }
            try {
                Main.telegramBot.getTelegramBot().sendMessage(config.get("staffchat-channel"), config.getString("formularios.telegram-new-form").replace("{players}", players), ParseMode.MARKDOWN, false, false, null, null, null);
            } catch (TelegramException e) {
                e.printStackTrace();
            }
        }
    }
}
