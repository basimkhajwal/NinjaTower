package net.net63.codearcade.NinjaTower;

import net.net63.codearcade.NinjaTower.screens.MainMenuScreen;
import net.net63.codearcade.NinjaTower.utils.Assets;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class GameMain extends Game {
	
	public AssetManager manager;
	public TextureAtlas preGame;
	
	private static FPSLogger fpsLogger;
	
	
	@Override
	public void create () {
		fpsLogger = new FPSLogger();
		
		super.setScreen(new MainMenuScreen(this));
	}

	@Override
	public void pause(){
		super.pause();
		
		if(Gdx.app.getType() == ApplicationType.Android){
			Assets.destroy(false);
		}
	}
	
	@Override
	public void resume(){
		
	}
	
	@Override
	public void dispose(){
		super.dispose();
		
		Assets.destroy(true);
	}
	
	@Override
	public void render () {
		super.render();
		
		if(Constants.DEV_MODE){
			fpsLogger.log();
		}
		
	}
}
