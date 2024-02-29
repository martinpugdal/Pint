package dk.martinersej.pint.game.games.simonsays.games.participation;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.Statistic;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;

public class JumpGame extends SimonGame {

    public JumpGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 5; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Hop";
    }


    @Override
    public void startGame() {
    }

    @Override
    public void stopGame() {
    }

    @EventHandler
    public void onPlayerStatisticIncrement(PlayerStatisticIncrementEvent event) {
        if (!getSimonSaysGame().isPlayerInGame(event.getPlayer())) return;
        if (event.getStatistic() == Statistic.JUMP) {
            getSimonSaysGame().finishedTask(event.getPlayer());
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PARTICIPATION;
    }
}