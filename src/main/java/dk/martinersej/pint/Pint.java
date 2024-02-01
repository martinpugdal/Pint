package dk.martinersej.pint;

import dk.martinersej.pint.command.*;
import dk.martinersej.pint.game.GameHandler;
import dk.martinersej.pint.listener.ListenerHandler;
import dk.martinersej.pint.map.MapHandler;
import dk.martinersej.pint.utils.gui.GuiListeners;
import dk.martinersej.pint.vote.VoteHandler;
import dk.martinersej.pint.vote.interaction.VoteListener;
import dk.martinersej.pint.warp.WarpHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Pint extends JavaPlugin {

    @Getter
    private static Pint instance;
    private GameHandler gameHandler;
    private MapHandler mapHandler;
    private VoteHandler voteHandler;

    @Override
    public void onEnable() {
        instance = this;

        setupHandlers();
        setupListeners();
        setupCommands();
        setupTasks();

        Bukkit.getScheduler().runTaskLater(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Pint.getInstance().getVoteHandler().getVoteUtil().setToVoteGamemode(player);
            }
        }, 1);

    }

    @Override
    public void onDisable() {

        stopTasks();
    }

    private void stopTasks() {
        instance.getServer().getScheduler().cancelTasks(instance);
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
