package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.utils.command.CommandResult;
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

        if (args.length < 1) {
            return CommandResult.wrongUsage(this);
        }

        String gameName = String.join(" ", args).toLowerCase();
        Game game = Pint.getInstance().getGameHandler().getGame(gameName);
        if (game == null) {
            sender.sendMessage("§cDer findes ikke et spil med det navn");
        } else {
            String name = game.getGameInformation().getName();
            if (game.getGameMaps().isEmpty()) {
                sender.sendMessage("§cDer findes ikke nogle maps for spillet " + name);

            } else {
                sender.sendMessage("§aMaps for spillet " + name + ":");
                for (GameMap gameMap : game.getGameMaps()) {
                    sender.sendMessage("§a- " + gameMap.getId() + " §7(" + (gameMap.isActive() ? "§aAktivt" : "§cInaktivt") + "§7)");
                }
            }
        }
        return CommandResult.success(this);
    }
}
