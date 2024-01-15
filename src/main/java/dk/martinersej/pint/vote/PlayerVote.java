package dk.martinersej.pint.vote;

import dk.martinersej.pint.game.Game;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter
public class PlayerVote {

    private final UUID uuid;
    @Setter
    private Game game;

    public PlayerVote(OfflinePlayer player, Game game) {
        this.uuid = player.getUniqueId();
        this.game = game;
    }

    public PlayerVote(Player player, Game game) {
        this.uuid = player.getUniqueId();
        this.game = game;
    }
}
