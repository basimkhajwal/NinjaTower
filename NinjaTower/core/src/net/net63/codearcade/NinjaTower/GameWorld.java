package net.net63.codearcade.NinjaTower;

import net.net63.codearcade.NinjaTower.components.BackgroundComponent;
import net.net63.codearcade.NinjaTower.components.BodyComponent;
import net.net63.codearcade.NinjaTower.components.BoundsComponent;
import net.net63.codearcade.NinjaTower.components.CameraComponent;
import net.net63.codearcade.NinjaTower.components.PlayerComponent;
import net.net63.codearcade.NinjaTower.components.StateComponent;
import net.net63.codearcade.NinjaTower.components.TextureComponent;
import net.net63.codearcade.NinjaTower.components.WorldComponent;
import net.net63.codearcade.NinjaTower.systems.BuildingGenerationSystem;
import net.net63.codearcade.NinjaTower.systems.CollisionSystem;
import net.net63.codearcade.NinjaTower.systems.DebugRenderingSystem;
import net.net63.codearcade.NinjaTower.utils.Assets;
import net.net63.codearcade.NinjaTower.utils.Constants;
import net.net63.codearcade.NinjaTower.utils.Textures;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class GameWorld {
	
	private Engine engine;
	
	private ComponentMapper<WorldComponent> worldMapper;
	
	public GameWorld(Engine engine){
		this.engine = engine;
		
		//Create the component mappers for later use
		worldMapper = ComponentMapper.getFor(WorldComponent.class);
	}
	
	public void create(){
		createWorld();
		
		Entity player = createPlayer();
		createCamera(player, engine.getSystem(DebugRenderingSystem.class).getCamera());
		
		//Make the 3 background entities
		float startX = Constants.PLAYER_START_X - (Constants.PLAYER_WIDTH / 2) - (Constants.WORLD_WIDTH / 2);
		
		createBackground(startX, Assets.regions.get(Textures.BACKGROUND_START), 1);
		createBackground(startX + Constants.WORLD_WIDTH, Assets.regions.get(Textures.BACKGROUND_2), 2);
		createBackground(startX + Constants.WORLD_WIDTH * 2, Assets.regions.get(Textures.BACKGROUND_START), 3);
		
		engine.getSystem(BuildingGenerationSystem.class).create();
	}
	
	public void createBackground(float x, TextureRegion texture, int backgroundNumber){
		Entity background = new Entity();

		BackgroundComponent bgComponent = new BackgroundComponent();
		BoundsComponent boundsComponent = new BoundsComponent();
		TextureComponent textureComponent = new TextureComponent();
		
		bgComponent.backgroundNumber = backgroundNumber;
		
		boundsComponent.bounds = new Rectangle();
		
		boundsComponent.bounds.x = x;
		boundsComponent.bounds.y = 0;
		
		boundsComponent.bounds.width = Constants.WORLD_WIDTH;
		boundsComponent.bounds.height =  Constants.WORLD_HEIGHT;
		
		textureComponent.texture = texture;
		
		background.add(bgComponent);
		background.add(boundsComponent);
		background.add(textureComponent);
		
		engine.addEntity(background);
	}
	
	//Create the box2d world entity to hold the bodies
	public void createWorld(){
		Entity world = new Entity();
		CollisionSystem collisionSystem = new CollisionSystem();
		
		WorldComponent worldComponent  = new WorldComponent();
		
		worldComponent.world = new World(Constants.GRAVITY, true);
		worldComponent.world.setContactListener(collisionSystem);
		
		world.add(worldComponent);
		
		engine.addEntity(world);
	}
	
	//Useful function when getting world
	@SuppressWarnings("unchecked")
	private World getWorld(){
		Entity w = engine.getEntitiesFor(Family.getFor(WorldComponent.class)).first();
		
		return worldMapper.get(w).world;
	}
	
	//Create the player for the game
	public Entity createPlayer(){
		Entity player = new Entity();
		World world = getWorld();
		
		BodyComponent bodyComponent = new BodyComponent();
		StateComponent stateComponent = new StateComponent();
		PlayerComponent playerComponent = new PlayerComponent();
		
		//Setup Box2d Body
		BodyDef bodyDef = new BodyDef();
		
		bodyDef.type = BodyType.DynamicBody;
		bodyDef.position.set(Constants.PLAYER_START_X, Constants.PLAYER_START_Y);
		bodyDef.fixedRotation = true;
		
		bodyComponent.body = world.createBody(bodyDef);
		bodyComponent.body.setGravityScale(Constants.PLAYER_GRAVITY_SCALE);
		
		//Create and set the fixture properties
		FixtureDef fixtureDef = new FixtureDef();
		
		PolygonShape playerBox = new PolygonShape();		
		playerBox.setAsBox(Constants.PLAYER_WIDTH / 2, Constants.PLAYER_HEIGHT / 2);
		
		fixtureDef.shape = playerBox;
		fixtureDef.density = 1f;
		fixtureDef.restitution = 0f;
		fixtureDef.friction = 0f;
		
		bodyComponent.body.createFixture(fixtureDef);
		bodyComponent.body.getFixtureList().peek().setUserData(player);
		
		player.add(bodyComponent);
		player.add(playerComponent);
		player.add(stateComponent);
		
		playerBox.dispose();
		
		engine.addEntity(player);
		
		return player;
	}
	
	//Create a camera to follow the player
	public Entity createCamera(Entity target, OrthographicCamera cam){
		Entity camera = new Entity();
		
		//Make and add a camera component with the camera and target
		CameraComponent cameraComponent = new CameraComponent();
		
		//Set the camera and the target entity, probably the player
		cameraComponent.camera = cam;
		cameraComponent.target = target;
		
		//Add the component
		camera.add(cameraComponent);
		
		//Finally add the entity to the engine
		engine.addEntity(camera);
		
		//Return the entity for other uses
		return camera;
	}
}
