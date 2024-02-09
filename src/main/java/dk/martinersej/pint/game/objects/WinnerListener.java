package dk.martinersej.pint.game.objects;

import org.bukkit.entity.Player;

import java.util.Set;
import java.util.function.Consumer;

public interface WinnerListener {

    void addWinListener(Consumer<Set<Player>> onWin);

    void callWinListeners(Set<Player> players);

    void win(Set<Player> players);
}
