package dk.martinersej.pint.map;

import dk.martinersej.pint.Pint;
import dk.martinersej.pint.manager.managertype.YamlManagerTypeImpl;

public class MapHandler extends YamlManagerTypeImpl {

    public MapHandler() {
        super(Pint.getInstance(), "maps.yml");
        load();
    }

    public void loadMaps() {
        load();
    }
}
