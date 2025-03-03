package dk.martinersej.pint.map.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import dk.martinersej.pint.map.objects.SpawnPoint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapSpawnpointsSubCommand extends SubCommand {

    public MapSpawnpointsSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Spawnpoints for et map",
                "<mapID> <add|delete|clear|list> [<SpawnpointID>]",
                "pint.map.spawnpoints",
                "spawnpoint", "spawnpoints"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length < 2) {
            return CommandResult.wrongUsage(this);
        }
        if (!(sender instanceof Player)) {
            return CommandResult.noConsole(this);
        }

        int mapID;
        try {
            mapID = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            return CommandResult.wrongUsage(this, "§cDu skal skrive et tal");
        }
        String action = args[1].toLowerCase();

        switch (action) {
            case "add":
                Player player = (Player) sender;
                if (player.getWorld().equals(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld())) {
                    sender.sendMessage("§cDu kan ikke tilføje et spawnpoint i serverens verden");
                    return CommandResult.success(this);
                }

                boolean yaw = false;
                boolean pitch = false;
                if (args.length > 2) {
                    String yawString = args[2];
                    if (yawString.equalsIgnoreCase("false") || yawString.equalsIgnoreCase("true")) {
                        yaw = Boolean.parseBoolean(yawString);
                    } else {
                        sender.sendMessage("§cYaw skal være true eller false");
                        return CommandResult.success(this);
                    }
                    if (args.length > 3) {
                        String pitchString = args[3];
                        if (pitchString.equalsIgnoreCase("false") || pitchString.equalsIgnoreCase("true")) {
                            pitch = Boolean.parseBoolean(pitchString);
                        } else {
                            sender.sendMessage("§cPitch skal være true eller false");
                            return CommandResult.success(this);
                        }
                    }
                }
                int spID = Pint.getInstance().getMapHandler().addSpawnPoint(mapID, player.getLocation(), yaw, pitch);
                sender.sendMessage("§aSpawnpoint med id " + spID + " er nu tilføjet til map med id " + mapID);
                break;
            case "delete":
                int spawnpointID;
                try {
                    spawnpointID = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cDu skal skrive et tal");
                    return CommandResult.wrongUsage(this, "§cDu skal skrive et tal");
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
                sender.sendMessage("§aSpawnpoint:");
                for (SpawnPoint spawnPoint : gameMap.getSpawnPoints()) {
                    Location location = Pint.getInstance().getMapHandler().getMapUtil().calculateSpawnLocationWithVoteMap(spawnPoint, gameMap);
                    sender.sendMessage("§a- " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + " Yaw: " + spawnPoint.getYaw() + " Pitch: " + spawnPoint.getPitch());
                }
                break;
            default:
                return CommandResult.wrongUsage(this);
        }

        return CommandResult.success(this);
    }
}
