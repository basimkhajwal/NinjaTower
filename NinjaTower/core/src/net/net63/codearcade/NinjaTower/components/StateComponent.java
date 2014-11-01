package net.net63.codearcade.NinjaTower.components;

import com.badlogic.ashley.core.Component;

public class StateComponent extends Component{
	private int state = 0;
	public float time = 0.0f;
	
	public int get() {
		return state;
	}
	
	public void set(int newState) {
		state = newState;
		time = 0.0f;
	}
}
