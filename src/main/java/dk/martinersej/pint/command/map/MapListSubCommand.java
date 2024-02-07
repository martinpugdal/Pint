package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.map.maps.GameMap;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MapListSubCommand extends SubCommand {

    public MapListSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Vis alle maps for et spil",
                "<gameID>",
                "pint.map.list",
                "list"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length < 1) {
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
            String name = game.getGameInformation().getName();
            if (game.getGameMaps().isEmpty()) {
                sender.sendMessage("§cDer findes ikke nogle maps for spillet " + name);

            } else {
                sender.sendMessage("§aMaps for spillet " + name + ":");
                for (GameMap gameMap : game.getGameMaps()) {
                    sender.sendMessage("§a- " + gameMap.getMapID() + " §7(" + (gameMap.isActive() ? "§aAktivt" : "§cInaktivt") + "§7)");
                }
            }
        }
        return CommandResult.success(this);
    }
}
