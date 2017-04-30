package com.elikill58.eliping.spigot;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public final class Utils {

    private Utils() {}

    public static boolean isConnectedPlayer(String player){
        return Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(player));
    }

    public static String getTabWithoutPing(String tab) {
        String[] stab = tab.split(" ");
        String newTab = "";

        for(String s : stab){
            if(!s.startsWith("(") && !s.endsWith(")")) {
                if(!newTab.equals(""))
                    newTab = newTab
                            + " " + s;
                else
                    newTab = s;
            }
        }

        return newTab;
    }

    public static String applyColorCodes(String message) {
        return message
                .replaceAll("&r", ChatColor.RESET.toString())
                .replaceAll("&0", ChatColor.BLACK.toString())
                .replaceAll("&1", ChatColor.DARK_BLUE.toString())
                .replaceAll("&2", ChatColor.DARK_GREEN.toString())
                .replaceAll("&3", ChatColor.DARK_AQUA.toString())
                .replaceAll("&4", ChatColor.DARK_RED.toString())
                .replaceAll("&5", ChatColor.DARK_PURPLE.toString())
                .replaceAll("&6", ChatColor.GOLD.toString())
                .replaceAll("&7", ChatColor.GRAY.toString())
                .replaceAll("&8", ChatColor.DARK_GRAY.toString())
                .replaceAll("&9", ChatColor.BLUE.toString())
                .replaceAll("&a", ChatColor.GREEN.toString())
                .replaceAll("&b", ChatColor.AQUA.toString())
                .replaceAll("&c", ChatColor.RED.toString())
                .replaceAll("&d", ChatColor.LIGHT_PURPLE.toString())
                .replaceAll("&e", ChatColor.YELLOW.toString())
                .replaceAll("&f", ChatColor.WHITE.toString())
                .replaceAll("&l", ChatColor.BOLD.toString())
                .replaceAll("&o", ChatColor.ITALIC.toString())
                .replaceAll("&n", ChatColor.UNDERLINE.toString())
                .replaceAll("&m", ChatColor.STRIKETHROUGH.toString());
    }
}
