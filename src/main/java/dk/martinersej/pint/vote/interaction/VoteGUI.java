package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.utils.ItemBuilder;
import dk.martinersej.pint.utils.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;

public class VoteGUI extends BaseGui {

    public VoteGUI() {
        super("Vote", 6);

        ItemBuilder item = new ItemBuilder(Material.BOOK);
        setItem(0, item.toItemStack());

        build();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        event.getWhoClicked().sendMessage("You clicked in the vote gui!");
    }
}
