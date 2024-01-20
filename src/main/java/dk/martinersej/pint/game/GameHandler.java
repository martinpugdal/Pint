package dk.martinersej.pint.game;

import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.game.objects.GamePool;
import dk.martinersej.pint.map.ServerWorld;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class GameHandler {

    private final JavaPlugin plugin;
    @Getter
    private final ServerWorld serverWorld;
    private final GamePool gamePool;
    @Getter
    private final Game currentGame = null;

    public GameHandler(JavaPlugin plugin, ServerWorld serverWorld) {
        this.plugin = plugin;
        this.gamePool = new GamePool();
        this.serverWorld = serverWorld;
    }

    public void addGameToPool(Game game) {
        gamePool.addGame(game);
    }

    public void removeGameFromPool(Game game) {
        gamePool.removeGame(game);
    }

    public boolean startGame(String name) {
        Game game = gamePool.getGame(name);
        if (game != null) {
            game.start();
            return true;
        }
        return false;
    }

    public boolean stopGame(String name) {
        Game game = gamePool.getGame(name);
        if (game != null) {
            game.stop();
            return true;
        }
        return false;
    }
}
