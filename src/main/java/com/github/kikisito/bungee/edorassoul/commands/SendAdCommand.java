package com.github.kikisito.bungee.edorassoul.commands;

import com.github.kikisito.bungee.edorassoul.Main;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.file.YamlFile;

import java.util.Random;

public class SendAdCommand implements SimpleCommand {
    final private Main plugin;
    final private YamlFile config;

    public SendAdCommand(Main plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("a51.publicidad.enviar") && invocation.source() instanceof Player;
    }

    @Override
    public void execute(final Invocation invocation) {
        Player sender = (Player) invocation.source();
        String[] args = invocation.arguments();

        JsonArray jsonArray = plugin.getAds();
        if(jsonArray.size() <= 0) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("publicidad.no-ads-available")));
            return;
        }
        switch(args.length) {
            case 0:
                int rnd = new Random().nextInt(jsonArray.size());
                for (Player p : plugin.getServer().getAllPlayers()) {
                    p.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("publicidad.format").replace("{message}", jsonArray.get(rnd).getAsJsonObject().get("contenido").getAsString())));
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
                    for (Player p : plugin.getServer().getAllPlayers()) {
                        p.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("publicidad.format").replace("{message}", anuncio.getAsJsonObject().get("contenido").getAsString())));
                    }
                } else {
                    sender.sendMessage(MiniMessage.miniMessage().deserialize( config.getString("publicidad.not-found").replace("{player}", args[0])));
                }
                break;
            default:
                sender.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("publicidad.usage")));
                break;
        }
    }
}
