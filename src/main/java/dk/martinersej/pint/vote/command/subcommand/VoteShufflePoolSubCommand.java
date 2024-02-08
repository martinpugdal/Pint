package dk.martinersej.pint.vote.command.subcommand;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteShufflePoolSubCommand extends SubCommand {

    public VoteShufflePoolSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Shuffle vote pool",
                "",
                "pint.vote.shufflepool",
                "shufflepool"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();

        sender.sendMessage("§aVote pool er blevet shufflet");

        return CommandResult.success(this);
    }
}
