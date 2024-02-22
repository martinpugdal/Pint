package dk.martinersej.pint.map.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.Map;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapTeleportSubCommand extends SubCommand {

    public MapTeleportSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Teleportér til det originale map",
                "<mapID>",
                "pint.map.teleport",
                "teleport", "tp"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length < 1) {
            return CommandResult.wrongUsage(this);
        }

        int mapID;
        try {
            mapID = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            sender.sendMessage("§cMap ID skal være et tal");
            return CommandResult.success(this);
        }
        Map map = Pint.getInstance().getMapHandler().getMap(mapID);
        if (map == null) {
            sender.sendMessage("§cEt map med id " + mapID + " findes ikke");
            return CommandResult.success(this);
        }

        Player player = (Player) sender;
        player.teleport(map.getZeroLocation());
        sender.sendMessage("§aDu er blevet teleporteret til map " + mapID);

        return CommandResult.success(this);
    }
}
