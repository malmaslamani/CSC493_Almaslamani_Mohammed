package com.almaslamanigdx.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.graphics.g2d.Sprite;


public class WorldRenderer implements Disposable
{
	//LibGDX comes with a ready-to-use
	//OrthographicCamera class to simplify our 2D rendering tasks.
	private OrthographicCamera camera;

	//this class is the actual workhorse that draws all our objects with respect to the
	//camera's current settings (for example, position, zoom, and so on) to the screen.
	private SpriteBatch batch;

	//needed to render all the game objects that managed by the controller.
	private WorldController worldController;

	public WorldRenderer(WorldController worldController)
	{
		this.worldController = worldController;
		init();
	}
	private void init()
	{
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
		camera.position.set(0,0,0);
		camera.update();
	}

	public void render()
	{
		renderTestObjects();
	}
	private void renderTestObjects() 
	{
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		for(Sprite sprite : worldController.testSprites)
		{
			sprite.draw(batch);
		}
		batch.end();
	}
	public void resize(int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT/height)*width;
		camera.update();
	}

	//call it to free the allocated memory.
	@Override
	public void dispose() 
	{		
		batch.dispose();
	}

}
