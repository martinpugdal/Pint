package dk.martinersej.pint.game.games.tnttag;

import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import org.bukkit.Material;

public class TntTagGame extends Game {

    public TntTagGame() {
        super(new GameInformation("TNT Tag", "§c", "Tag someone with TNT to win!", Material.TNT));
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
