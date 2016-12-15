package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.almaslamanigdx.game.Assets;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;


/**
 * uses the collected variable to store its current state of visibility.
 */
public class Banana extends AbstractGameObject
{
	private TextureRegion regBanana;
	public boolean collected;
	
	public Banana () 
	{
		init();
	}
	
	private void init () 
	{
		dimension.set(0.5f, 0.5f);
		regBanana = Assets.instance.banana.banana;
		
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
		
		
	}
	
	/**
	 * check the collected state to decide whether the object should be rendered or not.
	 */
	public void render (SpriteBatch batch) 
	{
		if (collected) 
			return;
		
		TextureRegion reg = null;
		
		reg = regBanana;
		
		batch.draw(reg.getTexture(), position.x, position.y,
				origin.x, origin.y, dimension.x, dimension.y, scale.x, scale.y,
				rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);
	}
	
	/**
	 * returns the item's score that the player will receive to collect it.
	 * @return 100
	 */
	public int getScore() 
	{
		return 100;
	}
}

