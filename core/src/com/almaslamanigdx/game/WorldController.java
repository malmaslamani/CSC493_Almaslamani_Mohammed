package com.almaslamanigdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.almaslamanigdx.game.Level;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import objects.Banana;
import objects.Monkey;
import objects.PineApple;
import objects.Rock;
import objects.Monkey.JUMP_STATE;
import screens.MenuScreen;
import util.AudioManager;
import util.CameraHelper;
import util.Constants;
import util.GamePreferences;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.Game;

/**
 * the brain of the game ,contains all the game logic to initialize and modify the
 * game world. It also needs access to CameraHelpe.
 * @author Mohammed Almaslamani
 *
 */
public class WorldController extends InputAdapter
{
	/**
	 * camera helper reference
	 */
	public CameraHelper cameraHelper;

	/**
	 * level reference
	 */
	public Level level;

	/**
	 * counter of the levels
	 */
	public int counter = 0;


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

	private boolean goalReached;
	public World b2world;
	private int highScore;
	private boolean isHighScore;
	public String ch;
	public String [] str = new String[10];

	/**
	 * a constructor that initialize the game 
	 * reference when we need to switch the screen
	 * @param game
	 */
	public WorldController (Game game) 
	{
		this.game = game;
		init();
		isHighScore = false;
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

	/**
	 * a method to initiate the level
	 */
	private void initLevel () 
	{
		//scoreVisual = score;
		GamePreferences.instance.load();
		setGoalReached(false);

		//if level is one, when you start the game 
		//score will be 0
		if(counter == 0)
		{
			score = 0;
			scoreVisual = score;
			level = new Level(Constants.LEVEL_01);
		}

		//if level is 2, keep the score 
		if(counter == 1)
		{
			//score = score;
			scoreVisual = score;
			level = new Level(Constants.LEVEL_02);
		}
		cameraHelper.setTarget(level.monkey);
		initPhysics();

	}

	/**
	 * makes the game return to menu if it is over,
	 * updates the lives we see , and update our level
	 * @param deltaTime
	 */
	public void update(float deltaTime)
	{
		handleDebugInput(deltaTime);

		//when game is over, back to menu
		if (isGameOver())
		{
			GamePreferences.instance.save();
			score = 0;
			timeLeftGameOverDelay -= deltaTime;

			if (timeLeftGameOverDelay < 0) 
				backToMenu();
		} 
		//when goal is reached and the counter is 0, go 
		//to first level
		if(isGoalReached() && counter == 0)
		{
			counter = 1;
			initLevel();
		}

		//when goal is reached and level is 2,
		//go to menu
		if(isGoalReached() && counter == 1)
		{
			GamePreferences.instance.save();

			score = 0;
			//game.setScreen(new HighScoreScreen(game, score));
			backToMenu();
		}

		else 
		{
			handleInputGame(deltaTime);
		}

		level.update(deltaTime);
		testCollisions();
//		b2world.step(deltaTime,  8,  3);
		cameraHelper.update(deltaTime);

		//when the game is not over yet, and the monkey fell in the water
		//decrease one life
		if (!isGameOver() && isPlayerInWater()) 
		{
			GamePreferences.instance.save();
			//will play live_lost.wav when player hit the water.
			AudioManager.instance.play(Assets.instance.sounds.liveLost);

			lives--;

			//if game is over, (no lives)
			//handle the message of GAME OVER and set a time delay for it.
			if (isGameOver())
			{
				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
			}
			else
			{
				initLevel();
			}
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

		//computations to compare different scores.
		if(score > GamePreferences.instance.highscore4 && score < GamePreferences.instance.highscore3)
		{
			GamePreferences.instance.highscore4 = score;
		}

		if(score > GamePreferences.instance.highscore3 && score < GamePreferences.instance.highscore2)
		{
			GamePreferences.instance.highscore3 = score;
		}

		if(score > GamePreferences.instance.highscore2 && score < GamePreferences.instance.highscore1)
		{
			GamePreferences.instance.highscore2 = score;
		}

		if(score > GamePreferences.instance.highscore1)
		{
			GamePreferences.instance.highscore1 = score;
		}
		isHighScore = true;



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

		// Test collision: Monkey <-> Goal
		if (!isGoalReached()) 
		{
			r2.set(level.goal.bounds);
			r2.x += level.goal.position.x;
			r2.y += level.goal.position.y;
			if (r1.overlaps(r2)) 
				onCollisionBunnyWithGoal();
		}
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
	private void handleInputGame(float deltaTime) 
	{
		if (cameraHelper.hasTarget(level.monkey)) 
		{
			// monkey Movement
			if (Gdx.input.isKeyPressed(Keys.LEFT)) 
			{
				level.monkey.velocity.x = -level.monkey.terminalVelocity.x;
			} 
			else if (Gdx.input.isKeyPressed(Keys.RIGHT)) 
			{
				level.monkey.velocity.x = level.monkey.terminalVelocity.x;
			}
		} 
		else 
		{
			// Execute auto-forward movement on non-desktop platform
			if (Gdx.app.getType() != ApplicationType.Desktop) 
			{
				level.monkey.velocity.x = level.monkey.terminalVelocity.x;
			}
		}

		// monkey is jumping
		if (Gdx.input.isTouched() || Gdx.input.isKeyPressed(Keys.SPACE)) 
		{
			level.monkey.setJumping(true);
		} 
		else 
		{
			level.monkey.setJumping(false);
		}
	}
//	private void handleInputGame (float deltaTime) 
//	{
//		if (cameraHelper.hasTarget(level.monkey)) 
//		{
//			// Player Movement
//			if (Gdx.input.isKeyPressed(Keys.LEFT)) 
//			{
//				level.monkey.velocity.x = -level.monkey.terminalVelocity.x;
//			} 
//			else if (Gdx.input.isKeyPressed(Keys.RIGHT))
//			{
//				level.monkey.velocity.x =level.monkey.terminalVelocity.x;
//			} 
//			else 
//			{
//				// Execute auto-forward movement on non-desktop platform
//				if (Gdx.app.getType() != ApplicationType.Desktop)
//				{
//					level.monkey.velocity.x = level.monkey.terminalVelocity.x;
//				}
//			}
//			// monkey Jump
//			if (Gdx.input.isTouched() ||Gdx.input.isKeyPressed(Keys.SPACE)) 
//			{
//				level.monkey.setJumping(true);
//			} 
//			else 
//			{
//				level.monkey.setJumping(false);
//			}
//		}
//	}

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

	/**
	 * when the player reach the goal- banana's rain
	 */
	private void onCollisionBunnyWithGoal() 
	{
		setGoalReached(true);
		timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_FINISHED;
	}


	public boolean isGoalReached() 
	{
		return goalReached;
	}


	public void setGoalReached(boolean goalReached) 
	{
		this.goalReached = goalReached;
	}

	private void initPhysics()
	{
		if(b2world!=null)
			b2world.dispose();
		
		b2world = new World(new Vector2(0, -9.81f), true);


		//Rocks
		Vector2 origin=new Vector2();
		for(Rock rock : level.rocks){
			BodyDef bDef=new BodyDef();
			bDef.type=BodyType.KinematicBody;
			bDef.position.set(rock.position);

			Body body=b2world.createBody(bDef);
			body.setUserData(rock); 
			rock.body=body;

			PolygonShape pShape=new PolygonShape();
			origin.x=rock.bounds.width/2.0f;
			origin.y=rock.bounds.height/2.0f;
			pShape.setAsBox(rock.bounds.width/1.8f, rock.bounds.height/2.0f, origin, 0);	
			FixtureDef fDef=new FixtureDef();
			fDef.shape=pShape;
			body.createFixture(fDef);
			pShape.dispose();
		}

		//player
		Monkey monkey = level.monkey;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(monkey.position);
		bodyDef.fixedRotation = true;

		Body body = b2world.createBody(bodyDef);
		body.setType(BodyType.DynamicBody);
		body.setGravityScale(9.8f);
		body.setUserData(monkey);
		monkey.body = body;

		PolygonShape polygonShape = new PolygonShape();
		origin.x = (monkey.bounds.width) / 2.0f;
		origin.y = (monkey.bounds.height) / 2.0f;
		polygonShape.setAsBox((monkey.bounds.width-0.7f) / 2.0f, (monkey.bounds.height-0.15f) / 2.0f, origin, 0);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		body.createFixture(fixtureDef);
		polygonShape.dispose();
	}

}
