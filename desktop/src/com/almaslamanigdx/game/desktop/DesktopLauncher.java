package com.almaslamanigdx.game.desktop;

import com.almaslamanigdx.game.CanyonBunnyMain;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;



public class DesktopLauncher 
{
	public static boolean rebuildAtlas = false;
	public static boolean drawDebugOutline = false;
	
	public static void main (String[] arg) 
	{
		if (rebuildAtlas) {
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.duplicatePadding = false;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "assets-raw/images", "../core/assets/images","canyonbunny.pack");
			}
		
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "CanyonBunny";
		cfg.width = 800;
		cfg.height = 480;
		new LwjglApplication(new CanyonBunnyMain(), cfg);
		
	}
}
