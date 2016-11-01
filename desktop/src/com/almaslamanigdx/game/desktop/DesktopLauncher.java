package com.almaslamanigdx.game.desktop;

import com.almaslamanigdx.game.AlmaslamaniGdxGame;
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
		if (rebuildAtlas) 
		{
			Settings settings = new Settings();
			settings.maxWidth = 1024;
			settings.maxHeight = 1024;
			settings.debug = drawDebugOutline;
			TexturePacker.process(settings, "assets-raw/images","../core/assets/images","monkeyLula.atlas");
			TexturePacker.process(settings, "assets-raw/images-ui","../core/assets/images","monkeyLula-ui.atlas");
		}

		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "Lula'sMonkeyGame";
		cfg.width = 800;
		cfg.height = 480;
		new LwjglApplication(new AlmaslamaniGdxGame(), cfg);

	}
}
