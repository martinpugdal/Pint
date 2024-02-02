package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.utils.ItemBuilder;
import dk.martinersej.pint.utils.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class VoteGUI extends BaseGui {

    private final Map<Game, Integer> itemSlots = new HashMap<>();

    public VoteGUI(UUID uuid) {
        super("Vote", 4);

        Game[] voteGames = Pint.getInstance().getGameHandler().getGamePool().getVoteGames();

        int games = 0;
        for (Game game : voteGames) {
            if (game != null) {
                games++;
            }
        }

        int slot = 13;
        int raiseSlot = 2;

        if (games == 3) {
            slot = 11;
        } else if (games == 2) {
            raiseSlot = 4;
        }

        if (games == 0) {
            setItem(slot, new ItemBuilder(Material.BARRIER).setName("§cDer er ingen spil at stemme på!").toItemStack());
        } else {
            if (voteGames[0] != null) {
                addUpdatingItem(slot, setGameItem(voteGames[0], uuid));
                itemSlots.put(voteGames[0], slot);
            }
            if (voteGames[1] != null) {
                addUpdatingItem(slot + raiseSlot, setGameItem(voteGames[1], uuid));
                itemSlots.put(voteGames[1], slot + raiseSlot);
            }
            if (voteGames[2] != null) {
                addUpdatingItem(slot + (raiseSlot * 2), setGameItem(voteGames[2], uuid));
                itemSlots.put(voteGames[2], slot + (raiseSlot * 2));
            }
        }

        setRow(BaseItem.FILLED.getItemStack(), 3);
        setItem(3 * 9 + 4, BaseItem.CLOSE_MENU.getItemStack());

        build();
    }

    private Supplier<ItemStack> setGameItem(Game game, UUID uuid) {
        ItemBuilder item = new ItemBuilder(game.getGameInformation().getIcon());
        String displayName = game.getGameInformation().getColor() + "§n" + game.getGameInformation().getName();
        item.setName(displayName);
        String ganeName = game.getGameInformation().getName();
        item.setNbt("vote", ganeName);
        item.setLore("", "§fTryk for at stemme på §l" + ganeName + "§f!", "", "");
        String voteString = "§7Votes: " + game.getGameInformation().getColor();
        return () -> {
            int votes = Pint.getInstance().getVoteHandler().gameVotes(game);
            item.setAmount(votes == 0 ? 1 : votes);
            item.addLoreLine(voteString + votes, 3);
            item.setGlowing(Pint.getInstance().getVoteHandler().getVote(uuid) == game);
            return item.toItemStack();
        };
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        if (event.getCurrentItem().getType().equals(Material.AIR)) {
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
        Game game = Pint.getInstance().getGameHandler().getGame(nbtGameString);
        if (game == null) {
            return;
        }
        UUID uuid = event.getWhoClicked().getUniqueId();
        Game hasVoted = Pint.getInstance().getVoteHandler().getVote(uuid);
        if (hasVoted != null && hasVoted.equals(game)) {
            Pint.getInstance().getVoteHandler().setVote(event.getWhoClicked().getUniqueId(), null);
            event.getWhoClicked().sendMessage("§aDu har fjernet din stemme fra §l" + game.getGameInformation().getDisplayName() + "§a!");
        } else {
            Pint.getInstance().getVoteHandler().setVote(event.getWhoClicked().getUniqueId(), game);
            event.getWhoClicked().sendMessage("§aDu har stemt på §l" + game.getGameInformation().getDisplayName() + "§a!");
        }
        this.getInventory().setItem(event.getSlot(), setGameItem(game, uuid).get());
        if (hasVoted != null) {
            this.getInventory().setItem(itemSlots.get(hasVoted), setGameItem(hasVoted, uuid).get());
        }
    }
}
