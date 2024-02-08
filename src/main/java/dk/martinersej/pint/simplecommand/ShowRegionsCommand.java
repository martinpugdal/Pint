package dk.martinersej.pint.simplecommand;

import com.sk89q.worldedit.regions.Region;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.map.objects.maps.GameMap;
import dk.martinersej.pint.utils.ParticleUtils;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ShowRegionsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (Pint.getInstance().getGameHandler().getCurrentGame() == null) {
            commandSender.sendMessage("§cDer er ikke noget spil igang");
            return true;
        }
        GameMap currentGameMap = Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap();
        if (currentGameMap == null) {
            commandSender.sendMessage("§cDer er ikke valgt et map");
            return true;
        }
        commandSender.sendMessage("§aRegions for map: " + currentGameMap.getMapID());

        List<Region> regions = Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap().getRegions();
        for (Region region : regions) {
            region = Pint.getInstance().getMapHandler().getMapUtil().calculateRegionWithVoteMap(region, currentGameMap);
            commandSender.sendMessage("§aRegion: " + region.getMinimumPoint() + " - " + region.getMaximumPoint());
            Location pos1 = new Location(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld(), region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
            Location pos2 = new Location(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld(), region.getMaximumPoint().getX(), region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
            ParticleUtils.drawRegionCuboid(pos1, pos2, (Player) commandSender);
        }

        return true;
    }
}

