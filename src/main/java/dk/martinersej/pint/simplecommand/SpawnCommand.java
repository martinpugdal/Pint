package dk.martinersej.pint.simplecommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof org.bukkit.entity.Player)) return false;

        Game game = Pint.getInstance().getGameHandler().getCurrentGame();
        Player player = (Player) commandSender;

        if (game != null) {
            if (game.getPlayers().contains(player)) {
                commandSender.sendMessage("§cDu kan ikke bruge denne kommando når du er i et spil");
                return true;
            }
        }
        player.teleport(Pint.getInstance().getVoteHandler().getVoteUtil().spawnLocation());
        commandSender.sendMessage("§aDu er blevet teleporteret til spawn");
        return true;
    }
}
