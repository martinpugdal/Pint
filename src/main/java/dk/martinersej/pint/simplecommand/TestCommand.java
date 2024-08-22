package dk.martinersej.pint.simplecommand;

import dk.martinersej.pint.Pint;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

public class TestCommand implements CommandExecutor {

    public TestCommand() {
        Pint.getInstance().getCommand("test").setPermission("pint.test");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {

        Player player = (Player) commandSender;

        // spawn it at the player's eye location
        ArmorStand prop = Bukkit.getWorld(player.getWorld().getName()).spawn(player.getLocation(), ArmorStand.class);

        prop.setGravity(false); // make the entity not fall
        prop.setBasePlate(false); // make the entity not have a base plate
        prop.setCanPickupItems(false); // make the entity not able to pick up items
        prop.setArms(true); // make the entity have arms
        prop.setVisible(false); // make the entity not visible
        prop.setCustomNameVisible(false); // make the entity's name not visible

        // Adjust the right arm pose to center the tool
        prop.setRightArmPose(new EulerAngle(degreesToRadians(-10), 0, degreesToRadians(-90)));

        // get the NMS entity
        EntityArmorStand nmsProp = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand) prop).getHandle();
        nmsProp.setInvisible(true); // make the entity invisible

        /// disable slots
        NBTTagCompound tag = new NBTTagCompound();
        nmsProp.c(tag);
        tag.setInt("DisabledSlots", 2039583);
        nmsProp.f(tag);

        // set the head to a random block (test block hardcoded)
        ItemStack propItem = player.getInventory().getItemInHand() == null ? new ItemStack(Material.DIAMOND_SWORD) : player.getInventory().getItemInHand();
        Bukkit.broadcastMessage(propItem.hasItemMeta() ? propItem.getItemMeta().toString() : "No meta");
        if (propItem.getType().isBlock()) {
            prop.setHelmet(propItem);
            prop.setSmall(true);
        } else {
            prop.setItemInHand(propItem);
        }

        // make the offset and height
        float height = propItem.getType().isBlock() ? nmsProp.getHeadHeight() - 0.27f : nmsProp.getHeadHeight() - 0.5f;
        Vector heightOffSet = new Vector(0, -height, 0);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (prop.isDead()) {
                    cancel();
                    return;
                }

                // Teleport the ArmorStand to the new position
                prop.teleport(player.getLocation().add(heightOffSet));
            }
        }.runTaskTimer(Pint.getInstance(), 0, 1L);

        // kill the entity after 10 seconds
        Bukkit.getScheduler().runTaskLater(Pint.getInstance(), prop::remove, 20 * 10);

        return true;
    }

    double degreesToRadians(double degrees) {
        return (degrees % 360) * (Math.PI / 180);
    }
}
