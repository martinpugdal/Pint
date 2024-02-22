package dk.martinersej.pint.map.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.Map;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapDeleteSubCommand extends SubCommand {

    public MapDeleteSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Sletter et map",
                "<mapID>",
                "pint.map.delete",
                "delete", "slet"
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
        Pint.getInstance().getMapHandler().deleteMap(mapID);
        player.sendMessage("§aMap med id " + mapID + " er blevet slettet");

        return CommandResult.success(this);
    }
}
