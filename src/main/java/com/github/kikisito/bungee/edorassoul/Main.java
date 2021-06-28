package com.github.kikisito.bungee.edorassoul;

import com.cadiducho.zincite.ZinciteBot;
import com.cadiducho.zincite.api.module.ZinciteModule;
import com.github.kikisito.bungee.edorassoul.commands.*;
import com.github.kikisito.bungee.edorassoul.listeners.PlayerJoin;
import com.github.kikisito.bungee.edorassoul.listeners.ProxyPingListener;
import com.github.kikisito.bungee.edorassoul.listeners.TelegramMessage;
import com.github.kikisito.bungee.edorassoul.tasks.Ads;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public final class Main extends Plugin {
    public static ZinciteBot telegramBot;
    public static Configuration config;
    public static Configuration whitelist;
    private ScheduledTask task;

    public List<Integer> protocolId = new ArrayList<>();
    public List<Integer> doNotKick = new ArrayList<>();

    public List<ProxiedPlayer> ignoreTelegram = new ArrayList<>();
    
    public boolean isWhitelist = false;

    @Override
    public void onEnable() {
        this.loadConfig();
        this.loadWhitelist();
        this.isWhitelist = whitelist.getBoolean("whitelist");
        this.getProxy().getPluginManager().registerCommand(this, new PendingFormsCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new SendAdCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new VoyCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new IgnoreTelegramCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new StaffChatCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new AdminChatCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new EventosChatCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new WhitelistCommand(this));
        this.getProxy().getPluginManager().registerListener(this, new PlayerJoin(this));
        this.getProxy().getPluginManager().registerListener(this, new ProxyPingListener(this));
        task = this.getProxy().getScheduler().schedule(this, new Ads(this), 5, config.getLong("publicidad.period"), TimeUnit.SECONDS);

        this.protocolId = config.getIntList("protocol.version");
        this.doNotKick = config.getIntList("protocol.do-not-kick");

        if (this.protocolId.isEmpty()) {
            this.protocolId.add(this.getProxy().getProtocolVersion());
        }

        telegramBot = new ZinciteBot(config.getString("telegram-bot-token"));
        telegramBot.getModuleManager().registerModule(new VoyZinciteCMD(this));
        telegramBot.getModuleManager().registerModule(new TelegramMessage(this));
        telegramBot.startServer();
    }

    public void loadConfig() {
        try {
            if (!getDataFolder().exists())
                getDataFolder().mkdir();
            File file = new File(getDataFolder(), "config.yml");
            if (!file.exists()) {
                try (InputStream in = getResourceAsStream("config.yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadWhitelist() {
        try {
            if (!getDataFolder().exists())
                getDataFolder().mkdir();
            File file = new File(getDataFolder(), "whitelist.yml");
            if (!file.exists()) {
                try (InputStream in = getResourceAsStream("whitelist.yml")) {
                    Files.copy(in, file.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            whitelist = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "whitelist.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Configuration getConfig(){ return config; }

    public Configuration getWhitelist(){ return whitelist; }

    public void saveWhitelist(){
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(whitelist, new File(getDataFolder(), "whitelist.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        task.cancel();
        telegramBot.getTelegramBot().stopUpdatesPoller();
        telegramBot.getModuleManager().getModules().forEach(ZinciteModule::onClose);
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
