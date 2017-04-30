package com.elikill58.eliping.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.List;

public class PingCommand extends Command implements TabExecutor {

    public PingCommand() {
        super("bping");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            if(sender instanceof ProxiedPlayer) {
                if(sender.hasPermission("eliping.ping")) {
                    sender.sendMessage(Messages.PING_GET
                            .replaceAll("<ping>", String.valueOf(((ProxiedPlayer) sender).getPing())));
                } else {
                    sender.sendMessage(Messages.NO_PERMISSION);
                }
            } else {
                sender.sendMessage(Messages.FROM_CONSOLE);
            }
        } else if(args[0].equalsIgnoreCase("reload")) {
            if(sender.hasPermission("eliping.ping.reload")) {
                try {
                    EliPing.reload();
                    sender.sendMessage(Messages.RELOAD_SUCCESS);
                } catch(Throwable t) {
                    t.printStackTrace();
                    sender.sendMessage(Messages.RELOAD_FAIL);
                }
            } else {
                sender.sendMessage(Messages.NO_PERMISSION);
            }
        } else if(ProxyServer.getInstance().getPlayer(args[0]) != null) {
            if(sender.hasPermission("eliping.ping.other")) {
                sender.sendMessage(Messages.PING_GET_OTHER
                        .replaceAll("<name>", ProxyServer.getInstance().getPlayer(args[0]).getName())
                        .replaceAll("<ping>", String.valueOf(ProxyServer.getInstance().getPlayer(args[0]).getPing())));
            } else {
                sender.sendMessage(Messages.NO_PERMISSION);
            }
        } else {
            sender.sendMessage(Messages.NOT_VALID_PLAYER
                    .replaceAll("<arg>", args[0]));
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        List<String> list = new ArrayList<>();
        if(EliPing.getConf().getBoolean("tabComplete"))
            ProxyServer.getInstance().getPlayers().forEach(proxiedPlayer -> list.add(proxiedPlayer.getName()));
        list.add("reload");
        return list;
    }
}
