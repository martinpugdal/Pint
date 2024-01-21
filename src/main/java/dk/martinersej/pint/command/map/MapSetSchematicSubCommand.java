package dk.martinersej.pint.command.map;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.WorldEditUtil;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapSetSchematicSubCommand extends SubCommand {

    public MapSetSchematicSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Gem schematic for map",
                "<id>",
                "pint.map.setschematic",
                "setschematic"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        } else if (!(sender instanceof Player)) {
            return Result.getCommandResult(Result.NO_PERMISSION, this);
        }

        int id = Integer.parseInt(args[0]);

        if (!Pint.getInstance().getMapHandler().mapExists(id)) {
            sender.sendMessage("§cEt map med id " + id + " findes ikke");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

        if (Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap().getId() == id) {
            sender.sendMessage("§cDu kan ikke redigere det map som er i brug");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

        Player player = (Player) sender;
        try {
            Region region = WorldEdit.getInstance().getSession(sender.getName()).getSelection(WorldEditUtil.getWEWorld(player.getWorld()));
            Location corner1 = new Location(player.getWorld(), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
            Location corner2 = new Location(player.getWorld(), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
            Pint.getInstance().getMapHandler().saveMapSchematic(id, corner1, corner2);
        } catch (IncompleteRegionException e) {
            sender.sendMessage("§cDu har ikke valgt et område");
        }

        sender.sendMessage("§aSchematic for map med id " + id + " er nu redigeret");

        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
