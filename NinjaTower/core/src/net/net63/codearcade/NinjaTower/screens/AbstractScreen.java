package net.net63.codearcade.NinjaTower.screens;

import net.net63.codearcade.NinjaTower.GameMain;
import net.net63.codearcade.NinjaTower.utils.Constants;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;

//Abstract screen class for all states of the game
public abstract class AbstractScreen implements Screen{
		//Setup all the variables usually needed in a screen
		protected final GameMain game;
		
		//Clear colour i.e background colour
	    protected Color clear = new Color(0f,0f,0f,1f);

		public AbstractScreen(GameMain game ){
			//Initialise variables
			this.game = game;
		}
		
		protected String getName(){
			return getClass().getSimpleName();
		}
		
		
		@Override
		public void show(){
			Gdx.app.log( Constants.LOG, "Showing screen: " + getName() );
		}
		
		//On resize
		@Override
		public void resize(int width,int height ){
			Gdx.app.log( Constants.LOG, "Resizing screen: " + getName() + " to: " + width + " x " + height );
		}

		@Override
		public void render(float delta){
			//Clear the screen
			Gdx.gl.glClearColor( clear.r, clear.g, clear.b, clear.a);
			Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );
		}

		@Override
		public void hide(){
			Gdx.app.log( Constants.LOG, "Hiding screen: " + getName() );
			
			dispose();
		}

		@Override
		public void pause(){
			Gdx.app.log( Constants.LOG, "Pausing screen: " + getName() );
		}

		@Override
		public void resume(){
			Gdx.app.log( Constants.LOG, "Resuming screen: " + getName() );
		}

		@Override
		public void dispose(){
			Gdx.app.log( Constants.LOG, "Disposing screen: " + getName() );
		}
}
