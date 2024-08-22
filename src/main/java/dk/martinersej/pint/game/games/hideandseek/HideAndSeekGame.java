package dk.martinersej.pint.game.games.hideandseek;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.blocks.BaseBlock;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.game.objects.GameInformation;
import net.minecraft.server.v1_8_R3.Block;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectTypeWrapper;
import org.bukkit.util.EulerAngle;

import java.util.*;

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
    private final Set<ItemStack> propCandidates = new HashSet<>();

    private final Material[] tools = new Material[]{
        Material.WOOD_SWORD,
        Material.WOOD_PICKAXE,
        Material.WOOD_SPADE,
        Material.WOOD_AXE,

        Material.STONE_SWORD,
        Material.STONE_PICKAXE,
        Material.STONE_SPADE,
        Material.STONE_AXE,

        Material.IRON_SWORD,
        Material.IRON_PICKAXE,
        Material.IRON_SPADE,
        Material.IRON_AXE,

        Material.GOLD_SWORD,
        Material.GOLD_PICKAXE,
        Material.GOLD_SPADE,
        Material.GOLD_AXE,

        Material.DIAMOND_SWORD,
        Material.DIAMOND_PICKAXE,
        Material.DIAMOND_SPADE,
        Material.DIAMOND_AXE
    };

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
        propCandidates.clear();

        // Loop blocks in the map, and if the condition is accepted, add the block to the list over blocks that can be used as props
        // 1. Condition: inside allowedblocks-region
        // 2. Condition: block is solid
        // 3. Condition: the block has other blocks in the same type & data around it
        // (e.g., a block of dirt has dirt blocks around it), so it can be used as prop.
        // 4. Condition: Don't add them if its floor or a wall.
        //
        // Random tools can be added as props, e.g., a sword, a pickaxe, a shovel, need at least 3 tools to be added

        for (Map.Entry<BlockVector, BaseBlock> entry : getCurrentGameMap().getBlocks().entrySet()) {
            BaseBlock baseBlock = entry.getValue();
            if (baseBlock.isAir()) continue;
            if (!getCurrentGameMap().getRegion("allowedblocks").contains(entry.getKey())) {
                System.out.println("Block not in allowedblocks region: " + baseBlock);
                continue;
            } // 1. Condition
            ItemStack itemStack = new ItemStack(baseBlock.getType(), 1, (short) baseBlock.getData());
            if (propCandidates.contains(itemStack)) return;
            Block block = Block.getById(baseBlock.getId());

            // 2. Condition
            if (block.w()) {
                // 3. Condition
                BlockVector blockVector = entry.getKey();
                Map<BlockVector, BaseBlock> neighbourBlocks = getCurrentGameMap().getNeighbourBlocks(blockVector);
                // need 1 above (y+1) and min. 2 at the same y
                if (neighbourBlocks.size() < 3) return; // min. 3 blocks

                BaseBlock topBlock = neighbourBlocks.get(
                    new BlockVector(blockVector.getBlockX(), blockVector.getBlockY() + 1, blockVector.getBlockZ())
                );
                if (topBlock == null) return;
                if (topBlock.isAir()) return;
                if (!topBlock.equalsFuzzy(baseBlock)) return;
                boolean sideBlock = neighbourBlocks.entrySet().stream().filter(entry1 -> entry1.getValue().equalsFuzzy(baseBlock) && blockVector.getBlockY() == entry1.getKey().getBlockY()).count() >= 2;
                if (!sideBlock) return;

                propCandidates.add(itemStack);
            }
        }

        // add tools as props
        int toolCount = 0;
        int toolCountMax = 3 + (int) (Math.random() * 3); // 3-5 tools
        while (toolCountMax > toolCount) {
            Material toolBlock = tools[(int) (Math.random() * tools.length)];
            ItemStack itemStack = new ItemStack(toolBlock);
            if (propCandidates.contains(itemStack)) continue;
            propCandidates.add(itemStack);
            toolCount++;
        }
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
