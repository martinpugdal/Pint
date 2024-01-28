package dk.martinersej.pint.utils.command;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class SubCommand extends Command {

    private final String description;
    private final String usage;
    private final String[] permissions;
    private final String[] aliases;

    public SubCommand(JavaPlugin plugin, String description, String usage, String permission, String... aliases) {
        super(plugin);

        this.description = description;
        this.usage = usage;
        this.permissions = new String[]{permission};
        this.aliases = aliases;
    }

    public SubCommand(JavaPlugin plugin, String description, String usage, String[] permissions, String... aliases) {
        super(plugin);

        this.description = description;
        this.usage = usage;
        this.permissions = permissions;
        this.aliases = aliases;
    }

    protected boolean containsAlias(String alias) {
        for (String s : this.aliases) {
            if (s.equalsIgnoreCase(alias)) {
                return true;
            }
        }
        return false;
    }

    public abstract CommandResult execute(CommandSender sender, String[] args);


    public String[] getPermissions() {
        return this.permissions;
    }

    public String getUsage(String label) {
        return label + " " + this.usage;
    }

    public String getDescription() {
        return this.description;
    }

    public String[] getAliases() {
        return this.aliases;
    }
}