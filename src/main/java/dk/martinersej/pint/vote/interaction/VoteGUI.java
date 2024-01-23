package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.utils.gui.BaseGui;
import org.bukkit.event.inventory.InventoryClickEvent;

public class VoteGUI extends BaseGui {

    public VoteGUI() {
        super("Vote", 6);
        build();
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {

    }
}
