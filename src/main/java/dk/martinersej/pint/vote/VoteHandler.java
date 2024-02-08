package dk.martinersej.pint.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.map.objects.maps.VoteMap;
import dk.martinersej.pint.utils.PacketUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class VoteHandler {

    private final Map<Player, Game> playerVotes = new HashMap<>();
    private final Map<Game, Integer> gameVotesCount = new HashMap<>();
    private final VoteUtil voteUtil;
    private final VoteMap voteMap;
    private BukkitRunnable voteTimer = null;
    private int cooldown;

    public VoteHandler() {
        voteUtil = new VoteUtil();
        voteMap = new VoteMap();
        startVoteTimer();
    }

    public void startVoteTimer() {
        // setup the vote timer and cooldown
        int fullCooldown = 3;
        cooldown = fullCooldown;
        if (voteTimer != null) {
            voteTimer.cancel();
        }
        voteTimer = new BukkitRunnable() {

            long tick = 0L;

            @Override
            public void run() {
                tick++;

                if (tick % 20 != 0) {
                    voteUtil.getVoteComponent().tick();
                    return;
                }

                // not enough players to start a game
                if (getAllVoters().size() < 1) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PacketUtil.sendActionBar(player, "§cIkke nok votes til at starte et spil");
                    }
                    cooldown = fullCooldown;
                    return;
                }

                if (Pint.getInstance().getGameHandler().getCurrentGame() != null) {
                    cooldown = fullCooldown;
                    return;
                }

                // timer is up, end vote
                if (cooldown <= 0) {
                    // start the game with most votes
                    Game game = getGameWithMostVotes();
                    Bukkit.broadcastMessage("§a" + game.getGameInformation().getName() + " §7har vundet med §a" + getGameVotesCount(game) + " §7votes!");
                    Pint.getInstance().getGameHandler().setupGame(game);
                    Pint.getInstance().getGameHandler().getCurrentGame().addPlayers(getAllVoters());
                    Pint.getInstance().getGameHandler().getCurrentGame().start();

                    // handle the vote part, so it's ready for the next vote
                    Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();
                    Pint.getInstance().getVoteHandler().refreshVotes();
                    voteUtil.getVoteScoreboard().removePlayers(getAllVoters());
                    voteUtil.getVoteScoreboard().close();
//                    game.getScoreboard().addPlayers(getAllVoters());
                    this.cancel();
                    return;
                }

                cooldown--;
            }
        };
        voteTimer.runTaskTimer(JavaPlugin.getProvidingPlugin(VoteHandler.class), 0, 1L);
    }

    public void setVote(Player player, Game game) {
        Game previousVote = playerVotes.put(player, game);
        if (game == null && previousVote != null) {
            gameVotesCount.put(previousVote, gameVotesCount.get(previousVote) - 1);
            playerVotes.remove(player);
        } else if (previousVote == null) {
            gameVotesCount.putIfAbsent(game, 0);
            gameVotesCount.put(game, gameVotesCount.get(game) + 1);
        } else {
            if (previousVote.equals(game)) {
                return;
            }
            gameVotesCount.putIfAbsent(game, 0);

            gameVotesCount.put(previousVote, gameVotesCount.get(previousVote) - 1);
            gameVotesCount.put(game, gameVotesCount.get(game) + 1);
        }
    }

    public Game getVote(Player player) {
        return playerVotes.get(player);
    }

    List<Player> getAllVoters() {
        return new ArrayList<>(playerVotes.keySet());
    }

    public Game getGameWithMostVotes() {
        List<Game> games = new ArrayList<>();
        int votes = 0;

        // get the games with the most votes
        for (Game game : gameVotesCount.keySet()) {
            if (gameVotesCount.get(game) > votes) {
                games.clear();
                games.add(game);
                votes = gameVotesCount.get(game);
            } else if (gameVotesCount.get(game) == votes) {
                games.add(game);
            }
        }

        // get a random game if there are multiple games with the same number of votes
        if (games.size() > 1) {
            return games.get((int) (Math.random() * games.size()));
        } else {
            return games.get(0);
        }
    }

    public int getGameVotesCount(Game game) {
        return gameVotesCount.get(game) == null ? 0 : gameVotesCount.get(game);
    }

    public void refreshVotes() {
        playerVotes.clear();
        gameVotesCount.clear();
    }

    public void loadVoteMap() {
        getVoteMap().load();
    }

    public boolean defaultVoteMap() {
        return getVoteMap().getZeroLocation().getBlock().getType().equals(Material.BEDROCK) && getVoteMap().getCorner1().equals(getVoteMap().getCorner2());
    }
}