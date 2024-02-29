package dk.martinersej.pint.vote;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.game.objects.Game;
import dk.martinersej.pint.map.objects.maps.VoteMap;
import dk.martinersej.pint.utils.PacketUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class VoteHandler {

    private final Map<Player, Game> playerVotes = new HashMap<>();
    private final Map<Game, Integer> gameVotesCount = new HashMap<>();
    private final VoteUtil voteUtil;
    private final VoteMap voteMap;
    private BukkitRunnable voteTimer = null;
    private final int fullCooldown = 5;
    private int cooldown;
    private final int voteAmountNeeded = 1;

    public VoteHandler() {
        voteUtil = new VoteUtil();
        voteMap = new VoteMap();
        startVoteTimer();
        checkPlayerFlyingBelowVoteMap();
    }

    private void checkPlayerFlyingBelowVoteMap() {
        new BukkitRunnable() {
            @Override
            public void run() {
                List<Player> players = new ArrayList<>(Pint.getInstance().getMapHandler().getMapUtil().getServerWorld().getWorld().getPlayers());
                if (Pint.getInstance().getGameHandler().getCurrentGame() != null) {
                    players.removeAll(Pint.getInstance().getGameHandler().getCurrentGame().getPlayers());
                }
                for (Player player : players) {
                    if (player.getLocation().getY() < voteMap.getSpawnLocation().getY() && !player.getGameMode().equals(GameMode.SPECTATOR)) {
                        player.setGameMode(GameMode.SPECTATOR);
                    } else if (player.getLocation().getY() > voteMap.getSpawnLocation().getY() && player.getGameMode().equals(GameMode.SPECTATOR)) {
                        voteUtil.setToPlainVoteGamemode(player);
                    }
                }
            }
        }.runTaskTimer(JavaPlugin.getProvidingPlugin(VoteHandler.class), 0, 1L);
    }

    public void startVoteTimer() {
        // setup the vote timer and cooldown
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
                if (getAllVoters().size() < voteAmountNeeded && !Pint.getInstance().getGameHandler().isGameRunning()) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PacketUtil.sendActionBar(player, "§cIkke nok votes til at starte et spil");
                    }
                    cooldown = fullCooldown;
                    return;
                }

                if (Pint.getInstance().getGameHandler().getCurrentGame() != null && Pint.getInstance().getGameHandler().isGameRunning()) {
                    cooldown = fullCooldown;
                    return;
                }

                if (cooldown == fullCooldown / 4 && Pint.getInstance().getGameHandler().getCurrentGame() == null) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        PacketUtil.sendActionBar(player, "§7Vælg et spil for at være med");
                    }
                }

                if (cooldown <= 0 && Pint.getInstance().getGameHandler().getCurrentGame() == null) {
                    // get the game with the most votes and start it
                    Game game = getGameWithMostVotes();
                    int votes = getGameVotesCount(game);
                    if (votes == 0) {
                        Bukkit.broadcastMessage("§aIngen valgte et spil, et tilfældigt spil er valgt!");
                        Bukkit.broadcastMessage("§a" + game.getGameInformation().getName() + " §7er valgt til at blive spillet!");
                    } else {
                        Bukkit.broadcastMessage("§a" + game.getGameInformation().getName() + " §7har vundet med §a" + getGameVotesCount(game) + " §7votes!");
                    }
                    Pint.getInstance().getGameHandler().setupGame(game);
                    cooldown += 5;
                }

                // timer is up, start the game
                if (cooldown <= 0 && Pint.getInstance().getGameHandler().getCurrentGame() != null) {
                    Pint.getInstance().getGameHandler().getCurrentGame().addPlayers(getAllVoters());
                    Pint.getInstance().getGameHandler().getCurrentGame().start();

                    // handle the vote part, so it's ready for the next vote
                    voteUtil.getVoteScoreboard().removePlayers(getAllVoters());
                    Pint.getInstance().getGameHandler().getCurrentGame().getScoreboard().addPlayers(getAllVoters());
                    Pint.getInstance().getGameHandler().getGamePool().shuffleVotePool();
                    Pint.getInstance().getVoteHandler().refreshVotes();

                    voteUtil.getVoteComponent().tick(); // update the sidebar so its showing what game is being played
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
        if (gameVotesCount.get(game) == 0) {
            gameVotesCount.remove(game);
        }
    }

    public void joinVoteWithNoVote(Player player) {
        Game previousVote = playerVotes.put(player, null);
        if (previousVote != null) {
            gameVotesCount.put(previousVote, gameVotesCount.get(previousVote) - 1);
        }
    }

    public void removeVote(Player player) {
        Game previousVote = playerVotes.remove(player);
        if (previousVote != null) {
            gameVotesCount.put(previousVote, gameVotesCount.get(previousVote) - 1);
        }
    }

    public Game getVote(Player player) {
        return playerVotes.get(player);
    }

    public List<Player> getAllVoters() {
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
        } else if (games.size() == 1) {
            return games.get(0);
        } else {
            int random = (int) (Math.random() * Pint.getInstance().getGameHandler().getGamePool().getVoteGames().length);
            return Pint.getInstance().getGameHandler().getGamePool().getVoteGames()[random];
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