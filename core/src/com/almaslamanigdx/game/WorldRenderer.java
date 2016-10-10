package com.almaslamanigdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

import util.Constants;
import util.GamePreferences;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;


public class WorldRenderer implements Disposable
{
	//LibGDX comes with a ready-to-use
	//OrthographicCamera class to simplify our 2D rendering tasks.
	private OrthographicCamera camera;

	// GUI only camera
	private OrthographicCamera cameraGUI;

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

		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();
	}

	private void renderWorld (SpriteBatch batch) 
	{
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();
	}

	//turn calls the render() method of Level to draw 
	//all the game objects of the loaded level.
	public void render()
	{
		renderWorld(batch);
		renderGui(batch);
	}


	public void resize(int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT/height)*width;
		camera.update();

		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT/ (float)height) * (float)width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2,cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
	}

	//to show the score in the GUI in Bigfont in the top left corner
	private void renderGuiScore (SpriteBatch batch) 
	{
		float x = -15;
		float y = -15;
		batch.draw(Assets.instance.goldCoin.goldCoin,x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch,"" + worldController.score,x + 75, y + 37);
	}


	//method to show the extra lives in the GUI in the top right corner
	//darkened if used and visible if it is still there
	private void renderGuiExtraLive (SpriteBatch batch)
	{
		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
		float y = -15;

		for (int i = 0; i < Constants.LIVES_START; i++) 
		{
			if (worldController.lives <= i)
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			batch.draw(Assets.instance.bunny.head,x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
		}
	}


	//this will show the FPS counter on the GUI
	private void renderGuiFpsCounter (SpriteBatch batch) 
	{
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;

		if (fps >= 45) 
		{
			// 45 or more FPS show up in green
			//good rendering performance
			fpsFont.setColor(0, 1, 0, 1);
		} 
		else if (fps >= 30)
		{
			// 30 or more FPS show up in yellow
			//average rendering performance
			fpsFont.setColor(1, 1, 0, 1);
		} 
		else 
		{
			// less than 30 FPS show up in red
			//really poor rendering performance
			fpsFont.setColor(1, 0, 0, 1);
		}

		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}

	//draw the GUI
	private void renderGui (SpriteBatch batch) 
	{
		batch.setProjectionMatrix(cameraGUI.combined);
		batch.begin();

		// draw collected gold coins icon + text
		// (anchored to top left edge)
		renderGuiScore(batch);

		// draw collected feather icon (anchored to top left edge)
		renderGuiFeatherPowerup(batch);

		// draw extra lives icon + text (anchored to top right edge)
		renderGuiExtraLive(batch);

		// draw FPS text (anchored to bottom right edge)
		if (GamePreferences.instance.showFpsCounter)
		{
			renderGuiFpsCounter(batch);
		}

		// draw game over text
		renderGuiGameOverMessage(batch);

		batch.end();
	}

	//This method calculates the center of the GUI camera's viewport. The text is rendered
	//using the big font from our assets
	private void renderGuiGameOverMessage (SpriteBatch batch) 
	{
		float x = cameraGUI.viewportWidth / 2;
		float y = cameraGUI.viewportHeight / 2;

		if (worldController.isGameOver()) 
		{
			BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
			fontGameOver.setColor(1, 0.75f, 0.25f, 1);
			fontGameOver.draw(batch, "GAME OVER", x, y, 0, Align.center, false);
			fontGameOver.setColor(1, 1, 1, 1);
		}
	}


	//checks whether there is still time left for the feather power-up effect
	//to end. Only if this is the case, a feather icon is drawn in the top-left corner under the
	//gold coin icon. A small number is drawn next to it that displays the rounded time
	//that is still left until the effect vanishes.some extra code that makes
	//the feather icon fade back and forth when there are less than four seconds of the
	//power-up effect to last.
	private void renderGuiFeatherPowerup (SpriteBatch batch) 
	{
		float x = -15;
		float y = 30;
		float timeLeftFeatherPowerup = worldController.level.bunnyHead.timeLeftFeatherPowerup;

		if (timeLeftFeatherPowerup > 0) 
		{
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds. The fade interval is set
			// to 5 changes per second.
			if (timeLeftFeatherPowerup < 4) 
			{
				if (((int)(timeLeftFeatherPowerup * 5) % 2) != 0) 
				{
					batch.setColor(1, 1, 1, 0.5f);
				}
			}
			batch.draw(Assets.instance.feather.feather,
					x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultSmall.draw(batch,
					"" + (int)timeLeftFeatherPowerup, x + 60, y + 57);
		}
	}

	//call it to free the allocated memory.
	@Override
	public void dispose() 
	{		
		batch.dispose();
	}

}
