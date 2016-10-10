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

	//reference when we need to switch the screen
	public WorldController (Game game) 
	{
		this.game = game;
		init();
	}


	//save a reference to the game instance, which will enable us to
	//switch to another screen.
	private void backToMenu ()
	{
		// switch to menu screen
		game.setScreen(new MenuScreen(game));
	}

	//called when a collision is detected. Then, the monkey game object
	//is moved accordingly to prevent it from falling through our platforms
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

	//collisions between the bunny head game object and a gold coin
	//game object. It simply flags the gold coin as being collected so that it will disappear.
	private void onCollisionMonkeyWithBanana(Banana banana) 
	{
		banana.collected = true;
		score += banana.getScore();
		Gdx.app.log(TAG, "banana collected");
	}

	//handles collisions between the bunny head game object and
	//a feather game object and refreshes the effect for the monkey
	private void onCollisionMonkeyWithPineApple(PineApple pineApple) 
	{
		pineApple.collected = true;
		score += pineApple.getScore();
		level.monkey.setPineApplePowerup(true);
		Gdx.app.log(TAG, "pineApple collected");
	}

	//testCollisions() that iterates through all
	//the game objects and tests whether there is a collision between the monkey and
	//another game object.
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


	//to rebuild whenever we want
	private void init() 
	{
		Gdx.input.setInputProcessor(this);
		cameraHelper = new CameraHelper();
		lives = Constants.LIVES_START;
		timeLeftGameOverDelay = 0;
		initLevel();
	}


	private void initLevel () 
	{
		score = 0;
		level = new Level(Constants.LEVEL_01);
		cameraHelper.setTarget(level.monkey);
	}

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

		//////////////////mdryy	handleInputGame(deltaTime);
		level.update(deltaTime);
		testCollisions();
		cameraHelper.update(deltaTime);

		if (!isGameOver() && isPlayerInWater()) 
		{
			lives--;
			if (isGameOver())
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			else
				initLevel();
		}
	}


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

	private void moveCamera(float x,float y)
	{
		x += cameraHelper.getPosition().x;
		y += cameraHelper.getPosition().y;
		cameraHelper.setPosition(x, y);
	}


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

	//control the monkey with right, left keys...
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

	public boolean isGameOver () 
	{
		return lives < 0;
	}

	//test the monkey vertical position to find out
	//whether it fell down into the water.
	public boolean isPlayerInWater () 
	{
		return level.monkey.position.y < -5;
	}
}
