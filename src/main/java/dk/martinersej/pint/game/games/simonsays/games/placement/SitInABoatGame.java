package dk.martinersej.pint.game.games.simonsays.games.placement;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import java.util.ArrayList;
import java.util.List;

public class SitInABoatGame extends SimonGame {

    private final List<Boat> boats = new ArrayList<>();

    public SitInABoatGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 15; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Sid i en båd";
    }

    private int getHighestBlockInGameMapAt(int x, int z) {
        int y = -1;
        int highestYLevel = Pint.getInstance().getMapHandler().getMapUtil().getHighestYLevel();
        World gameWorld = Pint.getInstance().getMapHandler().getMapUtil().getGameWorld();

        for (int i = 0; i < highestYLevel; i++) {
            if (gameWorld.getBlockAt(x, i, z).getType().isSolid()) {
                y = i;
                break;
            }
        }

        return y;
    }

    private List<Location> findBoatLocations(int amountOfPlayers) {
        List<Location> locations = new ArrayList<>();

        Location corner1 = Pint.getInstance().getMapHandler().getMapUtil().getCurrentGameMapCorner1();
        Location corner2 = Pint.getInstance().getMapHandler().getMapUtil().getCurrentGameMapCorner2();

        for (int i = 0; i < amountOfPlayers; i++) {
            double x = corner1.getX() + (corner2.getX() - corner1.getX()) * Math.random();
            double z = corner1.getZ() + (corner2.getZ() - corner1.getZ()) * Math.random();
            int y = getHighestBlockInGameMapAt((int) x, (int) z);
            if (y == -1) {
                i--;
                continue;
            }
            Location location = new Location(corner1.getWorld(), x, y+1, z);
            locations.add(location.clone());
        }
        return locations;
    }

    @Override
    public void startGame() {
        boats.clear();
        int amountOfPlayers = getSimonSaysGame().getPlayers().size();
        List<Location> locations = findBoatLocations(amountOfPlayers);
        for (int i = 0; i < amountOfPlayers; i++) {
            Location location = locations.get(i);
            Boat boat = location.getWorld().spawn(location, Boat.class);
            boat.setMaxSpeed(0);
            boats.add(boat);
        }
    }

    @Override
    public void stopGame() {
        for (Boat boat : boats) {
            boat.eject();
            boat.remove();
        }
        boats.clear();
    }

    @EventHandler
    public void onBoatEnter(VehicleEnterEvent event) {
        Player player = (Player) event.getEntered();
        if (event.getVehicle() instanceof Boat && boats.contains(event.getVehicle())) {
            if (getSimonSaysGame().isPlayerInGame(player)) {
                getSimonSaysGame().finishedTask(player);
            } else {
                // Player is not in game
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBoatExit(VehicleExitEvent event) {
        Player player = (Player) event.getExited();
        if (getSimonSaysGame().isPlayerInGame(player) && event.getVehicle() instanceof Boat) {
            if (boats.contains(event.getVehicle())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBoatDamage(VehicleDamageEvent event) {
        if (event.getVehicle() instanceof Boat) {
            if (boats.contains(event.getVehicle()))
                event.setCancelled(true);
        }
    }


    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}