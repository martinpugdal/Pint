package dk.martinersej.pint.game.games.shufflecolor;

import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class ShuffleColorGame extends Game {

    private final GameInformation gameInformation = new GameInformation("Color shuffle", "Stand on the right color to win!", new ItemStack(Material.WOOL, 1, (short) 14));

    public ShuffleColorGame() {
        super();
    }

    @Override
    public void onGameStart() {
        super.start();
    }

    @Override
    public void onGameEnd() {
        super.stop();
    }
}

