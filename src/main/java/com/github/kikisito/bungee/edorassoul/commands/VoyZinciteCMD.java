package com.github.kikisito.bungee.edorassoul.commands;

import com.cadiducho.telegrambotapi.Chat;
import com.cadiducho.telegrambotapi.Message;
import com.cadiducho.telegrambotapi.ParseMode;
import com.cadiducho.telegrambotapi.User;
import com.cadiducho.telegrambotapi.exception.TelegramException;
import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.command.BotCommand;
import com.cadiducho.zincite.api.command.CommandContext;
import com.cadiducho.zincite.api.command.CommandInfo;
import com.cadiducho.zincite.api.module.ModuleInfo;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.github.kikisito.bungee.edorassoul.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.config.Configuration;

import java.time.Instant;

@ModuleInfo(name = "Voy", description = "Notifica cuando vas a revisar el último formulario enviado")
public class VoyZinciteCMD implements ZinciteModule {
    private Main plugin;
    public VoyZinciteCMD(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad(){
        ZinciteBot.getInstance().getCommandManager().register(new CommandExecutor(plugin));
    }

    @CommandInfo(aliases = "/voy", description = "Notifica cuando vas a revisar el último formulario enviado")
    static class CommandExecutor implements BotCommand {
        private final Main plugin;
        public CommandExecutor(Main plugin) {
            this.plugin = plugin;
        }

        @Override
        public void execute(Chat chat, User user, CommandContext commandContext, Integer integer, Message message, Instant instant) throws TelegramException {
            Configuration config = plugin.getConfig();
            if (chat.getId().equals(Long.parseLong(config.getString("staffchat-channel")))) {
                plugin.getProxy().getPlayers().stream()
                        .filter(proxiedPlayer -> proxiedPlayer.hasPermission("a51.notificaciones"))
                        .forEach(proxiedPlayer -> {
                            proxiedPlayer.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', config.getString("formularios.voy").replace("{user}", user.getUsername()))));
                        });
                this.getBot().sendMessage(chat.getId(), config.getString("formularios.telegram-voy").replaceAll("\\*", "").replace("{user}", "[" + user.getUsername() + "](tg://user?id=" + user.getId() + ")"), ParseMode.MARKDOWN, false, false, null, null, null);
            } else {
                this.getBot().sendMessage(chat.getId(), config.getString("chat.telegram-chat-not-allowed").replaceAll("\\*", ""), ParseMode.MARKDOWN, false, false, null, null, null);
            }
        }
    }

}
