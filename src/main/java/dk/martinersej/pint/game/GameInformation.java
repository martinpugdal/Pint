package dk.martinersej.pint.game;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class GameInformation {

    private final String name;
    private final String description;
    private final ItemStack icon;

    public GameInformation(String name, String description, Material icon) {
        this.name = name;
        this.description = description;
        this.icon = new ItemStack(icon);
    }

    public GameInformation(String name, String description) {
        this(name, description, Material.BOOK);
    }

    public ItemStack getIcon() {
        return this.icon.clone();
    }
}
