package dk.martinersej.pint.map.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapSetPlayersSubCommand extends SubCommand {

    public MapSetPlayersSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Sætter max antal spillere for et map",
                "<mapID> <min|max> <antal spillere>",
                "pint.map.setmaxplayers",
                "setmaxplayers"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length != 3) {
            return CommandResult.wrongUsage(this);
        } else if (!(sender instanceof Player)) {
            return CommandResult.noConsole(this);
        }
        boolean max = true;
        if (args[1].equalsIgnoreCase("min")) {
            max = false;
        } else if (!args[1].equalsIgnoreCase("max")) {
            return CommandResult.wrongUsage(this, "§cDu skal skrive min eller max");
        }

        int id;
        int players;
        try {
            id = Integer.parseInt(args[0]);
            players = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return CommandResult.wrongUsage(this, "§cDu skal skrive et tal");
        }

        if (!Pint.getInstance().getMapHandler().mapIsPresent(id)) {
            return CommandResult.wrongUsage(this, "§cEt map med id " + id + " findes ikke");
        }

        if (max) {
            if (players < Pint.getInstance().getMapHandler().getMap(id).getMinPlayers()) {
                sender.sendMessage("§cMax spillere kan ikke være mindre end min spillere");
            } else {
                Pint.getInstance().getMapHandler().setMaxPlayers(id, players);
                sender.sendMessage("§aMax spillere for map " + id + " er nu " + players);
            }
        } else {
            if (players > Pint.getInstance().getMapHandler().getMap(id).getMaxPlayers()) {
                sender.sendMessage("§cMin spillere kan ikke være større end max spillere");
            } else {
                Pint.getInstance().getMapHandler().setMinPlayers(id, players);
                sender.sendMessage("§aMin spillere for map " + id + " er nu " + players);
            }
        }

        return CommandResult.success(this);
    }
}
