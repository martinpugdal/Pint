package dk.martinersej.pint.simplecommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.games.tnttag.TntTagGame;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TestCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {

        Player player = (Player) commandSender;
        ((TntTagGame) Pint.getInstance().getGameHandler().getGame(1)).blowUpTntPlayer(player);

        return true;
    }


//    @Override
//    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
//
//        if (strings.length > 0) {
//            if (Pint.getInstance().getGameHandler().getCurrentGame() == null) {
//                Game game = Pint.getInstance().getGameHandler().getGames().get(0);
//                Bukkit.broadcastMessage("Setting current game to " + game.getGameInformation().getName());
//                Pint.getInstance().getGameHandler().setCurrentGame(game);
//                GameMap gameMap = game.getRandomMap(0);
//                Bukkit.broadcastMessage("Setting current game map to " + gameMap);
//                game.setCurrentGameMap(gameMap);
//            }
//            Game game = Pint.getInstance().getGameHandler().getCurrentGame();
//            if (strings[0].equalsIgnoreCase("paste")) {
//                commandSender.sendMessage("Pasting schematic");
//                game.getCurrentGameMap().pasteSchematic();
//            } else if (strings[0].equalsIgnoreCase("clear")) {
//                commandSender.sendMessage("Clearing schematic");
//                if (game.getCurrentGameMap() != null) {
//                    game.getCurrentGameMap().clearSchematic();
//                    commandSender.sendMessage("Cleared schematic");
//                } else {
//                    commandSender.sendMessage("Current game map is null");
//                }
//            } else if (strings[0].equalsIgnoreCase("tp")) {
//                commandSender.sendMessage("Teleporting to spawn points");
//                new BukkitRunnable() {
//                    int i = 0;
//                    @Override
//                    public void run() {
//                        if (i < game.getCurrentGameMap().getSpawnPoints().size()) {
//                            Player player = (Player) commandSender;
//
//                            player.teleport(game.getCurrentGameMap().getSpawnPoint(i));
//                            i++;
//                        } else {
//                            cancel();
//                        }
//                    }
//                }.runTaskTimer(Pint.getInstance(), 0, 100);
//            } else {
//                commandSender.sendMessage("Korrekt brug: /test <paste/clear/tp>");
//            }
//        } else {
//            commandSender.sendMessage("Korrekt brug: /test <paste/clear/tp>");
//        }
//
//        return true;
//    }
}

