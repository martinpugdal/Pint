package dk.martinersej.pint.map;

import dk.martinersej.pint.utils.FileUtils;
import dk.martinersej.pint.utils.VoidGenerator;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;

public class ServerWorld {

    @Getter
    private World world;
    private Location zeroLocation;

    public ServerWorld() {
        deleteGameWorld();
        createWorld();
    }

    public void createWorld() {
        WorldCreator worldCreator = new WorldCreator(getClass().getSimpleName());
        worldCreator.generator(new VoidGenerator());
        worldCreator.type(WorldType.FLAT);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.generateStructures(false);
        this.world = worldCreator.createWorld();
        this.world.setAutoSave(false);
        this.world.setGameRuleValue("doMobSpawning", "false");
        this.world.setGameRuleValue("randomTickSpeed", "0");
        this.world.setGameRuleValue("doDaylightCycle", "false");
        this.world.setGameRuleValue("doWeatherCycle", "false");
        this.world.setGameRuleValue("showDeathMessages", "false");
        this.world.setDifficulty(Difficulty.EASY);

        this.zeroLocation = new Location(world, 0, 0, 0);
    }

    public void deleteGameWorld() {
        World gameWorld = Bukkit.getWorld(getClass().getSimpleName());

        if (gameWorld != null) {
            World world = Bukkit.getWorlds().get(0);
            for (Player player : gameWorld.getPlayers()) {
                player.teleport(world.getSpawnLocation());
            }
        }

        try {
            Bukkit.unloadWorld(getClass().getSimpleName(), false);
            this.world = null;
            this.zeroLocation = null;
        } catch (ArrayIndexOutOfBoundsException ignored) {
            System.out.println("Failed unloading the world!");
        }

        FileUtils.deleteDir(new File(getClass().getSimpleName()));
    }

    public Location getZeroLocation() {
        return zeroLocation.clone();
    }
}
