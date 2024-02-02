package dk.martinersej.pint.command.map;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.maps.GameMap;
import dk.martinersej.pint.map.maps.SpawnPoint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapRegionsSubCommand extends SubCommand {

    public MapRegionsSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Region for et map",
                "<MapID> <add|delete|clear|list> [<RegionID>]",
                "pint.map.regions",
                "region", "regions"
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
                    sender.sendMessage("§cDu kan ikke tilføje en region i serverens verden");
                    return CommandResult.success(this);
                }

                try {
                    LocalSession localSession = Fawe.get().getWorldEdit().getSession(player.getName());
                    RegionSelector selector = localSession.getRegionSelector(BukkitUtil.getLocalWorld(player.getWorld()));
                    Selection selection = new CuboidSelection(player.getWorld(), selector, (CuboidRegion) selector.getRegion());

                    Location corner1 = selection.getMinimumPoint();
                    Location corner2 = selection.getMaximumPoint();

                    int regionID = Pint.getInstance().getMapHandler().addRegion(mapID, corner1, corner2);
                    sender.sendMessage("§aRegion med id " + regionID + " er nu tilføjet til map med id " + mapID);

                } catch (IncompleteRegionException e) {
                    CommandResult.wrongUsage(this, "Du skal have en WorldEdit selection");
                }
                break;
            case "delete":
                int regionID;
                try {
                    regionID = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cDu skal skrive et tal");
                    return CommandResult.wrongUsage(this, "§cDu skal skrive et tal");
                }
                Pint.getInstance().getMapHandler().deleteRegion(mapID, regionID);
                sender.sendMessage("§aRegion med id " + regionID + " er nu slettet fra map med id " + mapID);
                break;
            case "clear":
                Pint.getInstance().getMapHandler().clearRegions(mapID);
                sender.sendMessage("§aAlle regions er nu slettet fra map med id " + mapID);
                break;
            case "list":
                GameMap gameMap = Pint.getInstance().getMapHandler().getMap(mapID);
                sender.sendMessage("§aRegions:");
                for (Region region : gameMap.getRegions()) {
                    Region region1 = Pint.getInstance().getMapHandler().getMapUtil().calculateRegionWithVoteMap(region, gameMap);
                    sender.sendMessage("----------------");
                    sender.sendMessage("§7ID: " + gameMap.getRegions().indexOf(region));
                    sender.sendMessage("§7Corner1: " + region1.getMinimumPoint());
                    sender.sendMessage("§7Corner2: " + region1.getMaximumPoint());
                    sender.sendMessage("----------------");
                }
                break;
            default:
                return CommandResult.wrongUsage(this);
        }

        return CommandResult.success(this);
    }
}
