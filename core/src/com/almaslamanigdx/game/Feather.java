package com.almaslamanigdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.almaslamanigdx.game.Assets;


//can be collected by the player's character by simply walking over it. As
//a result of the gold coin being collected, the object will turn invisible for the rest of
//the game.
public class Feather extends AbstractGameObject 
{
	private TextureRegion regFeather;
	public boolean collected;

	public Feather () 
	{
		init();
	}

	private void init () 
	{
		dimension.set(0.5f, 0.5f);
		regFeather = Assets.instance.feather.feather;

		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;

	}

	//The render() method will always check the collected state to decide whether the object
	//should be rendered or not
	@Override
	public void render(SpriteBatch batch) 
	{
		if (collected) 
			return;

		TextureRegion reg = null;
		reg = regFeather;

		batch.draw(reg.getTexture(), position.x, position.y,
				origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
				rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}

	//returns the item's score that the
	//player will receive to collect it.
	public int getScore() 
	{
		return 250;
	}
}