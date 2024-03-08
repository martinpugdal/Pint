package dk.martinersej.pint.game.games.hideandseek;

import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.util.EulerAngle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Notes:
 * - hider has invisibility and speed
 * - hider have three lives
 * - hider can change to another prop by right-clicking on an armor stand
 * - seeker have speed 2 and night vision
 * - if seeker is close to a hider, the seeker will have worldborder red overlay and pulse action bar message (( heart ))
 * - hider can change to another prop by right-clicking on an armor stand (if the prop is a valid prop)
 * - hider have special abilities, e.g. camouflage, stun, etc.
 */

public class HideAndSeekGame extends Game {

    private final List<Player> seekers = new ArrayList<>();
    private final List<Player> hiders = new ArrayList<>();
    private final Map<Player, ArmorStand> hiderProps = new HashMap<>();

    public HideAndSeekGame() {
        super(
            GameInformation.builder()
                .name("Hide and Seek")
                .color("§e")
                .description("Hide and seek is a game where the hiders hide and the seekers seek")
                .icon(Material.COMPASS)
                .build()
        );
    }

    @Override
    public void addWinListeners() {
    }

    @Override
    public void setup() {
        super.setup();

        // clear variables
        seekers.clear();
        hiders.clear();
        hiderProps.clear();

        // Loop blocks in the map, and if the condition is accepted, add the block to the list over blocks that can be used as props
        // 1. Condition: block is solid
        // 2. Condition: the block has other blocks in the same type around it.
        // (e.g., a block of dirt has dirt blocks around it), so it can be used as prop.
        // Don't add them if its floor or a wall.
        // 3. Condition: random tools can be added as props, e.g., a sword, a pickaxe, a shovel,
        // etc. (chance for adding a tool as a prop is 10%) But need at least 3 tools to be added
    }

    @Override
    public void onGameStart() {
        // get the seeker
        Player randomPlayer = getRandomPlayer(new ArrayList<>(getPlayers()));
        seekers.add(randomPlayer);

        // add the rest to hiders
        hiders.addAll(getPlayers());
        hiders.remove(randomPlayer);

        Location spawnCenter = getCurrentGameMap().getSpawnLocation();
        for (Player player : getPlayers()) {
            // teleport players to random spawn point on the map
            setPlayerToGameGamemode(player);
            Location location = !getCurrentGameMap().getSpawnPoints().isEmpty() ? getCurrentGameMap().getSpawnPoints().get((int) (Math.random() * getCurrentGameMap().getSpawnPoints().size())).getLocation(getCurrentGameMap()) : spawnCenter;
            player.teleport(location);
        }

        for (Player hider : hiders) {
            setHiderGamemode(hider);
        }
        for (Player seeker : seekers) {
            setSeekerGamemode(seeker);
        }
    }

    @Override
    public void onGameEnd() {
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isPlayerInGame(event.getPlayer())) return;

        Player player = event.getPlayer();
        if (seekers.contains(player)) {
            for (Player hider : hiders) {
                if (player.getLocation().distance(hider.getLocation()) < 5) {
                    // add red screen/overlay to the seeker
                }
            }
        } else if (hiders.contains(player)) {
            ArmorStand prop = hiderProps.get(player);
            // teleport the entity to the player's feet
            // get the height of the entity and add it to the player's feet
            prop.teleport(player.getLocation().subtract(0, prop.getEyeHeight(true), 0));

            // set the head pose of the entity to the player's head pose
            EulerAngle playerHeadPose = new EulerAngle(player.getEyeLocation().getPitch(), player.getEyeLocation().getYaw(), 0);
            prop.setHeadPose(playerHeadPose);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isPlayerInGame(player)) {
            removePlayer(player);
            seekers.remove(player);
            hiders.remove(player);
            if (seekers.isEmpty()) {
                // get a new seeker
                Player randomPlayer = getRandomPlayer(hiders);
                seekers.add(randomPlayer);
                hiders.remove(randomPlayer);
                setSeekerGamemode(randomPlayer);
            }
        }
    }


    private void setSeekerGamemode(Player player) {
        player.addPotionEffect(PotionEffectTypeWrapper.SPEED.createEffect(1000000, 2));
        player.addPotionEffect(PotionEffectTypeWrapper.NIGHT_VISION.createEffect(1000000, 1));
    }

    private void setHiderGamemode(Player player) {
        player.setHealth(3);
        player.setMaxHealth(3);
        player.addPotionEffect(PotionEffectTypeWrapper.SPEED.createEffect(1000000, 1));
        player.addPotionEffect(PotionEffectTypeWrapper.INVISIBILITY.createEffect(1000000, 1));
    }

    private void changePlayerToSeeker(Player player) {
        hiders.remove(player);
        seekers.add(player);
        setSeekerGamemode(player);
    }

    private void giveHiderAProp(Player player) {
        // give the player a prop
    }

    private void setHiderPropToClickedProp(Player player, Entity entity) {

    }

    private Player getRandomPlayer(List<Player> players) {
        return players.get((int) (Math.random() * players.size()));
    }
}
