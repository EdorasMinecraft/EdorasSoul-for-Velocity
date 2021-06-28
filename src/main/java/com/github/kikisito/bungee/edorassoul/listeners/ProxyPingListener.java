package com.github.kikisito.bungee.edorassoul.listeners;

import com.github.kikisito.bungee.edorassoul.Main;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import de.myzelyam.api.vanish.*;

public class ProxyPingListener implements Listener {

    final private Main plugin;
    final private Configuration config;

    public ProxyPingListener(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPing(ProxyPingEvent event) {
        ServerPing ping = event.getResponse();

        ServerPing.Protocol protocol = ping.getVersion();

        String protocolName;
        // Modo mantenimiento DESACTIVADO
        if(!plugin.isWhitelist){
            protocolName = config.getString("protocol.name");
            if (protocolName != null) {
                protocol.setName(protocolName);
            }

            if (!(plugin.protocolId.contains(protocol.getProtocol()))) {
                protocol.setProtocol(-1);
            }

            ServerPing.Players players = ping.getPlayers();
            ServerPing.PlayerInfo[] playerList = this.plugin.getProxy().getPlayers()
                    .stream()
                    .filter(proxiedPlayer -> !BungeeVanishAPI.isInvisible(proxiedPlayer))
                    .map(player -> new ServerPing.PlayerInfo(player.getName(), player.getUniqueId()))
                    .toArray(ServerPing.PlayerInfo[]::new);

            players.setSample(playerList);
            players.setOnline(playerList.length);
            ping.setPlayers(players);
        } else {
            // Modo mantenimiento
            ping.getPlayers().setOnline(0);
            protocolName = config.getString("protocol.whitelist");
            protocol.setName(protocolName);
            protocol.setProtocol(-1);
        }

        ping.setVersion(protocol);
        event.setResponse(ping);
    }
}
