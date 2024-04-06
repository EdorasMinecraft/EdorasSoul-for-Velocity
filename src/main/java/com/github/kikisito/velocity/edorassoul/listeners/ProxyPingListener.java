package com.github.kikisito.velocity.edorassoul.listeners;

import com.github.kikisito.velocity.edorassoul.Main;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyPingEvent;
import com.velocitypowered.api.proxy.server.ServerPing;
import de.myzelyam.api.vanish.VelocityVanishAPI;
import org.simpleyaml.configuration.file.YamlFile;

public class ProxyPingListener {

    final private Main plugin;
    final private YamlFile config;

    public ProxyPingListener(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onProxyPing(ProxyPingEvent event) {
        ServerPing ping = event.getPing();
        ServerPing.Builder serverPingBuilder = ping.asBuilder();

        ServerPing.Version playerVersion = ping.getVersion();
        int playerProtocol = playerVersion.getProtocol();

        String protocolName;
        // Modo mantenimiento DESACTIVADO
        protocolName = config.getString("protocol.name");
        if (protocolName != null) {
            ServerPing.Version version = new ServerPing.Version(playerProtocol, protocolName);
            serverPingBuilder.version(version);
        } else {
            protocolName = "Edoras";
        }

        if (!(plugin.protocolId.contains(playerProtocol))) {
            ServerPing.Version unsupportedProtocol = new ServerPing.Version(-1, protocolName);
            serverPingBuilder.version(unsupportedProtocol);
        }

        ServerPing.SamplePlayer[] playerList = plugin.getServer().getAllPlayers()
                .stream()
                .filter(proxiedPlayer -> !VelocityVanishAPI.isInvisible(proxiedPlayer))
                .map(player -> new ServerPing.SamplePlayer(player.getUsername(), player.getUniqueId()))
                .toArray(ServerPing.SamplePlayer[]::new);

        serverPingBuilder.maximumPlayers(playerList.length);
        serverPingBuilder.samplePlayers(playerList);

        // Creamos un nuevo ServerPing

        ServerPing response = serverPingBuilder.build();
        event.setPing(response);
    }
}
