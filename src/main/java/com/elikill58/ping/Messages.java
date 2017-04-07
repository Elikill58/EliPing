package com.elikill58.ping;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;

public class Messages {

    public static String PING_GET = Utils.applyColorCodes("&eYou have a ping of <ping> ms");
    public static String PING_GET_OTHER = Utils.applyColorCodes("&e<name> has <ping> ms of ping");
    public static String NOT_VALID_PLAYER = Utils.applyColorCodes("&c<arg> is not a valid player !");
    public static String FROM_CONSOLE = Utils.applyColorCodes("&cA console has no ping !");
    public static String RELOAD_SUCCESS = Utils.applyColorCodes("&aThe plugin has been successfully reloaded");
    public static String RELOAD_FAIL = Utils.applyColorCodes("&cThe plugin hasn't been successfully reloaded");
    public static String NO_PERMISSION = Utils.applyColorCodes("&cYou don't have permission to do this command");

    @SuppressWarnings("deprecation")
    public static void init() {
        YamlConfiguration langConf = new YamlConfiguration();
        try {
            langConf.load(EliPing.class.getResourceAsStream("/lang/" + EliPing.LANGUAGE + ".yml"));

            PING_GET = Utils.applyColorCodes(langConf.getString("ping_get"));
            PING_GET_OTHER = Utils.applyColorCodes(langConf.getString("ping_get_other"));
            NOT_VALID_PLAYER = Utils.applyColorCodes(langConf.getString("not_valid_player"));
            FROM_CONSOLE = Utils.applyColorCodes(langConf.getString("from_console"));
            RELOAD_SUCCESS = Utils.applyColorCodes(langConf.getString("reload_success"));
            RELOAD_FAIL = Utils.applyColorCodes(langConf.getString("reload_fail"));
            NO_PERMISSION = Utils.applyColorCodes(langConf.getString("no_permission"));
        } catch(IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

}
