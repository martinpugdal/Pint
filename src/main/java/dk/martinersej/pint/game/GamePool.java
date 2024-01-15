package dk.martinersej.pint.game;

import dk.martinersej.pint.exception.pool.PoolContainsGameException;
import dk.martinersej.pint.exception.pool.PoolDoesNotContainGameException;

import java.util.ArrayList;
import java.util.List;

public class GamePool {

    private final List<Game> games = new ArrayList<>();

    public void addGame(Game game) {
        if (!games.contains(game)) {
            games.add(game);
        } else {
            throw new PoolContainsGameException("Game already in pool");
        }
    }

    public void removeGame(Game game) {
        if (!games.contains(game)) {
            throw new PoolDoesNotContainGameException("Game not in pool");
        }
        games.remove(game);
    }

    public List<Game> getGames() {
        return games;
    }

    public Game getGame(String name) {
        for (Game game : games) {
            if (game.getClass().getSimpleName().equalsIgnoreCase(name)) {
                return game;
            }
        }
        return null;
    }
}
