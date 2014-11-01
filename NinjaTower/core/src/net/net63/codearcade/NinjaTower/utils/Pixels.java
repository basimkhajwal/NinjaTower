package net.net63.codearcade.NinjaTower.utils;

import com.badlogic.gdx.math.Vector2;

public class Pixels {

    public static float toMeter(float pixels) {
        return pixels * Constants.PIXELS_PER_METRE;
    }

    public static Vector2 toMeter(Vector2 vecPixel) {
        return new Vector2(Pixels.toMeter(vecPixel.x), Pixels.toMeter(vecPixel.y));
    }
}
