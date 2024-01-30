package dk.martinersej.pint.command;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        if (strings.length > 0) {
            if (Pint.getInstance().getGameHandler().getCurrentGame() == null) {
                Game game = Pint.getInstance().getGameHandler().getGamePool().getGames().get(0);
                Pint.getInstance().getGameHandler().setCurrentGame(game);
                game.setCurrentGameMap(game.getRandomMap(0));
            }
            Game game = Pint.getInstance().getGameHandler().getCurrentGame();
            if (strings[0].equalsIgnoreCase("paste")) {
                commandSender.sendMessage("Pasting schematic");
                game.getCurrentGameMap().pasteSchematic();
            } else if (strings[0].equalsIgnoreCase("clear")) {
                commandSender.sendMessage("Clearing schematic");
                if (game.getCurrentGameMap() != null) {
                    game.getCurrentGameMap().clearSchematic();
                    commandSender.sendMessage("Cleared schematic");
                } else {
                    commandSender.sendMessage("Current game map is null");
                }
            }
        } else {
            commandSender.sendMessage("Korrekt brug: /test <paste/clear>");
        }

        return true;
    }
}

