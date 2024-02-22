package dk.martinersej.pint.utils.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Command {

    private final JavaPlugin plugin;
    private final ArrayList<SubCommand> subCommands = new ArrayList<>();

    public Command(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    protected void addSubCommand(SubCommand subCommand) {
        this.subCommands.add(subCommand);
    }

    protected boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }

    protected boolean isPlayer(CommandSender sender, String notPlayerMessage) {
        if (this.isPlayer(sender)) {
            return true;
        }

        sender.sendMessage(notPlayerMessage);
        return false;
    }

    protected boolean hasPermission(CommandSender sender, String... permissions) {
        for (String perm : permissions) {
            if (sender.hasPermission(perm)) {
                return true;
            }
        }
        return permissions.length == 0;
    }

    protected SubCommand getSubCommandFromAlias(String alias) {
        for (SubCommand subCommand : this.subCommands) {
            if (subCommand.containsAlias(alias)) {
                return subCommand;
            }
        }
        return null;
    }

    protected CommandResult execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            return CommandResult.noSubCommandFound(null);
        }
        SubCommand subCommand = getSubCommandFromAlias(args[0]);
        if (subCommand == null) {
            return CommandResult.subCommandNotExists(null);
        }
        if (!this.hasPermission(sender, subCommand.getPermissions())) {
            return CommandResult.noPermission(subCommand);
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.execute(sender, newArgs);
    }

    protected ArrayList<SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public List<String> getAllowedSubCommands(CommandSender commandSender, String[] strings) {
        Set<String> allowedSubCommandsSet = new HashSet<>(); // Use a set to ensure uniqueness

        for (SubCommand subCommand : this.getSubCommands()) {
            if (hasPermission(commandSender, subCommand.getPermissions())) {
                if (strings.length == 0) {
                    allowedSubCommandsSet.add(subCommand.getAliases()[0]); // Add primary alias directly
                } else if (strings.length == 1) {
                    if (subCommand.containsAlias(strings[0])) {
                        allowedSubCommandsSet.add(strings[0]); // Add matching alias
                    }
                    else {
                        for (String alias : subCommand.getAliases()) {
                            if (alias.startsWith(strings[0])) {
                                allowedSubCommandsSet.add(alias); // Add matching alias
                                break;
                            }
                        }
                    }
                }
            }
        }
        return new ArrayList<>(allowedSubCommandsSet); // Convert set to list before returning
    }

}