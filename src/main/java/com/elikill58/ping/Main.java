package com.elikill58.ping;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Main extends JavaPlugin {

	private static Main instance;

	public static Main getInstance() {
		return instance;
	}

	public static int timeRefresh;
	public static String ping_get, ping_get_other, not_valid_player, tab, reload;
	private static Class<?> classPing;

	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		setupPing();
		timeRefresh = getConfig().getInt("timeRefresh");
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("timeRefresh") {
			@Override
			public String getValue() {
				getLogger().info("nice");
				return String.valueOf(timeRefresh);
			}
		});
		ping_get = getConfig().getString("Messages.ping_get");
		ping_get_other = getConfig().getString("Messages.ping_get_other");
		not_valid_player = getConfig().getString("Messages.not_valid_player");
		tab = getConfig().getString("Messages.tab");
		reload = getConfig().getString("Messages.reload");

		new Timer().runTaskTimer(this, 0, timeRefresh);
		getCommand("ping").setExecutor(new PingCommand());

	}

	private static void setupPing() {

		String version;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (ArrayIndexOutOfBoundsException e) {
			return;
		}

		Main.getInstance().getLogger().info("Your server is running version " + version.split("v")[1]);
		try {
			classPing = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static int getPing(Player player) {
		try {
			Object object = classPing.cast(player);
			Object entityPlayer = object.getClass().getMethod("getHandle", new Class[0]).invoke(object, new Object[0]);
			Field field = entityPlayer.getClass().getField("ping");
			return field.getInt(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
