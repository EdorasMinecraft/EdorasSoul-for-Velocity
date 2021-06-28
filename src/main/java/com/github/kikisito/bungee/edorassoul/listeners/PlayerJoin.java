package com.github.kikisito.bungee.edorassoul.listeners;

import com.github.kikisito.bungee.edorassoul.Main;
import com.github.kikisito.bungee.edorassoul.PendingForm;
import com.google.gson.JsonArray;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.concurrent.TimeUnit;

public class PlayerJoin implements Listener {
    final private Main plugin;
    final private Configuration config;

    public PlayerJoin(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PostLoginEvent e) {
        final ProxiedPlayer p = e.getPlayer();
        if (p.hasPermission("a51.notificaciones")) {
            PendingForm pendingForm = plugin.isPendingForms();
            if (pendingForm.isPendingForms()) {
                JsonArray jsonArray = pendingForm.getJsonArray();
                StringBuilder players = new StringBuilder();
                for (int i = 0; i < jsonArray.size(); i++) {
                    players.append(jsonArray.get(i).getAsJsonObject().get("nick").getAsString());
                    if (i + 1 <= jsonArray.size() - 2) players.append(", ");
                    if (i + 1 == jsonArray.size() - 1) players.append(" y ");
                }
                plugin.getProxy().getScheduler().schedule(plugin, () -> {
                    p.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("formularios.new-form")).replace("{players}", players.toString())));
                }, 5, TimeUnit.SECONDS);
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLoginEvent(PreLoginEvent e) {
        if (!plugin.protocolId.contains(e.getConnection().getVersion()) && !plugin.doNotKick.contains(e.getConnection().getVersion())) {
            e.setCancelReason(TextComponent.fromLegacyText(
                    ChatColor.translateAlternateColorCodes('&', config.getString("protocol.kick-message")
                            .replaceAll(".newline.", "\n"))));
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLoginEvent(PostLoginEvent e){
        if(plugin.isWhitelist && !e.getPlayer().hasPermission("edorassoul.bypass.whitelist") && !plugin.getWhitelist().getStringList("allowed-players").contains(e.getPlayer().getUniqueId().toString())){
            e.getPlayer().disconnect(TextComponent.fromLegacyText(
                    ChatColor.translateAlternateColorCodes('&', config.getString("protocol.whitelist-message")
                            .replaceAll(".newline.", "\n"))));
        }
    }
}
