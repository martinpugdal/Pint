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
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VoteSetSchematicSubCommand extends SubCommand {

    public VoteSetSchematicSubCommand(JavaPlugin plugin) {
        super(
                plugin,
                "Gem schematic for vote map",
                "",
                "pint.vote.setschematic",
                "setschematic"
        );
    }

    @Override
    public CommandResult execute(CommandSender sender, String[] args) {

        if (!(sender instanceof Player)) {
            return CommandResult.noConsole(this);
        }

        Player player = (Player) sender;

        try {
            LocalSession localSession = Fawe.get().getWorldEdit().getSession(player.getName());
            RegionSelector selector = localSession.getRegionSelector(BukkitUtil.getLocalWorld(player.getWorld()));
            Selection selection = new CuboidSelection(player.getWorld(), selector, (CuboidRegion) selector.getRegion());

            Location corner1 = selection.getMinimumPoint();
            Location corner2 = selection.getMaximumPoint();

            Pint.getInstance().getVoteHandler().getVoteUtil().saveMapSchematic(corner1, corner2);
            try {
                Pint.getInstance().getVoteHandler().getVoteMap().clearSchematic();
            } catch (Exception e) {
                Bukkit.getLogger().warning("Could not clear schematic for vote map");
            }
            Pint.getInstance().getVoteHandler().loadVoteMap();
            Pint.getInstance().getVoteHandler().getVoteMap().pasteSchematic();

            for (Player p : Bukkit.getOnlinePlayers()) {
                if (Pint.getInstance().getGameHandler().getCurrentGame() != null) {
                    if (Pint.getInstance().getGameHandler().getCurrentGame().getPlayers().contains(p)) {
                        continue;
                    }
                }
                p.teleport(Pint.getInstance().getVoteHandler().getVoteUtil().spawnLocation());
            }
        } catch (IncompleteRegionException e) {
            CommandResult.wrongUsage(this, "Du skal have en WorldEdit selection");
        }

        sender.sendMessage("§aSchematic for vote map er blevet sat og pastet ind");

        return CommandResult.success(this);
    }
}
