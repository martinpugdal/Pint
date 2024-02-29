package dk.martinersej.pint.game.games.simonsays.games.placement;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TypeInChatGame extends SimonGame {

    public TypeInChatGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 10; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Skriv først i chatten";
    }


    @Override
    public void startGame() {
    }

    @Override
    public void stopGame() {
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (getSimonSaysGame().isPlayerInGame(event.getPlayer()) && !getFinishedPlayers().contains(event.getPlayer())) {
            event.setCancelled(true);
            getSimonSaysGame().finishedTask(event.getPlayer());
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}