package dk.martinersej.pint.command.vote;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteUnsetSchematicSubCommand extends SubCommand {

    public VoteUnsetSchematicSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Fjern schematic for vote map",
                "",
                "pint.vote.unsetschematic",
                "unsetschematic"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return CommandResult.noConsole(this);
        }

        Game currentGame = Pint.getInstance().getGameHandler().getCurrentGame();
        if (currentGame != null) {
            if (currentGame.getCurrentGameMap() != null) {
                if (currentGame.getCurrentGameMap().isPasted()) {
                    sender.sendMessage("§cDu kan ikke fjerne schematic for vote map mens en game kører");
                    return CommandResult.success(this);
                }
            }
        }

        Pint.getInstance().getVoteHandler().getVoteUtil().removeMapSchematic();
        sender.sendMessage("§aSchematic for vote map er blevet fjernet");

        return CommandResult.success(this);
    }
}
