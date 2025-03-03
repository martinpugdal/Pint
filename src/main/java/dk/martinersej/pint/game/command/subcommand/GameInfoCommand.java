package dk.martinersej.pint.game.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class GameInfoCommand extends SubCommand {


    public GameInfoCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Info om spil",
                "",
                "pint.game.info",
                "info"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        Set<Game> games = Pint.getInstance().getGameHandler().getGames();
        Set<Game> gamesInPool = Pint.getInstance().getGameHandler().getGamePool().getGames();

        for (Game game : games) {
            sender.sendMessage("§7-----------------");
            sender.sendMessage("§a" + game.getGameInformation().getName() + "§7:");
            sender.sendMessage("§7- Maps: §a" + game.getGameMaps().size());
            sender.sendMessage("§7- Aktivt map: §a" + (game.getCurrentGameMap() != null ? game.getCurrentGameMap().getMapID() : "Ingen"));
            sender.sendMessage("§7- Spil i pool: §a" + gamesInPool.contains(game));
            Game currentGame = Pint.getInstance().getGameHandler().getCurrentGame();
            sender.sendMessage("§7- Spil i gang: §a" + ((currentGame != null && currentGame.equals(game)) ? "Ja" : "Nej"));
            sender.sendMessage("§7-----------------");
        }


        return CommandResult.success(this);
    }
}
