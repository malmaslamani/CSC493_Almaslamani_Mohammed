package com.almaslamanigdx.game;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import screens.MenuScreen;
import util.AudioManager;
import util.GamePreferences;

import com.almaslamanigdx.game.Assets;

public class AlmaslamaniGdxGame extends Game 
{
	@Override
	public void create ()
	{
		// Set Libgdx log level
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
	
		// Load assets
		Assets.instance.init(new AssetManager());
		
		// Load preferences for audio settings and start playing music
		//via play() in AudioManager
		GamePreferences.instance.load();
		AudioManager.instance.play(Assets.instance.music.song01);
		
		// Start game at menu screen
		setScreen(new MenuScreen(this));
	}
}
