package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.almaslamanigdx.game.Assets;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Rock extends AbstractGameObject
{
	//to store texture corresponding edge and middle part
	private TextureRegion regEdge;
	private TextureRegion regMiddle;

	//the resulting length of the rock
	private int length;

	//vars to make the rocks float 
	private final float FLOAT_CYCLE_TIME = 2.0f;
	private final float FLOAT_AMPLITUDE = 0.25f;
	private float floatCycleTimeLeft;
	private boolean floatingDownwards;
	private Vector2 floatTargetPosition;

	public Rock () 
	{
		init();
	}


	private void init () 
	{
		//1 meter wide and 1.5 m tall
		dimension.set(1, 1.5f);

		regEdge = Assets.instance.rock.edge;
		regMiddle = Assets.instance.rock.middle;

		// Start length of this rock
		setLength(1);

		//to initialize , and start floating UP
		floatingDownwards = false;
		floatCycleTimeLeft = MathUtils.random(0,FLOAT_CYCLE_TIME / 2);
		floatTargetPosition = null;
	}

	//to set the starting length
	//of the rock.
	public void setLength (int length) 
	{
		this.length = length;

		// Update bounding box for collision detection
		bounds.set(0, 0, dimension.x * length, dimension.y);
	}

	//to increase the length of
	//the rock by a given amount.
	public void increaseLength (int amount) 
	{
		setLength(length + amount);
	}

	@Override
	public void render(SpriteBatch batch) 
	{
		TextureRegion reg = null;
		float relX = 0;
		float relY = 0;

		// Draw left edge
		reg = regEdge;

		relX -= dimension.x / 4;
		batch.draw(reg.getTexture(), position.x + relX, position.y +
				relY, origin.x, origin.y, dimension.x / 4, dimension.y,
				scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(), false, false);

		// Draw middle
		relX = 0;
		reg = regMiddle;
		for (int i = 0; i < length; i++) 
		{
			batch.draw(reg.getTexture(), position.x + relX, position.y
					+ relY, origin.x, origin.y, dimension.x, dimension.y,
					scale.x, scale.y, rotation, reg.getRegionX(), reg.getRegionY(),
					reg.getRegionWidth(), reg.getRegionHeight(), false, false);
			relX += dimension.x;
		}
		// Draw right edge
		reg = regEdge;
		batch.draw(reg.getTexture(),position.x + relX, position.y +
				relY, origin.x + dimension.x / 8, origin.y, dimension.x / 4,
				dimension.y, scale.x, scale.y, rotation, reg.getRegionX(),
				reg.getRegionY(), reg.getRegionWidth(), reg.getRegionHeight(),
				true, false);
	}

	@Override
	public void update (float deltaTime) 
	{
		super.update(deltaTime);
		
		//used to store the next target position
		floatCycleTimeLeft -= deltaTime;
		
		if (floatTargetPosition == null)
			floatTargetPosition = new Vector2(position);
		
		if (floatCycleTimeLeft<= 0) 
		{
			floatCycleTimeLeft = FLOAT_CYCLE_TIME;
			floatingDownwards = !floatingDownwards;
			floatTargetPosition.y += FLOAT_AMPLITUDE* (floatingDownwards ? -1 : 1);
		}
		position.lerp(floatTargetPosition, deltaTime);
	}

}
