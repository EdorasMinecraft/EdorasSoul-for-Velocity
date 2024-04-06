package com.github.kikisito.velocity.edorassoul.commands;

import com.github.kikisito.velocity.edorassoul.Main;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class IgnoreTelegramCommand implements SimpleCommand {
    final private Main plugin;

    public IgnoreTelegramCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean hasPermission(final Invocation invocation) {
        return invocation.source().hasPermission("edorassoul.receive.telegram") && invocation.source() instanceof Player;
    }

    @Override
    public void execute(final Invocation invocation) {
        Player sender = (Player) invocation.source();
        if(!plugin.ignoreTelegram.contains(sender)) {
            plugin.ignoreTelegram.add(sender);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("chat.muted-chat")));
        } else {
            plugin.ignoreTelegram.remove(sender);
            sender.sendMessage(MiniMessage.miniMessage().deserialize(plugin.getConfig().getString("chat.unmuted-chat")));

        }
    }
}
