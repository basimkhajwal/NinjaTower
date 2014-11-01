package net.net63.codearcade.NinjaTower.systems;

import net.net63.codearcade.NinjaTower.components.WorldComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class WorldSystem extends IteratingSystem{

	private ComponentMapper<WorldComponent> wm;
	
	@SuppressWarnings("unchecked")
	public WorldSystem() {
		super(Family.getFor(WorldComponent.class), Constants.SYSTEM_PRIORITIES.WORLD);
		
		wm = ComponentMapper.getFor(WorldComponent.class);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime){
		
		WorldComponent wc = wm.get(entity);
		wc.world.step(1f/Constants.BOX2D_FPS, Constants.BOX2D_VELOCITY_TICKS, Constants.BOX2D_POSITION_TICKS);
	}

}
