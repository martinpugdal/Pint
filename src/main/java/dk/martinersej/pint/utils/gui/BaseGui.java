package dk.martinersej.pint.utils.gui;

import dk.martinersej.pint.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.WeakHashMap;
import java.util.function.Supplier;

public abstract class BaseGui implements InventoryHolder {

    private final int rows;
    private final WeakHashMap<Integer, ItemStack> items = new WeakHashMap<>();
    private final WeakHashMap<Player, Long> cooldowns = new WeakHashMap<>();
    private final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(getClass());
    private String title;
    private Inventory inventory;
    private double cooldown = 0;

    public BaseGui(String title, int rows) {
        this.title = title;
        this.rows = rows;
        this.inventory = plugin.getServer().createInventory(this, rows * 9, title);
    }

    public int getRows() {
        return rows;
    }

    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    public WeakHashMap<Integer, ItemStack> getItems() {
        return items;
    }

    public String getTitle() {
        return title;
    }

    public BaseGui setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void setItem(int slot, ItemStack item) {
        items.put(slot, item);
    }

    public void setItem(int row, int col, ItemStack item) {
        setItem((row - 1) * 9 + col, item);
    }

    public void updateItem(int slot, ItemStack item) {
        if (items.get(slot) == null) {
            items.put(slot, item);
        } else {
            items.replace(slot, item);
        }
        inventory.setItem(slot, item);
        for (HumanEntity viewer : inventory.getViewers()) {
            viewer.getOpenInventory().setItem(slot, item);
        }
    }

    public void updateItem(int row, int col, ItemStack item) {
        updateItem((row - 1) * 9 + col, item);
    }

    public void addUpdatingItem(int slot, Supplier<ItemStack> supplier) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (getInventory().getViewers().isEmpty()) {
                    cancel();
                }
                updateItem(slot, supplier.get());
            }
        }.runTaskTimerAsynchronously(plugin, 0L, 1L);
    }

    public void clearItems() {
        items.clear();
    }

    void populateGui() {
        items.forEach(inventory::setItem);
    }

    public void rerender() {
        inventory.clear();
        populateGui();
        for (HumanEntity viewer : new ArrayList<>(inventory.getViewers())) ((Player) viewer).updateInventory();
    }

    /**
     * Rerenders the inventory, where the title and rows are updated.
     *
     * @param hardRerender If true, the inventory is recreated, and all viewers are reopened.
     */
    public void rerender(boolean hardRerender) {
        if (hardRerender) {
            Inventory oldInventory = inventory;
            inventory = plugin.getServer().createInventory(this, rows * 9, title);
            new ArrayList<>(oldInventory.getViewers()).forEach(viewer -> viewer.openInventory(inventory));
        }
    }

    public void setRow(ItemStack item, int... row) {
        for (int i : row) {
            for (int j = 0; j < 9; j++) {
                int slot = i * 9 + j;
                setItem(slot, item);
            }
        }
    }

    public void setCol(ItemStack item, int... col) {
        for (int i : col) {
            for (int j = 0; j < rows; j++) {
                int slot = j * 9 + i;
                setItem(slot, item);
            }
        }
    }

    public void setCooldown(double cooldownInSeconds) {
        this.cooldown = cooldownInSeconds;
    }

    public boolean cooldownEnabled() {
        return cooldown > 0;
    }

    private boolean hasCooldown(Player player) {
        return cooldowns.containsKey(player) && getCooldownLeft(player) > 0;
    }

    private double getCooldownLeft(Player player) {
        return (cooldowns.get(player) + cooldown * 1000 - System.currentTimeMillis()) / 1000;
    }

    private void setCooldown(Player player) {
        cooldowns.put(player, System.currentTimeMillis());
    }

    public abstract void onInventoryClick(final InventoryClickEvent event);

    public void cooldownCheck(InventoryClickEvent event) {
        if (cooldownEnabled()) {
            if (hasCooldown((Player) event.getWhoClicked())) {
                event.setCancelled(true);
                event.getWhoClicked().sendMessage(cooldownMessage(((Player) event.getWhoClicked())));
                return;
            }
            setCooldown((Player) event.getWhoClicked());
        }
    }

    public String cooldownMessage(Player player) {
        return "§cDu skal vente " + getCooldownLeft(player) + " sekunder før du kan gøre det igen.";
    }

    public void build() {
        inventory.clear();
        populateGui();
    }

    public void open(Player player) {
        rerender();
        player.openInventory(inventory);
    }

    public void close(Player player) {
        player.closeInventory();
    }

    public enum BaseItem {

        CLOSE_MENU(new ItemStack(Material.NETHER_STAR), "§cLuk menuen", "§fTryk for at lukke menuen."),
        BACK(new ItemStack(Material.ARROW), "§cTilbage", "§fTryk for at gå tilbage."),
        NEXT(new ItemStack(Material.ARROW), "§cNæste", "§fTryk for at gå videre."),
        PREVIOUS(new ItemStack(Material.ARROW), "§cForrige", "§fTryk for at gå tilbage."),
        FILLED(new ItemStack(Material.STAINED_GLASS_PANE), "§f", "§f");

        private final ItemStack itemStack;

        BaseItem(ItemStack itemStack, String name, String... lores) {
            this.itemStack = new ItemBuilder(itemStack).setName(name).setLore(lores).build();
        }

        public ItemStack getItemStack() {
            return itemStack.clone();
        }
    }
}