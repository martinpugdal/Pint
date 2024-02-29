package dk.martinersej.pint.game.games.simonsays.games.participation;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class HitAPlayerGame extends SimonGame {

    public HitAPlayerGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 10; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Slå en spiller";
    }


    @Override
    public void startGame() {
    }

    @Override
    public void stopGame() {
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        if (attacker == null || victim == null) return;
        if (getSimonSaysGame().isPlayerInGame(attacker) && getSimonSaysGame().isPlayerInGame(victim)) {
            event.setCancelled(true);
            if (attacker == victim) return;
            getSimonSaysGame().finishedTask(attacker);
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PARTICIPATION;
    }
}