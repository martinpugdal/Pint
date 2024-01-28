package dk.martinersej.pint.game;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.tnttag.TntTagGame;
import dk.martinersej.pint.game.objects.GamePool;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameHandler {

    private final GamePool gamePool;
    private final Game currentGame = null;
    private final List<Game> games = new ArrayList<>();

    public GameHandler() {
        this.gamePool = new GamePool();

        Pint.getInstance().getMapHandler().loadMaps();
        Pint.getInstance().getVoteHandler().loadVoteMap();
        if (Pint.getInstance().getVoteHandler().getVoteMap().isPresent()) {
            Pint.getInstance().getVoteHandler().getVoteMap().pasteSchematic();
        } else {
            Bukkit.getLogger().warning("Vote map is not present and will not be pasted");
        }

        initGames();
    }

    public void initGames() {
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
