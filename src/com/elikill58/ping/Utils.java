package com.elikill58.ping;

import org.bukkit.Bukkit;

@SuppressWarnings("unused")
public class Utils {
	
	public static boolean isInteger(String arg) {
		try {
			int i = Integer.parseInt(arg);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static boolean isConnectedPlayer(String arg){
		if(Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(arg))){
			return true;
		} else {
			return false;
		}
	}
}
