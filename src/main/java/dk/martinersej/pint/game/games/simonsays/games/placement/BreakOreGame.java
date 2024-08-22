package dk.martinersej.pint.game.games.simonsays.games.placement;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import dk.martinersej.pint.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

public class BreakOreGame extends SimonGame {

    private Material oreToBreak;
    private Material blockTypeBefore;
    private Location oreSpawnLocation;

    public BreakOreGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 10; // default is 10 in the super class
    }

    private Material[] getOres() {
        return new Material[]{
            Material.COAL_ORE,
            Material.IRON_ORE,
            Material.GOLD_ORE,
            Material.DIAMOND_ORE,
            Material.EMERALD_ORE,
            Material.REDSTONE_ORE,
            Material.LAPIS_ORE
        };
    }

    private Material getRandomOre() {
        return getOres()[(int) (Math.random() * getOres().length)];
    }

    @Override
    public String sayText() {
        return "§7Smadre " + StringUtils.formatEnum(oreToBreak);
    }


    @Override
    public void startGame() {
        oreToBreak = getRandomOre();

        for (Player player : getSimonSaysGame().getPlayers()) {
            player.getInventory().addItem(new ItemStack(Material.DIAMOND_PICKAXE));
        }

        int randomX = (int) (Math.random() * 8) - 4;
        int randomZ = (int) (Math.random() * 8) - 4;
        oreSpawnLocation = getSimonSaysGame().getCurrentGameMap().getCenterLocation().clone().add(randomX, 0, randomZ).getBlock().getLocation();
        blockTypeBefore = oreSpawnLocation.getBlock().getType();
        oreSpawnLocation.getBlock().setType(oreToBreak);
    }

    @Override
    public void stopGame() {
        oreToBreak = null;
        for (Player player : getSimonSaysGame().getPlayers()) {
            getSimonSaysGame().clearInventory(player);
        }
        if (blockTypeBefore != null) {
            oreSpawnLocation.getBlock().setType(blockTypeBefore);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!getSimonSaysGame().isPlayerInGame(event.getPlayer())) return;
        if (event.getBlock().getLocation().equals(oreSpawnLocation)) {
            event.setCancelled(true);
            getSimonSaysGame().finishedTask(event.getPlayer());
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}