package com.github.kikisito.edorassoulwi.tasks;

import com.github.kikisito.edorassoulwi.Main;
import com.google.gson.JsonArray;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.Random;

public class Ads implements Runnable {
    final private Main plugin;
    final private Configuration config;

    public Ads(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void run(){
        JsonArray jsonArray = plugin.getAds();
        int rnd = new Random().nextInt(jsonArray.size());
        for (ProxiedPlayer p : plugin.getProxy().getPlayers()) {
            p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("publicidad.format")).replace("{message}", jsonArray.get(rnd).getAsJsonObject().get("contenido").getAsString())));
        }
    }
}
