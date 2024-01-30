package dk.martinersej.pint.command.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteUnsetSpawnSubCommand extends SubCommand {

    public VoteUnsetSpawnSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Fjern spawnpoint for vote map",
                "",
                "pint.vote.unsetspawn",
                "unsetspawn"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        Pint.getInstance().getVoteHandler().getVoteUtil().deleteSpawnPoint();

        sender.sendMessage("§aSpawnpoint for vote map er blevet fjernet");

        return CommandResult.success(this);
    }
}
