package net.net63.codearcade.NinjaTower.systems;

import net.net63.codearcade.NinjaTower.components.WorldComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;

public class DebugRenderingSystem extends IteratingSystem{
	
	private Box2DDebugRenderer debugRenderer;
	private ComponentMapper<WorldComponent> wm;
	private OrthographicCamera camera;
	
	@SuppressWarnings("unchecked")
	public DebugRenderingSystem(OrthographicCamera camera){
		super(Family.getFor(WorldComponent.class), Constants.SYSTEM_PRIORITIES.DEBUG_RENDERING);
		
		debugRenderer = new Box2DDebugRenderer();
		wm = ComponentMapper.getFor(WorldComponent.class);
		this.camera = camera;
	}
	
	public OrthographicCamera getCamera(){
		return camera;
	}
	
	@Override
	public void processEntity(Entity entity, float delta){
		debugRenderer.render(wm.get(entity).world, camera.combined.cpy().scl(Constants.PIXELS_PER_METRE, Constants.PIXELS_PER_METRE, 0));
	}
}
