package net.net63.codearcade.NinjaTower.systems;

import java.util.Arrays;
import java.util.Comparator;

import net.net63.codearcade.NinjaTower.components.BodyComponent;
import net.net63.codearcade.NinjaTower.components.BoundsComponent;
import net.net63.codearcade.NinjaTower.components.RenderComponent;
import net.net63.codearcade.NinjaTower.components.TextureComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;

public class RenderingSystem extends EntitySystem{
	
	private OrthographicCamera camera;
	private SpriteBatch batch;
	
	private ComponentMapper<TextureComponent> textureMapper;
	private ComponentMapper<BodyComponent> bodyMapper;
	private ComponentMapper<BoundsComponent> boundsMapper;
	private ComponentMapper<RenderComponent> renderMapper;
	
	private ImmutableArray<Entity> entities;
	private Comparator<Entity> comparator;
	
	public RenderingSystem(OrthographicCamera camera){
		super(Constants.SYSTEM_PRIORITIES.RENDERING);
		
		this.camera = camera;
		batch = new SpriteBatch();
		
		textureMapper = ComponentMapper.getFor(TextureComponent.class);
		bodyMapper = ComponentMapper.getFor(BodyComponent.class);
		boundsMapper = ComponentMapper.getFor(BoundsComponent.class);
		renderMapper = ComponentMapper.getFor(RenderComponent.class);
		
		comparator = new Comparator<Entity>() {
			
			@Override
			public int compare(Entity a, Entity b) {
				
				return (int) Math.signum(renderMapper.get(a).z - renderMapper.get(b).z);
			}
		};
	}
	
	@Override
	public void update(float deltaTime){
		batch.setProjectionMatrix(camera.combined.cpy().scl(Constants.PIXELS_PER_METRE, Constants.PIXELS_PER_METRE, 0));
		
		Entity[] sortedEntities = entities.toArray();
		
		Arrays.sort(sortedEntities, comparator);
		
		batch.begin();
		
		for(Entity e: sortedEntities){
			processEntity(e, deltaTime);
		}
		
		batch.end();
	}
	
	public void processEntity(Entity entity, float deltaTime){
		
		TextureRegion texture = textureMapper.get(entity).texture;
		
		if(bodyMapper.has(entity)){
			Body body = bodyMapper.get(entity).body;
			
			Vector2 position = new Vector2();
			
			position.x = body.getPosition().x;
			position.y = body.getPosition().y;
			
			Vector2 dimensions = new Vector2();
			
			for(Fixture fixture: body.getFixtureList()){
				
				if(fixture.getType() == Type.Polygon){
					PolygonShape polygonShape = (PolygonShape) fixture.getShape();
					polygonShape.getVertex(0, dimensions);
					
					dimensions.x = Math.abs(dimensions.x);
					dimensions.y = Math.abs(dimensions.y);
					
				}else if(fixture.getType() == Type.Circle){
					CircleShape circleShape = (CircleShape) fixture.getShape();
					
					float radius = circleShape.getRadius();
					float size = Vector2.len(radius, radius);
					
					dimensions.x = size;
					dimensions.y = size;
				}else{
					continue;
				}
				
				batch.draw(texture, position.x - dimensions.x, position.y - dimensions.y, dimensions.x * 2, dimensions.y * 2);
			}
		}else{
			Rectangle bounds = boundsMapper.get(entity).bounds;
			
			batch.draw(texture, bounds.x, bounds.y, bounds.width, bounds.height);
		}
		
	}
}
