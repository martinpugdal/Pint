package dk.martinersej.pint.game.objects;

import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.LocationUtil;
import dk.martinersej.pint.utils.WorldEditUtil;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class GameMap {

    private final int id;

    private String gameName;

    private Vector corner1;
    private Vector corner2;
    private List<Vector> spawnPoints;

    private int minPlayers;
    private int maxPlayers;

    public GameMap(int id) {
        this.id = id;
        load();
    }

    public GameMap(String id) {
        this(Integer.parseInt(id));
    }

    public GameMap(int id, Vector corner1, Vector corner2, List<Vector> spawnPoints) {
        this.id = id;
        this.corner1 = corner1;
        this.corner2 = corner2;
        this.spawnPoints = spawnPoints;
    }

    public void load() {
        ConfigurationSection section = Pint.getInstance().getMapHandler().getConfig();

        // Load game name
        this.gameName = section.getString("gameName");

        // Load corners
        this.corner1 = LocationUtil.stringToVector(section.getString("corner1"));
        this.corner2 = LocationUtil.stringToVector(section.getString("corner2"));

        // Load min and max players
        this.minPlayers = section.getInt("minPlayers", 0);
        this.maxPlayers = section.getInt("maxPlayers", 16);

        //Load spawnpoints
        this.spawnPoints = new ArrayList<>();
        for (String key : section.getConfigurationSection("spawnpoints").getKeys(false)) {
            spawnPoints.add(LocationUtil.stringToVector(section.getString("spawnpoints." + key)));
        }
    }

    private String getSchematicPath() {
        return Pint.getInstance().getDataFolder() + "/maps/" + id + ".schematic";
    }

    public void pasteSchematic() {
        File schematicFile = new File(getSchematicPath());
        WorldEditUtil.loadSchematic(schematicFile, WorldEditUtil.getWEWorld(Pint.getInstance().getGameHandler().getServerWorld().getWorld()));
    }

    public void clearSchematic() {
        File schematicFile = new File(getSchematicPath());
        Clipboard clipBoard = WorldEditUtil.loadSchematic(schematicFile, WorldEditUtil.getWEWorld(Pint.getInstance().getGameHandler().getServerWorld().getWorld()));
        WorldEditUtil.setAllTo(clipBoard, new BaseBlock(0));
    }

    public int getGameYLevel() {
        return this.getSpawnPoints().get(0).getBlockY();
    }

    public Location getCenterLocation() {
        Location corner1Location = getLocationFromOffset(corner1);
        Location corner2Location = getLocationFromOffset(corner2);
        Location center = LocationUtil.getCenterLocation(corner1Location, corner2Location);
        return center.add(0.5, 0, 0.5);
    }

    private Location getLocationFromOffset(Vector offset) {
        Location location = Pint.getInstance().getGameHandler().getServerWorld().getZeroLocation();
        location.add(offset);
        return location;
    }
}
