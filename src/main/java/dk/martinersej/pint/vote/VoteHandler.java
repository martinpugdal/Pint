package dk.martinersej.pint.vote;

import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.map.maps.VoteMap;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class VoteHandler {

    private final Map<UUID, Game> votes = new HashMap<>();
    private final Map<Game, Integer> gameVotes = new HashMap<>();
    private final VoteUtil voteUtil;
    private final VoteMap voteMap;

    public VoteHandler() {
        voteUtil = new VoteUtil();
        voteMap = new VoteMap();
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
