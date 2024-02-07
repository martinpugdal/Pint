package dk.martinersej.pint.command.map;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
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
                "<mapID>",
                "pint.map.setschematic",
                "setschematic"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length != 1) {
            return CommandResult.wrongUsage(this);
        } else if (!(sender instanceof Player)) {
            return CommandResult.noConsole(this);
        }

        int id = Integer.parseInt(args[0]);

        if (!Pint.getInstance().getMapHandler().mapIsPresent(id)) {
            sender.sendMessage("§cEt map med id " + id + " findes ikke");
            return CommandResult.success(this);
        }
        if (Pint.getInstance().getGameHandler().getCurrentGame() != null && Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap().getMapID() == id) {
            sender.sendMessage("§cDu kan ikke redigere det map som er i brug");
            return CommandResult.success(this);
        }

        Player player = (Player) sender;
        if (player.getWorld().equals(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld())) {
            sender.sendMessage("§cDu kan ikke sætte et schematic i serverens verden");
            return CommandResult.success(this);
        }

        try {
            LocalSession localSession = Fawe.get().getWorldEdit().getSession(player.getName());
            RegionSelector selector = localSession.getRegionSelector(BukkitUtil.getLocalWorld(player.getWorld()));
            Selection selection = new CuboidSelection(player.getWorld(), selector, (CuboidRegion) selector.getRegion());

            Location corner1 = selection.getMinimumPoint();
            Location corner2 = selection.getMaximumPoint();

            Pint.getInstance().getMapHandler().saveMapSchematic(id, corner1, corner2);
        } catch (IncompleteRegionException e) {
            CommandResult.wrongUsage(this, "Du skal have en WorldEdit selection");
        }

        sender.sendMessage("§aSchematic for map med id " + id + " er nu redigeret");

        return CommandResult.success(this);
    }
}
