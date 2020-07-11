package com.github.kikisito.edorassoulwi;

import com.github.kikisito.edorassoulwi.commands.PendingFormsCommand;
import com.github.kikisito.edorassoulwi.commands.SendAdCommand;
import com.github.kikisito.edorassoulwi.listeners.PlayerJoin;
import com.github.kikisito.edorassoulwi.tasks.Ads;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class Main extends Plugin {
    public static Configuration config;
    private ScheduledTask task;

    @Override
    public void onEnable() {
        this.loadConfig();
        this.getProxy().getPluginManager().registerCommand(this, new PendingFormsCommand(this));
        this.getProxy().getPluginManager().registerCommand(this, new SendAdCommand(this));
        this.getProxy().getPluginManager().registerListener(this, new PlayerJoin(this));
        task = this.getProxy().getScheduler().schedule(this, new Ads(this), 5, 900, TimeUnit.SECONDS);
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

    public Configuration getConfig(){ return config; }

    @Override
    public void onDisable() {
        task.cancel();
    }

    public PendingForm isPendingForms(){
        boolean forms = false;
        JsonArray jsonArray = null;
        try {
            URL formJson = new URL(config.getString("new-forms-link"));
            InputStream in = formJson.openStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
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
            URL formJson = new URL(config.getString("ads-link"));
            InputStream in = formJson.openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
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
