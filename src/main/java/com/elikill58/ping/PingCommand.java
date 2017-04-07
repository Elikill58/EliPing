package com.elikill58.ping;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PingCommand implements TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            if(sender instanceof Player) {
                sender.sendMessage(Messages.PING_GET.replaceAll("<ping>", String.valueOf(EliPing.getPing((Player) sender))));
            } else {
                sender.sendMessage(Messages.FROM_CONSOLE);
            }
        } else {
            if(args[0].equalsIgnoreCase("reload")) {
                if(!sender.hasPermission("ping.reload")) {
                    sender.sendMessage(Messages.NO_PERMISSION);
                    return true;
                }

                try {
                    EliPing.reload();
                    sender.sendMessage(Messages.RELOAD_SUCCESS);
                } catch(Throwable t) {
                    t.printStackTrace();
                    sender.sendMessage(Messages.RELOAD_FAIL);
                }
            } else if (Utils.isConnectedPlayer(args[0])) {
                Player target = Bukkit.getPlayer(args[0]);
                int otherping = EliPing.getPing(target);
                sender.sendMessage(Messages.PING_GET_OTHER
                        .replaceAll("<name>", target.getName())
                        .replaceAll("<ping>", String.valueOf(otherping)));
            } else {
                sender.sendMessage(Messages.NOT_VALID_PLAYER
                        .replaceAll("<arg>", args[0]));
            }
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> list = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
        list.add("reload");
        return list;
    }
}
