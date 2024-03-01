package dk.martinersej.pint.game.games.simonsays.games.placement;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import dk.martinersej.pint.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class BreakOreGame extends SimonGame {

    private Material oreToBreak;

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
        Location oreSpawnLocation = getSimonSaysGame().getCurrentGameMap().getCenterLocation().clone().add(randomX, 0, randomZ);
        oreSpawnLocation.getBlock().setType(oreToBreak);
    }

    @Override
    public void stopGame() {
        oreToBreak = null;
        for (Player player : getSimonSaysGame().getPlayers()) {
            getSimonSaysGame().clearInventory(player);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() == oreToBreak) {
            event.setCancelled(true);
            event.getPlayer().sendBlockChange(event.getBlock().getLocation(), Material.AIR, (byte) 0);
        }
    }

    @EventHandler
    public void onBlockInteraction(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == oreToBreak) {
            event.setCancelled(true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().sendBlockChange(event.getClickedBlock().getLocation(), oreToBreak, (byte) 0);
                }
            }.runTaskLater(Pint.getInstance(), 1L);
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PLACEMENT;
    }
}