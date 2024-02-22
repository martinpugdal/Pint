package dk.martinersej.pint.game.games.simonsays.objects;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class SimonGame implements Listener {

    private final SimonSaysGame simonSaysGame;
    private final List<Player> finishedPlayers = new ArrayList<>();

    public SimonGame(SimonSaysGame simonSaysGame) {
        this.simonSaysGame = simonSaysGame;
    }

    public void start() {
        Pint plugin = Pint.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        finishedPlayers.clear();
        startGame();
        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(plugin, 20 * 10); // 10 seconds
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        stopGame();
        if (simonSaysGame.getCurrentGame() == this) {
            simonSaysGame.setCurrentGame(null);
            simonSaysGame.getPlayedGames().add(this);
            simonSaysGame.nextGame();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        finishedPlayers.remove(event.getPlayer());
    }

    public abstract void startGame();

    public abstract void stopGame();

    public abstract ScoringType getScoringType();
}
