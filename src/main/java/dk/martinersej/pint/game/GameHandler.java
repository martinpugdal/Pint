package dk.martinersej.pint.game;

import dk.martinersej.pint.game.objects.GamePool;
import org.bukkit.plugin.java.JavaPlugin;

public class GameHandler {

    private static GameHandler gameHandler;
    private final JavaPlugin plugin;
    private final GamePool gamePool;

    public GameHandler(JavaPlugin plugin) {
        this.plugin = plugin;
        this.gamePool = new GamePool();
    }

    public static GameHandler create(JavaPlugin javaPlugin) {
        //singleton pattern
        if (gameHandler == null) {
            gameHandler = new GameHandler(javaPlugin);
        }
        return gameHandler;
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
