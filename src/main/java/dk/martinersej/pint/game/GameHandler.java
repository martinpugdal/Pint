package dk.martinersej.pint.game;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.allekyllingerkomhjem.AlleKyllingerKomHjemGame;
import dk.martinersej.pint.game.games.shufflecolor.ShuffleColorGame;
import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.tnttag.TntTagGame;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GamePool;
import dk.martinersej.pint.map.objects.maps.VoteMap;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

@Getter
public class GameHandler {

    private final GamePool gamePool;
    private final Set<Game> games = new HashSet<>();
    @Setter
    private Game currentGame = null;
    @Setter
    private boolean gameRunning = false;

    public GameHandler() {
        this.gamePool = new GamePool();

        Pint.getInstance().getMapHandler().loadMaps();
        Pint.getInstance().getVoteHandler().loadVoteMap();
        if (Pint.getInstance().getVoteHandler().getVoteMap().isPresent()) {
            Pint.getInstance().getVoteHandler().getVoteMap().pasteSchematic();
        } else {
            Bukkit.getLogger().warning("Vote map is not present and will paste default vote map");
            pasteDefaultVoteMap();
        }

        initGames();
    }

    private void pasteDefaultVoteMap() {
        FastAsyncWorldEditUtil.runSession(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld(),
                session -> {
                    try {
                        session.setBlock(new Vector(0, 0, 0), new BaseBlock(7));
                    } catch (MaxChangedBlocksException e) {
                        e.printStackTrace();
                    }
                }
        );
        VoteMap voteMap = Pint.getInstance().getVoteHandler().getVoteMap();
        org.bukkit.util.Vector corner = new org.bukkit.util.Vector(0, 0, 0);
        voteMap.setCorner1(corner);
        voteMap.setCorner2(corner);
        voteMap.setZeroLocation(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation());
    }

    public void initGames() {
        /*
         * Don't rearrange the order of the games, if you do, you need to update maps.yml for the correct game id.
         */
        Game tntTagGame = new TntTagGame(); // id 1
        addGame(tntTagGame);
        addGameToPool(tntTagGame);

        Game shuffleGame = new ShuffleColorGame(); // id 2
        addGame(shuffleGame);
        addGameToPool(shuffleGame);

        Game simonSaysGame = new SimonSaysGame(); // id 3
        addGame(simonSaysGame);
        addGameToPool(simonSaysGame);

        Game alleKyllingerKomHjemGame = new AlleKyllingerKomHjemGame(); // id 4
        addGame(alleKyllingerKomHjemGame);
//        addGameToPool(alleKyllingerKomHjemGame);
        

        gamePool.shuffleVotePool();
    }

    private void addGame(Game game) {
        games.add(game);
    }

    private void removeGame(Game game) {
        games.remove(game);
    }

    public boolean addGameToPool(Game game) {
        return gamePool.addGame(game);
    }

    public void removeGameFromPool(Game game) {
        gamePool.removeGame(game);
    }

    public void setupGame(Game game) {
        if (game != null) {
            currentGame = game;
            game.setup();
        }
    }

    public void startGame(Game game) {
        if (game != null && game.equals(currentGame)) {
            game.start();
        }
    }

    public void stopGame(Game game) {
        if (game != null && game.equals(currentGame)) {
            game.stop();
        } else {
            Bukkit.getLogger().warning("SimonGame is not running");
        }
    }

    public Game getGame(int id) {
        for (Game game : games) {
            if (game.getId() == id) {
                return game;
            }
        }
        return null;
    }

    public boolean isPlayerInGame(Player player) {
        if (currentGame == null) {
            return false;
        }
        return currentGame.isPlayerInGame(player);
    }
}
