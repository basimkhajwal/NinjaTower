package net.net63.codearcade.NinjaTower.systems;

import java.util.Random;

import net.net63.codearcade.NinjaTower.components.BodyComponent;
import net.net63.codearcade.NinjaTower.components.BuildingComponent;
import net.net63.codearcade.NinjaTower.components.PlayerComponent;
import net.net63.codearcade.NinjaTower.components.WorldComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BuildingGenerationSystem extends EntitySystem{
	
	public Entity[] previousChunk;
	public Entity[] currentChunk;
	public Entity[] nextChunk;
	
	private Engine engine;
	private Random random;
	
	private ImmutableArray<Entity> players;
	
	private float changeX;
	
	private ComponentMapper<BodyComponent> bodyMapper;
	private ComponentMapper<WorldComponent> worldMapper;
	private ComponentMapper<BuildingComponent> buildingMapper;
	
	
	public BuildingGenerationSystem(){
		super(Constants.SYSTEM_PRIORITIES.BUILDING_GENERATION);
		
		bodyMapper = ComponentMapper.getFor(BodyComponent.class);
		worldMapper = ComponentMapper.getFor(WorldComponent.class);
		buildingMapper = ComponentMapper.getFor(BuildingComponent.class);
		
		
		random = new Random(System.currentTimeMillis());
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine){
		this.engine = engine;
		
		players = engine.getEntitiesFor(Family.getFor(PlayerComponent.class));
	}
	
	
	public void create(){
		createBuilding(Constants.PLAYER_START_X, Constants.BUILDING_START_WIDTH,Constants.BUILDING_START_HEIGHT);
		
		nextChunk = generateChunk(Constants.PLAYER_START_X + Constants.BUILDING_START_WIDTH, Constants.BUILDING_START_HEIGHT);
	
		
		updateChunks();
	}
	
	public void updateChunks(){
		if(previousChunk != null){
			destroyChunk(previousChunk);
		}
		
		previousChunk = currentChunk;
		currentChunk = nextChunk;
		
		nextChunk = createChunk(currentChunk);
		
		updateChangeX(currentChunk);
	}
	
	//Utility function to update the changeX where new chunks are created
	public void updateChangeX(Entity[] chunk){
		Entity last = chunk[chunk.length - 1];
		
		Body body = bodyMapper.get(last).body;
		BuildingComponent building = buildingMapper.get(last);
		
		//Get the x and width of the last building and add them together to make changeX
		changeX = body.getPosition().x;
		changeX += building.width / 2;
	}
	
	@SuppressWarnings("unchecked")
	private World getWorld(){
		if(engine == null){
			return null;
		}
		
		Entity w = engine.getEntitiesFor(Family.getFor(WorldComponent.class)).first();
		
		return worldMapper.get(w).world;
	}
	
	@Override
	public void update(float deltaTime){
		
		Entity player = players.peek();
		
		Body body  = bodyMapper.get(player).body;
		
		if(body.getPosition().x > changeX){
			updateChunks();
		}
	}
	
	//Create buildings that the player walks/jumps on
	public Entity createBuilding(float x, float width, float height){
		Entity building = new Entity();
		World world = getWorld();
		
		BodyComponent bodyComponent = new BodyComponent();
		BuildingComponent buildingComponent = new BuildingComponent();
		
		//Setup Box2d body
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyType.StaticBody;
		bodyDef.position.set(x + width/2, height/2);
		
		bodyComponent.body = world.createBody(bodyDef);
		
		//Generate the fixture and set the properties
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape buildingBox = new PolygonShape();
		buildingBox.setAsBox(width/2, height/2);
		
		fixtureDef.shape = buildingBox;
		fixtureDef.density = 0.5f;
		fixtureDef.restitution = 0f;
		fixtureDef.friction = 0f;
		
		bodyComponent.body.createFixture(fixtureDef);
		
		buildingComponent.width = width;
		buildingComponent.height = height;
		
		//Add all the components and add to the engine
		building.add(bodyComponent);
		building.add(buildingComponent);
		
		buildingBox.dispose();
		
		engine.addEntity(building);
		
		return building;
	}
	
	//Utility function to create a new chunk
	public Entity[] createChunk(Entity[] previousChunk){
		//Get the last building and all of its values / components
		Entity last = previousChunk[previousChunk.length - 1];
		Body lastBody = bodyMapper.get(last).body;
		BuildingComponent lastComponent = buildingMapper.get(last);
		
		//Get the properties of the last component
		float height = lastComponent.height;
		float width = lastComponent.width;
		float x = lastBody.getPosition().x;
		
		return generateChunk(x + width/2, height);
	}
	
	//Generate a chunk for level creation
	public Entity[] generateChunk(float startX, float startHeight){
		
		//The building entities themselves
		Entity[] buildings = new Entity[Constants.BUILDINGS_PER_CHUNK];
		
		//Values for the entities before creating them
		float[][] buildingValues = new float[Constants.BUILDINGS_PER_CHUNK][3];
		
		//Generate the building settings
		for(int i = 0; i < Constants.BUILDINGS_PER_CHUNK; i++){
			//Gap from last building
			float gap = random.nextInt((int) (Constants.BUILDING_GAP_MAX - Constants.BUILDING_GAP_MIN)) + Constants.BUILDING_GAP_MIN;
			
			//Width of building
			float width = Constants.BUILDING_WIDTHS[random.nextInt(Constants.BUILDING_WIDTHS.length)];
			
			//Height of building
			float height;
			
			do{
				//The change of the building height, dependant on the minimum and maximum values
				float change = (random.nextInt((int) (Constants.BUILDING_MAX_HEIGHT_CHANGE - Constants.BUILDING_MIN_HEIGHT_CHANGE)) + Constants.BUILDING_MIN_HEIGHT_CHANGE);
				
				//So that height can decrease as well
				if(random.nextBoolean()){
					change *= -1;
				}
				
				//Set the height to previous height + change
				height = startHeight + change;
			}while(height < Constants.BUILDING_MIN_HEIGHT || height > Constants.BUIILDING_MAX_HEIGHT); //Keep running until building is within bounds
			
			//Set all the building values
			buildingValues[i][0] = startX + gap;
			buildingValues[i][1] = width;
			buildingValues[i][2] = height;
			
			//Set the next x-value
			startX += gap + width;
			
			//Set the nextHeight as the current height
			startHeight = height;
		}
		
		//Generate the buildings
		for(int i = 0; i < Constants.BUILDINGS_PER_CHUNK; i++){
			buildings[i] = createBuilding(buildingValues[i][0], buildingValues[i][1], buildingValues[i][2]);
		}
		
		//Finally return the chunk
		return buildings;
	}
	
	//Utility function to destroy a chunk
	public void destroyChunk(Entity[] chunk){
		
		World world = getWorld(); //The world for Box2d
		
		//Iterate over all the buildings in the chunk
		for(Entity entity: chunk){
			//Remove all the bodies in the Box2d world
			world.destroyBody(bodyMapper.get(entity).body);
			
			//Remove the entity itself from the engine
			engine.removeEntity(entity);
		}
	}
}
