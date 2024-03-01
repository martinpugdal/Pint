package dk.martinersej.pint.game.games.simonsays.games.placement;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class Rotate360Game extends SimonGame {

    public Rotate360Game(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 15; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Drej hele vejen rundt";
    }

    @Override
    public void startGame() {
        for (Player player : getSimonSaysGame().getPlayers()) {
            playerStartYaw.put(player, player.getLocation().getYaw() + 360);
            playerDirection.put(player, Direction.RIGHT);
            playerHalfway.put(player, false);
        }
    }

    @Override
    public void stopGame() {
        playerStartYaw.clear();
        playerDirection.clear();
        playerHalfway.clear();
    }

    enum Direction {
        LEFT,
        RIGHT;
    }

    private final Map<Player, Float> playerStartYaw = new HashMap<>();
    private final Map<Player, Direction> playerDirection = new HashMap<>();
    private final Map<Player, Boolean> playerHalfway = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player is in the Simon Says game
        if (!getSimonSaysGame().isPlayerInGame(player)) return;

        float startYaw = playerStartYaw.get(player);
        float currentYaw = event.getPlayer().getLocation().getYaw() + 360;

        if (currentYaw > startYaw) {
            if (playerDirection.get(player) == Direction.LEFT) {
                playerDirection.put(player, Direction.RIGHT);
                playerStartYaw.put(player, currentYaw);
                playerHalfway.put(player, false);
            } else {
                if (currentYaw - startYaw >= 177) {
                    boolean halfway = playerHalfway.get(player);
                    if (halfway) {
                        getSimonSaysGame().finishedTask(player);
                    } else {
                        playerStartYaw.put(player, currentYaw);
                        playerHalfway.put(player, true);
                    }
                }
            }
        } else if (currentYaw < startYaw) {
            if (playerDirection.get(player) == Direction.RIGHT) {
                playerDirection.put(player, Direction.LEFT);
                playerStartYaw.put(player, currentYaw);
                playerHalfway.put(player, false);
            } else {
                if (startYaw - currentYaw >= 177) {
                    boolean halfway = playerHalfway.get(player);
                    if (halfway) {
                        getSimonSaysGame().finishedTask(player);
                    } else {
                        playerStartYaw.put(player, currentYaw);
                        playerHalfway.put(player, true);
                    }
                }
            }
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}