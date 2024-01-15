package dk.martinersej.pint.utils.npc;

import lombok.Getter;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;

@Getter
public class NPCBuilder {

    private static final NPCRegistry npcRegistry = CitizensAPI.createNamedNPCRegistry("Pint-NPCRegistry", new MemoryNPCDataStore());

    private final String id;

    private Location location = null;

    private String name;
    private String skinTexture = null;
    private String skinSignature = null;
    private boolean lookClose = false;
    private boolean nameVisible = false;


    public NPCBuilder(String id) {
        this.id = id;
        name = "npc" + id;
    }

    public NPCBuilder withLocation(Location location) {
        this.location = location;
        return this;
    }

    public NPCBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public NPCBuilder withSkinTexture(String skinTexture) {
        this.skinTexture = skinTexture;
        return this;
    }

    public NPCBuilder withSkinSignature(String skinSignature) {
        this.skinSignature = skinSignature;
        return this;
    }

    public NPCBuilder withLookClose(boolean lookClose) {
        this.lookClose = lookClose;
        return this;
    }

    public NPCBuilder withNameVisible(boolean nameVisible) {
        this.nameVisible = nameVisible;
        return this;
    }

    public NPC build() {
        NPC npc = npcRegistry.createNPC(EntityType.PLAYER, name);
        if (lookClose) npc.getOrAddTrait(LookClose.class).lookClose(lookClose);
        if (nameVisible) npc.data().set(NPC.Metadata.NAMEPLATE_VISIBLE, nameVisible);
        if (skinTexture != null && skinSignature != null) {
            npc.data().set("npcTexture", skinSignature);
            npc.data().set("npcSignature", skinTexture);
        }
        return npc;
    }
}
