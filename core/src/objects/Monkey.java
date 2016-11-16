package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import objects.Monkey.JUMP_STATE;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import util.AudioManager;
import util.CharacterSkin;
import util.Constants;
import util.GamePreferences;

import com.almaslamanigdx.game.Assets;

public class Monkey extends AbstractGameObject
{
	public static final String TAG = Monkey.class.getName();
	private final float JUMP_TIME_MAX = 0.3f;
	private final float JUMP_TIME_MIN = 0.1f;
	private final float JUMP_TIME_OFFSET_FLYING = JUMP_TIME_MAX - 0.018f;
	public ParticleEffect dustParticles = new ParticleEffect();
	private TextureRegion regMonkey;
	public VIEW_DIRECTION viewDirection;
	public float timeJumping;
	public JUMP_STATE jumpState;
	public boolean hasPineApplePowerup;
	public float timeLeftPineApplePowerup;

	//Animations
	private Animation animNormal;
	private Animation animCopterTransform;
	private Animation animCopterTransformBack;
	private Animation animCopterRotate;

	/**
	 * Directions that the monkey can look at.
	 */
	public enum VIEW_DIRECTION 
	{ 
		LEFT, RIGHT 
	}

	/**
	 * States for the monkey
	 */
	public enum JUMP_STATE 
	{
		GROUNDED, FALLING, JUMP_RISING, JUMP_FALLING
	}


	/**
	 * Monkey Constructor.
	 */
	public Monkey() 
	{
		init();
	}

	/**
	 * initializes the monkey game object by setting its physics values,
	 *  a starting view direction, and jump state. 
	 *  It also deactivates the pineapple power-up effect.
	 */
	public void init () 
	{

		dimension.set(1, 1);
		//regMonkey = Assets.instance.monkey.monkey;

		animNormal = Assets.instance.monkey.animNormal;
		animCopterTransform = Assets.instance.monkey.animCopterTransform;
		animCopterTransformBack =
				Assets.instance.monkey.animCopterTransformBack;
		animCopterRotate = Assets.instance.monkey.animCopterRotate;
		setAnimation(animNormal);

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
		hasPineApplePowerup = false;
		timeLeftPineApplePowerup = 0;	

		// Particles
		dustParticles.load(Gdx.files.internal("particles/dust.pfx"),
				Gdx.files.internal("particles"));


	}

	/**
	 * allows us to make the monkey jump. 
	 * The state handling in the code will decide whether jumping is currently possible 
	 * and whether it is a single or a multi jump.
	 * @param jumpKeyPressed
	 */
	public void setJumping (boolean jumpKeyPressed) 
	{
		switch (jumpState) 
		{
		case GROUNDED: // Character is standing on a platform

			if (jumpKeyPressed) 
			{
				//play jump sound 
				AudioManager.instance.play(Assets.instance.sounds.jump);

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

			if (jumpKeyPressed && hasPineApplePowerup) 
			{
				AudioManager.instance.play(Assets.instance.sounds.jumpWithPineapple, 1,MathUtils.random(1.0f, 1.1f));
				timeJumping = JUMP_TIME_OFFSET_FLYING;
				//jumpState = JUMP_STATE.JUMP_RISING;
			}
			break;
		}
	}

	//to toggle the PineApple power-up effect.
	public void setPineApplePowerup (boolean pickedUp) 
	{
		hasPineApplePowerup = pickedUp;
		if (pickedUp) 
		{
			timeLeftPineApplePowerup = Constants.ITEM_PINEAPPLE_POWERUP_DURATION;
		}	
	}

	/**
	 * find out whether the power-up is still active.
	 */
	public boolean hasPineApplePowerup () 
	{
		return hasPineApplePowerup && timeLeftPineApplePowerup > 0;
	}

	@Override
	public void update (float deltaTime) 
	{
		//		super.update(deltaTime); 
		updateMotionX(deltaTime);
		updateMotionY(deltaTime);
		if (body != null)
		{
			body.setLinearVelocity(velocity);
			position.set(body.getPosition());
		}

		if (body.getLinearVelocity().x != 0) 
		{
			viewDirection = body.getLinearVelocity().x < 0 ? VIEW_DIRECTION.LEFT :VIEW_DIRECTION.RIGHT;
		}

		if (timeLeftPineApplePowerup > 0) 
		{
			if (animation == animCopterTransformBack) 
			{
				// Restart "Transform" animation if another feather power-up
				// was picked up during "TransformBack" animation. Otherwise,
				// the "TransformBack" animation would be stuck while the
				// power-up is still active.
				setAnimation(animCopterTransform);
			}
			timeLeftPineApplePowerup -= deltaTime;
			body.getPosition().x += 1.5 * velocity.x * deltaTime;//double the speed when hasPineApple (Assignment 6 C)// brought it back to 1.5 becuz it was too fast

			if (timeLeftPineApplePowerup < 0) 
			{
				// disable power-up
				timeLeftPineApplePowerup = 0;
				setPineApplePowerup(false);
				setAnimation(animCopterTransformBack);
			}
		}

		dustParticles.update(deltaTime);

		// Change animation state according to feather power-up
		if (hasPineApplePowerup) {
			if (animation == animNormal) 
			{
				setAnimation(animCopterTransform);
			} 
			else if (animation == animCopterTransform) 
			{
				if (animation.isAnimationFinished(stateTime))
					setAnimation(animCopterRotate);
			}
		} 
		else 
		{
			if (animation == animCopterRotate) 
			{
				if (animation.isAnimationFinished(stateTime))
					setAnimation(animCopterTransformBack);
			} 
			else if (animation == animCopterTransformBack)
			{
				if (animation.isAnimationFinished(stateTime))
					setAnimation(animNormal);
			}
		}
	}

	/**
	 * handles the calculations and switching of states that is needed to enable jumping and falling.
	 */
	@Override
	protected void updateMotionY(float deltaTime)
	{
		switch (jumpState)
		{
		case GROUNDED:
			jumpState = JUMP_STATE.FALLING;

		case JUMP_RISING:
			timeJumping += deltaTime;
			if (timeJumping <= JUMP_TIME_MAX)
			{
				velocity.y = terminalVelocity.y;
			}
			else
				jumpState = JUMP_STATE.JUMP_FALLING;
			break;
		case FALLING:
			jumpState = JUMP_STATE.GROUNDED;
			break;
		case JUMP_FALLING:
			velocity.y = -terminalVelocity.y;
			break;
		}
		if (jumpState != JUMP_STATE.GROUNDED)
		{
			//Gdx.app.log(TAG, "stopping particles");
			dustParticles.allowCompletion();
			super.updateMotionY(deltaTime);
		}
	}

	//	/**
	//	 * handles the calculations and switching of states that is needed to enable jumping and falling.
	//	 */
	//	@Override
	//	protected void updateMotionY (float deltaTime) 
	//	{
	//		switch (jumpState) 
	//		{
	//		case GROUNDED://the player is standing on a platform.
	//			jumpState = JUMP_STATE.FALLING;
	//
	//			//if moving, dust appear
	//			if (velocity.x != 0) 
	//			{
	//				dustParticles.setPosition(position.x + dimension.x / 2,position.y-.25f);
	//				dustParticles.start();
	//			}
	//			break;
	//
	//		case JUMP_RISING://the player has initiated a jump and is still rising. The maximum jump height has not been reached.
	//			// Keep track of jump time
	//			timeJumping += deltaTime;
	//
	//			// Jump time left?
	//			if (timeJumping <= JUMP_TIME_MAX) 
	//			{
	//				// Still jumping
	//				velocity.y = terminalVelocity.y;
	//			}
	//			break;
	//
	//		case FALLING://the player is falling down.
	//			break;
	//
	//		case JUMP_FALLING://the player is falling down after a previously
	//			//initiated jump. This state is reached either by jumping as long as possible or
	//			//by releasing the jump key earlier than that.
	//
	//			// Add delta times to track jump time
	//			timeJumping += deltaTime;
	//
	//			// Jump to minimal height if jump key was pressed too short
	//			if (timeJumping > 0 && timeJumping <= JUMP_TIME_MIN) 
	//			{
	//				// Still jumping
	//				velocity.y = terminalVelocity.y;
	//			}
	//		}
	//
	//		if (jumpState != JUMP_STATE.GROUNDED)
	//			dustParticles.allowCompletion();
	//		super.updateMotionY(deltaTime);
	//	}


	/**
	 * drawing of the image for the monkey game object. The image will be tinted orange if the PineApple power-up effect is active.
	 */
	@Override
	public void render(SpriteBatch batch) 
	{
		TextureRegion reg = null;

		// Draw Particles
		dustParticles.draw(batch);

		// Apply Skin Color
		batch.setColor(CharacterSkin.values()[GamePreferences.instance.charSkin].getColor());

		float dimCorrectionX = 0;
		float dimCorrectionY = 0;
		if (animation != animNormal) 
		{
			dimCorrectionX = 0.05f;
			dimCorrectionY = 0.2f;
		}

		// Set special color when game object has a feather power-up
		if (hasPineApplePowerup) 
		{
			batch.setColor(1.0f, 0.8f, 0.0f, 1.0f);
		}

		// Draw image
		reg = animation.getKeyFrame(stateTime, true);

		//the player's character will always
		//look in the direction it is moving.
		batch.draw(reg.getTexture(),
				position.x, position.y-0.5f,
				origin.x, origin.y,
				dimension.x + dimCorrectionX,
				dimension.y + dimCorrectionY,
				scale.x, scale.y,
				rotation,
				reg.getRegionX(), reg.getRegionY(),
				reg.getRegionWidth(), reg.getRegionHeight(),
				viewDirection == VIEW_DIRECTION.LEFT, false);


		// Reset color to white
		batch.setColor(1, 1, 1, 1);

	}

}