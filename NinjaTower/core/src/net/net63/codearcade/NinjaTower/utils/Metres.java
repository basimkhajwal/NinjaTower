package net.net63.codearcade.NinjaTower.utils;

import com.badlogic.gdx.math.Vector2;

public class Metres {
	

    public static float toPixel(float metre) {
        return metre * Constants.METRES_PER_PIXEL;
    }
    
    public static Vector2 toPixel(Vector2 metre) {
        return new Vector2(toPixel(metre.x), toPixel(metre.y));
    }
}
