package net.net63.codearcade.NinjaTower.utils;

public class Textures {
	
	public static final int NUM_BACKGROUNDS = 3;
	public static final int CURRENT_BACKGROUND = (NUM_BACKGROUNDS + 1) / 2;
	
	public static final String BACKGROUND_START = "background-start";
	public static final String BACKGROUND_2 = "background-2";
	
	//The texture height of the building, to keep it from being resized, in game metres
	public static final float BUILDING_TEXTURE_HEIGHT = 983 * Constants.METRES_PER_PIXEL;
	
	public static final String[] BUILDINGS = new String[]{"building-ten", "building-ten", "building-ten"};
}
