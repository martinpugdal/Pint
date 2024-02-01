package dk.martinersej.pint.command.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteSetSpawnSubCommand extends SubCommand {

    public VoteSetSpawnSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Sæt spawnpoint for vote map",
                "[yaw (boolean)] [pitch (boolean)]",
                "pint.vote.setspawn",
                "setspawn"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return CommandResult.noConsole(this);
        }

        Player player = (Player) sender;

        boolean yaw = false;
        boolean pitch = false;
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("false") || args[0].equalsIgnoreCase("true")) {
                yaw = Boolean.parseBoolean(args[0]);
            } else {
                sender.sendMessage("§cYaw skal være true eller false");
                return CommandResult.success(this);
            }
            if (args.length > 1) {
                if (args[1].equalsIgnoreCase("false") || args[1].equalsIgnoreCase("true")) {
                    pitch = Boolean.parseBoolean(args[1]);
                } else {
                    sender.sendMessage("§cPitch skal være true eller false");
                    return CommandResult.success(this);
                }
            }
        }

        Pint.getInstance().getVoteHandler().getVoteUtil().setSpawnPoint(player.getLocation(), yaw, pitch);

        sender.sendMessage("§aSpawnpoint for vote map er blevet sat");

        return CommandResult.success(this);
    }
}
