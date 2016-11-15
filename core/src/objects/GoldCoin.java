package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.almaslamanigdx.game.Assets;
import com.badlogic.gdx.math.MathUtils;


/**
 * can be collected by the player's character by simply walking over it. As
 * a result of the gold coin being collected, the object will turn invisible for the rest of
 * the game.
 *
 */
public class GoldCoin extends AbstractGameObject 
{

	private TextureRegion regGoldCoin;
	public boolean collected;

	public GoldCoin () 
	{
		init();
	}

	private void init () 
	{
		dimension.set(0.5f, 0.5f);
		
		setAnimation(Assets.instance.goldCoin.animGoldCoin);
		stateTime = MathUtils.random(0.0f, 1.0f);
		

		// Set bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);
		collected = false;
	}

	//The render() method will always check the collected state to decide whether the object
	//should be rendered or not
	@Override
	public void render (SpriteBatch batch) 
	{
		if (collected) 
			return;

		TextureRegion reg = null;
		reg = animation.getKeyFrame(stateTime, true);
		
		batch.draw(reg.getTexture(),
				position.x, position.y,
				origin.x, origin.y,
				dimension.x, dimension.y,
				scale.x, scale.y,
				rotation,
				reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(),
				false, false);
	}

	//The render() method will always check the collected state to decide whether the object
	//should be rendered or not
	public int getScore() 
	{
		return 100;
	}	
}

