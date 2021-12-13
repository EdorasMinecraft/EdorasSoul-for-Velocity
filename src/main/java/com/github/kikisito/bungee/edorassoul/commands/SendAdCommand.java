package com.github.kikisito.bungee.edorassoul.commands;

import com.github.kikisito.bungee.edorassoul.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.config.Configuration;

import java.util.Random;

public class SendAdCommand extends Command {
    final private Main plugin;
    final private Configuration config;

    public SendAdCommand(Main plugin) {
        super("sendad", "a51.publicidad.enviar");
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        JsonArray jsonArray = plugin.getAds();
        if(jsonArray.size() <= 0) {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("publicidad.no-ads-available"))));
            return;
        }
        switch(args.length) {
            case 0:
                int rnd = new Random().nextInt(jsonArray.size());
                for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("publicidad.format")).replace("{message}", jsonArray.get(rnd).getAsJsonObject().get("contenido").getAsString())));
                }
                break;
            case 1:
                JsonElement anuncio = null;
                for(JsonElement jsonElement : jsonArray){
                    if(jsonElement.getAsJsonObject().get("nick").getAsString().equals(args[0])){
                        anuncio = jsonElement;
                        break;
                    }
                }
                if(anuncio != null){
                    for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
                        p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("publicidad.format")).replace("{message}", anuncio.getAsJsonObject().get("contenido").getAsString())));
                    }
                } else {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("publicidad.not-found").replace("{player}", args[0]))));
                }
                break;
            default:
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("publicidad.usage"))));
                break;
        }
    }
}
