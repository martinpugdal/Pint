package dk.martinersej.pint.command;

import dk.martinersej.pint.Pint;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {


        Pint.getInstance().getVoteHandler().loadVoteMap();

        if (strings.length > 0) {
            if (strings[0].equalsIgnoreCase("paste")) {
                Pint.getInstance().getVoteHandler().getVoteMap().pasteSchematic();
            } else if (strings[0].equalsIgnoreCase("clear")) {
                Pint.getInstance().getVoteHandler().getVoteMap().clearSchematic();
            }
        } else {
            commandSender.sendMessage("Korrekt brug: /test <paste/clear>");
        }

        return true;
    }
}

