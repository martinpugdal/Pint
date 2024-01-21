package dk.martinersej.pint.command.map;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.utils.WorldEditUtil;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapCreateSubCommand extends SubCommand {

    public MapCreateSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Opretter et map med id",
                "<game>",
                "pint.map.create",
                "create", "opret"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length != 1) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        }

        String gameName = args[0].toLowerCase();
        Game game = Pint.getInstance().getGameHandler().getGamePool().getGame(gameName);
        if (game == null) {
            sender.sendMessage("§cDer findes ikke et spil med det navn");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

        Player player = (Player) sender;
        try {
            Region region = WorldEdit.getInstance().getSession(sender.getName()).getSelection(WorldEditUtil.getWEWorld(player.getWorld()));
            Location corner1 = new Location(player.getWorld(), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
            Location corner2 = new Location(player.getWorld(), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
            int mapID = Pint.getInstance().getMapHandler().createMap();
            Pint.getInstance().getMapHandler().saveMapSchematic(mapID, corner1, corner2);
            game.getGameMaps().add(Pint.getInstance().getMapHandler().getMap(mapID));
        } catch (IncompleteRegionException e) {
            sender.sendMessage("§cDu har ikke valgt et område");
        }


        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
