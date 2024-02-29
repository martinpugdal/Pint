package dk.martinersej.pint.game.games.simonsays.games.participation;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import dk.martinersej.pint.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EatItemGame extends SimonGame {

    public EatItemGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public int getaskDuration() {
        return 5; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Spis " + StringUtils.formatEnum(foodItem) + "!";
    }

    private Material foodItem;
    private final List<Material> edibles = getAllEdible();

    List<Material> getAllEdible() {
        List<Material> edible = new ArrayList<>();
        for (Material material : Material.values()) {
            if (material.isEdible()) {
                edible.add(material);
            }
        }
        edible.remove(Material.ROTTEN_FLESH);
        edible.remove(Material.SPIDER_EYE);
        return edible;
    }

    private List<ItemStack> get8RandomEdibles() {
        List<ItemStack> randomEdibles = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int randomIndex = (int) (Math.random() * edibles.size());
            if (edibles.get(randomIndex) == foodItem) {
                i--;
                continue;
            }
            randomEdibles.add(new ItemStack(edibles.get(randomIndex)));
        }
        return randomEdibles;
    }

    @Override
    public void startGame() {
        int randomIndex = (int) (Math.random() * edibles.size());
        foodItem = edibles.get(randomIndex);
        List<ItemStack> randomEdibles = get8RandomEdibles();
        randomEdibles.add(new ItemStack(foodItem));
        for (Player player : getSimonSaysGame().getPlayers()) {
            Collections.shuffle(randomEdibles);
            player.getInventory().setContents(randomEdibles.toArray(new ItemStack[0]));
            player.setFoodLevel(19);
        }
    }

    @Override
    public void stopGame() {
        for (Player player : getSimonSaysGame().getPlayers()) {
            getSimonSaysGame().clearInventory(player);
            player.setFoodLevel(20);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onConsumeItem(PlayerDropItemEvent event) {
        if (getSimonSaysGame().isPlayerInGame(event.getPlayer())) {
            if (event.getItemDrop().getItemStack().getType() == foodItem) {
                getSimonSaysGame().finishedTask(event.getPlayer());
            } else {
                getSimonSaysGame().failedPlayer(event.getPlayer());
            }
            event.getItemDrop().remove();
            getSimonSaysGame().clearInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        if (getSimonSaysGame().isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @Override
    public ScoringType getScoringType() {
        return ScoringType.PARTICIPATION;
    }
}