package dk.martinersej.pint.map.maps;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class YawPitch {

    private final float yaw;
    private final float pitch;

    public YawPitch(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }
}
