package dk.martinersej.pint.game.games.tnttag;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class TNTRound {

    private final TntTagGame game;
    private final int roundNumber;
    private final int roundDuration;
    private final Set<Player> players;
    private BukkitRunnable roundTask;

    public TNTRound(TntTagGame game, int roundNumber, int roundDuration, Set<Player> players) {
        this.game = game;
        this.roundNumber = roundNumber;
        this.roundDuration = roundDuration;
        this.players = players;

        this.game.getScoreboard().line(1, Component.text("§7Round: " + roundNumber));
    }

    public void start() {
        int tntPlayersAmount = (int) Math.ceil((double) getPlayers().size() / 8);

        // find the tnt players by getPlayers()
        List<Player> tntPlayers = new ArrayList<>();

        for (int i = 0; i < tntPlayersAmount; i++) {
            Player player = getRandomPlayer();
            if (!tntPlayers.contains(player)) {
                tntPlayers.add(player);
                game.tagPlayer(null, player);
            } else {
                i--;
            }
        }

        game.getScoreboard().line(2, Component.text("§7Duration: " + roundDuration));
        game.getScoreboard().line(3, Component.text("§7Players: " + players.size()));

        roundTask = new BukkitRunnable() {
            int timeLeft = roundDuration;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    for (Player player : game.getTntPlayers()) {
                        game.blowUpTntPlayer(player);
                    }
                    game.endRound();
                    cancel();
                    return;
                }

                game.getScoreboard().line(4, Component.text("§7Time left: " + timeLeft));
                timeLeft--;
            }
        };
        roundTask.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 20);
    }

    public Player getRandomPlayer() {
        List<Player> players = new ArrayList<>(getPlayers());
        return players.get((int) (Math.random() * players.size()));
    }
}
