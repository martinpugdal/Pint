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
            float startYaw = normalizeYaw(player.getLocation().getYaw());
            playerStartYaw.put(player, startYaw);
            playerProgress.put(player, 0f);
        }
    }

    @Override
    public void stopGame() {
        playerStartYaw.clear();
        playerProgress.clear();
    }

    private final Map<Player, Float> playerStartYaw = new HashMap<>();
    private final Map<Player, Float> playerProgress = new HashMap<>();

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Check if the player is in the Simon Says game
        if (!getSimonSaysGame().isPlayerInGame(player)) return;

        float startYaw = playerStartYaw.get(player);
        float currentYaw = normalizeYaw(player.getLocation().getYaw());
        float progress = playerProgress.get(player);

        float deltaYaw = calculateDeltaYaw(startYaw, currentYaw);
        progress += deltaYaw;
        playerProgress.put(player, progress);

        playerStartYaw.put(player, currentYaw);

        if (progress >= 360) {
            getSimonSaysGame().finishedTask(player);
        }
    }

    private float normalizeYaw(float yaw) {
        yaw = yaw % 360;
        if (yaw < 0) {
            yaw += 360;
        }
        return yaw;
    }

    private float calculateDeltaYaw(float startYaw, float currentYaw) {
        float delta = currentYaw - startYaw;
        if (delta > 180) {
            delta -= 360;
        } else if (delta < -180) {
            delta += 360;
        }
        return Math.abs(delta);
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}