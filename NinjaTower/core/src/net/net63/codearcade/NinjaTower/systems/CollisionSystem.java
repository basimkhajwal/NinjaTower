package net.net63.codearcade.NinjaTower.systems;

import net.net63.codearcade.NinjaTower.components.PlayerComponent;
import net.net63.codearcade.NinjaTower.components.StateComponent;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionSystem implements ContactListener{
	
	private ComponentMapper<PlayerComponent> pm;
	private ComponentMapper<StateComponent> sm;
	
	public CollisionSystem(){
		pm = ComponentMapper.getFor(PlayerComponent.class);
		sm = ComponentMapper.getFor(StateComponent.class);
	}

	@Override
	public void beginContact(Contact contact) {
		Entity entityA = (Entity) contact.getFixtureA().getUserData();
		Entity entityB = (Entity) contact.getFixtureB().getUserData();
		Entity player = null;
		
		if(pm.has(entityA)){
			player = entityA;
		}else if(pm.has(entityB)){
			player = entityB;
		}
		
		if(player != null){
			StateComponent sc = sm.get(player);
			
			sc.set(PlayerComponent.STATE_MOVING);
		}
	}

	@Override
	public void endContact(Contact contact) {
		
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}
}
