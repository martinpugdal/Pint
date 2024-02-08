package dk.martinersej.pint.game.games.shufflecolor;

import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class ShuffleColorGame extends Game {

    public ShuffleColorGame() {
        super(new GameInformation("Color shuffle", "§5", "Stand on the right color to win!", new ItemStack(Material.WOOL, 1, (short) 12)));
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void onGameEnd() {
        super.stop();
    }
}

