package dk.martinersej.pint.vote;

import dk.martinersej.pint.game.Game;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class VoteHandler {

    @Getter
    private final Map<Player, Game> votes = new WeakHashMap<>();

    public VoteHandler() {
    }

    public void addVote(Player player, Game game) {
        votes.put(player, game);
    }

    public void removeVote(Player player) {
        votes.remove(player);
    }

    public void refreshVotes() {
        votes.clear();
    }
}
