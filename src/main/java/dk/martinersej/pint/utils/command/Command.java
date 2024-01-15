package dk.martinersej.pint.utils.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
            return new CommandResult(null, Result.NO_SUB_COMMAND_FOUND);
        }
        SubCommand subCommand = getSubCommandFromAlias(args[0]);
        if (subCommand == null) {
            return new CommandResult(subCommand, Result.NO_SUB_COMMAND_FOUND);
        }
        if (!this.hasPermission(sender, subCommand.getPermissions())) {
            return new CommandResult(subCommand, Result.NO_PERMISSION);
        }
        String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
        return subCommand.execute(sender, newArgs);
    }

    protected ArrayList<SubCommand> getSubCommands() {
        return this.subCommands;
    }

    public List<String> getAllowedSubCommands(CommandSender commandSender, org.bukkit.command.Command command, String label, String[] strings) {
        ArrayList<String> allowedSubCommands = new ArrayList<>();
        for (SubCommand subCommand : this.getSubCommands()) {
            if (hasPermission(commandSender, subCommand.getPermissions()) && strings.length == 1) {
                if (subCommand.containsAlias(strings[0])) {
                    allowedSubCommands.add(subCommand.getAliases()[0]);
                } else {
                    for (String alias : subCommand.getAliases()) {
                        if (alias.startsWith(strings[0])) {
                            allowedSubCommands.add(alias);
                            break;
                        }
                    }
                }
            }
        }
        return allowedSubCommands;
    }
}