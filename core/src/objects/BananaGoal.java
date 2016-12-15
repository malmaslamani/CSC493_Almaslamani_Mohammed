package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.almaslamanigdx.game.Assets;

/**
 * class to handle raining Bananas at the goal
 * @author HP
 *
 */
public class BananaGoal extends AbstractGameObject 
{

	private TextureRegion regBananaGoal;

	public BananaGoal () 
	{
		init();
	}

	private void init () {

		dimension.set(0.25f, 0.5f);
		regBananaGoal = Assets.instance.levelDecoration.bananaGoal;
		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		origin.set(dimension.x / 2, dimension.y / 2);
	}

	public void render (SpriteBatch batch) 
	{
		TextureRegion reg = null;
		reg = regBananaGoal;
		batch.draw(reg.getTexture(), position.x - origin.x,
				position.y - origin.y, origin.x, origin.y, dimension.x,
				dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
				reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), false, false);
	}
}
