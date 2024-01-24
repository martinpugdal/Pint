package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.GameMap;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class MapSpawnpointsSubCommand extends SubCommand {

    public MapSpawnpointsSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Vis alle maps for et spil",
                "<MapID> <add|delete|clear|list> [<SpawnpointID>]",
                "pint.map.spawnpoints",
                "spawnpoint", "spawnpoints"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length <= 2) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        }
        if (!(sender instanceof Player)) {
            return Result.getCommandResult(Result.NO_PERMISSION, this);
        }

        int mapID;
        try {
            mapID = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cDu skal skrive et tal");
            return Result.getCommandResult(Result.SUCCESS, this);
        }
        String action = args[1].toLowerCase();

        switch (action) {
            case "add":
                int spID = Pint.getInstance().getMapHandler().addSpawnPoint(mapID, ((Player) sender).getLocation());
                sender.sendMessage("§aSpawnpoint med id " + spID + " er nu tilføjet til map med id " + mapID);
                break;
            case "delete":
                int spawnpointID;
                try {
                    spawnpointID = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cDu skal skrive et tal");
                    return Result.getCommandResult(Result.SUCCESS, this);
                }
                Pint.getInstance().getMapHandler().deleteSpawnPoint(mapID, spawnpointID);
                sender.sendMessage("§aSpawnpoint med id " + spawnpointID + " er nu slettet fra map med id " + mapID);
                break;
            case "clear":
                Pint.getInstance().getMapHandler().clearSpawnPoints(mapID);
                sender.sendMessage("§aAlle spawnpoints er nu slettet fra map med id " + mapID);
                break;
            case "list":
                GameMap gameMap = Pint.getInstance().getMapHandler().getMap(mapID);
                Location zeroLocation = gameMap.getZeroLocation().clone();
                sender.sendMessage("§aSpawnpoint:");
                for (Vector vector : gameMap.getSpawnPoints()) {
                    Location location = zeroLocation.clone().add(vector);
                    sender.sendMessage("§a- " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ());
                }
                break;
            default:
                return Result.getCommandResult(Result.WRONG_USAGE, this);
        }

        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
