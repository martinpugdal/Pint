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
                "",
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

        Pint.getInstance().getVoteHandler().getVoteUtil().setSpawnPoint(player.getLocation());

        sender.sendMessage("§aSpawnpoint for vote map er blevet sat");

        return CommandResult.success(this);
    }
}
