package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MapInfoSubCommand extends SubCommand {

    public MapInfoSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Vis information om et map",
                "<map>",
                "pint.map.info",
                "info"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length != 1) {
            return CommandResult.wrongUsage(this);
        }

        int mapId;
        try {
            mapId = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            return CommandResult.wrongUsage(this, "§cMap ID skal være et tal");
        }

        GameMap gameMap = Pint.getInstance().getMapHandler().getMap(mapId);
        if (gameMap == null) {
            sender.sendMessage("§cDer findes ikke et map med det ID");
        } else {
            String mapInfo = "§aID: " + gameMap.getId() + "\n" +
                    "§aNavn: " + gameMap.getGameName() + "\n" +
                    "§aAktiv: " + (gameMap.isActive() ? "Ja" : "Nej") + "\n" +
                    "§aCorners: " + gameMap.getCorner1() + " - " + gameMap.getCorner2() + "\n" +
                    "§aCenter: " + gameMap.getCenterLocation() + "\n" +
                    "§aZero location: " + gameMap.getZeroLocation() + "\n" +
                    "§aMin spillere: " + gameMap.getMinPlayers() + "\n" +
                    "§aMax spillere: " + gameMap.getMaxPlayers() + "\n" +
                    "§aSpawnpoints: " + gameMap.getSpawnPoints().size();
            sender.sendMessage(mapInfo);
        }

        return CommandResult.success(this);
    }
}
