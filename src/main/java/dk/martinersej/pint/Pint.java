package dk.martinersej.pint;

import dk.martinersej.pint.simplecommand.*;
import dk.martinersej.pint.game.command.GameCommand;
import dk.martinersej.pint.map.command.MapCommand;
import dk.martinersej.pint.vote.command.VoteCommand;
import dk.martinersej.pint.game.GameHandler;
import dk.martinersej.pint.listener.ListenerHandler;
import dk.martinersej.pint.map.MapHandler;
import dk.martinersej.pint.utils.gui.GuiListeners;
import dk.martinersej.pint.vote.VoteHandler;
import dk.martinersej.pint.vote.interaction.VoteListener;
import dk.martinersej.pint.warp.WarpHandler;
import dk.martinersej.pint.warp.command.WarpCommand;
import lombok.Getter;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Pint extends JavaPlugin {

    @Getter
    private static Pint instance;
    @Getter
    private static ScoreboardLibrary scoreboardLibrary;
    private GameHandler gameHandler;
    private MapHandler mapHandler;
    private VoteHandler voteHandler;

    @Override
    public void onEnable() {
        instance = this;

        setupScoreboardLibrary();
        setupHandlers();
        setupListeners();
        setupCommands();
        setupTasks();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getGameMode().equals(GameMode.CREATIVE)) {
                    Pint.getInstance().getVoteHandler().getVoteUtil().setToVoteGamemode(player);
                }
            }
        }, 1L);
    }

    @Override
    public void onDisable() {
        scoreboardLibrary.close();
        stopTasks();
    }

    private void stopTasks() {
        Bukkit.getServer().getScheduler().cancelTasks(instance);
    }

    private static void setupScoreboardLibrary() {
        try {
            scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(instance);
        } catch (NoPacketAdapterAvailableException e) {
            // If no packet adapter was found, you can fallback to the no-op implementation:
            scoreboardLibrary = new NoopScoreboardLibrary();
            Bukkit.getLogger().warning("No scoreboard packet adapter available!");
        }
    }

    private void setupHandlers() {
        //setup handlers
        mapHandler = new MapHandler(); // needs to be loaded before all other handlers
        voteHandler = new VoteHandler();
        gameHandler = new GameHandler();
    }

    private void setupCommands() {

        //setup commands
        this.getServer().getPluginCommand("map").setExecutor(new MapCommand(this));
        this.getServer().getPluginCommand("game").setExecutor(new GameCommand(this));
        this.getServer().getPluginCommand("vote").setExecutor(new VoteCommand(this));
        this.getServer().getPluginCommand("warp").setExecutor(new WarpCommand(this, new WarpHandler()));

        // simple commands
        this.getServer().getPluginCommand("spawn").setExecutor(new SpawnCommand());
        this.getServer().getPluginCommand("test").setExecutor(new TestCommand());
        this.getServer().getPluginCommand("showregions").setExecutor(new ShowRegionsCommand());
        this.getServer().getPluginCommand("join").setExecutor(new JoinCommand());
    }

    private void setupListeners() {
        //setup listeners
        new ListenerHandler();
        new VoteListener();
        new GuiListeners();
    }

    private void setupTasks() {
        //setup tasks
    }
}
