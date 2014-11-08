package net.net63.codearcade.NinjaTower.utils;

import java.util.HashMap;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;

//Class to store all the assets efficiently in a game
public class Assets {
	
	//The asset manager from libGDX to store the assets
	private static final AssetManager assetManager = new AssetManager();
	
	//The individual assets themselves for use by other classes
	public static Texture logo;
	public static Texture splashBackground;
	
	public static TextureAtlas textureAtlas;
	public static HashMap<String, AtlasRegion> regions;
	
	public static BitmapFont[] font;
	
	
	//Booleans for checking if the sections are loaded or not
	public static boolean initLoaded = false;
	public static boolean allLoaded = false;
	
	//Load the initial assets required for the Splash Screen
	public static void loadBefore(){
		//Create a new resolver and set the loaders so we can load fonts
		FileHandleResolver resolver = new InternalFileHandleResolver();
		
		assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
		
		//Set all the assets that need to be loaded
		assetManager.load(Constants.LOGO, Texture.class);
		assetManager.load(Constants.SPLASH_BACKGROUND, Texture.class);
		assetManager.load(Constants.TURTLE_FONT, FreeTypeFontGenerator.class);
		
		//Stop the thread until they are loaded
		assetManager.finishLoading();
		
		//Assign the loaded textures to the correct variables
		logo = assetManager.get(Constants.LOGO, Texture.class);
		splashBackground = assetManager.get(Constants.SPLASH_BACKGROUND, Texture.class);
		
		//Load all the fonts
		font = new BitmapFont[Constants.FONT_SIZES.length];
		
		//Get the font generator and make a new font parameters class
		FreeTypeFontGenerator turtleFontGenerator = assetManager.get(Constants.TURTLE_FONT, FreeTypeFontGenerator.class);
		FreeTypeFontParameter turtleFontParameters = new FreeTypeFontParameter();
		
		//Loop through all the font sizes
		for(int i = 0; i < font.length; i++){
			//Set the size to the correct size
			turtleFontParameters.size = Constants.FONT_SIZES[i];
			
			//Load the bitmap font
			font[i] = turtleFontGenerator.generateFont(turtleFontParameters);
		}
		
		//Dispose of the generator, it isn't needed anymore
		assetManager.unload(Constants.TURTLE_FONT);
		
		//Set the initial load to true
		initLoaded = true;
	}
	
	//Load the main body of the assets
	public static void loadAll(){
		//If the initial section isn't loaded then load it first
		if(! initLoaded){
			loadBefore();
		}
		
		//Load the texture pack
		assetManager.load(Constants.TEXTURE_PACK, TextureAtlas.class);
		
		//All the textures are now loaded
		allLoaded = true;
	}
	
	public static float getProgress(){
		//Get the progress percentage from the asset manager
		float progress = assetManager.getProgress();
		assetManager.update();
		//Check if it is all loaded, then 
		if(progress == 1 && allLoaded){
			if(textureAtlas == null){
				//Get the texture atlas
				textureAtlas = assetManager.get(Constants.TEXTURE_PACK, TextureAtlas.class);
				
				//Create a new hashmap for all the textures and add them from the texture atlas
				regions = new HashMap<String, AtlasRegion>();
				
				for(AtlasRegion region: textureAtlas.getRegions()){
					regions.put(region.name, region);
				}
				
			}
		}
		
		return progress;
	}
	
	public static void destroy(boolean destroy){
		//Clear all the textures from the asset manager
		assetManager.clear();
		
		//If destroy then dispose the asset manager as well
		if(destroy){
			assetManager.dispose();
		}
	}
}
