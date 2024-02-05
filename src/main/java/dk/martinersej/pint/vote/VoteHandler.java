package dk.martinersej.pint.vote;

import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.map.maps.VoteMap;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class VoteHandler {

    private final Map<UUID, Game> votes = new HashMap<>();
    private final Map<Game, Integer> gameVotes = new HashMap<>();
    private final VoteUtil voteUtil;
    private final VoteMap voteMap;
    private BukkitRunnable voteTimer = null;

    public VoteHandler() {
        voteUtil = new VoteUtil();
        voteMap = new VoteMap();
    }

    public void startVoteTimer() {
        voteTimer = new BukkitRunnable() {

            private int cooldown = 60;

            @Override
            public void run() {
                voteUtil.getVoteComponent().tick();

                // timer is up, end vote
                if (cooldown <= 0) {
                    cancel();
                }

                cooldown--;
            }
        };
        voteTimer.runTaskTimer(JavaPlugin.getProvidingPlugin(VoteHandler.class), 0, 20);
    }

    public void setVote(UUID uuid, Game game) {
        Game putGame = votes.put(uuid, game);
        if (game == null && putGame != null) {
            gameVotes.put(putGame, gameVotes.get(putGame) - 1);
        } else if (putGame == null) {
            gameVotes.putIfAbsent(game, 0);
            gameVotes.put(game, gameVotes.get(game) + 1);
        } else {
            if (putGame.equals(game)) {
                return;
            }
            gameVotes.putIfAbsent(game, 0);

            gameVotes.put(putGame, gameVotes.get(putGame) - 1);
            gameVotes.put(game, gameVotes.get(game) + 1);
        }
    }

    public Game getVote(UUID uuid) {
        return votes.get(uuid);
    }

    public int gameVotes(Game game) {
        return gameVotes.get(game) == null ? 0 : gameVotes.get(game);
    }

    public void refreshVotes() {
        votes.clear();
        gameVotes.clear();
    }

    public void loadVoteMap() {
        getVoteMap().load();
    }
}
