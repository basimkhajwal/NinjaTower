package net.net63.codearcade.NinjaTower.systems;

import net.net63.codearcade.NinjaTower.components.BodyComponent;
import net.net63.codearcade.NinjaTower.components.PlayerComponent;
import net.net63.codearcade.NinjaTower.components.StateComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PlayerMovementSystem extends PausedIteratingSystem{
	
	private ComponentMapper<BodyComponent> bm;
	private ComponentMapper<StateComponent> sm;
	
	private boolean isTouched;
	
	@SuppressWarnings("unchecked")
	public PlayerMovementSystem(){
		super(Family.getFor(PlayerComponent.class), Constants.SYSTEM_PRIORITIES.PLAYER_MOVEMENT);
		
		isTouched = false;
		
		bm = ComponentMapper.getFor(BodyComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
	}
	
	@Override
	public void update(float deltaTime){
		if(! isPaused()){
			super.update(deltaTime);
		}
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		BodyComponent bodyComponent = bm.get(entity);
		Body body = bodyComponent.body;
		
		//Set constants x velocity
		float speed = Constants.PLAYER_SPEED;
		float impulse = body.getMass() * (speed - body.getLinearVelocity().x);
		
		body.applyLinearImpulse(new Vector2(impulse, 0), body.getWorldCenter(), true);
		
		//Set jump velocity
		StateComponent stateComponent = sm.get(entity);
		
		if(isTouched){
			isTouched = false;
			
			switch(stateComponent.get()){
			case PlayerComponent.STATE_MOVING:
				stateComponent.set(PlayerComponent.STATE_JUMPING);
				
				impulse = body.getMass() * 20;
				
				body.applyLinearImpulse(new Vector2(0, impulse), body.getWorldCenter(), true);
				
				break;
				
			case PlayerComponent.STATE_JUMPING:
				stateComponent.set(PlayerComponent.STATE_DOUBLE_JUMPING);
				
				impulse = body.getMass() * 20;
				body.applyLinearImpulse(new Vector2(0, impulse), body.getWorldCenter(), true);
				break;
			}
			
			
		}
		
	}
	
	public void touched(){
		isTouched = true;
	}
}
