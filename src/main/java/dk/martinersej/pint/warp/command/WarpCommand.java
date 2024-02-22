package dk.martinersej.pint.warp.command;

import dk.martinersej.pint.utils.command.Command;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.warp.WarpHandler;
import dk.martinersej.pint.warp.WarpUtil;
import dk.martinersej.pint.warp.command.subcommand.WarpDeleteCommand;
import dk.martinersej.pint.warp.command.subcommand.WarpSetCommand;
import org.bukkit.Location;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class WarpCommand extends Command implements CommandExecutor, TabCompleter {

    private final WarpHandler warpHandler;

    public WarpCommand(JavaPlugin plugin, WarpHandler warpHandler) {
        super(plugin);
        this.warpHandler = warpHandler;
        addSubCommand(new WarpSetCommand(plugin, warpHandler));
        addSubCommand(new WarpDeleteCommand(plugin, warpHandler));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        CommandResult result = super.execute(commandSender, strings);
        switch (result.getResult()) {
            case WRONG_USAGE:
                if (result.wrongUsageMessageIsPresent()) {
                    commandSender.sendMessage(result.getWrongUsageMessage());
                } else {
                    String subCommand = strings.length > 0 ? strings[0] : "";
                    commandSender.sendMessage("Korrekt brug: /" + command.getLabel() + " " + result.getSubCommand().getUsage(subCommand));
                }
                break;
            case NO_PERMISSION:
                commandSender.sendMessage("§cDu har ikke adgang til dette");
                break;
            case NO_SUB_COMMAND_FOUND:
                if (hasPermission(commandSender, "pint.warp.use")) {
                    warps((Player) commandSender, strings);
                } else {
                    commandSender.sendMessage("§cDu har ikke adgang til dette");
                }
                break;
            case NO_CONSOLE:
                commandSender.sendMessage("§cDu kan ikke bruge denne kommando fra konsollen");
                break;
        }
        return true;
    }

    private void warps(Player player, String... args) {

        WarpUtil warp = warpHandler.getWarpUtil();

        if (args.length < 1) {
            String warps = String.join(", ", warp.getAllWarps());
            player.sendMessage("§aWarps: §2" + warps);
            return;
        }

        String warpID = args[0].toLowerCase();

        if (!warp.doesWarpExist(warpID)) {
            String warps = String.join(", ", warp.getAllWarps());
            player.sendMessage("§cWarp §4" + warpID + " §ceksisterer ikke!");
            player.sendMessage("§aWarps: §2" + warps);
            return;
        }

        Location location = warp.getWarpLocation(warpID);
        player.teleport(location);
        player.sendMessage("§aTeleporteret til warp §2" + warpID + "§a!");
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        List<String> tabComplete = warpHandler.getWarpUtil().getAllWarps();
        tabComplete.addAll(getAllowedSubCommands(commandSender, strings));

        if (strings.length == 1) {
            for (String warp : warpHandler.getWarpUtil().getAllWarps()) {
                if (!warp.startsWith(strings[0])) {
                    tabComplete.remove(warp);
                }
            }
        }

        return tabComplete;
    }
}

