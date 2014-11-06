package net.net63.codearcade.NinjaTower.systems;

import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

public abstract class PausedIteratingSystem extends IteratingSystem{
	
	private boolean paused;
	
	public PausedIteratingSystem(Family family, int priority){
		super(family, priority);
	
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
