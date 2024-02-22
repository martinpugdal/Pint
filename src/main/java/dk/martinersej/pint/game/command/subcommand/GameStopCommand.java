package dk.martinersej.pint.game.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class GameStopCommand extends SubCommand {

    public GameStopCommand(JavaPlugin plugin) {
        super(
            plugin,
            "stop et aktivt spil",
            "",
            "pint.game.stop",
            "stop"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length != 0) {
            return CommandResult.wrongUsage(this);
        }

        if (!Pint.getInstance().getGameHandler().isGameRunning()) {
            sender.sendMessage("§cDer er ikke noget spil igang");
        } else {
            Pint.getInstance().getGameHandler().getCurrentGame().stop();
            sender.sendMessage("§aDet aktive spil er stoppet");
        }

        return CommandResult.success(this);
    }
}
