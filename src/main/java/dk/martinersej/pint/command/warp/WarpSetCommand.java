package dk.martinersej.pint.command.warp;

import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import dk.martinersej.pint.warp.WarpHandler;
import dk.martinersej.pint.warp.WarpUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpSetCommand extends SubCommand {

    private final WarpUtil warp;

    public WarpSetCommand(JavaPlugin plugin, WarpHandler warpHandler) {
        super(
                plugin,
                "Sæt en warp",
                "<navn>",
                "pint.warp.set",
                "set"
        );
        this.warp = warpHandler.getWarpUtil();
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (args.length < 1) {
            return CommandResult.wrongUsage(this);
        }
        Player player = (Player) sender;

        String warpID = args[0].toLowerCase();

        String[] notAllowed = {"set", "delete"};

        for (String s : notAllowed) {
            if (warpID.equals(s)) {
                player.sendMessage("§cDu kan ikke kalde en warp det!");
                return CommandResult.success(this);
            }
        }

        warp.setWarp(warpID, player.getLocation());
        player.sendMessage("§aWarp §2" + warpID + " §aer blevet sat!");

        return CommandResult.success(this);
    }
}
