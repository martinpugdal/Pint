package dk.martinersej.pint.game.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class GameListCommand extends SubCommand {


    public GameListCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Vis alle spil",
                "",
                "pint.game.list",
                "list"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        Set<Game> games = Pint.getInstance().getGameHandler().getGames();
        Set<Game> gamesInPool = Pint.getInstance().getGameHandler().getGamePool().getGames();
        for (Game game : games) {
            sender.sendMessage(" §8§m-§f " + game.getGameInformation().getName() + " §7(§cID: " + game.getId() + "§7)" + (gamesInPool.contains(game) ? "  §7(§a§naktiv§r§7)" : ""));
        }
        return CommandResult.success(this);
    }
}
