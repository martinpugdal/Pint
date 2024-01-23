package dk.martinersej.pint;

import dk.martinersej.pint.command.GameCommand;
import dk.martinersej.pint.command.MapCommand;
import dk.martinersej.pint.game.GameHandler;
import dk.martinersej.pint.listener.ListenerHandler;
import dk.martinersej.pint.map.ServerWorld;
import dk.martinersej.pint.map.MapHandler;
import dk.martinersej.pint.vote.VoteHandler;
import dk.martinersej.pint.vote.interaction.VoteListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Pint extends JavaPlugin {

    @Getter
    private static Pint instance;
    private GameHandler gameHandler;
    private MapHandler mapHandler;

    @Override
    public void onEnable() {
        instance = this;

        setupHandlers();
        setupListeners();
        setupCommands();
        setupTasks();
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
        ServerWorld serverWorld = new ServerWorld();
        mapHandler = new MapHandler();
        gameHandler = new GameHandler(this, serverWorld);
        VoteHandler voteHandler = new VoteHandler();
    }

    private void setupCommands() {
        //setup commands
        this.getServer().getPluginCommand("map").setExecutor(new MapCommand(this));
        this.getServer().getPluginCommand("game").setExecutor(new GameCommand(this));
    }

    private void setupListeners() {
        //setup listeners
        new ListenerHandler();
        new VoteListener();
    }

    private void setupTasks() {
        //setup tasks
    }
}
