package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MapListSubCommand extends SubCommand {

    public MapListSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Vis alle maps for et spil",
                "<game>",
                "pint.map.list",
                "list"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length != 1) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        }

        String gameName = String.join(" ", args).toLowerCase();
        Game game = Pint.getInstance().getGameHandler().getGame(gameName);
        if (game == null) {
            sender.sendMessage("§cDer findes ikke et spil med det navn");
        } else {
            for (GameMap gameMap : game.getGameMaps()) {
                sender.sendMessage("§a" + gameMap.getGameName() + " - (" + gameMap.getId() + ")" + (gameMap.isActive() ? " - (Aktivt)" : ""));
            }
        }

        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
