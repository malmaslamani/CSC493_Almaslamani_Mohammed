package com.almaslamanigdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.almaslamanigdx.game.WorldController;
import com.almaslamanigdx.game.WorldRenderer;
import com.almaslamanigdx.game.GamePreferences;

public class GameScreen extends AbstractGameScreen 
{
	private static final String TAG = GameScreen.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;

	public GameScreen (Game game) 
	{
		super(game);
	}

	@Override
	public void render(float deltaTime) 
	{
		// Do not update game world when paused.
		if (!paused) 
		{
			// Update game world by the time that has passed
			// since last rendered frame.
			worldController.update(deltaTime);
		}
		
		// Sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64 / 255.0f, 0x95 / 255.0f,0xed /255.0f, 0xff / 255.0f);
		
		// Clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// Render game world to screen
		worldRenderer.render();
	}

	@Override
	public void resize(int width, int height) 
	{
		worldRenderer.resize(width, height);
	}

	//act like create()
	@Override
	public void show() 
	{
		//ensures that the game screen will always work with the latest game
		//settings
		GamePreferences.instance.load();
		worldController = new WorldController(game);
		worldRenderer = new WorldRenderer(worldController);
		Gdx.input.setCatchBackKey(true);
	}

	//act like dispose()
	@Override
	public void hide() 
	{
		worldRenderer.dispose();
		Gdx.input.setCatchBackKey(false);
	}

	@Override
	public void pause() 
	{
		paused = true;
	}
	

}
