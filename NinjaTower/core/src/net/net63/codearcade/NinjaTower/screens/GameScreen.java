package net.net63.codearcade.NinjaTower.screens;

import net.net63.codearcade.NinjaTower.GameMain;
import net.net63.codearcade.NinjaTower.GameWorld;
import net.net63.codearcade.NinjaTower.systems.BackgroundSystem;
import net.net63.codearcade.NinjaTower.systems.BuildingGenerationSystem;
import net.net63.codearcade.NinjaTower.systems.CameraSystem;
import net.net63.codearcade.NinjaTower.systems.DebugRenderingSystem;
import net.net63.codearcade.NinjaTower.systems.PlayerMovementSystem;
import net.net63.codearcade.NinjaTower.systems.RenderingSystem;
import net.net63.codearcade.NinjaTower.systems.WorldSystem;
import net.net63.codearcade.NinjaTower.utils.Assets;
import net.net63.codearcade.NinjaTower.utils.Constants;
import net.net63.codearcade.NinjaTower.utils.Textures;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends AbstractScreen implements InputProcessor {
	static final int GAME_RUNNING = 0;
	static final int GAME_PAUSED = 1;
	static final int GAME_OVER = 2;
	
	private Vector3 touchPoint;
	
	private OrthographicCamera camera, guiCam;
	private GameWorld gameWorld;
	private Engine engine;
	private SpriteBatch batch;
	
	private Texture overlay;
	
	private TextureRegion pauseDown, pauseUp;
	
	private Rectangle pauseBounds;
	
	private int state;
	
	public GameScreen(GameMain game) {
		super(game);
		
		//Game variables
		state = GAME_RUNNING;
		touchPoint = new Vector3();
		
		//Textures
		batch = new SpriteBatch();
		
		//Set input
		Gdx.input.setInputProcessor(this);
		
		//Ashley and Box2D
		camera = new OrthographicCamera();
		guiCam = new OrthographicCamera();
		engine = new Engine();
		
		engine.addSystem(new WorldSystem());
		engine.addSystem(new DebugRenderingSystem(camera));
		engine.addSystem(new BackgroundSystem(camera));
		engine.addSystem(new RenderingSystem(camera));
		engine.addSystem(new PlayerMovementSystem());
		engine.addSystem(new CameraSystem());
		engine.addSystem(new BuildingGenerationSystem());
		
		gameWorld = new GameWorld(engine);
		gameWorld.create();
		
		//Overlay to create shadowed effect
		Pixmap.setBlending(Blending.None);
		Pixmap.setFilter(Pixmap.Filter.BiLinear);
		
		Pixmap pixmap = new Pixmap(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), Format.Alpha);
		
		pixmap.setColor(0f, 0f, 0f, Constants.OVERLAY_OPACITY);
		pixmap.fill();
		
		overlay = new Texture(pixmap);
		
		pixmap.dispose();
		
		//Get pause textures
		pauseDown = Assets.regions.get(Textures.PAUSE_DOWN);
		pauseUp = Assets.regions.get(Textures.PAUSE_UP);
		
		//Create bounds for the pause button to be displayed in
		pauseBounds = new Rectangle();
	}
	
	@Override
	public void dispose(){
		super.dispose();
		
		if(overlay != null){
			overlay.dispose();
		}
	}
	
	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		
		//Set the camera and use y-down false
		camera.setToOrtho(false);
		guiCam.setToOrtho(false);
		
		guiCam.update();
		
		//Set the pause location
		pauseBounds.x = width - (Constants.PAUSE_BUTTON_WIDTH + Constants.PAUSE_PADDING_X) * Constants.SCREEN_WIDTH_RATIO;
		pauseBounds.y = height - (Constants.PAUSE_BUTTON_HEIGHT + Constants.PAUSE_PADDING_Y) * Constants.SCREEN_HEIGHT_RATIO;
	
		pauseBounds.width = Constants.PAUSE_BUTTON_WIDTH * Constants.SCREEN_WIDTH_RATIO;
		pauseBounds.height = Constants.PAUSE_BUTTON_HEIGHT * Constants.SCREEN_HEIGHT_RATIO;
	}
	
	
	@Override
	public void render(float delta){
		super.render(delta);//Clear the screen
		
		//Update the game
		update(delta);
	}
	
	public void update(float delta){
		//Update the correct state
		switch(state){
		case GAME_RUNNING:
			updateRunning(delta);
			break;
		case GAME_PAUSED:
			updatePaused(delta);
			break;
		}
	}
	
	public void updatePaused(float delta){
		engine.update(delta);
		
		if(Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Keys.P)){
			state = GAME_RUNNING;
			resumeSystems();
		}
		
		batch.setProjectionMatrix(guiCam.combined);
		
		batch.begin();
		
		batch.draw(overlay, 0, 0);
		
		batch.draw(pauseDown, pauseBounds.x, pauseBounds.y, pauseBounds.width, pauseBounds.height);
		
		batch.end();
	}
	
	public void pauseSystems(){
		engine.getSystem(PlayerMovementSystem.class).pause();
		engine.getSystem(WorldSystem.class).pause();
		
	}
	
	public void resumeSystems(){
		engine.getSystem(PlayerMovementSystem.class).resume();
		engine.getSystem(WorldSystem.class).resume();
	}
	
	public void updateRunning(float delta){
		
		if(Gdx.input.justTouched()){
			touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			
			guiCam.unproject(touchPoint);
			
			if(pauseBounds.contains(touchPoint.x, touchPoint.y)){
				state = GAME_PAUSED;
				pauseSystems();
			}else{
				engine.getSystem(PlayerMovementSystem.class).touched();
			}
		}
		
		engine.update(delta);
		
		batch.setProjectionMatrix(guiCam.combined);
		
		//Draw the GUI
		batch.begin();
		
		batch.draw(pauseUp, pauseBounds.x, pauseBounds.y, pauseBounds.width, pauseBounds.height);
		
		batch.end();
	}

	@Override
	public boolean keyDown(int keycode) {
		System.out.println("Key Typed!!! 1");
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		System.out.println("Key Typed!!!  2");
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		System.out.println("Key Typed!!! 3");
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

}
