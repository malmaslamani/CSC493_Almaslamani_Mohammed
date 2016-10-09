package com.almaslamanigdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import screens.MenuScreen;

import com.almaslamanigdx.game.Assets;

public class CanyonBunnyMain extends Game 
{
	//setting the log level and loading our assets, 
	//and then start with the menu screen.
	@Override
	public void create () 
	{
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	
		// Load assets
		Assets.instance.init(new AssetManager());
		
		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}
}
