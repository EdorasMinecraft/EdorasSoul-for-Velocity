package com.github.kikisito.velocity.edorassoul.tasks;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.net.URL;
import java.util.Scanner;

public class Utils {
    public static String requestUUID(String name) throws Exception {
        // Obtención de UUID
        Scanner scanner = new Scanner(new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection().getInputStream());
        JsonObject json = new Gson().fromJson(scanner.next(), JsonObject.class);
        scanner.close();
        // Se añaden los guiones de las UUID
        StringBuilder stringBuilder = new StringBuilder(json.get("id").getAsString());
        stringBuilder.insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-");
        return stringBuilder.toString();
    }

    public static String requestName(String uuid) throws Exception {
        // Obtención de nombre
        Scanner scanner = new Scanner(new URL("https://api.mojang.com/user/profiles/" + uuid + "/names").openConnection().getInputStream());
        JsonArray json = new Gson().fromJson(scanner.next(), JsonArray.class);
        scanner.close();
        return json.get(json.size() - 1).getAsJsonObject().get("name").getAsString();
    }
}
