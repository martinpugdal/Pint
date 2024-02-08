package dk.martinersej.pint.game.games.dåseskjul;

import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DaaseskjulGame extends Game {

    public DaaseskjulGame() {
        super(new GameInformation("Dåseskjul", "§5", "Dåseskjul som det barneleg", new ItemStack(Material.GLASS_BOTTLE, 1)));
    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void onGameEnd() {

    }
}