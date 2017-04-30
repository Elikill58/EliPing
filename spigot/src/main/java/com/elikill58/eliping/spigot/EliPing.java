package com.elikill58.eliping.spigot;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class EliPing extends JavaPlugin {

    private static EliPing INSTANCE;

    public static EliPing getInstance() {
        return INSTANCE;
    }

    public static int REFRESH_FREQUENCY = 20;
    public static String LANGUAGE = "en_US";
    public static String TAB_FORMAT = "<tab> (<ping>)";

    private static Class<?> classPing;


    public void onEnable() {
        INSTANCE = this;

        loadConf();

        setupPing();

        new Metrics(this).addCustomChart(new Metrics.SimplePie("timerefresh") {
            @Override
            public String getValue() {
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

    /**
     * @param player the player from which we want the ping
     * @return the ping of the player
     */
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

    private static void loadConf() {
        INSTANCE.getDataFolder().mkdir();

        File conf = new File(INSTANCE.getDataFolder(), "config.yml");

        File lang = new File(INSTANCE.getDataFolder(), "lang");

        lang.mkdir();

        File en_US = new File(INSTANCE.getDataFolder(), "lang/en_US.yml");
        File fr_FR = new File(INSTANCE.getDataFolder(), "lang/fr_FR.yml");

        try {
            if(!conf.exists())
                Files.copy(INSTANCE.getClass().getResourceAsStream("/spigot/config.yml"),
                        conf.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            if(!en_US.exists())
                Files.copy(INSTANCE.getClass().getResourceAsStream("/spigot/lang/en_US.yml"),
                        en_US.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            if(!fr_FR.exists())
                Files.copy(INSTANCE.getClass().getResourceAsStream("/spigot/lang/fr_FR.yml"),
                        fr_FR.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);


            REFRESH_FREQUENCY = INSTANCE.getConfig().getInt("timeRefresh");
            LANGUAGE = INSTANCE.getConfig().getString("language", "en_US");
            TAB_FORMAT = INSTANCE.getConfig().getString("tab_format");

            Messages.init();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    static void reload() {
        EliPing.getInstance().reloadConfig();
        Messages.init();
        Bukkit.getPluginManager().disablePlugin(EliPing.getInstance());
        Bukkit.getPluginManager().enablePlugin(EliPing.getInstance());
    }
}
