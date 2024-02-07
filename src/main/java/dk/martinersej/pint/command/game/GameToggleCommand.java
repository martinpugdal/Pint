package dk.martinersej.pint.command.game;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GameToggleCommand extends SubCommand {

    public GameToggleCommand(JavaPlugin plugin) {
        super(
                plugin,
                "tilføj/fjern et spil til poolen",
                "<gameID>",
                "pint.game.toggle",
                "toggle"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.wrongUsage(this);
        }
        int gameID;
        try {
            gameID = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage("§cGame ID skal være et tal");
            return CommandResult.success(this);
        }
        Game game = Pint.getInstance().getGameHandler().getGame(gameID);
        if (game == null) {
            sender.sendMessage("§cEt spil med id " + gameID + " findes ikke");
            return CommandResult.success(this);
        } else {
            Game gameInPool = Pint.getInstance().getGameHandler().getGamePool().getGame(game);
            if (gameInPool != null) {
                Pint.getInstance().getGameHandler().getGamePool().removeGame(gameInPool);
                sender.sendMessage("§aSpillet " + game.getGameInformation().getName() + " er nu fjernet fra poolen");
            } else {
                boolean added = Pint.getInstance().getGameHandler().getGamePool().addGame(game);
                if (!added) {
                    sender.sendMessage("§cSpillet " + game.getGameInformation().getName() + " har ingen maps og kan derfor ikke tilføjes til poolen");
                } else {
                    sender.sendMessage("§aSpillet " + game.getGameInformation().getName() + " er nu tilføjet til poolen");
                }
            }
        }

        return CommandResult.success(this);
    }
}
