package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import util.CharacterSkin;
import util.Constants;
import util.GamePreferences;
import com.almaslamanigdx.game.Assets;

public class BunnyHead extends AbstractGameObject
{
	public static final String TAG = BunnyHead.class.getName();

	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;

	//hold a reference to our loaded and readyto-
	//fire dust particle effect.
	public ParticleEffect dustParticles = new ParticleEffect();

	//defined the viewing direction�a state for jumping and another
	//state for the feather power-up
	public enum VIEW_DIRECTION 
	{ 
		LEFT, RIGHT 
	}

	public enum JUMP_STATE 
	{
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}

	private TextureRegion regHead;
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public JUMP_STATE jumpState;
	public boolean hasFeatherPowerup;
	public float timeLeftFeatherPowerup;

	public BunnyHead () 
	{
		init();
	}

	//initializes the bunny head game object by setting its physics
	//values, a starting view direction, and jump state. It also deactivates the feather
	//power-up effect.
	public void init () 
	{
		dimension.set(1, 1);
		regHead = Assets.instance.bunny.head;

		// Center image on game object
		origin.set(dimension.x / 2, dimension.y / 2);

		// Bounding box for collision detection
		bounds.set(0, 0, dimension.x, dimension.y);

		// Set physics values
		terminalVelocity.set(3.0f, 4.0f);
		friction.set(12.0f, 0.0f);
		acceleration.set(0.0f, -25.0f);

		// View direction
		viewDirection = VIEW_DIRECTION.RIGHT;

		// Jump state
		jumpState = JUMP_STATE.FALLING;
		timeJumping = 0;

		// Power-ups
		hasFeatherPowerup = false;
		timeLeftFeatherPowerup = 0;

		// Particles
		dustParticles.load(Gdx.files.internal("particles/dust.pfx"),
				Gdx.files.internal("particles"));
	}


	//The state handling in the
	//code will decide whether jumping is currently possible and whether it is a single or a
	//multi jump.
	public void setJumping (boolean jumpKeyPressed) 
	{
		switch (jumpState) 
		{
		case GROUNDED: // Character is standing on a platform
			if (jumpKeyPressed) 
			{
				// Start counting jump time from the beginning
				timeJumping = 0;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;

		case JUMP_RISING: // Rising in the air
			if (!jumpKeyPressed)
				jumpState = JUMP_STATE.JUMP_FALLING;
			break;

		case FALLING:// Falling down

		case JUMP_FALLING: // Falling down after jump	
			if (jumpKeyPressed && hasFeatherPowerup) 
			{
				timeJumping = JUMP_TIME_OFFSET_FLYING;
				jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		}
	}

	//allows us to toggle the feather power-up effect via the
	//setFeatherPowerup() method.
	public void setFeatherPowerup (boolean pickedUp) 
	{
		hasFeatherPowerup = pickedUp;

		if (pickedUp) 
		{
			timeLeftFeatherPowerup = Constants.ITEM_FEATHER_POWERUP_DURATION;
		}	
	}

	// The hasFeatherPowerup() method can be used to
	//find out whether the power-up is still active
	public boolean hasFeatherPowerup() 
	{
		return hasFeatherPowerup && timeLeftFeatherPowerup > 0;
	}


	//handles the drawing of the image for the bunny head game
	//object. The image will be tinted orange if the feather power-up effect is active.
	@Override
	public void render(SpriteBatch batch) 
	{
		TextureRegion reg = null;

		// Draw Particles
		dustParticles.draw(batch);

		// Apply Skin Color
		batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());

		// Set special color when game object has a feather power-up
		if (hasFeatherPowerup) 
		{
			batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
		}

		// Draw image
		reg = regHead;
		batch.draw(reg.getTexture(), position.x, position.y, origin.x,
				origin.y, dimension.x, dimension.y, scale.x, scale.y, rotation,
				reg.getRegionX(), reg.getRegionY(), reg.getRegionWidth(),
				reg.getRegionHeight(), viewDirection == VIEW_DIRECTION.LEFT,
				false);

		// Reset color to white
		batch.setColor(1, 1, 1, 1);
	}


	//handles the switching of the viewing direction according to the
	//current move direction. Also, the time remaining of the power-up effect is checked. If
	//the time is up, the feather power-up effect is disabled.
	@Override
	public void update (float deltaTime) 
	{
		super.update(deltaTime);

		if (velocity.x != 0) 
		{
			viewDirection = velocity.x < 0 ? VIEW_DIRECTION.LEFT :
				VIEW_DIRECTION.RIGHT;
		}

		if (timeLeftFeatherPowerup > 0) 
		{
			timeLeftFeatherPowerup -= deltaTime;

			if (timeLeftFeatherPowerup < 0) 
			{
				// disable power-up
				timeLeftFeatherPowerup = 0;
				setFeatherPowerup(false);
			}
		}
		dustParticles.update(deltaTime);

	}

	//handles the calculations and switching of states that is needed to
	//enable jumping and falling.
	@Override
	protected void updateMotionY (float deltaTime) 
	{
		switch (jumpState) 
		{
		case GROUNDED:
			jumpState = JUMP_STATE.FALLING;
			
			//when moving, the dust will be drawn 
			if (velocity.x != 0) 
			{
				dustParticles.setPosition(position.x + dimension.x / 2,position.y);
				dustParticles.start();
			}
			jumpState = JUMP_STATE.FALLING;
			break;

		case JUMP_RISING:
			// Keep track of jump time
			timeJumping += deltaTime;

			// Jump time left?
			if (timeJumping <= JUMP_TIME_MAX) 
			{
				// Still jumping
				velocity.y = terminalVelocity.y;
			}
			break;

		case FALLING:
			break;

		case JUMP_FALLING:
			// Add delta times to track jump time
			timeJumping += deltaTime;

			// Jump to minimal height if jump key was pressed too short
			if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) 
			{
				// Still jumping
				velocity.y = terminalVelocity.y;
			}
		}
		if (jumpState != JUMP_STATE.GROUNDED)
			//need to be stopped to become invisible
			dustParticles.allowCompletion();
		super.updateMotionY(deltaTime);
	}

}
