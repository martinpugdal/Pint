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

        // spawn it at the player's location
        ArmorStand prop = Bukkit.getWorld(player.getWorld().getName()).spawn(player.getLocation(), ArmorStand.class);

        prop.setGravity(false); // make the entity not fall
        prop.setBasePlate(false); // make the entity not have a base plate
        prop.setCanPickupItems(false); // make the entity not able to pick up items
        prop.setArms(true); // make the entity have arms
        prop.setVisible(false); // make the entity not visible
        prop.setCustomNameVisible(false); // make the entity's name not visible

        prop.setRightArmPose(new EulerAngle(degreesToRadians(-10), 0, degreesToRadians(-90))); // set the new pose

        // get the NMS entity
        EntityArmorStand nmsProp = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand) prop).getHandle();
        nmsProp.setInvisible(true); // make the entity invisible

        /// disable slots
        NBTTagCompound tag = new NBTTagCompound();
        nmsProp.c(tag);
        tag.setInt("DisabledSlots", 2039583);
        nmsProp.f(tag);

        // set the head to a random block (test block hardcoded)
        ItemStack propItem = new ItemStack(Material.DIAMOND_PICKAXE, 1);
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

                if (prop.getHelmet() != null && prop.getHelmet().getType().isBlock()) {
                    // head is a block
                    prop.teleport(player.getLocation().add(heightOffSet));
                } else {
                    // head is null, so it's a tool, and it's in the hand
                    double xOffset = 0.1985;
                    double zOffset = 0.35;

                    Vector playerDirection = player.getLocation().getDirection().normalize();
                    Vector xVector = new Vector(-playerDirection.getZ() * xOffset, 0, playerDirection.getX() * xOffset); // get the x offset
                    Vector backVector = playerDirection.multiply(zOffset); // get the back offset
                    Vector offset = xVector.subtract(backVector); // add the offsets together
                    offset.setY(0);

                    prop.teleport(player.getLocation().add(heightOffSet).add(offset));
                }
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

