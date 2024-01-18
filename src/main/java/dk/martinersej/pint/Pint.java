package dk.martinersej.pint;

import dk.martinersej.pint.command.MapCommand;
import dk.martinersej.pint.game.GameHandler;
import dk.martinersej.pint.listener.ListenerHandler;
import dk.martinersej.pint.map.MapHandler;
import dk.martinersej.pint.vote.VoteHandler;
import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPCRegistry;
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
        gameHandler = GameHandler.create(this);
        VoteHandler voteHandler = new VoteHandler();
        mapHandler = new MapHandler();
    }

    private void setupCommands() {
        //setup commands
        this.getServer().getPluginCommand("mapcreator").setExecutor(new MapCommand(this));
    }

    private void setupListeners() {
        //setup listeners
        new ListenerHandler(this);
    }

    private void setupTasks() {
        //setup tasks
    }
}
