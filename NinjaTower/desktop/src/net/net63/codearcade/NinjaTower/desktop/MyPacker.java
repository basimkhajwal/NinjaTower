package net.net63.codearcade.NinjaTower.desktop;

import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

//Texture packer class to pack textures in a pack for later use, not run alongside the game
public class MyPacker {
	
	private static final String FOLDER = "../android/assets/";
	
	public static void main(String[] args){
		
		Settings settings = new Settings();
		
		//Set the max dimensions of the outputted texture image
		settings.maxWidth = 1<<15;
		settings.maxHeight = 1<<15;
		
		//Pack the texture pack from the source folder
		TexturePacker.process(settings, FOLDER + Constants.SRC_FOLDER, FOLDER + Constants.PACK_FOLDER, Constants.PACK_NAME);
	}
	
}
