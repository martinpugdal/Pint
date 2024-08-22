package dk.martinersej.pint.game.games.simonsays.games.participation;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import dk.martinersej.pint.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DropItemGame extends SimonGame {

    Material[] materials = Material.values();
    // items is not visible in inventory so it is not needed
    Material[] blacklist = new Material[]{
        Material.AIR,
        Material.SIGN_POST,
        Material.WALL_SIGN,
    };
    private Material dropItem;

    public DropItemGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 5; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Drop " + StringUtils.formatEnum(dropItem) + "!";
    }

    private List<ItemStack> getRandomItems(int amount) {
        List<ItemStack> randomItems = new ArrayList<>();
        materialsLoop:
        for (int i = 0; i < amount; i++) {
            int randomIndex = (int) (Math.random() * materials.length);
            if (randomItems.contains(new ItemStack(materials[randomIndex]))) {
                i--;
                continue;
            }
            for (Material material : blacklist) {
                if (materials[randomIndex] == material) {
                    i--;
                    continue materialsLoop;
                }
            }
            randomItems.add(new ItemStack(materials[randomIndex]));
        }
        return randomItems;
    }

    @Override
    public void startGame() {
        List<ItemStack> randomItems = getRandomItems(20);
        int randomIndex = (int) (Math.random() * randomItems.size());
        dropItem = randomItems.get(randomIndex).getType();

        ItemStack airItem = new ItemStack(Material.AIR);
        int numberOfAirItems = 9;
        for (int i = 0; i < numberOfAirItems; i++) {
            randomItems.add(airItem);
        }
        for (Player player : getSimonSaysGame().getPlayers()) {
            Collections.shuffle(randomItems);
            player.getInventory().clear();
            player.getInventory().setContents(randomItems.toArray(new ItemStack[0]));
        }
    }

    @Override
    public void stopGame() {
        for (Player player : getSimonSaysGame().getPlayers()) {
            getSimonSaysGame().clearInventory(player);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (getSimonSaysGame().isPlayerInGame(event.getPlayer())) {
            if (event.getItemDrop().getItemStack().getType() == dropItem) {
                getSimonSaysGame().finishedTask(event.getPlayer());
            } else {
                getSimonSaysGame().failedPlayer(event.getPlayer());
                event.setCancelled(true);
            }
            event.getItemDrop().setPickupDelay(Integer.MAX_VALUE);
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getItemDrop().remove();
                }
            }.runTaskLater(Pint.getInstance(), 20L * 2);
            getSimonSaysGame().clearInventory(event.getPlayer());
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PARTICIPATION;
    }
}