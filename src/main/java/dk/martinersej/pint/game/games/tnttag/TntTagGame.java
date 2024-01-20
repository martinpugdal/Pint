package dk.martinersej.pint.game.games.tnttag;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import dk.martinersej.pint.game.objects.GameMap;
import lombok.Getter;
import org.bukkit.Material;

public class TntTagGame extends Game {

    @Getter
    private final GameInformation gameInformation = new GameInformation("TNT Tag", "Tag someone with TNT to win!", Material.TNT);
    private final GameMap gameMap = Pint.getInstance().getMapHandler().getMap("1");

    public TntTagGame() {
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
