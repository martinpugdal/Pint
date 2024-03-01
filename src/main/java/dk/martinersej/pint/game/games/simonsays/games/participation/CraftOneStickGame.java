package dk.martinersej.pint.game.games.simonsays.games.participation;

import dk.martinersej.pint.game.games.simonsays.SimonSaysGame;
import dk.martinersej.pint.game.games.simonsays.objects.ScoringType;
import dk.martinersej.pint.game.games.simonsays.objects.SimonGame;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CraftOneStickGame extends SimonGame {

    List<ItemStack> craftingItems = new ArrayList<>();

    public CraftOneStickGame(SimonSaysGame simonSaysGame) {
        super(simonSaysGame);
    }

    @Override
    public void startGame() {
        craftingItems.clear();
        ItemStack item = new ItemStack(Material.LOG);
        for (Player player : getSimonSaysGame().getPlayers()) {
            // set the item in a random slot in hotbar
            int random = (int) (Math.random() * 9);
            player.getInventory().setItem(random, item.clone());
        }
    }

    @Override
    public int getaskDuration() {
        return 5; // default is 10 in the super class
    }

    @Override
    public String sayText() {
        return "§7Lav en pind";
    }

    @Override
    public void stopGame() {
        for (ItemStack craftingItem : craftingItems) {
            if (craftingItem != null)
                craftingItem.setType(Material.AIR);
        }
        craftingItems.clear();
        for (Player player : getSimonSaysGame().getPlayers()) {
            getSimonSaysGame().clearInventory(player);
            player.closeInventory();
        }
    }

    @EventHandler
    public void onCraftStick(CraftItemEvent event) {
        if (!getSimonSaysGame().isPlayerInGame((Player) event.getWhoClicked())) return;
        if (event.getRecipe().getResult().getType().equals(Material.STICK)) {
            getSimonSaysGame().finishedTask((Player) event.getWhoClicked());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (getSimonSaysGame().isPlayerInGame((Player) event.getWhoClicked())) {
            if (event.getClickedInventory().getType().equals(InventoryType.CRAFTING)) {
                craftingItems.add(event.getCurrentItem());
            }
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