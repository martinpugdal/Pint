package dk.martinersej.pint.game.objects;

import dk.martinersej.pint.game.Game;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.List;

public class GamePool {

    @Getter
    private final List<Game> games = new ArrayList<>();

    public boolean addGame(Game game) {
        if (!games.contains(game)) {
            if (game.getGameMaps().isEmpty()) {
                Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "Game " + game.getGameInformation().getName() + " has no maps and will not be added to the pool");
                return false;
            } else {
                games.add(game);
            }
        } else {
            throw new IllegalStateException("Game already in pool");
        }
        return true;
    }

    public void removeGame(Game game) {
        if (!games.contains(game)) {
            throw new IllegalStateException("Game not in pool");
        }
        games.remove(game);
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
