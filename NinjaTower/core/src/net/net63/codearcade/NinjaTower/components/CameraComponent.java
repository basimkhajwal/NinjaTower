package net.net63.codearcade.NinjaTower.components;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraComponent extends Component{
	public OrthographicCamera camera;
	public Entity target;
}
