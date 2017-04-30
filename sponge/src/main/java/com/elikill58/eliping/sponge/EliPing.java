package com.elikill58.eliping.sponge;

import com.google.inject.Inject;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.scoreboard.Score;
import org.spongepowered.api.scoreboard.critieria.Criteria;
import org.spongepowered.api.scoreboard.displayslot.DisplaySlots;
import org.spongepowered.api.scoreboard.objective.Objective;
import org.spongepowered.api.scoreboard.objective.displaymode.ObjectiveDisplayModes;
import org.spongepowered.api.text.LiteralText;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;

import static com.elikill58.eliping.sponge.Utils.applyColorCodes;

@Plugin(id = "eliping",
        name = "EliPing",
        version = "1.5",
        description = "A simple plugin that gives your ping or that of another",
        authors = "RedNesto")
public class EliPing {

    private static EliPing INSTANCE;

    @Inject
    private PluginContainer plugin;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path defaultConf;

    private ConfigurationNode confRoot;

    private static Task REFRESH_TASK;

    private static Map<String, Integer> TAB_LAST_PING = new HashMap<>();
    private static Map<String, Integer> SB_LAST_PING = new HashMap<>();

    public static int REFRESH_RATE = 20;
    public static String LANGUAGE = "en_US";
    public static boolean TAB_COMPLETE = true;
    public static String TAB_FORMAT = "<tab> &e(<ping>)";
    public static boolean SCOREBOARD_ENABLED = false;
    public static String OBJECTIVE_NAME = "ping";
    public static String SCOREBOARD_FORMAT = "&eping: &o<ping>";
    public static String SCOREBOARD_TITLE = " ";
    public static int SCOREBOARD_SCORE = 0;

    @Listener
    public void onPreInit(GamePreInitializationEvent event) {
        INSTANCE = this;

        loadConf();
    }

    @Listener
    public void onGameStarting(GameStartingServerEvent event) {
        Sponge.getCommandManager().register(this, new PingCommand(), "ping");

        // TODO DEBUG
        Sponge.getCommandManager().register(this, CommandSpec.builder().executor((source, args) -> {
            ((Player) source).getScoreboard().getObjective(DisplaySlots.SIDEBAR).ifPresent(objective -> objective.getOrCreateScore(Text.of(" Salut ")).setScore(500));
            return CommandResult.empty();
        }).build(), "sbadd");

        startRefreshTask();
    }

    @Listener
    public void onReload(GameReloadEvent event) {
        reload();
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join event, @First Player player) {
        TAB_LAST_PING.put(player.getName(), player.getConnection().getLatency());
        SB_LAST_PING.put(player.getName(), player.getConnection().getLatency());
    }

    @Listener
    public void onLeft(ClientConnectionEvent.Disconnect event, @First Player player) {
        TAB_LAST_PING.remove(player.getName());
        SB_LAST_PING.remove(player.getName());

        player.getScoreboard().getObjective(OBJECTIVE_NAME).ifPresent(objective -> {
            player.getScoreboard().removeObjective(objective);
        });
    }

    public static void loadConf() {
        try {
            Sponge.getAssetManager().getAsset(INSTANCE.plugin, "eliping.conf").ifPresent(asset -> {
                try {
                    asset.copyToDirectory(INSTANCE.configDir.toPath());
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });

            Sponge.getAssetManager().getAsset(INSTANCE.plugin, "lang/en_US.conf").ifPresent(asset -> {
                try {
                    asset.copyToDirectory(new File(INSTANCE.configDir, "lang").toPath());
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });

            Sponge.getAssetManager().getAsset(INSTANCE.plugin, "lang/fr_FR.conf").ifPresent(asset -> {
                try {
                    asset.copyToDirectory(new File(INSTANCE.configDir, "lang").toPath());
                } catch(IOException e) {
                    e.printStackTrace();
                }
            });

            ConfigurationLoader<CommentedConfigurationNode> confLoader = HoconConfigurationLoader.builder().setPath(INSTANCE.defaultConf).build();
            INSTANCE.confRoot = confLoader.load();

            REFRESH_RATE = INSTANCE.confRoot.getNode("refresh-rate").getInt(REFRESH_RATE);
            LANGUAGE = INSTANCE.confRoot.getNode("language").getString(LANGUAGE);
            TAB_COMPLETE = INSTANCE.confRoot.getNode("tab-complete").getBoolean(TAB_COMPLETE);
            TAB_FORMAT = INSTANCE.confRoot.getNode("tablist-format").getString(TAB_FORMAT);
            SCOREBOARD_ENABLED = INSTANCE.confRoot.getNode("scoreboard", "enabled").getBoolean(SCOREBOARD_ENABLED);
            OBJECTIVE_NAME = INSTANCE.confRoot.getNode("scoreboard", "objective-name").getString(OBJECTIVE_NAME);
            SCOREBOARD_FORMAT = INSTANCE.confRoot.getNode("scoreboard", "format").getString(SCOREBOARD_FORMAT);
            SCOREBOARD_SCORE = INSTANCE.confRoot.getNode("scoreboard", "score").getInt(SCOREBOARD_SCORE);
            SCOREBOARD_TITLE = INSTANCE.confRoot.getNode("scoreboard", "title").getString(SCOREBOARD_TITLE);

            new File(INSTANCE.configDir, "lang").mkdir();

            Messages.init();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void startRefreshTask() {
        REFRESH_TASK = Task.builder()
                .name("eliping-tablist_refresh")
                .intervalTicks(REFRESH_RATE)
                .execute(() -> {
                    refreshTabList();

                    if(SCOREBOARD_ENABLED)
                        refreshScoreboard();
                })
                .submit(INSTANCE);
    }

    static void reload() {
        REFRESH_TASK.cancel();

        Sponge.getServer().getOnlinePlayers().forEach(player -> {

            player.getTabList().getEntry(player.getUniqueId()).ifPresent(entry -> {
                entry.getDisplayName().ifPresent(displayname -> {
                    entry.setDisplayName(getTablistWithoutPing(player).get());
                });
            });

            TAB_LAST_PING.remove(player.getName());
            TAB_LAST_PING.put(player.getName(), player.getConnection().getLatency());

            if(player.getScoreboard().getObjective(DisplaySlots.SIDEBAR).isPresent()) {
                Objective objective = player.getScoreboard().getObjective(DisplaySlots.SIDEBAR).get();
                if(objective.getScores().size() == 1) {
                    player.getScoreboard().removeObjective(objective);
                    player.getScoreboard().updateDisplaySlot(null, DisplaySlots.SIDEBAR);
                } else {
                    objective.getScores().values().forEach(score -> {
                        if(((LiteralText)score.getName()).getContent().equals(SCOREBOARD_FORMAT.replaceAll("<ping>", String.valueOf(SB_LAST_PING.get(player.getName())))))
                            objective.removeScore(score);
                    });
                    //objective.removeScore(Text.of(SCOREBOARD_FORMAT.replaceAll("<ping>", String.valueOf(SB_LAST_PING.get(player.getName())))));
                }
            }

            SB_LAST_PING.remove(player.getName());
            SB_LAST_PING.put(player.getName(), player.getConnection().getLatency());
        });

        loadConf();

        startRefreshTask();
    }

    public static Logger getLogger() {
        return INSTANCE.logger;
    }

    public static EliPing getInstance() {
        return INSTANCE;
    }

    public static Path getDefaultConf() {
        return INSTANCE.defaultConf;
    }

    public static ConfigurationNode getConfRoot() {
        return INSTANCE.confRoot;
    }

    public static File getConfDir() {
        return INSTANCE.configDir;
    }

    /**
     * Refresh the tab of the given player
     *
     * @param player the player
     */
    public static void refreshTabList(Player player) {
        Optional<Text> maybeDisplayname = getTablistWithoutPing(player);
        TAB_LAST_PING.put(player.getName(), player.getConnection().getLatency());
        if(maybeDisplayname.isPresent()) {
            Text displayname = maybeDisplayname.get();
            player.getTabList().getEntry(player.getUniqueId()).ifPresent(entry -> entry.setDisplayName(Text.of(displayname, getTabPingFormat(player))));
        } else {
            player.getTabList().getEntry(player.getUniqueId()).ifPresent(entry -> entry.setDisplayName(Text.of(player.getName(), getTabPingFormat(player))));
        }
    }

    /**
     * Refresh the tab of all connected players
     */
    public static void refreshTabList() {
        Sponge.getServer().getOnlinePlayers().forEach(EliPing::refreshTabList);
    }

    /**
     * Returns the {@link TabListEntry} without the ping
     *
     * @param player the player
     * @return an {@link Optional#empty()} if the entry or the displayname is not present,
     *         otherwise a {@link Text} without the ping
     */
    public static Optional<Text> getTablistWithoutPing(Player player) {
        Optional<TabListEntry> maybeEntry = player.getTabList().getEntry(player.getUniqueId());
        if(maybeEntry.isPresent()) {
            TabListEntry entry = maybeEntry.get();
            if(entry.getDisplayName().isPresent()) {
                LiteralText display = ((LiteralText) entry.getDisplayName().get());
                List<Text> result = new ArrayList<>();

                result.add(Text.of(display.getColor(), display.getContent()));

                final int[] i = {0};
                display.getChildren().forEach(children -> {
                    if(!((LiteralText) children).getContent().equals(getTabPingFormat(player)) && !children.isEmpty()) {
                        System.out.println("adding");
                        result.add(Text.of(children.getColor().equals(TextColors.NONE) ? result.get(i[0]).getColor() : children.getColor(), children));
                        i[0]++;
                        System.out.println(i[0]);
                    } else {
                        System.out.println("element is empty");
                    }
                });

                if(result.size() == 1) {
                    System.out.println("YAY");
                    return Optional.of(display);
                }

                Text finalResult = Text.of();
                for(Text element : result) {
                    if(!element.isEmpty())
                        finalResult = finalResult.concat(Text.of(element));
                    else
                        System.out.println("element is empty");
                }

                return Optional.of(finalResult);
            }
        }

        return Optional.empty();
    }

    /**
     * Refreshed the scoreboard of all connected players
     */
    public static void refreshScoreboard() {
        Sponge.getServer().getOnlinePlayers().forEach(EliPing::refreshScoreboard);
    }

    /**
     * Refreshes the scoreboard of the given player
     *
     * @param player the player
     */
    public static void refreshScoreboard(Player player) {
        if(!SCOREBOARD_ENABLED)
            return;

        // TODO redo this method

        Text oldScoreText = Text.of(applyColorCodes(SCOREBOARD_FORMAT.replaceAll("<ping>", String.valueOf(SB_LAST_PING.get(player.getName())))));
        SB_LAST_PING.put(player.getName(), player.getConnection().getLatency());
        Text newScoreText = Text.of(applyColorCodes(SCOREBOARD_FORMAT.replaceAll("<ping>", String.valueOf(SB_LAST_PING.get(player.getName())))));

        Optional<Objective> maybeObjective = player.getScoreboard().getObjective(DisplaySlots.SIDEBAR);
        if(maybeObjective.isPresent()) {
            Objective objective = maybeObjective.get();

            objective.removeScore(oldScoreText);

            Score score = objective.getOrCreateScore(newScoreText);
            score.setScore(SCOREBOARD_SCORE);

            if(!objective.hasScore(score.getName()))
                objective.addScore(score);
        } else {
            Objective objective = Objective.builder()
                    .name(OBJECTIVE_NAME)
                    .displayName(Text.of(applyColorCodes(SCOREBOARD_TITLE)))
                    .criterion(Criteria.DUMMY)
                    .objectiveDisplayMode(ObjectiveDisplayModes.INTEGER)
                    .build();

            Score score = objective.getOrCreateScore(newScoreText);
            score.setScore(SCOREBOARD_SCORE);

            if(!objective.hasScore(score.getName()))
                objective.addScore(score);

            player.getScoreboard().addObjective(objective);

            player.getScoreboard().updateDisplaySlot(objective, DisplaySlots.SIDEBAR);
        }
    }

    private static String getTabPingFormat(Player player) {
        return applyColorCodes(TAB_FORMAT
                .replaceAll("<tab>", "")
                .replaceAll("<ping>", String.valueOf(TAB_LAST_PING.get(player.getName()))));
    }
}
