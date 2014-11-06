package net.net63.codearcade.NinjaTower.systems;

import java.util.Random;

import net.net63.codearcade.NinjaTower.components.BodyComponent;
import net.net63.codearcade.NinjaTower.components.BoundsComponent;
import net.net63.codearcade.NinjaTower.components.BuildingComponent;
import net.net63.codearcade.NinjaTower.components.CoinComponent;
import net.net63.codearcade.NinjaTower.components.PlayerComponent;
import net.net63.codearcade.NinjaTower.components.RenderComponent;
import net.net63.codearcade.NinjaTower.components.TextureComponent;
import net.net63.codearcade.NinjaTower.components.WorldComponent;
import net.net63.codearcade.NinjaTower.utils.Assets;
import net.net63.codearcade.NinjaTower.utils.Constants;
import net.net63.codearcade.NinjaTower.utils.Textures;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class BuildingGenerationSystem extends EntitySystem{
	
	//The chunks
	public Entity[] previousChunk;
	public Entity[] currentChunk;
	public Entity[] nextChunk;
	
	//The game engine
	private Engine engine;
	
	//Random for making the buildings
	private Random random;
	
	//The players in the world
	private ImmutableArray<Entity> players;
	
	//ChangeX when to change chunks at a certain point
	private float changeX;
	
	//The component mappers
	private ComponentMapper<BodyComponent> bodyMapper;
	private ComponentMapper<WorldComponent> worldMapper;
	private ComponentMapper<BuildingComponent> buildingMapper;
	
	
	public BuildingGenerationSystem(){
		super(Constants.SYSTEM_PRIORITIES.BUILDING_GENERATION);
		
		//Initialise all the mappers
		bodyMapper = ComponentMapper.getFor(BodyComponent.class);
		worldMapper = ComponentMapper.getFor(WorldComponent.class);
		buildingMapper = ComponentMapper.getFor(BuildingComponent.class);
		
		//Create a new random with a time-set seed that will be different each time
		random = new Random(System.currentTimeMillis());
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void addedToEngine(Engine engine){
		this.engine = engine; //Set the engine
		
		//Get the players
		players = engine.getEntitiesFor(Family.getFor(PlayerComponent.class));
	}
	
	//Function when new level is being created
	public void create(){
		//Set the start building to give player time to adjust
		createBuilding(Constants.PLAYER_START_X, Constants.BUILDING_START_WIDTH,Constants.BUILDING_START_HEIGHT, Assets.regions.get(Textures.BUILDINGS[0]));
		
		//Initiliase the next chunk
		nextChunk = generateChunk(Constants.PLAYER_START_X + Constants.BUILDING_START_WIDTH, Constants.BUILDING_START_HEIGHT);
		
		//Update the chunks
		updateChunks();
	}
	
	//Create a coin in the game
	public Entity createCoin(){
		Entity coin = new Entity();
		
		World world = getWorld();
		
		CoinComponent coinComponent = new CoinComponent();
		BodyComponent bodyComponent = new BodyComponent();
		
		BodyDef bodyDef = new BodyDef();
		CircleShape shape = new CircleShape();
		
		shape.setRadius(5f);
		
		bodyComponent.body = world.createBody(bodyDef);
		shape.dispose();
		
		coin.add(coinComponent);
		coin.add(bodyComponent);
		
		engine.addEntity(coin);
		
		return coin;
	}
	
	//Function to switch around chunks and make a new one
	public void updateChunks(){
		//Check if it isn't null and if not then destroy all un-needed buildings
		if(previousChunk != null){
			destroyChunk(previousChunk);
		}
		
		//Move the chunks down one
		previousChunk = currentChunk;
		currentChunk = nextChunk;
		
		//Create a new chunk
		nextChunk = createChunk(currentChunk);
		
		//Make a different changeX point
		updateChangeX(currentChunk);
	}
	
	//Utility function to update the changeX where new chunks are created
	public void updateChangeX(Entity[] chunk){
		//Get the last entity in the chunk
		Entity last = chunk[chunk.length - 1];
		
		Body body = bodyMapper.get(last).body;
		BuildingComponent building = buildingMapper.get(last);
		
		//Get the x and width of the last building and add them together to make changeX
		changeX = body.getPosition().x;
		changeX += building.width / 2;
	}
	
	@SuppressWarnings("unchecked")
	//Utility function to get the Box2D world out of the component
	private World getWorld(){
		//Make sure engine isn't null
		if(engine == null){
			return null;
		}
		
		//Get the entity
		Entity w = engine.getEntitiesFor(Family.getFor(WorldComponent.class)).first();
		
		//Return the world component's world
		return worldMapper.get(w).world;
	}
	
	@Override
	public void update(float deltaTime){
		//Get the current player
		Entity player = players.peek();
		
		//Get the body of the player
		Body body  = bodyMapper.get(player).body;
		
		//Check if the x position has reached the stage where the chunks are moved forward
		if(body.getPosition().x > changeX){
			updateChunks();
		}
	}
	
	//Create buildings that the player walks/jumps on
	public Entity createBuilding(float x, float width, float height, TextureRegion texture){
		Entity building = new Entity();
		World world = getWorld();
		
		//Declare and initialise all the components
		TextureComponent textureComponent = new TextureComponent();
		RenderComponent renderComponent = new RenderComponent();
		BoundsComponent boundsComponent = new BoundsComponent();
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
		
		//Set the building component values
		buildingComponent.width = width;
		buildingComponent.height = height;
		
		//Set the bounds values
		boundsComponent.bounds = new Rectangle();
		
		boundsComponent.bounds.x = x;
		boundsComponent.bounds.y = Math.min(0, height - Textures.BUILDING_TEXTURE_HEIGHT);
		boundsComponent.bounds.width = width;
		boundsComponent.bounds.height = Math.max(height, Textures.BUILDING_TEXTURE_HEIGHT);
		
		//Set the texture
		textureComponent.texture = texture;
		
		//Set the z-value
		renderComponent.z = Constants.RENDER_ORDER.BUILDING;
		
		//Add all the components and add to the engine
		building.add(bodyComponent);
		building.add(buildingComponent);
		building.add(textureComponent);
		building.add(renderComponent);
		building.add(boundsComponent);
		
		//Dispose the box to avoid memory leaks
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
			
			//Width of building, relative to constants array of allowed widths
			float width = random.nextInt(Constants.BUILDING_WIDTHS.length);
			
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
			startX += gap + Constants.BUILDING_WIDTHS[(int) width];
			
			//Set the nextHeight as the current height
			startHeight = height;
		}
		
		//Generate the buildings
		//Texture region to avoid re-defining it multiple times
		TextureRegion texture;
		
		for(int i = 0; i < Constants.BUILDINGS_PER_CHUNK; i++){
			//Get the correct texture
			texture = Assets.regions.get(Textures.BUILDINGS[(int) buildingValues[i][1]]);
			
			//Create the building
			buildings[i] = createBuilding(buildingValues[i][0], Constants.BUILDING_WIDTHS[(int)buildingValues[i][1]], buildingValues[i][2], texture);
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
