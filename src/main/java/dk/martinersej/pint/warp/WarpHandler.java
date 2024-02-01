package dk.martinersej.pint.warp;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.manager.managertype.YamlManagerTypeImpl;
import lombok.Getter;

@Getter
public class WarpHandler extends YamlManagerTypeImpl {

    private final WarpUtil warpUtil;
    @Getter
    private final static String warpSection = "warps";

    public WarpHandler() {
        super(Pint.getInstance(), "warps.yml");

        if (getConfig().getConfigurationSection(warpSection) == null) {
            getConfig().createSection(warpSection);
            save();
        }

        this.warpUtil = new WarpUtil(this);
    }
}
