package dk.martinersej.pint.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LabymodUtils {

    /**
     * Mute a player for someone.
     *
     * This should to be sent to every LabyMod player as custom
     * addons could otherwise bypass this if only sent to the
     * muted player(s) themselves.
     */
    public static void sendMutedPlayerTo(Player player, UUID mutedPlayer, boolean muted) {
        JsonObject voiceChatObject = new JsonObject();
        JsonArray mutePlayersArray = new JsonArray();

        // You can also add multiple players to this at once
        JsonObject mutePlayerObject = new JsonObject();
        mutePlayerObject.addProperty("mute", muted);
        mutePlayerObject.addProperty("target", mutedPlayer.toString());
        mutePlayersArray.add(mutePlayerObject);

        voiceChatObject.add("mute_players", mutePlayersArray);

        Bukkit.broadcastMessage(voiceChatObject.toString());
        // Send to LabyMod using the API
        LabyModProtocol.sendLabyModMessage(player, "voicechat", voiceChatObject);
    }
}
