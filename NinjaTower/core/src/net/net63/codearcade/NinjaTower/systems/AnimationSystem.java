package net.net63.codearcade.NinjaTower.systems;

import net.net63.codearcade.NinjaTower.components.AnimationComponent;
import net.net63.codearcade.NinjaTower.components.StateComponent;
import net.net63.codearcade.NinjaTower.components.TextureComponent;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.Animation;

public class AnimationSystem extends IteratingSystem{
	
	private ComponentMapper<AnimationComponent> animationMapper;
	private ComponentMapper<TextureComponent> textureMapper;
	private ComponentMapper<StateComponent> stateMapper;
	
	@SuppressWarnings("unchecked")
	public AnimationSystem(){
		super(Family.getFor(AnimationComponent.class, TextureComponent.class, StateComponent.class), Constants.SYSTEM_PRIORITIES.ANIMATION);
	
		animationMapper = ComponentMapper.getFor(AnimationComponent.class);
		textureMapper = ComponentMapper.getFor(TextureComponent.class);
		stateMapper = ComponentMapper.getFor(StateComponent.class);
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime){
		
		//Get all the required components from the mappers
		AnimationComponent animationComponent = animationMapper.get(entity);
		TextureComponent textureComponent = textureMapper.get(entity);
		StateComponent stateComponent = stateMapper.get(entity);
		
		//Get the current animation
		Animation animation = animationComponent.animations.get(stateComponent.get());
		
		//If not null then set the current texture to the current time of the animation
		if(animation != null){
			textureComponent.texture = animation.getKeyFrame(stateComponent.time);
		}
		
		//Update the time of the state
		stateComponent.time += deltaTime;
		
	}
	
}
