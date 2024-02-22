package dk.martinersej.pint.game.games.simonsays.objects;

import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class SimonPlayer {

    private final Player player;
    private int points;

    public SimonPlayer(Player player) {
        this.player = player;
        points = 0;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }
}
