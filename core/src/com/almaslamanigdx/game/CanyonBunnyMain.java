package com.almaslamanigdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import screens.MenuScreen;

import com.almaslamanigdx.game.Assets;

public class CanyonBunnyMain extends Game 
{
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
