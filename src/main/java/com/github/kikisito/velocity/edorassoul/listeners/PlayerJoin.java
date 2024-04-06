package com.github.kikisito.velocity.edorassoul.listeners;

import com.github.kikisito.velocity.edorassoul.Main;
import com.github.kikisito.velocity.edorassoul.PendingForm;
import com.google.gson.JsonArray;
import com.velocitypowered.api.event.*;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.simpleyaml.configuration.file.YamlFile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

public class PlayerJoin {
    final private Main plugin;
    final private YamlFile config;

    public PlayerJoin(Main plugin){
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    @Subscribe
    public EventTask onPostLogin(PostLoginEvent e, Continuation continuation) {
        final Player player = e.getPlayer();

        return EventTask.async(() -> {
            // Comprobamos si es staff para enviarle notificaciones de formularios pendientes
            if (player.hasPermission("a51.notificaciones")) {
                PendingForm pendingForm = plugin.isPendingForms();
                if (pendingForm.isPendingForms()) {
                    JsonArray jsonArray = pendingForm.getJsonArray();
                    StringBuilder players = new StringBuilder();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        players.append(jsonArray.get(i).getAsJsonObject().get("nick").getAsString());
                        if (i + 1 <= jsonArray.size() - 2) players.append(", ");
                        if (i + 1 == jsonArray.size() - 1) players.append(" y ");
                    }

                    plugin.getServer().getScheduler().buildTask(plugin, () -> {
                        player.sendMessage(MiniMessage.miniMessage().deserialize(config.getString("formularios.new-form").replace("{players}", players.toString())));
                    }).delay(5, TimeUnit.SECONDS).schedule();
                }
            }

            // Insertar el nombre del usuario a la base de datos según su UUID
            try (final Connection connection = plugin.database.getConnection()) {
                PreparedStatement statement = connection.prepareStatement("""
                    INSERT INTO `player_uuids` (`uuid`, `name`)
                    VALUES (?, ?)
                    ON DUPLICATE KEY UPDATE `name`=?
                """);

                statement.setString(1, player.getUniqueId().toString());
                statement.setString(2, player.getUsername());
                statement.setString(3, player.getUsername());

                statement.executeUpdate();
            } catch (SQLException ex) {
                plugin.getLogger().warn("Error guardando el nombre de un usuario en la base de datos:");
                plugin.getLogger().warn(ex.getMessage());
            }
        });
    }

    @Subscribe(order = PostOrder.FIRST)
    public void onLogin(LoginEvent e) {
        int playerProtocol = e.getPlayer().getProtocolVersion().getProtocol();

        if (!plugin.protocolId.contains(playerProtocol) && !plugin.doNotKick.contains(playerProtocol)) {
            e.setResult(ResultedEvent.ComponentResult.denied(MiniMessage.miniMessage().deserialize(
                    config.getString("protocol.kick-message")
            )));
        }
    }
}
