package dk.martinersej.pint.utils.npc;

import dk.martinersej.pint.Pint;
import lombok.Getter;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

@Getter
public abstract class NPC implements Listener {

    private NPCBuilder npcBuilder;
    private net.citizensnpcs.api.npc.NPC npc = null;

    public NPC(String id) {
        this.npcBuilder = new NPCBuilder(id);
    }

    public void spawn() {
        if (npc == null || npcBuilder.getLocation() == null) return;
        npc.spawn(npcBuilder.getLocation());
        Bukkit.getServer().getPluginManager().registerEvents(this, Pint.getInstance());
    }

    public void despawn() {
        if (npc == null) return;
        npc.despawn();
        HandlerList.unregisterAll(this);
    }

    public abstract void onRightClick(NPCRightClickEvent event);

    public abstract void onLeftClick(NPCRightClickEvent event);

    @EventHandler
    public void onNpcRightClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(npc)) return;
        onRightClick(event);
    }

    @EventHandler
    public void onNpcLeftClick(NPCRightClickEvent event) {
        if (!event.getNPC().equals(npc)) return;
        onLeftClick(event);
    }
}
