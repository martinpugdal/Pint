package dk.martinersej.pint.command.game;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GameToggleCommand extends SubCommand {

    public GameToggleCommand(JavaPlugin plugin) {
        super(
                plugin,
                "tilføj/fjern et spil til poolen",
                "<game>",
                "pint.game.toggle",
                "toggle"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        }

        String gameName = args[0];
        Game game = Pint.getInstance().getGameHandler().getGame(gameName);
        if (game == null) {
            sender.sendMessage("§cEt spil med navnet " + gameName + " findes ikke");
        } else {
            Game gameInPool = Pint.getInstance().getGameHandler().getGamePool().getGame(gameName);
            if (gameInPool != null) {
                Pint.getInstance().getGameHandler().getGamePool().removeGame(gameInPool);
                sender.sendMessage("§aSpillet " + gameName + " er nu fjernet fra poolen");
            } else {
                Pint.getInstance().getGameHandler().getGamePool().addGame(game);
                sender.sendMessage("§aSpillet " + gameName + " er nu tilføjet til poolen");
            }
        }

        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
