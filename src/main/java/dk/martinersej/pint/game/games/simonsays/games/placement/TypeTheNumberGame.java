package dk.martinersej.pint.game.games.simonsays.games.placement;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TypeTheNumberGame extends SimonGame {

    public TypeTheNumberGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    private String numberToType;

    @Override
    public int getaskDuration() {
        return 10; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Skriv tallet " + numberToType;
    }

    private void randomizeNumber(int min, int max) {
        numberToType = String.valueOf((int) (Math.random() * (max - min + 1) + min));
    }



    @Override
    public void startGame() {
        randomizeNumber(1, 100);
    }

    @Override
    public void stopGame() {
        numberToType = null;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (getSimonSaysGame().isPlayerInGame(event.getPlayer()) && !getFinishedPlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
            if (event.getMessage().equals(numberToType)) {
                getSimonSaysGame().finishedTask(event.getPlayer());
            }
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}