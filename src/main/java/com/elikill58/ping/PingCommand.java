package com.elikill58.ping;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] arg) {
		
		if (sender instanceof Player) {
			Player p = (Player) sender;
			int ping = Main.getPing(p);
			if (arg.length == 0) {
				p.sendMessage(Main.ping_get.replaceAll("<ping>", ping + ""));
			} else {
				if(arg[0].equalsIgnoreCase("reload")){
					Main.getInstance().reloadConfig();
					Bukkit.getPluginManager().disablePlugin(Main.getInstance());
					Bukkit.getPluginManager().enablePlugin(Main.getInstance());
					p.sendMessage(Main.reload);
				}else if (Utils.isConnectedPlayer(arg[0])) {
					Player cible = Bukkit.getPlayer(arg[0]);
					int otherping = Main.getPing(cible);
					p.sendMessage(Main.ping_get_other.replaceAll("<name>", cible.getName()).replaceAll("<ping>", otherping + ""));
				} else
					p.sendMessage(Main.not_valid_player.replaceAll("<arg>", arg[0]));
			}
		} else {
			if (arg.length == 0) {
				sender.sendMessage("Vous ne pouvez pas savoir votre ping.");
				sender.sendMessage(ChatColor.RED + "Usage : /ping [player]");
			} else {
				if (Utils.isConnectedPlayer(arg[0])) {
					Player cible = Bukkit.getPlayer(arg[0]);
					int otherping = Main.getPing(cible);
					sender.sendMessage(Main.ping_get_other.replaceAll("<name>", cible.getName()).replaceAll("<ping>", otherping + ""));
				} else
					sender.sendMessage(Main.not_valid_player.replaceAll("<arg>", arg[0]));
				
			}

		}
		return true;
	}

}
