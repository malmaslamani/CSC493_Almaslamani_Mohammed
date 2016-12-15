package util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;


public class GamePreferences 
{
	public static final String TAG = GamePreferences.class.getName();
	public static final GamePreferences instance = new GamePreferences();
	public boolean sound;
	public boolean music;
	public float volSound;
	public float volMusic;
	public int charSkin;
	public boolean showFpsCounter;
    public int highscore1;
    public int highscore2;
    public int highscore3;
    public int highscore4;
    
	private Preferences prefs;

	// singleton: prevent instantiation from other classes
	private GamePreferences () 
	{
		prefs = Gdx.app.getPreferences(Constants.PREFERENCES);
	}

	//can be called from virtually anywhere inside our project
	public void load () 
	{
		sound = prefs.getBoolean("sound", true);
		music = prefs.getBoolean("music", true);
		
		//will return a value of 0.5f if there is no value found for the key named volSound.
		//clamp will ensure that the value is within the allowed range of values 0.0f and 1.0f
		volSound = MathUtils.clamp(prefs.getFloat("volSound", 0.5f),0.0f, 1.0f);
		volMusic = MathUtils.clamp(prefs.getFloat("volMusic", 0.5f),0.0f, 1.0f);
		charSkin = MathUtils.clamp(prefs.getInteger("charSkin", 0),0, 2);
		
		//high score holders
        highscore1 = prefs.getInteger("highScore1");
        highscore2 = prefs.getInteger("highScore2");
        highscore3 = prefs.getInteger("highScore3");
        highscore4 = prefs.getInteger("highScore4");

		showFpsCounter = prefs.getBoolean("showFpsCounter", false);
	}
	
	//takes the current values of its
	//public variables and puts them into the map of the preferences file.
	public void save () 
	{
		prefs.putBoolean("sound", sound);
		prefs.putBoolean("music", music);
		prefs.putFloat("volSound", volSound);
		prefs.putFloat("volMusic", volMusic);
		prefs.putInteger("charSkin", charSkin);
        prefs.putInteger("highScore1", highscore1);
        prefs.putInteger("highScore2", highscore2);
        prefs.putInteger("highScore3", highscore3);
        prefs.putInteger("highScore4", highscore4);
		prefs.putBoolean("showFpsCounter", showFpsCounter);
		prefs.flush();
	}

}
