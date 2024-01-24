package dk.martinersej.pint.command.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.Result;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MapSetPlayersSubCommand extends SubCommand {

    public MapSetPlayersSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Sætter max antal spillere for et map",
                "<id> <min|max> <antal spillere>",
                "pint.map.setmaxplayers",
                "setmaxplayers"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {
        if (args.length != 3) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        } else if (!(sender instanceof Player)) {
            return Result.getCommandResult(Result.NO_PERMISSION, this);
        }
        boolean max = true;
        if (args[1].equalsIgnoreCase("min")) {
            max = false;
        } else if (!args[1].equalsIgnoreCase("max")) {
            return Result.getCommandResult(Result.WRONG_USAGE, this);
        }


        int id;
        int players;
        try {
            id = Integer.parseInt(args[0]);
            players = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            sender.sendMessage("§cDu skal skrive et tal");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

        if (!Pint.getInstance().getMapHandler().mapIsPresent(id)) {
            sender.sendMessage("§cEt map med id " + id + " findes ikke");
            return Result.getCommandResult(Result.SUCCESS, this);
        }

//        if (Pint.getInstance().getGameHandler().getCurrentGame().getCurrentGameMap().getId() == id) {
//            sender.sendMessage("§cDu kan ikke redigere det map som er i brug");
//            return Result.getCommandResult(Result.SUCCESS, this);
//        }

        if (max) {
            if (players < Pint.getInstance().getMapHandler().getMap(id).getMinPlayers()) {
                sender.sendMessage("§cMax spillere kan ikke være mindre end min spillere");
            } else {
                Pint.getInstance().getMapHandler().setMinPlayers(id, players);
                sender.sendMessage("§aMin spillere for map " + id + " er nu " + players);
            }
        } else {
            if (players > Pint.getInstance().getMapHandler().getMap(id).getMaxPlayers()) {
                sender.sendMessage("§cMin spillere kan ikke være større end max spillere");
            } else {
                Pint.getInstance().getMapHandler().setMaxPlayers(id, players);
                sender.sendMessage("§aMax spillere for map " + id + " er nu " + players);
            }
        }

        return Result.getCommandResult(Result.SUCCESS, this);
    }
}
