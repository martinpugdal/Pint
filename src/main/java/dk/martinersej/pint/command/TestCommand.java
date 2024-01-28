package dk.martinersej.pint.command;

import dk.martinersej.pint.Pint;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        if (strings.length > 0) {
            if (strings[0].equalsIgnoreCase("paste")) {
                commandSender.sendMessage("Pasting schematic");
                Pint.getInstance().getVoteHandler().getVoteMap().pasteSchematic();
            } else if (strings[0].equalsIgnoreCase("clear")) {

                if (Pint.getInstance().getVoteHandler().getVoteMap().isPresent()) {
                    Pint.getInstance().getVoteHandler().getVoteMap().clearSchematic();
                    commandSender.sendMessage("Clearing schematic");
                } else {
                    commandSender.sendMessage("Vote map is not present and will not be cleared");
                }
            }
        } else {
            commandSender.sendMessage("Korrekt brug: /test <paste/clear>");
        }

        return true;
    }
}

