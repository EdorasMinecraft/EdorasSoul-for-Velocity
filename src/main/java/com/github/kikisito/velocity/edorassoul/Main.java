package com.github.kikisito.velocity.edorassoul;

import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.github.kikisito.velocity.edorassoul.commands.*;
import com.github.kikisito.velocity.edorassoul.listeners.PlayerJoin;
import com.github.kikisito.velocity.edorassoul.listeners.ProxyPingListener;
import com.github.kikisito.velocity.edorassoul.listeners.TelegramMessage;
import com.github.kikisito.velocity.edorassoul.tasks.Ads;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import lombok.Getter;
import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;

import javax.inject.Inject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Plugin(
        id = "edorassoul",
        name = "EdorasSoul",
        version = "1.0",
        description = "Plugin de EdorasSoul para Velocity",
        authors = {"Kikisito"},
        dependencies = {
                @Dependency(id = "luckperms", optional = true),
        })
public final class Main {
    public static ZinciteBot telegramBot;

    @Getter
    public YamlFile config;

    public Database database;
    private ScheduledTask task;

    public List<Integer> protocolId = new ArrayList<>();
    public List<Integer> doNotKick = new ArrayList<>();

    public List<Player> ignoreTelegram = new ArrayList<>();

    @Getter
    private ProxyServer server;

    @Getter
    private Logger logger;

    private Path dataDirectory;

    @Inject
    public Main(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        this.loadConfig();
        this.database = new Database(this);

        CommandManager commandManager = this.getServer().getCommandManager();

        // COMANDO /ADMINCHAT
        CommandMeta adminChatCommandMeta = commandManager.metaBuilder("atel")
                .aliases("sendadmin", "atelegram", "at")
                .plugin(this)
                .build();
        SimpleCommand adminChatCommand = new AdminChatCommand(this);
        commandManager.register(adminChatCommandMeta, adminChatCommand);

        // COMANDO /TEL
        CommandMeta staffChatCommandMeta = commandManager.metaBuilder("tel")
                .aliases("sendtelegram", "stelegram", "ste")
                .plugin(this)
                .build();
        SimpleCommand staffChatCommand = new StaffChatCommand(this);
        commandManager.register(staffChatCommandMeta, staffChatCommand);

        // COMANDO /EVCHAT
        CommandMeta eventosChatCommandMeta = commandManager.metaBuilder("evchat")
                .aliases("evc", "eventoschat", "evtel", "etel", "eventostelegram", "eventostel")
                .plugin(this)
                .build();
        SimpleCommand eventosChatCommand = new EventosChatCommand(this);
        commandManager.register(eventosChatCommandMeta, eventosChatCommand);

        // COMANDO /MUTETELEGRAM
        CommandMeta ignoreTelegramCommandMeta = commandManager.metaBuilder("mutetelegram")
                .plugin(this)
                .build();
        SimpleCommand ignoreTelegramCommand = new IgnoreTelegramCommand(this);
        commandManager.register(ignoreTelegramCommandMeta, ignoreTelegramCommand);

        // COMANDO /CHECKFORMS
        CommandMeta pendingFormsCommandMeta = commandManager.metaBuilder("checkforms")
                .plugin(this)
                .build();
        SimpleCommand pendingFormsCommand = new PendingFormsCommand(this);
        commandManager.register(pendingFormsCommandMeta, pendingFormsCommand);

        // COMANDO /SENDAD
        CommandMeta sendAdCommandMeta = commandManager.metaBuilder("sendad")
                .plugin(this)
                .build();
        SimpleCommand sendAdCommand = new SendAdCommand(this);
        commandManager.register(sendAdCommandMeta, sendAdCommand);

        // COMANDO /VOY
        CommandMeta voyCommandMeta = commandManager.metaBuilder("voy")
                .plugin(this)
                .build();
        SimpleCommand voyCommand = new VoyCommand(this);
        commandManager.register(voyCommandMeta, voyCommand);

        this.getServer().getEventManager().register(this, new PlayerJoin(this));
        this.getServer().getEventManager().register(this, new ProxyPingListener(this));
        task = this.getServer().getScheduler().buildTask(this, new Ads(this)).repeat(config.getLong("publicidad.period"), TimeUnit.SECONDS).schedule();

        this.protocolId = config.getIntegerList("protocol.version");
        this.doNotKick = config.getIntegerList("protocol.do-not-kick");

        if (this.protocolId.isEmpty()) {
            this.protocolId.add(ProtocolVersion.MAXIMUM_VERSION.getProtocol());
        }

        telegramBot = new ZinciteBot(config.getString("telegram-bot-token"));
        telegramBot.getModuleManager().registerModule(new VoyZinciteCMD(this));
        telegramBot.getModuleManager().registerModule(new TelegramMessage(this));
        telegramBot.startServer();
    }

    public void loadConfig() {
        File pluginFolder = this.dataDirectory.toFile();
        try {
            if (!pluginFolder.exists())
                pluginFolder.mkdir();
            File file = new File(pluginFolder, "config.yml");
            if (!file.exists()) {
                try (InputStream in = this.getClass().getClassLoader().getResourceAsStream("config.yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            final YamlFile config = new YamlFile(file);
            config.createOrLoadWithComments();
            this.config = config;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if(task != null) task.cancel();

        if(telegramBot != null){
            telegramBot.getTelegramBot().stopUpdatesPoller();
            telegramBot.getModuleManager().getModules().forEach(ZinciteModule::onClose);
        }
    }

    public PendingForm isPendingForms(){
        boolean forms = false;
        JsonArray jsonArray = null;

        try {
            URLConnection connection = new URL(config.getString("new-forms-link")).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setConnectTimeout(3000);
            connection.connect();

            // Debería devolver un mensaje de error a Player
            if(((HttpURLConnection) connection).getResponseCode() != HttpURLConnection.HTTP_OK) return new PendingForm(false, new JsonArray());

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            jsonArray = new Gson().fromJson(br, JsonArray.class);
            if(jsonArray.size() != 0){
                forms = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PendingForm(forms, jsonArray);
    }

    public JsonArray getAds(){
        JsonArray jsonArray;
        JsonArray finaljsonArray = new JsonArray();

        try {
            URLConnection connection = new URL(config.getString("ads-link")).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.setConnectTimeout(3000);
            connection.connect();

            // Debería devolver un mensaje de error a Player
            if(((HttpURLConnection) connection).getResponseCode() != HttpURLConnection.HTTP_OK) return finaljsonArray;

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            jsonArray = new Gson().fromJson(br, JsonArray.class);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");

            for(JsonElement jsonElement : jsonArray){
                Date fechafin = simpleDateFormat.parse(jsonElement.getAsJsonObject().get("fechafin").getAsString());
                Date now = simpleDateFormat.parse(simpleDateFormat.format(new Date()));
                if(fechafin.after(now)) finaljsonArray.add(jsonElement);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return finaljsonArray;
    }
}
