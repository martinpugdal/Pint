package dk.martinersej.pint.warp.command.subcommand;

import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import dk.martinersej.pint.warp.WarpHandler;
import dk.martinersej.pint.warp.WarpUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpDeleteCommand extends SubCommand {

    private final WarpUtil warp;

    public WarpDeleteCommand(JavaPlugin plugin, WarpHandler warpHandler) {
        super(
                plugin,
                "Slet en warp",
                "<navn>",
                "pint.warp.delete",
                "delete"
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

        if (!warp.doesWarpExist(warpID)) {
            player.sendMessage("§cWarp §4" + warpID + " §ceksisterer ikke!");

        } else {
            warp.deleteWarp(warpID);
            player.sendMessage("§aWarp §2" + warpID + " §aer blevet slettet!");
        }

        return CommandResult.success(this);
    }
}
