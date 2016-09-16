package com.almaslamanigdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.almaslamanigdx.game.Assets;

public class CanyonBunnyMain implements ApplicationListener 
{
	private static final String TAG = CanyonBunnyMain.class.getName();
	private WorldController worldController;
	private WorldRenderer worldRenderer;
	private boolean paused;


	@Override
	public void create() 
	{
		//o print out everything to the console 
		//that might be logged during runtime.
		//set libGdx log level to DEBUG
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// Load assets
		Assets.instance.init(new AssetManager());
		
		//Initialize controller and rendered
		worldController = new WorldController();
		worldRenderer = new WorldRenderer(worldController);

		//Game world is active on start
		paused = false;
		
	
	}

	@Override
	public void resize(int width, int height) 
	{	
		worldRenderer.resize(width, height);
	}

	@Override
	public void render() 
	{
			//Update game world by the time that has passed 
			//since last rendered frame.
			worldController.update(Gdx.graphics.getDeltaTime());
			
		//sets the clear screen color to: Cornflower Blue
		Gdx.gl.glClearColor(0x64/255.0f,  0x95/255.0f, 0xed/255.0f, 0xff/255.0f);
		
		//clears the screen
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		//render game world to screen
		worldRenderer.render();
	}

	@Override
	public void pause() 
	{		
		paused = true;
	}

	@Override
	public void resume() 
	{		
		paused = false;
	}

	@Override
	public void dispose() 
	{
		worldRenderer.dispose();
		Assets.instance.dispose();
	}

}
