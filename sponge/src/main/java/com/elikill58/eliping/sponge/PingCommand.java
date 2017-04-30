package com.elikill58.eliping.sponge;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PingCommand implements CommandCallable {

    @Override
    public CommandResult process(CommandSource src, String args) throws CommandException {
        if(args.isEmpty()) {
            if(src instanceof Player) {
                if(src.hasPermission("eliping.ping")) {
                    src.sendMessage(Text.of(Messages.PING_GET
                            .replaceAll("<ping>", String.valueOf(((Player) src).getConnection().getLatency()))));
                } else {
                    src.sendMessage(Text.of(Messages.NO_PERMISSION));
                }
            } else {
                src.sendMessage(Text.of(Messages.FROM_CONSOLE));
            }
        } else if(args.split(" ")[0].equalsIgnoreCase("reload")) {
            if(src.hasPermission("eliping.ping.reload")) {
                try {
                    EliPing.reload();
                    src.sendMessage(Text.of(Messages.RELOAD_SUCCESS));
                } catch(Throwable t) {
                    src.sendMessage(Text.of(Messages.RELOAD_FAIL));
                }
            } else {
                src.sendMessage(Text.of(Messages.NO_PERMISSION));
            }
        } else if(args.split(" ")[0].equalsIgnoreCase("refresh")) {
            if(src.hasPermission("eliping.ping.refresh")) {
                EliPing.refreshTabList();
                EliPing.refreshScoreboard();
                src.sendMessage(Text.of(Messages.REFRESH));
            } else {
                src.sendMessage(Text.of(Messages.NO_PERMISSION));
            }
        } else if(Sponge.getServer().getPlayer(args.split(" ")[0]).isPresent()) {
            if(src.hasPermission("eliping.ping.other")) {
                Player target = Sponge.getServer().getPlayer(args.split(" ")[0]).get();
                src.sendMessage(Text.of(Messages.PING_GET_OTHER
                        .replaceAll("<name>", target.getName())
                        .replaceAll("<ping>", String.valueOf(target.getConnection().getLatency()))));
            } else {
                src.sendMessage(Text.of(Messages.NO_PERMISSION));
            }
        } else {
            if(src.hasPermission("eliping.ping.other")) {
                src.sendMessage(Text.of(Messages.NOT_VALID_PLAYER.replaceAll("<arg>", args.split(" ")[0])));
            } else {
                src.sendMessage(Text.of(Messages.NO_PERMISSION));
            }
        }

        return CommandResult.empty();
    }

    @Override
    public List<String> getSuggestions(CommandSource src, String args, @Nullable Location<World> targetPosition) throws CommandException {
        List<String> list = new ArrayList<>();

        if(EliPing.TAB_COMPLETE && src.hasPermission("eliping.ping.other"))
            Sponge.getServer().getOnlinePlayers().forEach(player -> list.add(player.getName()));

        if(src.hasPermission("eliping.ping.reload"))
            list.add("reload");

        if(src.hasPermission("eliping.ping.refresh"))
            list.add("refresh");

        return list;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return true;
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Gives your ping or that of another"));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return getShortDescription(source);
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("/ping [player | reload | refresh]");
    }
}
