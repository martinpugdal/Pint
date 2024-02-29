package dk.martinersej.pint.game.games.simonsays.games.placement;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class LookDirectionGame extends SimonGame {

    enum Direction {
        UP("op"),
        DOWN("ned"),
        LEFT("til venstre"),
        RIGHT("til højre");

        private final String name;

        Direction(String name) {
            this.name = name;
        }
    }

    private Direction lookDirection;

    public LookDirectionGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 8; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Kig " + lookDirection.name;
    }

    private void randomizeDirection() {
        int random = (int) (Math.random() * 4);
        lookDirection = Direction.values()[random];
    }


    @Override
    public void startGame() {
        for (Player player : getSimonSaysGame().getPlayers()) {
            playerLocation.put(player, player.getLocation().getYaw() + 180);
        }
        randomizeDirection();
    }

    @Override
    public void stopGame() {
        playerLocation.clear();
        lookDirection = null;
    }

    private final Map<Player, Float> playerLocation = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!getSimonSaysGame().isPlayerInGame(event.getPlayer())) return;
        switch (lookDirection) {
            case UP:
                // max UP is -90 degrees pitch and normal is 0
                // if player's pitch is between -45 and -90, they have finished
                if (event.getFrom().getPitch() > event.getTo().getPitch()) {
                    if (event.getTo().getPitch() < -45) {
                        getSimonSaysGame().finishedTask(event.getPlayer());
                    }
                }
                break;
            case DOWN:
                // max DOWN is 90 degrees pitch and normal is 0
                // if player's pitch is between 45 and 90, they have finished
                if (event.getFrom().getPitch() < event.getTo().getPitch()) {
                    if (event.getTo().getPitch() > 45) {
                        getSimonSaysGame().finishedTask(event.getPlayer());
                    }
                }
                break;
            case LEFT:
                // use player's location from the map to compare the yaw
                // they need to have 10 degrees to the left to finish
                // i added 180 to the yaw to make it easier to compare the yaw
                float startYawL  = playerLocation.get(event.getPlayer());
                float currentYawL = event.getTo().getYaw() + 180;

                if (startYawL - currentYawL >= 10) {
                    getSimonSaysGame().finishedTask(event.getPlayer());
                }
                break;
            case RIGHT:
                // use player's location from the map to compare the yaw
                // they need to have 10 degrees to the right to finish
                // i added 180 to the yaw to make it easier to compare the yaw
                float startYawR  = playerLocation.get(event.getPlayer());
                float currentYawR = event.getTo().getYaw() + 180;

                if (currentYawR - startYawR >= 10) {
                    getSimonSaysGame().finishedTask(event.getPlayer());
                }
                break;
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}