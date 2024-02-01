package dk.martinersej.pint.vote.interaction;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.Game;
import dk.martinersej.pint.utils.ItemBuilder;
import dk.martinersej.pint.utils.gui.BaseGui;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.function.Supplier;

public class VoteGUI extends BaseGui {

    public VoteGUI(UUID uuid) {
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

        if (games < 1) {
            return;
        } else if (games == 1) {
            slot = 13;
        } else if (games == 2) {
            raiseSlot = 4;
        }

        if (voteGames[0] != null) addUpdatingItem(slot, setGameItem(voteGames[0], uuid));
        if (voteGames[1] != null) addUpdatingItem(slot + raiseSlot, setGameItem(voteGames[1], uuid));
        if (voteGames[2] != null) addUpdatingItem(slot + (raiseSlot * 2), setGameItem(voteGames[2], uuid));

        setRow(BaseItem.FILLED.getItemStack(), 3);
        setItem(3*9 + 4, BaseItem.CLOSE_MENU.getItemStack());

        build();
    }

    private Supplier<ItemStack> setGameItem(Game game, UUID uuid) {
        ItemBuilder item = new ItemBuilder(game.getGameInformation().getIcon());
        item.setName(game.getGameInformation().getDisplayName());
        item.setLore("", "§f" + game.getGameInformation().getDescription());
        return () -> {
            item.setLore();
            item.addLoreLine("", "§fTryk for at stemme på §l" + game.getGameInformation().getName() + "§f!");
            int votes = Pint.getInstance().getVoteHandler().gameVotes(game);
            item.setAmount(votes == 0 ? 1 : votes);
            item.addLoreLine("", "§7Votes: " + game.getGameInformation().getColor() + votes);
            item.setNbt("vote", game.getGameInformation().getName());
            item.setGlowing(Pint.getInstance().getVoteHandler().getVotes().get(uuid) == game);
            return item.toItemStack();
        };
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
        // check for 2nd row click
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
        Pint.getInstance().getVoteHandler().setVote(event.getWhoClicked().getUniqueId(), game);
        event.getWhoClicked().sendMessage("§aDu har stemt på §l" + game.getGameInformation().getName() + "§a!");
    }
}
