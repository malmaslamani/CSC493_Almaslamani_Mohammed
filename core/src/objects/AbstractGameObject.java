package objects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

public abstract class AbstractGameObject 
{
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;
	
	// Non-Box2D Physics
	public Vector2 velocity;//the object's current speed in m/s.
	public Vector2 terminalVelocity;//positive and negative maximum speed in m/s.
	public Vector2 friction;//opposing force that slows down the object
	public Vector2 acceleration;//constant acceleration in m/s².
	public Rectangle bounds;//used for collision detection with other objects.

	// Box2D Physics
	public Body body;
	
	// Animation
	//public float stateTime;
	//public Animation animation;


	public AbstractGameObject () 
	{
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
		velocity = new Vector2();
		terminalVelocity = new Vector2(1, 1);
		friction = new Vector2();
		acceleration = new Vector2();
		bounds = new Rectangle();

	}

	/**
	 * called on every update cycle to calculate the next x and y 
	 * components of the object's velocity in terms of the given delta time
	 * @param deltaTime
	 */
	protected void updateMotionX (float deltaTime) 
	{
		if (velocity.x != 0) 
		{
			// Apply friction
			if (velocity.x > 0)
			{
				velocity.x = Math.max(velocity.x - friction.x * deltaTime, 0);
			} 
			else 
			{
				velocity.x = Math.min(velocity.x + friction.x * deltaTime, 0);
			}
		}
		
		// Apply acceleration
		velocity.x += acceleration.x * deltaTime;
		
		// Make sure the object's velocity does not exceed the
		// positive or negative terminal velocity
		velocity.x = MathUtils.clamp(velocity.x,-terminalVelocity.x, terminalVelocity.x);
	}
	
	public void update (float deltaTime) 
	{
		//stateTime += deltaTime; needed for animation ?? or not
		if(body == null)
		{
			updateMotionX(deltaTime);
			updateMotionY(deltaTime);
			
			// Move to new position
			position.x += velocity.x * deltaTime;
			position.y += 2*velocity.y * deltaTime; //doubled (Assignment 6 C)
		}
		else
		{
			position.set(body.getPosition());
			rotation = body.getAngle() * MathUtils.radiansToDegrees;
		}
		
	}

	public abstract void render (SpriteBatch batch);



	protected void updateMotionY (float deltaTime) 
	{
		if (velocity.y != 0) 
		{

			// Apply friction
			if (velocity.y > 0) 
			{
				velocity.y = Math.max(velocity.y - friction.y *deltaTime, 0);
			} 
			else 
			{
				velocity.y = Math.min(velocity.y + friction.y *deltaTime, 0);
			}
		}

		// Apply acceleration
		velocity.y += acceleration.y * deltaTime;

		// Make sure the object's velocity does not exceed the
		// positive or negative terminal velocity
		velocity.y = MathUtils.clamp(velocity.y, -terminalVelocity.y, terminalVelocity.y);
	}
}