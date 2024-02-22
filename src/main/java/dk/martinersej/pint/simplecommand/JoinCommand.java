package dk.martinersej.pint.simplecommand;

import dk.martinersej.pint.Pint;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor {

    public JoinCommand() {
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cDu skal være en spiller for at kunne bruge denne kommando");
            return true;
        }

        Pint.getInstance().getVoteHandler().joinVoteWithNoVote((Player) commandSender);
        Pint.getInstance().getVoteHandler().getVoteUtil().updateJoinItem((Player) commandSender);

        commandSender.sendMessage("§aDu vil nu deltage i spillet!");
        return true;
    }
}

