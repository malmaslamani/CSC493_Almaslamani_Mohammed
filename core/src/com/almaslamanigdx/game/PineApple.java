package com.almaslamanigdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.almaslamanigdx.game.Assets;

//uses the collected variable to store its current state of visibility.
//same as Banana.java
public class PineApple extends AbstractGameObject
{
	private TextureRegion regPineApple;
	public boolean collected;

	public PineApple()
	{
		init();
	}

	private void init () 
	{
		dimension.set(0.5f, 0.5f);
		regPineApple = Assets.instance.pineApple.pineApple;

		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}

	public void render (SpriteBatch batch) 
	{
		if (collected) 
			return;
		
		TextureRegion reg = null;
		
		reg = regPineApple;
		
		batch.draw(reg.getTexture(), position.x, position.y,
				origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
				rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	public int getScore() 
	{
		return 250;
	}
}
