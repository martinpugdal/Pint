package dk.martinersej.pint.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.map.maps.VoteMap;
import dk.martinersej.pint.utils.LocationUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;

@Getter
public class VoteHandler {

    private final Map<Player, Game> votes = new WeakHashMap<>();
    private final VoteUtil voteUtil;
    private final VoteMap voteMap;

    public VoteHandler() {
        voteUtil = new VoteUtil();
        voteMap = new VoteMap();
    }

    public void setVote(Player player, Game game) {
        votes.put(player, game);
    }

    public void refreshVotes() {
        votes.clear();
    }

    public void loadVoteMap() {
        getVoteMap().load();
    }
}
