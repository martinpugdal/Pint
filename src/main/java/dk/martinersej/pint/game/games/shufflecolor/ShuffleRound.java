package dk.martinersej.pint.game.games.shufflecolor;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import dk.martinersej.pint.utils.PacketUtil;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

@Getter
public class ShuffleRound {

    private final ShuffleColorGame game;
    private final int roundNumber;
    private final int roundDuration;
    private BukkitRunnable roundTask;
    private ItemStack blockToStandOn;
    private final Set<BaseBlock> blocksNotToStandOn = new HashSet<>();
    private Region region;

    public ShuffleRound(ShuffleColorGame game, int roundNumber, int roundDuration) {
        this.game = game;
        this.roundNumber = roundNumber;
        this.roundDuration = roundDuration;

        this.game.getScoreboard().line(1, Component.text("§7Round: " + roundNumber));

        if (roundNumber > 1) {
            this.game.getPickedMaps().get(roundNumber - 2).clearSchematic();
            this.game.getPickedMaps().get(roundNumber - 1).pasteSchematic();
            this.game.setCurrentGameMap(this.game.getPickedMaps().get(roundNumber - 1));
        }
        getRandomBlockToStandOn();
    }

    private void getRandomBlockToStandOn() {
        Vector min = game.getCurrentGameMap().getCorner1();
        Vector max = game.getCurrentGameMap().getCorner2();
        com.sk89q.worldedit.Vector minVector = new com.sk89q.worldedit.Vector(min.getX(), min.getY(), min.getZ());
        com.sk89q.worldedit.Vector maxVector = new com.sk89q.worldedit.Vector(max.getX(), max.getY(), max.getZ());

        World world = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld();
        Region region = new CuboidRegion(FastAsyncWorldEditUtil.getWEWorld(world), minVector, maxVector);
        this.region = Pint.getInstance().getMapHandler().getMapUtil().calculateRegionWithVoteMap(region, game.getCurrentGameMap());

        Set<ItemStack> blocks = new HashSet<>();
        for (BlockVector blockVector : this.region) {
            Location location = new Location(world, blockVector.getBlockX(), blockVector.getBlockY(), blockVector.getBlockZ());
            blocks.add(new ItemStack(location.getBlock().getType(), 1, location.getBlock().getState().getData().getData()));
        }

        blocks.remove(new ItemStack(Material.AIR)); // just in case we are making more than 1 layer of blocks. We don't want air

        int item = new Random().nextInt(blocks.size());
        Iterator<ItemStack> iterator = blocks.iterator();
        for (int i = 0; i <= item; i++) {
            ItemStack block = iterator.next();
            // set the block to stand on
            this.blockToStandOn = new ItemStack(block.getType(), 1, block.getDurability());
        }
        iterator.remove(); // remove the current block from the set

        Set<BaseBlock> blocksNotToStandOn = new HashSet<>();
        for (ItemStack b : blocks) {
            blocksNotToStandOn.add(new BaseBlock(b.getType().getId(), b.getData().getData()));
        }
        this.blocksNotToStandOn.addAll(blocksNotToStandOn);
    }

    public void start() {
        game.getScoreboard().line(2, Component.text("§7Duration: " + roundDuration));
        game.getScoreboard().line(3, Component.text("§7Players: " + game.getPlayers().size()));

        giveAllPlayersBlockToStandOn();

        if (roundNumber > game.getMaxRounds() / 2.5) {
            for (Player player : game.getPlayers()) {
                PacketUtil.sendActionBar(player, "§cPVP is enabled");
            }
            game.setPvpEnabled(true);
            game.getScoreboard().line(6, Component.text("§cPVP is enabled"));
        } else {
            game.getScoreboard().line(6, Component.text("§aPVP is disabled"));
        }

        roundTask = new BukkitRunnable() {
            int timeLeft = roundDuration;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    game.setPvpEnabled(false);
                    removeAllColorBlocks();
                    game.endRound();
                    cancel();
                    return;
                }

                game.getScoreboard().line(4, Component.text("§7Time left: " + timeLeft));
                timeLeft--;
            }
        };
        roundTask.runTaskTimer(JavaPlugin.getProvidingPlugin(getClass()), 0, 20);
    }

    private void giveAllPlayersBlockToStandOn() {
        for (Player player : game.getPlayers()) {
            player.getInventory().clear();
            player.getInventory().addItem(blockToStandOn);
        }
    }

    private void removeAllColorBlocks() {
        BaseBlock airBlock = new BaseBlock(0);

        FastAsyncWorldEditUtil.runSession(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld(), session -> {
            session.setFastMode(true);
            try {
                session.replaceBlocks(region, blocksNotToStandOn, airBlock);
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        });
    }
}
