package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.utils.ItemBuilder;
import dk.martinersej.pint.utils.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class VoteGUI extends BaseGui {

    private final Map<Game, Integer> itemSlots = new HashMap<>();

    public VoteGUI(Player player) {
        super("Vote", 4);

        Game[] voteGames = Pint.getInstance().getGameHandler().getGamePool().getVoteGames();

        int games = 0;
        for (Game game : voteGames) {
            if (game != null) {
                games++;
            }
        }

        int slot = 11;
        int raiseSlot = 2;

        if (games == 1) {
            slot = 13;
        } else if (games == 2) {
            slot = 12;
        }

        if (games == 0) {
            slot = 13;
            setItem(slot, new ItemBuilder(Material.BARRIER).setName("§cDer er ingen spil at stemme på!").toItemStack());
        } else {
            if (voteGames[0] != null) {
                addUpdatingItem(slot, setGameItem(voteGames[0], player));
                itemSlots.put(voteGames[0], slot);
            }
            if (voteGames[1] != null) {
                addUpdatingItem(slot + raiseSlot, setGameItem(voteGames[1], player));
                itemSlots.put(voteGames[1], slot + raiseSlot);
            }
            if (voteGames[2] != null) {
                addUpdatingItem(slot + (raiseSlot * 2), setGameItem(voteGames[2], player));
                itemSlots.put(voteGames[2], slot + (raiseSlot * 2));
            }
        }

        setRow(BaseItem.FILLED.getItemStack(), 3);
        setItem(3 * 9 + 4, BaseItem.CLOSE_MENU.getItemStack());

        build();
    }

    private Supplier<ItemStack> setGameItem(Game game, Player player) {
        ItemBuilder item = new ItemBuilder(game.getGameInformation().getIcon());
        String displayName = game.getGameInformation().getColor() + "§n" + game.getGameInformation().getName();
        item.setName(displayName);
        String gameID = String.valueOf(game.getId());
        String ganeName = game.getGameInformation().getName();
        item.setNbt("vote", gameID);
        item.setLore("", "§f" + game.getGameInformation().getDescription(), "", "§fTryk for at stemme på §l" + ganeName + "§f!", "", "");
        String voteString = "§7Votes: " + game.getGameInformation().getColor();
        return () -> {
            int votes = Pint.getInstance().getVoteHandler().getGameVotesCount(game);
            item.setAmount(votes == 0 ? 1 : votes);
            item.addLoreLine(voteString + votes, 5);
            item.setGlowing(Pint.getInstance().getVoteHandler().getVote(player) == game);
            return item.toItemStack();
        };
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        if (event.getCurrentItem().equals(BaseItem.CLOSE_MENU.getItemStack())) {
            close((Player) event.getWhoClicked());
            return;
        }
        String nbtGameString = ItemBuilder.getNbt(event.getCurrentItem(), "vote");
        if (nbtGameString == null) {
            return;
        }
        int gameID = Integer.parseInt(nbtGameString);
        Game game = Pint.getInstance().getGameHandler().getGame(gameID);
        if (game == null) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Game hasVoted = Pint.getInstance().getVoteHandler().getVote(player);
        if (hasVoted != null && hasVoted.equals(game)) {
            Pint.getInstance().getVoteHandler().setVote(player, null);
            event.getWhoClicked().sendMessage("§aDu har fjernet din stemme fra §l" + game.getGameInformation().getDisplayName() + "§a!");
        } else {
            Pint.getInstance().getVoteHandler().setVote(player, game);
            event.getWhoClicked().sendMessage("§aDu har stemt på §l" + game.getGameInformation().getDisplayName() + "§a!");
        }
        this.getInventory().setItem(event.getSlot(), setGameItem(game, player).get());
        if (hasVoted != null) {
            this.getInventory().setItem(itemSlots.get(hasVoted), setGameItem(hasVoted, player).get());
        }
    }
}
