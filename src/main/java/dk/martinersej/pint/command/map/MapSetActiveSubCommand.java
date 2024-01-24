package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapSetActiveSubCommand extends SubCommand {

    public MapSetActiveSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "om map skal bruges",
                "<id> <false/true>",
                "pint.map.setactive",
                "setactive"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        } else if (!(sender instanceof Player)) {
            return Result.getCommandResult(Result.NO_PERMISSION, this);
        }

        int id;
        try {
            id = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cDu skal skrive et tal");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

        boolean active;
        try {
            active = Boolean.parseBoolean(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cDu skal skrive true eller false");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

        if (!Pint.getInstance().getMapHandler().mapIsPresent(id)) {
            sender.sendMessage("§cEt map med id " + id + " findes ikke");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

        Pint.getInstance().getMapHandler().setActive(id, active);
        sender.sendMessage("§aMap med id " + id + " er nu sat til at være " + (active ? "aktivt" : "inaktivt"));

        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
