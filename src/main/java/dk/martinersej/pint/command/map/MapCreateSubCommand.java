package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class MapCreateSubCommand extends SubCommand {

    public MapCreateSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Opretter et map med id",
                "<id>",
                "pint.map.create",
                "create", "opret"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length != 1) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        }

        String id = args[0].toLowerCase();

        if (Pint.getInstance().getMapHandler().mapExists(id)) {
            sender.sendMessage("§cEt map med dette id eksisterer allerede");
        }

        Pint.getInstance().getMapHandler().createMap(id);
        sender.sendMessage("§aMap med id " + id + " er blevet oprettet");

        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
