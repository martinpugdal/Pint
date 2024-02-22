package dk.martinersej.pint.simplecommand;

import dk.martinersej.pint.Pint;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class WhitelistCommand implements CommandExecutor {

    public static boolean WIHTELIST_MODE = true;

    public WhitelistCommand() {
        Pint.getInstance().getCommand("whitelist").setPermission("pint.whitelist");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {

        WIHTELIST_MODE = !WIHTELIST_MODE;
        commandSender.sendMessage("§aWhitelist mode er blevet slået " + (WIHTELIST_MODE ? "til" : "fra"));
        return true;
    }
}

