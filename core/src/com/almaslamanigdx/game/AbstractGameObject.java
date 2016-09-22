package com.almaslamanigdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

//This class is able to store the position, dimension, origin, scale factor, and angle
//of rotation of a game object.
public abstract class AbstractGameObject 
{
	public Vector2 position;
	public Vector2 dimension;
	public Vector2 origin;
	public Vector2 scale;
	public float rotation;

	public AbstractGameObject () 
	{
		position = new Vector2();
		dimension = new Vector2(1, 1);
		origin = new Vector2();
		scale = new Vector2(1, 1);
		rotation = 0;
	}
	
	//will be called inside our world controller
	public void update(float deltaTime)
	{
	}
	//will be called inside our world renderer
	public abstract void render(SpriteBatch batch);
}