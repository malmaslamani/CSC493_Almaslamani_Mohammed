package com.almaslamanigdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.almaslamanigdx.game.Level;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import objects.AbstractGameObject;
import objects.Banana;
import objects.CollisionHandler;
import objects.Monkey;
import objects.PineApple;
import objects.Rock;
import objects.Monkey.JUMP_STATE;
import screens.MenuScreen;
import util.AudioManager;
import util.CameraHelper;
import util.Constants;

import com.badlogic.gdx.Game;

public class WorldController extends InputAdapter implements Disposable
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
	
	// Box2D Collisions
	public World myWorld;
	public Array<AbstractGameObject> objectsToRemove;


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
	 * to rebuild whenever we want
	 */
	private void init() 
	{
		objectsToRemove = new Array<AbstractGameObject>();

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
		initPhysics();

	}
	
	private void initPhysics()
	{
		if (myWorld != null)
			myWorld.dispose();
		myWorld = new World(new Vector2(0, -9.81f), true);
		myWorld.setContactListener(new CollisionHandler(this));  // Not in the book
		Vector2 origin = new Vector2();
		
		//for rocks 
		for (Rock rock : level.rocks)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(rock.position);
			bodyDef.type = BodyType.KinematicBody;
			Body body = myWorld.createBody(bodyDef);
			//body.setType(BodyType.DynamicBody);
			body.setUserData(rock);
			rock.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = rock.bounds.width / 2.0f;
			origin.y = rock.bounds.height / 2.0f;
			polygonShape.setAsBox(rock.bounds.width / 2.0f, (rock.bounds.height-0.04f) / 4.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
		}
		
		//for banana 
		for (Banana banana : level.banana)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(banana.position);
			bodyDef.type = BodyType.KinematicBody;
			Body body = myWorld.createBody(bodyDef);
			//body.setType(BodyType.DynamicBody);
			body.setUserData(banana);
			banana.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = banana.bounds.width / 2.0f;
			origin.y = banana.bounds.height / 2.0f;
			polygonShape.setAsBox(banana.bounds.width / 2.0f, (banana.bounds.height-0.04f) / 4.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
		}
		
		//for pineapple powerUp
		for (PineApple pineapple : level.pineApple)
		{
			BodyDef bodyDef = new BodyDef();
			bodyDef.position.set(pineapple.position);
			bodyDef.type = BodyType.KinematicBody;
			Body body = myWorld.createBody(bodyDef);
			//body.setType(BodyType.DynamicBody);
			body.setUserData(pineapple);
			pineapple.body = body;
			PolygonShape polygonShape = new PolygonShape();
			origin.x = pineapple.bounds.width / 2.0f;
			origin.y = pineapple.bounds.height / 2.0f;
			polygonShape.setAsBox(pineapple.bounds.width / 2.0f, (pineapple.bounds.height-0.04f) / 4.0f, origin, 0);
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = polygonShape;
			body.createFixture(fixtureDef);
			polygonShape.dispose();
		}

		// For PLayer
		Monkey monkey = level.monkey;
		BodyDef bodyDef = new BodyDef();
		bodyDef.position.set(monkey.position);
		bodyDef.fixedRotation = true;

		Body body = myWorld.createBody(bodyDef);
		body.setType(BodyType.DynamicBody);
		body.setGravityScale(9.8f);
		body.setUserData(monkey);
		monkey.body = body;

		PolygonShape polygonShape = new PolygonShape();
		origin.x = (monkey.bounds.width) / 2.0f;
		origin.y = (monkey.bounds.height-0.8f) / 2.0f;
		polygonShape.setAsBox((monkey.bounds.width-0.7f) / 2.0f, (monkey.bounds.height-0.15f) / 2.0f, origin, 0);

		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = polygonShape;
		body.createFixture(fixtureDef);
		polygonShape.dispose();
		
	
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
	
//	private void spawnBlocks(Vector2 pos, int numBlocks, float radius)
//	{
//		float blockShapeScale = 0.5f;
//		for (int i = 0; i<numBlocks; i++)
//		{
//			Banana banana = new Banana();
//			float x = MathUtils.random(-radius,radius);
//			float y = MathUtils.random(5.0f, 15.0f);
//			//float rotation = MathUtils.random(0.0f, 360.0f) * MathUtils.degreesToRadians;
//			float blockScale = MathUtils.random(0.5f, 1.5f);
//			banana.scale.set(blockScale, blockScale);
//
//			BodyDef bodyDef = new BodyDef();
//			bodyDef.position.set(pos);
//			bodyDef.position.add(x, y);
//			bodyDef.angle = 0; // rotation;
//			Body body = myWorld.createBody(bodyDef);
//			body.setType(BodyType.DynamicBody);
//			body.setUserData(banana);
//			banana.body = body;
//
//			PolygonShape polygonShape = new PolygonShape();
//			float halfWidth = banana.bounds.width / 2.0f * blockScale;
//			float halfHeight = banana.bounds.height / 2.0f * blockScale;
//			polygonShape.setAsBox(halfWidth * blockShapeScale, halfHeight * blockShapeScale);
//
//			FixtureDef fixtureDef = new FixtureDef();
//			fixtureDef.shape = polygonShape;
//			fixtureDef.density = 50;
//			fixtureDef.restitution = 0.5f;
//			fixtureDef.friction = 0.5f;
//			body.createFixture(fixtureDef);
//			polygonShape.dispose();
//			level.banana.add(banana);
//		}
//	}
	
	public void flagForRemoval(AbstractGameObject obj)
	{
		objectsToRemove.add(obj);
	}
	
	
	public void update(float deltaTime)
	{
		// Because the Box2D step function is not running I know
		// that nothing new is being added to objectsToRemove.
		handleDebugInput(deltaTime);
		if (objectsToRemove.size > 0)
		{
			for (AbstractGameObject obj : objectsToRemove)
			{
				if (obj instanceof Banana)
				{
					int index = level.banana.indexOf((Banana) obj, true);
					if (index != -1)
					{
					    level.banana.removeIndex(index);
					    myWorld.destroyBody(obj.body);
					}
				}
			}
			objectsToRemove.removeRange(0, objectsToRemove.size - 1);
		}

		handleInputGame(deltaTime);

		if (MathUtils.random(0.0f, 2.0f) < deltaTime)
		{
		    // Temp Location to Trigger Blocks
		    Vector2 centerPos = new Vector2(level.monkey.position);
		    centerPos.x += level.monkey.bounds.width;
		  //  spawnBlocks(centerPos, Constants.BLOCKS_SPAWN_MAX, Constants.BLOCKS_SPAWN_RADIUS);
		}

		myWorld.step(deltaTime, 8, 3);  // Tell the Box2D world to update.
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
	 * called when a collision is detected. 
	 * Then, the monkey game object is moved accordingly to prevent it from falling through our platforms
	 * @param rock
	 */
	private void onCollisionMonkeyWithRock(Rock rock) 
	{
		Monkey monkey = level.monkey;

//		float heightDifference = Math.abs(monkey.position.y - ( rock.position.y + rock.bounds.height));
//
//		if (heightDifference > 0.25f) 
//		{
//			boolean hitRightEdge = monkey.position.x > (rock.position.x + rock.bounds.width / 2.0f);
//
//			if (hitRightEdge) 
//			{
//				monkey.position.x = rock.position.x + rock.bounds.width;
//			} 
//			else 
//			{
//				monkey.position.x = rock.position.x -monkey.bounds.width;
//			}
//			return;
//		}

		switch (monkey.jumpState) 
		{
		case GROUNDED:
			break;

		case FALLING:

		case JUMP_FALLING: 
			//monkey.position.y = rock.position.y + monkey.bounds.height + monkey.origin.y;
			monkey.jumpState = JUMP_STATE.GROUNDED;
			break;

		case JUMP_RISING:
			//monkey.position.y = rock.position.y + monkey.bounds.height + monkey.origin.y;
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

		// Test collision: monkey <-> bananas
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
	 * makes the game return to menu if it is over,
	 * updates the lives we see , and update our level
	 * @param deltaTime
	 */
//	public void update(float deltaTime)
//	{
//		handleDebugInput(deltaTime);
//		if (isGameOver()) 
//		{
//			timeLeftGameOverDelay -= deltaTime;
//
//			if (timeLeftGameOverDelay < 0) 
//				backToMenu();
//
//		} 
//		else 
//		{
//			handleInputGame(deltaTime);
//		}
//
//		level.update(deltaTime);
//		testCollisions();
//		cameraHelper.update(deltaTime);
//
//		if (!isGameOver() && isPlayerInWater()) 
//		{
//			//will play live_lost.wav when player hit the water.
//			AudioManager.instance.play(Assets.instance.sounds.liveLost);
//		
//			lives--;
//			
//			if (isGameOver())
//				timeLeftGameOverDelay = Constants.TIME_DELAY_GAME_OVER;
//			else
//				initLevel();
//		}
//		
//		//All three mountain layers will now scroll at different speeds: 30 percent, 50 percent,
//		//and 80 percent.
//		level.mountains.updateScrollPosition
//		(cameraHelper.getPosition());
//		
//		//This enables us to play an animation as long
//		//as livesVisual has not yet reached the current value of lives
//		if (livesVisual> lives)
//			livesVisual = Math.max(lives, livesVisual - 1 * deltaTime);
//		
//		if (scoreVisual< score)
//			scoreVisual = Math.min(score, scoreVisual + 250 * deltaTime);
//	}

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
	
	@Override
	public void dispose()
	{
		if (myWorld != null)
			myWorld.dispose();
	}
}
