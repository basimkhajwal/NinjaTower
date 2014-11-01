package net.net63.codearcade.NinjaTower.screens;

import net.net63.codearcade.NinjaTower.GameMain;
import net.net63.codearcade.NinjaTower.utils.Assets;
import net.net63.codearcade.NinjaTower.utils.Constants;
import net.net63.codearcade.NinjaTower.utils.Textures;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class MainMenuScreen extends AbstractScreen{
	//State system to avoid duplicate code
	private final static int STATE_LOADING = 0;
	private final static int STATE_TRANSITION = 1;
	private final static int STATE_MENU = 2;
	private final static int STATE_GAME = 3;
	
	private int state;
	
	private final static String TOUCH_MESSAGE = "Touch Anywhere To Play";
	
	//Transition state variables
	private float currentY;
	private float currentTimeTransition;
	
	//Menu state variables
	private float opacity;
	private float currentTimeMenu;
	private boolean direction;
	
	private AtlasRegion background;
	
	private SpriteBatch	batch;
	private OrthographicCamera camera;
	
	private Rectangle logoBounds;
	private Vector2 textBounds;
	private Vector2 touchToPlayBounds;
	
	private BitmapFont fontFifty;
	private BitmapFont fontHundred;
	
	public MainMenuScreen(GameMain game) {
		super(game);
		
		//Set the batch and the camera
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		
		//Load the assets 
		Assets.loadBefore();
		
		//Get the bitmap fonts
		fontFifty = Assets.turtleFonts[Constants.FONT_SIZE.FIFTY];
		fontHundred = Assets.turtleFonts[Constants.FONT_SIZE.HUNDRED];
		
		//Set the state to loading
		state = STATE_LOADING;
		
		//Set transition state variables
		currentY = 0;
		currentTimeTransition = 0;
		
		
		//Set the menu variables
		opacity = 0;
		currentTimeMenu = 0;
		direction = true;
		
		
		//Begin loading all the assets
		Assets.loadAll();
	}
	
	@Override
	public void resize(int width, int height){
		//Set the camera to orthogonal projection, with y going up
		camera.setToOrtho(false);
		
		//Set the correct projection matrix
		batch.setProjectionMatrix(camera.combined);
		
		//Set the position of the logo on the screen, relative to the size
		float logoWidth = width * 0.8f;
		logoBounds = new Rectangle( (width - logoWidth) / 2 , 123 /* Temporary */, logoWidth ,logoWidth / Assets.logo.getWidth() * Assets.logo.getHeight());
	
		logoBounds.y = (height - logoBounds.height) * 0.9f;
		
		//Set the position of the loading section
		textBounds = new Vector2();
		
		textBounds.x = (width - fontHundred.getBounds("Loading... 12%").width) / 2;
		textBounds.y = height * 0.5f;
		
		
		//Set menu state variables
		touchToPlayBounds = new Vector2();
		
		touchToPlayBounds.x = (width - fontFifty.getBounds(TOUCH_MESSAGE).width) / 2;
		touchToPlayBounds.y = height * 0.35f;
	}
	
	@Override
	public void render(float delta){
		super.render(delta);
		
		//Render and update the correct state
		switch(state){
		case STATE_LOADING:
			renderLoading(delta);
			break;
		case STATE_TRANSITION:
			renderTransition(delta);
			break;
		case STATE_MENU:
			renderMenu(delta);
			break;
		case STATE_GAME:
			batch.dispose();
			fontFifty.setColor(1,1,1,1);
			game.setScreen(new GameScreen(game));
			break;
		}
	}
	
	//The loading screen whilst the assets are loading
	public void renderLoading(float delta){
		
		//Begin the sprite batch's drawing queue
		batch.begin();
		
		//Draw the background image , loading text and the logo at the correct position
		batch.draw(Assets.splashBackground, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(Assets.logo, logoBounds.x, logoBounds.y, logoBounds.width, logoBounds.height);
		
		//Get the current progress of the resource loading
		int progress = (int)(Assets.getProgress() * 100);
		
		//Draw the progress onto the screen
		fontHundred.draw(batch, "Loading.. " + progress + "%", textBounds.x, textBounds.y);
		
		//End the sprite batch's drawing queue
		batch.end();
		
		if(progress == 100){
			background = Assets.regions.get(Textures.BACKGROUND_START);
			state = STATE_TRANSITION;
		}
	}
	
	//Nice transition screen between the initial loading and the menu
	public void renderTransition(float delta){
		//Do the updating
		if(currentTimeTransition >= Constants.TIME_TO_TRANSITION){
			state = STATE_MENU;
		}else{
			currentY = Interpolation.pow5Out.apply(0, Gdx.graphics.getHeight(), currentTimeTransition / Constants.TIME_TO_TRANSITION);
		}
		
		//Make sure it is never more than the height of the window
		currentY = (float) Math.min(currentY, Gdx.graphics.getHeight());
		
		currentTimeTransition += delta;
		
		//Do the drawing
		//Begin the sprite batch's drawing queue
		batch.begin();
		
		//Draw the two screen images at the correct distance
		batch.draw(Assets.splashBackground, 0, currentY, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.draw(background, 0, currentY - Gdx.graphics.getHeight(), Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//Draw the logo
		batch.draw(Assets.logo, logoBounds.x, logoBounds.y, logoBounds.width, logoBounds.height);
		
		//End the sprite batch's drawing queue
		batch.end();
	}
	
	//The menu screen from which to start the game
	public void renderMenu(float delta){
		//Updating
		//If the user has clicked then go onto the game state
		if(Gdx.input.justTouched()){
			state = STATE_GAME;
		}
		
		if(currentTimeMenu >= Constants.TIME_FLASHING){
			currentTimeMenu = 0;
			direction = ! direction;
		}
		
		opacity = Interpolation.fade.apply(0, 1, currentTimeMenu / Constants.TIME_FLASHING);
		
		currentTimeMenu += delta;
		
		if(direction){
			fontFifty.setColor(1f, 1f, 1f, opacity);
		}else{
			fontFifty.setColor(1f, 1f, 1f, 1 - opacity);
		}
		
		//Do the drawing
		//Begin the sprite batch's drawing queue
		batch.begin();
		
		//Draw the start background at the correct position and size
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		//Draw the logo
		batch.draw(Assets.logo, logoBounds.x, logoBounds.y, logoBounds.width, logoBounds.height);
		
		//Draw the text
		fontFifty.draw(batch, TOUCH_MESSAGE, touchToPlayBounds.x, touchToPlayBounds.y);
		
		//End the sprite batch's drawing queue
		batch.end();
	}
}
