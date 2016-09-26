package com.almaslamanigdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;

//import com.packtpub.libgdx.canyonbunny.util.Constants;
import com.almaslamanigdx.game.Constants;

//to use our new inner classes
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class Assets implements Disposable, AssetErrorListener
{
	public static final String TAG = Assets.class.getName();
	public static final Assets instance = new Assets();
	private AssetManager assetManager;
	public AssetLevelDecoration levelDecoration;
	public AssetMonkey monkey;
	public AssetBanana banana;
	public AssetPineApple pineApple;
	public AssetRock rock;
	public AssetFonts fonts;

	//Singleton: prevent instantiation from other classes 
	private Assets()
	{

	}

	//this method will load all the assets
	public void init (AssetManager assetManager) 
	{
		this.assetManager = assetManager;

		// set asset manager error handler
		assetManager.setErrorListener(this);

		// load texture atlas
		assetManager.load(Constants.TEXTURE_ATLAS_OBJECTS,TextureAtlas.class);

		// start loading assets and wait until finished
		assetManager.finishLoading();
		Gdx.app.debug(TAG, "# of assets loaded: "+ assetManager.getAssetNames().size);

		for (String a : assetManager.getAssetNames())
		{
			Gdx.app.debug(TAG, "asset: " + a);

		}

		//retrieve the reference to the loaded texture
		//atlas by calling the get() method of the asset manager.
		TextureAtlas atlas = assetManager.get(Constants.TEXTURE_ATLAS_OBJECTS);

		// enable texture filtering for pixel smoothing
		for (Texture t : atlas.getTextures()) 
		{
			t.setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}

		// create game resource objects
		fonts = new AssetFonts();
		levelDecoration = new AssetLevelDecoration(atlas);
		monkey = new AssetMonkey(atlas);
		banana = new AssetBanana(atlas);
		pineApple = new AssetPineApple(atlas);
		rock = new AssetRock(atlas);
		

	}


	@Override
	public void dispose() 
	{
		assetManager.dispose();
		fonts.defaultSmall.dispose();
		fonts.defaultNormal.dispose();
		fonts.defaultBig.dispose();
	}

	@Override
	public void error(AssetDescriptor asset, Throwable throwable) 
	{
		Gdx.app.error(TAG, "Couldn't load asset '" +asset.fileName + "'", (Exception)throwable);
	}



	//The constructor takes a reference of the corresponding
	//atlas in which it will find the atlas region it wants.
	//this class contains monkey asset
	public class AssetMonkey
	{
		public final AtlasRegion monkey;
		public AssetMonkey (TextureAtlas atlas) 
		{
			monkey = atlas.findRegion("monkey");
		}
	}

	//this class contains rock assets
	public class AssetRock 
	{
		public final AtlasRegion edge;
		public final AtlasRegion middle;
		public AssetRock (TextureAtlas atlas) 
		{
			edge = atlas.findRegion("SmallRock");
			middle = atlas.findRegion("MediumRock");
		}
	}

	//this class contains banana asset
	public class AssetBanana
	{
		public final AtlasRegion banana;
		public AssetBanana (TextureAtlas atlas) 
		{
			banana = atlas.findRegion("banana");
		}
	}

	//this class contains banana asset
	public class AssetPineApple
	{
		public final AtlasRegion pineApple;
		public AssetPineApple (TextureAtlas atlas) 
		{
			pineApple = atlas.findRegion("pineApple");
		}
	}


	//this class contains all the
	//decorative images that only add to the look and feel of the level
	public class AssetLevelDecoration
	{
		public final AtlasRegion cloud01;
		public final AtlasRegion cloud02;
		public final AtlasRegion cloud03;
		public final AtlasRegion mountainLeft;
		public final AtlasRegion mountainRight;
		public final AtlasRegion waterOverlay;

		public AssetLevelDecoration (TextureAtlas atlas)
		{
			cloud01 = atlas.findRegion("cloud01");
			cloud02 = atlas.findRegion("cloud02");
			cloud03 = atlas.findRegion("cloud03");
			mountainLeft = atlas.findRegion("mountain_left");
			mountainRight = atlas.findRegion("mountain_right");
			waterOverlay = atlas.findRegion("water_overlay");
		}
	}

	public class AssetFonts 
	{
		public final BitmapFont defaultSmall;
		public final BitmapFont defaultNormal;
		public final BitmapFont defaultBig;
		public AssetFonts () 
		{
			
			// create three fonts using Libgdx's 15px bitmap font
			defaultSmall = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultNormal = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			defaultBig = new BitmapFont(
					Gdx.files.internal("images/arial-15.fnt"), true);
			
			// set font sizes
			defaultSmall.getData().setScale(0.75f);
			defaultNormal.getData().setScale(1.0f);
			defaultBig.getData().setScale(2.0f);
			
			// enable linear texture filtering for smooth fonts
			defaultSmall.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
			defaultNormal.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
			defaultBig.getRegion().getTexture().setFilter(
					TextureFilter.Linear, TextureFilter.Linear);
		}
	}


}
