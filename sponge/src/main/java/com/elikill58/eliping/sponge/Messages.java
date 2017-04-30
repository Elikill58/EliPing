package com.elikill58.eliping.sponge;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import java.io.File;
import java.io.IOException;

public class Messages {

    public static String PING_GET = Utils.applyColorCodes("&eYou have a ping of <ping> ms");
    public static String PING_GET_OTHER = Utils.applyColorCodes("&e<name> has <ping> ms of ping");
    public static String NOT_VALID_PLAYER = Utils.applyColorCodes("&c<arg> is not a valid player !");
    public static String FROM_CONSOLE = Utils.applyColorCodes("&cA console has no ping !");
    public static String RELOAD_SUCCESS = Utils.applyColorCodes("&aThe plugin has been successfully reloaded");
    public static String RELOAD_FAIL = Utils.applyColorCodes("&cThe plugin hasn't been successfully reloaded");
    public static String NO_PERMISSION = Utils.applyColorCodes("&cYou don't have permission to do this command");
    public static String REFRESH = Utils.applyColorCodes("&aPlayers' pings refreshed");

    @SuppressWarnings("deprecation")
    public static void init() {
        try {
            ConfigurationLoader<CommentedConfigurationNode> langConfLoader = HoconConfigurationLoader.builder().setPath(new File(EliPing.getConfDir(), "lang/" + EliPing.LANGUAGE + ".conf").toPath()).build();
            ConfigurationNode langRoot = langConfLoader.load();

            PING_GET = Utils.applyColorCodes(langRoot.getNode("ping_get").getString());
            PING_GET_OTHER = Utils.applyColorCodes(langRoot.getNode("ping_get_other").getString());
            NOT_VALID_PLAYER = Utils.applyColorCodes(langRoot.getNode("not_valid_player").getString());
            FROM_CONSOLE = Utils.applyColorCodes(langRoot.getNode("from_console").getString());
            RELOAD_SUCCESS = Utils.applyColorCodes(langRoot.getNode("reload_success").getString());
            RELOAD_FAIL = Utils.applyColorCodes(langRoot.getNode("reload_fail").getString());
            NO_PERMISSION = Utils.applyColorCodes(langRoot.getNode("no_permission").getString());
            REFRESH = Utils.applyColorCodes(langRoot.getNode("refresh").getString());
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
