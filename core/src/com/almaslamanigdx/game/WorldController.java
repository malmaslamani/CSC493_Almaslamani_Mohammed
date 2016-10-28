package com.almaslamanigdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.almaslamanigdx.game.Level;
import com.badlogic.gdx.math.Rectangle;

import objects.Banana;
import objects.Monkey;
import objects.PineApple;
import objects.Rock;
import objects.Monkey.JUMP_STATE;
import screens.MenuScreen;
import util.AudioManager;
import util.CameraHelper;
import util.Constants;

import com.badlogic.gdx.Game;

public class WorldController extends InputAdapter
{
	public CameraHelper cameraHelper;
	public Level level;
	public int lives;
	public int score;
	private float timeLeftGameOverDelay;
	private static final String TAG = WorldController.class.getName();
	private Game game;
	
	// Rectangles for collision detection
	private Rectangle r1 = new Rectangle();
	private Rectangle r2 = new Rectangle();

	//vars to help the lives,bananas animation
	public float livesVisual;
	public float scoreVisual;
	
	//reference when we need to switch the screen
	public WorldController (Game game) 
	{
		this.game = game;
		init();
	}


	/**
	 * save a reference to the game instance, which will enable us to switch to another screen.
	 */
	private void backToMenu ()
	{
		// switch to menu screen
		game.setScreen(new MenuScreen(game));
	}

	/**
	 * called when a collision is detected. 
	 * Then, the monkey game object is moved accordingly to prevent it from falling through our platforms
	 * @param rock
	 */
	private void onCollisionMonkeyWithRock(Rock rock) 
	{
		Monkey monkey = level.monkey;

		float heightDifference = Math.abs(monkey.position.y - ( rock.position.y + rock.bounds.height));

		if (heightDifference > 0.25f) 
		{
			boolean hitRightEdge = monkey.position.x > (rock.position.x + rock.bounds.width / 2.0f);

			if (hitRightEdge) 
			{
				monkey.position.x = rock.position.x + rock.bounds.width;
			} 
			else 
			{
				monkey.position.x = rock.position.x -monkey.bounds.width;
			}
			return;
		}

		switch (monkey.jumpState) 
		{
		case GROUNDED:
			break;

		case FALLING:

		case JUMP_FALLING: 
			monkey.position.y = rock.position.y + monkey.bounds.height + monkey.origin.y;
			monkey.jumpState = JUMP_STATE.GROUNDED;
			break;

		case JUMP_RISING:
			monkey.position.y = rock.position.y + monkey.bounds.height + monkey.origin.y;
			break;
		}
	}

	/**
	 * collisions between the bunny head game object and a gold coin
	 * 	game object. It simply flags the gold coin as being collected so that it will disappear.

	 */
	private void onCollisionMonkeyWithBanana(Banana banana) 
	{
		banana.collected = true;
		AudioManager.instance.play(Assets.instance.sounds.eatBanana);
		score += banana.getScore();
		Gdx.app.log(TAG, "banana collected");
	}

	/**
	 * handles collisions between the bunny head game object 
	 * and a feather game object and refreshes the effect for the monkey
	 * @param pineApple
	 */
	private void onCollisionMonkeyWithPineApple(PineApple pineApple) 
	{
		pineApple.collected = true;
		
		score += pineApple.getScore();
		AudioManager.instance.play(Assets.instance.sounds.eatBanana);
		level.monkey.setPineApplePowerup(true);
		Gdx.app.log(TAG, "pineApple collected");
	}

	/**
	 * testCollisions() that iterates through all the game objects 
	 * and tests whether there is a collision 
	 * between the monkey and another game object.
	 */
	private void testCollisions () 
	{
		r1.set(level.monkey.position.x, level.monkey.position.y,
				level.monkey.bounds.width, level.monkey.bounds.height);

		// Test collision: monkey <-> Rocks
		for (Rock rock : level.rocks) 
		{
			r2.set(rock.position.x, rock.position.y, rock.bounds.width,
					rock.bounds.height);
			if (!r1.overlaps(r2)) 
				continue;
			onCollisionMonkeyWithRock(rock);
			// IMPORTANT: must do all collisions for valid
			// edge testing on rocks.
		}

		// Test collision: Bunny Head <-> bananas
		for (Banana banana : level.banana)
		{
			if (banana.collected) 
				continue;

			r2.set(banana.position.x, banana.position.y,
					banana.bounds.width, banana.bounds.height);

			if (!r1.overlaps(r2)) 
				continue;

			onCollisionMonkeyWithBanana(banana);
			break;
		}

		// Test collision: Bunny Head <-> pineApple
		for (PineApple pineApple : level.pineApple) 
		{
			if (pineApple.collected) 
				continue;

			r2.set(pineApple.position.x, pineApple.position.y,
					pineApple.bounds.width, pineApple.bounds.height);

			if (!r1.overlaps(r2)) 
				continue;

			onCollisionMonkeyWithPineApple(pineApple);
			break;
		}
	}


	/**
	 * to rebuild whenever we want
	 */
	private void init() 
	{
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		livesVisual = lives;
		timeLeftGameOverDelay = 0;
		initLevel();
	}


	private void initLevel () 
	{
		score = 0;
		scoreVisual = score;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.monkey);
	}

	/**
	 * makes the game return to menu if it is over,
	 * updates the lives we see , and update our level
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		handleDebugInput(deltaTime);
		if (isGameOver()) 
		{
			timeLeftGameOverDelay -= deltaTime;

			if (timeLeftGameOverDelay < 0) 
				backToMenu();

		} 
		else 
		{
			handleInputGame(deltaTime);
		}

		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);

		if (!isGameOver() && isPlayerInWater()) 
		{
			//will play live_lost.wav when player hit the water.
			AudioManager.instance.play(Assets.instance.sounds.liveLost);
		
			lives--;
			
			if (isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
		
		//All three mountain layers will now scroll at different speeds: 30 percent, 50 percent,
		//and 80 percent.
		level.mountains.updateScrollPosition
		(cameraHelper.getPosition());
		
		//This enables us to play an animation as long
		//as livesVisual has not yet reached the current value of lives
		if (livesVisual> lives)
			livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
		
		if (scoreVisual< score)
			scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);
	}

	/**
	 * handles the camera movement
	 * @param deltaTime
	 */
	private void handleDebugInput(float deltaTime) 
	{
		if(Gdx.app.getType() != ApplicationType.Desktop)
		{
			return;
		}

		if (!cameraHelper.hasTarget(level.monkey)) 
		{
			//camera controls (move)
			float camMoveSpeed = 5 * deltaTime;
			float camMoveSpeedAccelerationFactor = 5;

			if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
			{
				camMoveSpeed *=camMoveSpeedAccelerationFactor;
			}
			if(Gdx.input.isKeyPressed(Keys.LEFT))
			{
				moveCamera(-camMoveSpeed,0);
			}
			if(Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				moveCamera(camMoveSpeed,0);
			}
			if(Gdx.input.isKeyPressed(Keys.UP))
			{
				moveCamera(0,camMoveSpeed);
			}
			if(Gdx.input.isKeyPressed(Keys.DOWN))
			{
				moveCamera(0,-camMoveSpeed);
			}
			if(Gdx.input.isKeyPressed(Keys.BACKSPACE))
			{
				cameraHelper.setPosition(0, 0);
			}
		}
		//camera controls   (zoom)
		float camZoomSpeed = 1* deltaTime;
		float camZoomSpeedAccelerationFactor = 5;

		if(Gdx.input.isKeyPressed(Keys.SHIFT_LEFT))
		{
			camZoomSpeed *= camZoomSpeedAccelerationFactor;
		}
		if(Gdx.input.isKeyPressed(Keys.COMMA))
		{
			cameraHelper.addZoom(camZoomSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.PERIOD))
		{
			cameraHelper.addZoom(-camZoomSpeed);
		}
		if(Gdx.input.isKeyPressed(Keys.SLASH))
		{
			cameraHelper.setZoom(1);
		}
	}

	/**
	 * Moving the camera around
	 * @param x
	 * @param y
	 */
	private void moveCamera(float x,float y)
	{
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}


	/**
	 * handles resetting the game or following the character by camera.
	 */
	@Override
	public boolean keyUp(int keycode)
	{
		//reset game world 
		if(keycode == Keys.R)
		{
			init();
			Gdx.app.debug(TAG, "Game world resetted");
		}

		// Toggle camera follow
		else if (keycode == Keys.ENTER)
		{
			cameraHelper.setTarget(cameraHelper.hasTarget()? null: level.monkey);
			Gdx.app.debug(TAG, "Camera follow enabled: "+ cameraHelper.hasTarget());
		}

		// Back to Menu
		else if (keycode == Keys.ESCAPE || keycode == Keys.BACK) 
		{
			backToMenu();
		}

		return false;
	}

	/**
	 * control the monkey with right, left keys...
	 * @param deltaTime
	 */
	private void handleInputGame (float deltaTime) 
	{
		if (cameraHelper.hasTarget(level.monkey)) 
		{
			// Player Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT)) 
			{
				level.monkey.velocity.x = -level.monkey.terminalVelocity.x;
			} 
			else if (Gdx.input.isKeyPressed(Keys.RIGHT))
			{
				level.monkey.velocity.x =level.monkey.terminalVelocity.x;
			} 
			else 
			{
				// Execute auto-forward movement on non-desktop platform
				if (Gdx.app.getType() != ApplicationType.Desktop)
				{
					level.monkey.velocity.x = level.monkey.terminalVelocity.x;
				}
			}
			// monkey Jump
			if (Gdx.input.isTouched() ||Gdx.input.isKeyPressed(Keys.SPACE)) 
			{
				level.monkey.setJumping(true);
			} 
			else 
			{
				level.monkey.setJumping(false);
			}
		}
	}

	/**
	 * checks if game is over
	 * @return
	 */
	public boolean isGameOver () 
	{
		return lives < 0;
	}

	/**
	 * test the monkey vertical position to find out whether it fell down into the water.
	 */
	public boolean isPlayerInWater () 
	{
		return level.monkey.position.y < -5;
	}
}
