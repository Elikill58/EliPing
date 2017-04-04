package com.elikill58.ping;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class EliPing extends JavaPlugin {

    private static EliPing instance;

    public static EliPing getInstance() {
        return instance;
    }

    public static int REFRESH_FREQUENCY = 20;
    public static String LANGUAGE = "en_US";
    public static String TAB_FORMAT = "<tab> (<ping>)";

    private static Class<?> classPing;


    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        setupPing();

        REFRESH_FREQUENCY = getConfig().getInt("timeRefresh");
        LANGUAGE = getConfig().getString("language", "en_US");
        TAB_FORMAT = getConfig().getString("tab_format");

        Messages.init();

        new Metrics(this).addCustomChart(new Metrics.SimplePie("timerefresh") {
            @Override
            public String getValue() {
                getLogger().info("nice");
                return String.valueOf(REFRESH_FREQUENCY);
            }
        });

        new PingTimer().runTaskTimer(this, 0, REFRESH_FREQUENCY);

        getCommand("ping").setExecutor(new PingCommand());
    }

    private static void setupPing() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            return;
        }

        EliPing.getInstance().getLogger().info("Your server is running version " + version.split("v")[1]);
        try {
            classPing = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int getPing(Player player) {
        try {
            Object object = classPing.cast(player);
            Object entityPlayer = object.getClass().getMethod("getHandle", new Class[0]).invoke(object);
            Field field = entityPlayer.getClass().getField("ping");
            return field.getInt(entityPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    static void reload() {
        EliPing.getInstance().reloadConfig();
        Messages.init();
        Bukkit.getPluginManager().disablePlugin(EliPing.getInstance());
        Bukkit.getPluginManager().enablePlugin(EliPing.getInstance());
    }
}
