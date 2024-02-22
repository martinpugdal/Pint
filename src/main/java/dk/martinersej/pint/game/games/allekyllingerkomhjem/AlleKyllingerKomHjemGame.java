package dk.martinersej.pint.game.games.allekyllingerkomhjem;

import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AlleKyllingerKomHjemGame extends Game {
    public AlleKyllingerKomHjemGame() {
        super(new GameInformation("Alle kyllinger kom hjem", "§5", "Går ud på at få alle kyllingerne hjem til hønsegården", new ItemStack(Material.MONSTER_EGG, 1, (short) 93)));
    }

    @Override
    public void addWinListeners() {

    }

    @Override
    public void onGameStart() {

    }

    @Override
    public void onGameEnd() {

    }
}
