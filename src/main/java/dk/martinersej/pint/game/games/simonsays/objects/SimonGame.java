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
    private final List<Player> failedPlayers = new ArrayList<>();

    public SimonGame(SimonSaysGame simonSaysGame) {
        this.simonSaysGame = simonSaysGame;
    }

    public void start() {
        Pint plugin = Pint.getInstance();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        finishedPlayers.clear();
        failedPlayers.clear();
        startGame();
        simonSaysGame.say();
        new BukkitRunnable() {
            @Override
            public void run() {
                stop();
            }
        }.runTaskLater(plugin, 20L * getaskDuration()); // 20 * time in seconds
    }

    public abstract String sayText();

    public int getaskDuration() {
        return 10;
    }

    public void stop() {
        HandlerList.unregisterAll(this);
        stopGame();
        if (simonSaysGame.getCurrentGame() == this) {
            simonSaysGame.setCurrentGame(null);
            simonSaysGame.getPlayedGames().add(this);

            if (simonSaysGame.getGameAmount() > simonSaysGame.getPlayedGames().size()) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        simonSaysGame.nextGame();
                    }
                }.runTaskLater(Pint.getInstance(), (long) (20 * 1.5));
            } else {
                simonSaysGame.win(simonSaysGame.getPlayers());
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        finishedPlayers.remove(event.getPlayer());
        failedPlayers.remove(event.getPlayer());
    }

    public abstract void startGame();

    public abstract void stopGame();

    public abstract ScoringType getScoringType();
}
