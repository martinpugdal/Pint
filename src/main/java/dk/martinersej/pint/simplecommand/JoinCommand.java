package dk.martinersej.pint.simplecommand;

import dk.martinersej.pint.Pint;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("§cDu skal være en spiller for at kunne bruge denne kommando");
            return true;
        }
        Player player = (Player) commandSender;
        if (Pint.getInstance().getVoteHandler().getVote(player) != null) {
            Pint.getInstance().getVoteHandler().removeVote(player);
            player.sendMessage("§cDu vil nu ikke længere deltage i det næste spil!");
        } else {
            Pint.getInstance().getVoteHandler().joinVoteWithNoVote(player);
            player.sendMessage("§aDu vil nu deltage det næste spil!");
        }
        Pint.getInstance().getVoteHandler().getVoteUtil().updateJoinItem(player);

        Bukkit.getOfflinePlayer(player.getUniqueId());

        return true;
    }
}

