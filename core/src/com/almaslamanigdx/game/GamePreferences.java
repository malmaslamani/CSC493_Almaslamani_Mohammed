package com.almaslamanigdx.game;
//should be in util.
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;

public class GamePreferences 
{
	public static final String TAG =GamePreferences.class.getName();
	public static final GamePreferences instance =new GamePreferences();
	public boolean sound;
	public boolean music;
	public float volSound;
	public float volMusic;
	public int charSkin;
	public boolean showFpsCounter;
	private Preferences prefs;


	// singleton: prevent instantiation from other classes
	//to be called from anywhere
	private GamePreferences () 
	{
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}

	//will try to get the best value
	public void load () 
	{ 
		sound = prefs.getBoolean("sound", true);
		music = prefs.getBoolean("music", true);
		//will return a value of 0.5f if there is no value found for the key named volSound.
		//and clamp will mae sure it stays between 0.0f and 1.0f
		volSound = MathUtils.clamp(prefs.getFloat("volSound", 0.5f),0.0f, 1.0f);
		volMusic = MathUtils.clamp(prefs.getFloat("volMusic", 0.5f),0.0f, 1.0f);
		charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0),0, 2);
		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
	}
	
	//takes the current values of its
	//public variables and puts them into the map of the preferences file
	public void save () 
	{ 
		prefs.putBoolean("sound", sound);
		prefs.putBoolean("music", music);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin", charSkin);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		
		//called on the preferences file to actually write the changed values into the file.
		prefs.flush();
	}
}