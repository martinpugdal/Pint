package dk.martinersej.pint.map.command.subcommand;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.map.objects.maps.GameMap;
import dk.martinersej.pint.utils.command.CommandResult;
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
                "<gameID>",
                "pint.map.create",
                "create", "opret"
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
        }

        Player player = (Player) sender;
        try {
            LocalSession localSession = Fawe.get().getWorldEdit().getSession(player.getName());
            RegionSelector selector = localSession.getRegionSelector(BukkitUtil.getLocalWorld(player.getWorld()));
            Selection selection = new CuboidSelection(player.getWorld(), selector, (CuboidRegion) selector.getRegion());

            Location corner1 = selection.getMinimumPoint();
            Location corner2 = selection.getMaximumPoint();

            int mapID = Pint.getInstance().getMapHandler().createMap(game.getGameInformation().getName(), corner1, corner2);
            GameMap map = Pint.getInstance().getMapHandler().getMap(mapID);
            game.getGameMaps().add(map);
            sender.sendMessage("§aDu har oprettet et map med id " + mapID + " til spillet " + game.getGameInformation().getName() + "(" + gameID + ")");
        } catch (IncompleteRegionException e) {
            return CommandResult.wrongUsage(this, "§cDu har ikke valgt et område");
        }

        return CommandResult.success(this);
    }
}
