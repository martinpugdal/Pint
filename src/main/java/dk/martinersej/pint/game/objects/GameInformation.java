package dk.martinersej.pint.game.objects;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

@Getter
public class GameInformation {

    private final String displayName;
    private final String color;
    private final String name;
    private final String description;
    private final ItemStack icon;

    public GameInformation(String name, String color, String description, ItemStack icon) {
        this.displayName = color + name;
        this.color = color;
        this.name = ChatColor.stripColor(name);
        this.description = description;
        this.icon = icon.clone();
    }

    public GameInformation(String name, String color, String description, Material icon) {
        this(name, color, description, new ItemStack(icon));
    }

    public ItemStack getIcon() {
        return this.icon.clone();
    }
}
