package com.github.kikisito.velocity.edorassoul.tasks;

import com.github.kikisito.velocity.edorassoul.Main;
import com.google.gson.JsonArray;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Random;

public class Ads implements Runnable {
    final private Main plugin;
    final private YamlFile config;

    public Ads(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public void run(){
        JsonArray jsonArray = plugin.getAds();;
        if(jsonArray.size() <= 0) {
            plugin.getLogger().info("No ads could be found");
            return;
        }
        int rnd = new Random().nextInt(jsonArray.size());
        for (Player p : plugin.getServer().getAllPlayers()) {
            Component ad = MiniMessage.miniMessage().deserialize(config.getString("publicidad.format").replace("{message}", jsonArray.get(rnd).getAsJsonObject().get("contenido").getAsString()));
            p.sendMessage(ad);
        }
    }
}
