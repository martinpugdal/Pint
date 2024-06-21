package dk.martinersej.pint.game.objects;

import dk.martinersej.pint.Pint;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.fusesource.jansi.Ansi;

import java.util.*;

@Getter
public class GamePool {

    private final Set<Game> games = new HashSet<>();
    private final Game[] voteGames = new Game[3];

    public boolean addGame(Game game) {
        if (!games.contains(game)) {
            if (game.getActiveMaps().isEmpty()) {
                Bukkit.getLogger().warning(Ansi.ansi().fg(Ansi.Color.RED).toString() + "SimonGame " + game.getGameInformation().getName() + " has no active maps and will not be added to the pool" + Ansi.ansi().fg(Ansi.Color.WHITE).toString());
                return false;
            } else {
                games.add(game);
            }
        } else {
            throw new IllegalStateException("SimonGame already in pool");
        }
        return true;
    }

    public void removeGame(Game game) {
        if (!games.contains(game)) {
            throw new IllegalStateException("SimonGame not in pool");
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

    public Game getGame(Game game) {
        for (Game g : games) {
            if (g.equals(game)) {
                return g;
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

    public List<Game> getVoteGamesWithoutNull() {
        List<Game> games = new ArrayList<>();
        for (Game game : voteGames) {
            if (game != null) {
                games.add(game);
            }
        }
        return games;
    }
}
