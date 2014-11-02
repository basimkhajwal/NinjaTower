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

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends AbstractScreen {
	static final int GAME_RUNNING = 1;
	
	private Vector3 touchPoint;
	
	private OrthographicCamera camera;
	private GameWorld gameWorld;
	private Engine engine;
	
	private int state;
	
	public GameScreen(GameMain game) {
		super(game);
		
		//Game variables
		state = GAME_RUNNING;
		touchPoint = new Vector3();
		
		//Ashley and Box2D
		camera = new OrthographicCamera();
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
	}
	
	@Override
	public void resize(int width, int height){
		super.resize(width, height);
		
		camera.setToOrtho(false);
	}
	
	
	@Override
	public void render(float delta){
		super.render(delta);
		
		update(delta);
	}
	
	public void update(float delta){
		switch(state){
		case GAME_RUNNING:
			updateRunning(delta);
			break;
		}
	}
	
	public void updateRunning(float delta){
		
		if(Gdx.input.justTouched()){
			touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
			
			engine.getSystem(PlayerMovementSystem.class).touched();
		}
		
		engine.update(delta);
	}

}