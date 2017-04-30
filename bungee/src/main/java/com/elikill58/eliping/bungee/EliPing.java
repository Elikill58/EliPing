package com.elikill58.eliping.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


public class EliPing extends Plugin {

    private static EliPing INSTANCE;

    private static String LANGUAGE = "en_US";

    private static Configuration CONF;

    @Override
    public void onEnable() {
        INSTANCE = this;

        loadConf();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new PingCommand());
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
                Files.copy(INSTANCE.getClass().getResourceAsStream("/bungee/config.yml"),
                        conf.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            if(!en_US.exists())
                Files.copy(INSTANCE.getClass().getResourceAsStream("/bungee/lang/en_US.yml"),
                        en_US.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            if(!fr_FR.exists())
                Files.copy(INSTANCE.getClass().getResourceAsStream("/bungee/lang/fr_FR.yml"),
                        fr_FR.toPath(),
                        StandardCopyOption.REPLACE_EXISTING);

            CONF = ConfigurationProvider.getProvider(YamlConfiguration.class).load(conf);

            LANGUAGE = CONF.getString("language");

            Messages.init();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    static void reload() {
        loadConf();
    }

    public static EliPing getInstance() {
        return INSTANCE;
    }

    public static String getLanguage() {
        return LANGUAGE;
    }

    public static Configuration getConf() {
        return CONF;
    }

}
