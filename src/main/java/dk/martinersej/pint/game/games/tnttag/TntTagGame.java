package dk.martinersej.pint.game.games.tnttag;

import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.game.GameHandler;
import dk.martinersej.pint.game.objects.GameInformation;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public class TntTagGame extends Game {

    private final GameInformation gameInformation = new GameInformation("TNT Tag", "Tag someone with TNT to win!", Material.TNT);

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
