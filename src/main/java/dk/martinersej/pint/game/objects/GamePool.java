package dk.martinersej.pint.game.objects;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.fusesource.jansi.Ansi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class GamePool {

    private final List<Game> games = new ArrayList<>();
    private final Game[] voteGames = new Game[3];

    public boolean addGame(Game game) {
        if (!games.contains(game)) {
            if (game.getActiveMaps().isEmpty()) {
                Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "Game " + game.getGameInformation().getName() + " has no active maps and will not be added to the pool");
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

    public void shuffleVotePool() {
        Pint.getInstance().getVoteHandler().refreshVotes();
        List<Game> shuffleList = new ArrayList<>(this.games);
        Collections.shuffle(shuffleList);
        voteGames[0] = null;
        voteGames[1] = null;
        voteGames[2] = null;
        for (int i = 0; i < voteGames.length; i++) {
            if (shuffleList.size() <= i) {
                break;
            }
            voteGames[i] = shuffleList.get(i);
        }
    }
}
