package net.net63.codearcade.NinjaTower.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class Constants {
	//General game settings
	public static final String GAME_TITLE = "Ninja Tower";
	public static final String LOG  = GAME_TITLE;
	
	public static final boolean DEV_MODE = true;
	
	//File paths
	public static final String LOGO = "data/logo.png";
	public static final String SPLASH_BACKGROUND = "data/splashBackground.png";
	
	public static final String TURTLE_FONT = "data/fonts/turtles.ttf";
	
	//Texture atlas settings
	public static final String SRC_FOLDER = "src";
	public static final String PACK_FOLDER = "packs";
	public static final String PACK_NAME = "texture";
	
	public static final String TEXTURE_PACK = PACK_FOLDER + "/" + PACK_NAME + ".atlas";
	
	//Font settings
	public static final int[] FONT_SIZES = new int[]{10,20,50,100};
	
	public static class FONT_SIZE{
		public static final int TEN = 0;
		public static final int TWENTY = 1;
		public static final int FIFTY = 2;
		public static final int HUNDRED = 3;
	}
	
	//Desktop settings
	public static final int DESKTOP_GAME_WIDTH = 1600;
	public static final int DESKTOP_GAME_HEIGHT = 1200;
	
	//World constants for Box2D, units in meters
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	
	//Number of pixels in a metre for the normal screen width
	public static final float PIXEL_SIZE_NORMAL = 30f;
	
	public static final float PIXELS_PER_METRE = PIXEL_SIZE_NORMAL * (Gdx.graphics.getWidth() / SCREEN_WIDTH);
	public static final float METRES_PER_PIXEL = 1/PIXELS_PER_METRE;
	
	//The world width and height in metres
	public static final float WORLD_WIDTH = SCREEN_WIDTH / PIXEL_SIZE_NORMAL;
	public static final float WORLD_HEIGHT = SCREEN_HEIGHT / PIXEL_SIZE_NORMAL;
	
	public static final Vector2 GRAVITY = new Vector2(0, -10);
	
	public static final int BOX2D_FPS = 60;
	public static final int BOX2D_VELOCITY_TICKS = 6;
	public static final int BOX2D_POSITION_TICKS = 2;
	
	//Entity system running priorities, lower is first
	public static class SYSTEM_PRIORITIES{
		public static final int BACKGROUND = 4;
		public static final int RENDERING = 5;
		public static final int ANIMATION = 2;
		public static final int BUILDING_GENERATION = 3;
		public static final int CAMERA = 0;
		public static final int PLAYER_MOVEMENT = 2;
		public static final int DEBUG_RENDERING = 10;
		public static final int WORLD = 0;
	}
	
	//Player settings
	public static final float PLAYER_WIDTH = 1f;
	public static final float PLAYER_HEIGHT = 1f;
	
	public static final float PLAYER_START_X = 10f;
	public static final float PLAYER_START_Y = 10f;
	
	public static final float PLAYER_SPEED = 10f;
	
	public static final float PLAYER_GRAVITY_SCALE = 5f;
	
	//Render order of the objects
	public static class RENDER_ORDER{
		
		public static final int BACKGROUND = 1;
		public static final int BUILDING = 2;
	}
	
	//Memory management / world generation settings
	public static final int BUILDINGS_PER_CHUNK = 10;
	
	public static final float BUILDING_GAP_MIN = 2f;
	public static final float BUILDING_GAP_MAX = 8f;
	
	public static final int[] BUILDING_WIDTHS = new int[]{10,15,20};
	
	public static final float BUILDING_START_HEIGHT = 5f;
	public static final float BUILDING_START_WIDTH = BUILDING_WIDTHS[0];
	
	public static final float BUILDING_MIN_HEIGHT = 2;
	public static final float BUIILDING_MAX_HEIGHT = 18;
	
	public static final float BUILDING_MIN_HEIGHT_CHANGE = 2;
	public static final float BUILDING_MAX_HEIGHT_CHANGE = 7;
	
	//Parallax scrolling settings
	public static final float BACKGROUND_SPEED = 0.8f; //Relative to the player speed
	
	//Menu Screen Settings
	public static final float TIME_TO_TRANSITION = 3f;
	public static final float TIME_FLASHING = 0.5f;
	
}
