package dk.martinersej.pint.command.vote;

import com.boydti.fawe.Fawe;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.RegionSelector;
import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.utils.FastAsyncWorldEditUtil;
import dk.martinersej.pint.utils.command.CommandResult;
import dk.martinersej.pint.utils.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
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

        Game currentGame = Pint.getInstance().getGameHandler().getCurrentGame();
        boolean isGameRunning = false;
        if (currentGame != null) {
            if (currentGame.getCurrentGameMap() != null) {
                if (currentGame.getCurrentGameMap().isPasted()) {
                    isGameRunning = true;
                }
            }
        }

        if (player.getWorld().equals(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld())) {
            sender.sendMessage("§cDu kan ikke sætte et schematic i serverens verden");
            return CommandResult.success(this);
        }

        try {
            LocalSession localSession = Fawe.get().getWorldEdit().getSession(player.getName());
            RegionSelector selector = localSession.getRegionSelector(BukkitUtil.getLocalWorld(player.getWorld()));
            Selection selection = new CuboidSelection(player.getWorld(), selector, (CuboidRegion) selector.getRegion());

            Location corner1 = selection.getMinimumPoint();
            Location corner2 = selection.getMaximumPoint();
            Pint.getInstance().getVoteHandler().getVoteUtil().saveMapSchematic(corner1, corner2);
            if (isGameRunning) {
                sender.sendMessage("§aSchematic er gemt for dette vote map");
                return CommandResult.success(this);
            }
            try {
                if (Pint.getInstance().getVoteHandler().defaultVoteMap()) {
                    Bukkit.getLogger().info("Clearing schematic for default vote map");
                    World serverWorld = Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld();
                    FastAsyncWorldEditUtil.runSession(serverWorld, session -> {
                        try {
                            session.setBlock(new Vector(0, 0, 0), new BaseBlock(0));
                        } catch (MaxChangedBlocksException ex) {
                            ex.printStackTrace();
                        }
                    });
                } else {
                    Pint.getInstance().getVoteHandler().getVoteMap().clearSchematic();
                }
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
