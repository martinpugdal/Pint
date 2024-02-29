package dk.martinersej.pint.game.games.simonsays.objects;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.util.UUID;

@Getter
public class SimonPlayer {

    private final OfflinePlayer player;
    private int points;

    public SimonPlayer(UUID uuid) {
        this.player = Bukkit.getOfflinePlayer(uuid);
        points = 0;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void removePoints(int points) {
        this.points -= points;
    }
}
