package net.net63.codearcade.NinjaTower.systems;

import net.net63.codearcade.NinjaTower.components.BodyComponent;
import net.net63.codearcade.NinjaTower.components.CameraComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public class CameraSystem extends IteratingSystem{
	
	private ComponentMapper<CameraComponent> cm;
	private ComponentMapper<BodyComponent> bm;

	@SuppressWarnings("unchecked")
	public CameraSystem(){
		super(Family.getFor(CameraComponent.class), Constants.SYSTEM_PRIORITIES.CAMERA);
		
		cm = ComponentMapper.getFor(CameraComponent.class);
		bm = ComponentMapper.getFor(BodyComponent.class);
	}
	
	
	@Override
	public void processEntity(Entity entity, float deltaTime){
		CameraComponent camera = cm.get(entity);
		
		if(camera.target == null){
			return;
		}
		
		BodyComponent body = bm.get(camera.target);
		
		camera.camera.position.x = body.body.getPosition().x * Constants.PIXELS_PER_METRE;
		camera.camera.update();
		
	}
}
