package com.elikill58.ping;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PingTimer extends BukkitRunnable {

    public void run() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            int ping = EliPing.getPing(p);
            p.setPlayerListName(Utils.applyColorCodes(EliPing.TAB_FORMAT
                    .replaceAll("<tab>", Utils.getTabWithoutPing(p.getPlayerListName()))
                    .replaceAll("<name>", p.getName())
                    .replaceAll("<ping>", ping + "")));
        }
    }
}
