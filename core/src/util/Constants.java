package util;

public class Constants 
{
	//visible game world is 5 meters wide
	public static final float VIEWPORT_WIDTH = 5.0f;

	//visible game world is 5 meters tall
	public static final float VIEWPORT_HEIGHT = 5.0f;

	// GUI Width
	public static final float VIEWPORT_GUI_WIDTH = 800.0f;

	// GUI Height
	public static final float VIEWPORT_GUI_HEIGHT = 480.0f;

	//Location of description file for texture atlas
	public static final String TEXTURE_ATLAS_OBJECTS = "images/monkeyLula.atlas";

	// Location of image file for level 01
	public static final String LEVEL_01 = "levels/level-01.png";

	// Amount of extra lives at level start
	public static final int LIVES_START = 3;

	// Duration of feather power-up in seconds
	public static final float ITEM_PINEAPPLE_POWERUP_DURATION = 9;

	// Delay after game over
	public static final float TIME_DELAY_GAME_OVER = 3;

	public static final String TEXTURE_ATLAS_UI = "images/monkeyLula-ui.atlas";
	
	public static final String TEXTURE_ATLAS_LIBGDX_UI = "images/uiskin.atlas";
	
	// Location of description file for skins
	public static final String SKIN_LIBGDX_UI = "images/uiskin.json";
	
	public static final String SKIN_CANYONBUNNY_UI = "images/monkeyLula-ui.json";

	public static final String PREFERENCES = "canyonbunny.prefs";

	public static final int BLOCKS_SPAWN_MAX = 1;

	public static final float BLOCKS_SPAWN_RADIUS = 1.5f;

}
