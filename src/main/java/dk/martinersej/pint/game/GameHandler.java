package dk.martinersej.pint.game;

import dk.martinersej.pint.game.games.tnttag.TntTagGame;
import dk.martinersej.pint.game.objects.GamePool;
import dk.martinersej.pint.map.ServerWorld;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class GameHandler {

    @Getter
    private final ServerWorld serverWorld;
    @Getter
    private final GamePool gamePool;
    @Getter
    private final Game currentGame = null;
    @Getter
    private final List<Game> games = new ArrayList<>();

    public GameHandler(ServerWorld serverWorld) {
        this.gamePool = new GamePool();
        this.serverWorld = serverWorld;
        initGames();
    }

    private void initGames() {
        Game tntTagGame = new TntTagGame();
        addGame(tntTagGame);
        addGameToPool(tntTagGame);
    }

    private void addGame(Game game) {
        games.add(game);
    }

    private void removeGame(Game game) {
        games.remove(game);
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

    public Game getGame(String name) {
        for (Game game : games) {
            if (game.getGameInformation().getName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }
}
