package net.net63.codearcade.NinjaTower.systems;

import com.badlogic.ashley.core.EntitySystem;

public abstract class PausedEntitySystem extends EntitySystem{
	
	private boolean paused;
	
	public PausedEntitySystem(int priority){
		super(priority);
		
		resume();
	}
	
	@Override
	public void update(float deltaTime){
		if(!paused){
			super.update(deltaTime);
		}
	}
	
	public void resume(){
		paused = false;
	}
	
	public void pause(){
		paused = true;
	}
	
	public boolean isPaused(){
		return paused;
	}
	
}
