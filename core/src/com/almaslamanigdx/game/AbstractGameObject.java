package com.almaslamanigdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.MathUtils;

//This class is able to store the position, dimension, origin, scale factor, and angle
//of rotation of a game object.
public abstract class AbstractGameObject 
{
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;

	//these vars to apply the job of the asset
	public Vector2 velocity;
	public Vector2 terminalVelocity;
	public Vector2 friction;
	public Vector2 acceleration;
	public Rectangle bounds;

	public AbstractGameObject () 
	{
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;

		//current speed in m/s
		velocity = new Vector2();
		//positive and negative max speed in m/s
		terminalVelocity = new Vector2(1, 1);
		//opposing force that slows down the object
		friction = new Vector2();
		//constant acceleration in m/s^2
		acceleration = new Vector2();
		//the physical body that will be used for collision detection with other objects.
		bounds = new Rectangle();
	}

	//will be called inside our world controller
	public void update(float deltaTime)
	{
		updateMotionX(deltaTime);
		updateMotionY(deltaTime);
		
		// Move to new position
		position.x += velocity.x * deltaTime;
		position.y += velocity.y * deltaTime;
	}
	
	//will be called inside our world renderer
	public abstract void render(SpriteBatch batch);

	
	//simple physics simulation code that makes use of the new physics attributes
	//calculate the next x component of the object's velocity in terms
	//of the given delta time.
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
				velocity.x =Math.min(velocity.x + friction.x * deltaTime, 0);
			}
		}
		
		// Apply acceleration
		velocity.x += acceleration.x * deltaTime;
		
		// Make sure the object's velocity does not exceed the
		// positive or negative terminal velocity
		velocity.x = MathUtils.clamp(velocity.x,-terminalVelocity.x, terminalVelocity.x);
	}
	
	//calculate the next y component of the object's velocity in terms
	//of the given delta time.
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