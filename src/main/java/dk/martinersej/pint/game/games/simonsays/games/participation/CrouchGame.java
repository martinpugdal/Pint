package dk.martinersej.pint.game.games.simonsays.games.participation;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class CrouchGame extends SimonGame {
    public CrouchGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 5; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Crouch (shift)";
    }


    @Override
    public void startGame() {
    }

    @Override
    public void stopGame() {
    }

    @EventHandler
    public void onPlayerCrouch(PlayerToggleSneakEvent event) {
        if (!getSimonSaysGame().isPlayerInGame(event.getPlayer())) return;
        if (event.isSneaking()) {
            getSimonSaysGame().finishedTask(event.getPlayer());
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PARTICIPATION;
    }
}