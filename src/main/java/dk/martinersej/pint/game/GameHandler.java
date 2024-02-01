package dk.martinersej.pint.game;

import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.tnttag.TntTagGame;
import dk.martinersej.pint.game.objects.GamePool;
import dk.martinersej.pint.map.maps.VoteMap;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameHandler {

    private final GamePool gamePool;
    private final List<Game> games = new ArrayList<>();
    @Setter
    private Game currentGame = null;

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
        voteMap.setCorner1(new org.bukkit.util.Vector(0, 0, 0));
        voteMap.setCorner2(new org.bukkit.util.Vector(0, 0, 0));
        voteMap.setZeroLocation(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getZeroLocation());
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

    public boolean startGame(Game game) {
        if (game != null) {
            game.start();
            return true;
        }
        return false;
    }

    public boolean stopGame(Game game) {
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
