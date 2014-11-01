package net.net63.codearcade.NinjaTower.systems;

import java.util.Arrays;
import java.util.Comparator;

import net.net63.codearcade.NinjaTower.components.BackgroundComponent;
import net.net63.codearcade.NinjaTower.components.BodyComponent;
import net.net63.codearcade.NinjaTower.components.BoundsComponent;
import net.net63.codearcade.NinjaTower.components.PlayerComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;
import net.net63.codearcade.NinjaTower.utils.Textures;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;

public class BackgroundSystem extends EntitySystem{
	
	private ComponentMapper<BackgroundComponent> backgroundMapper;
	private ComponentMapper<BoundsComponent> boundsMapper;
	private ComponentMapper<BodyComponent> bodyMapper;
	
	private Comparator<Entity> comparator;
	
	private ImmutableArray<Entity> backgrounds;
	private ImmutableArray<Entity> players;
	
	private OrthographicCamera camera;

	private float playerPreviousX;
	
	public BackgroundSystem(OrthographicCamera camera){
		super(Constants.SYSTEM_PRIORITIES.BACKGROUND);
		
		this.camera = camera;
		
		backgroundMapper = ComponentMapper.getFor(BackgroundComponent.class);
		boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
		bodyMapper = ComponentMapper.getFor(BodyComponent.class);
		
		comparator = new Comparator<Entity>() {
			@Override
			public int compare(Entity entityA, Entity entityB) {
				float x1 = boundsMapper.get(entityA).bounds.x;
				float x2 = boundsMapper.get(entityB).bounds.x;
				
				return (int)Math.signum(x1 - x2);
			}
		};
		
		playerPreviousX = Constants.PLAYER_START_X;
	}


	@Override
	@SuppressWarnings("unchecked")
	public void addedToEngine(Engine engine){
		
		//Get the backgrounds and players
		backgrounds = engine.getEntitiesFor(Family.getFor(BackgroundComponent.class));
		players = engine.getEntitiesFor(Family.getFor(PlayerComponent.class));
	}
	
	@Override
	public void update(float deltaTime){
		
		//Switch around backgrounds if they are below the screen position
		
		//Sort the backgrounds by their numbers using our comparator
		Entity[] sortedBackgrounds = backgrounds.toArray(Entity.class);
		
		Arrays.sort(sortedBackgrounds, comparator);
		
		//Get the last and the first backgrounds for use in the loop
		BackgroundComponent firstBackground = backgroundMapper.get(sortedBackgrounds[0]);
		BackgroundComponent lastBackground = backgroundMapper.get(sortedBackgrounds[sortedBackgrounds.length - 1]);
		
		//Get the last and first bounds of the backgrounds, these contain their positions, also the current backgrounds
		//that is being drawn in the centre
		Rectangle firstBounds = boundsMapper.get(sortedBackgrounds[0]).bounds;
		Rectangle lastBounds = boundsMapper.get(sortedBackgrounds[sortedBackgrounds.length - 1]).bounds;
		Rectangle currentBounds = boundsMapper.get(sortedBackgrounds[Textures.CURRENT_BACKGROUND - 1]).bounds;
		
		//Get the zero position in camera co-ordinates
		float zero = camera.position.x - camera.viewportWidth / 2;
		zero *= Constants.METRES_PER_PIXEL;
		
		//Check if the background is less than zero relative to the camera
		if(currentBounds.x < zero){
			//Move the first background to the position of the last background added to the width
			firstBounds.x = lastBounds.x + lastBounds.width;
			firstBackground.backgroundNumber = lastBackground.backgroundNumber; //Set it to the correct number
			
			//Decrement all the other backgrounds
			for(int i = 1; i < sortedBackgrounds.length; i++){
				backgroundMapper.get(sortedBackgrounds[i]).backgroundNumber -= 1;
			}
		}
		
		//Move the backgrounds with the player
		Body body = bodyMapper.get(players.peek()).body;
		
		//Get the changed x of the player and get how much we want to change the building value
		float changeX = body.getPosition().x - playerPreviousX;
		float velocityX = changeX * Constants.BACKGROUND_SPEED;
		
		for(int i = 0; i < backgrounds.size(); i++){
			
			//Update all of the bounds of each background with the changed velocity
			boundsMapper.get(backgrounds.get(i)).bounds.x += velocityX;
			
		}
		
		playerPreviousX = body.getPosition().x;
	}

}
