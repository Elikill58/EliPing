package com.elikill58.ping;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer extends BukkitRunnable {

	public void run(){
		for(Player p : Bukkit.getOnlinePlayers()){
			int ping = Main.getPing(p);
			p.setPlayerListName(Main.tab.replaceAll("<tab>", getTabWithoutPing(p.getPlayerListName())).replaceAll("<name>", p.getName()).replaceAll("<ping>", ping + ""));
		}
	}
	
	public String getTabWithoutPing(String tab){
		
		String[] stab = tab.split(" ");
		String newTab = "";
		
		for(String s : stab){
			if(s.startsWith("(") && s.endsWith(")"))
				continue;
			else {
				if(!newTab.equals(""))
					newTab = newTab + " " + s;
				else
					newTab = s;
			}
		}
		
		return newTab;
	}
	
}
