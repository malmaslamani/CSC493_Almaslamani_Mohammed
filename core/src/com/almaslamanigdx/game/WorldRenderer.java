package com.almaslamanigdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import util.Constants;
import util.GamePreferences;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.MathUtils;

public class WorldRenderer implements Disposable
{
	//LibGDX comes with a ready-to-use
	//OrthographicCamera class to simplify our 2D rendering tasks.
	private OrthographicCamera camera;

	//GUI camera
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
		//main camera init
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Constants.VIEWPORT_WIDTH,Constants.VIEWPORT_HEIGHT);
		camera.position.set(0,0,0);
		camera.update();

		//gui camera init
		cameraGUI = new OrthographicCamera(Constants.VIEWPORT_GUI_WIDTH,
				Constants.VIEWPORT_GUI_HEIGHT);
		cameraGUI.position.set(0, 0, 0);
		cameraGUI.setToOrtho(true); // flip y-axis
		cameraGUI.update();

	}

	public void render()
	{
		renderWorld(batch);
		renderGui(batch);
	}

	private void renderWorld (SpriteBatch batch) 
	{
		worldController.cameraHelper.applyTo(camera);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		worldController.level.render(batch);
		batch.end();
	
	}

	public void resize(int width, int height)
	{
		camera.viewportWidth = (Constants.VIEWPORT_HEIGHT/height)*width;
		camera.update();

		//gui resizer
		cameraGUI.viewportHeight = Constants.VIEWPORT_GUI_HEIGHT;
		cameraGUI.viewportWidth = (Constants.VIEWPORT_GUI_HEIGHT/ (float)height) * (float)width;
		cameraGUI.position.set(cameraGUI.viewportWidth / 2,cameraGUI.viewportHeight / 2, 0);
		cameraGUI.update();
	}

	/**
	 * to draw the score in the gui
	 * @param batch
	 */
	private void renderGuiScore (SpriteBatch batch) 
	{
		float x = -15;
		float y = -15;
		float offsetX = 50;
		float offsetY = 50;
		
		if (worldController.scoreVisual<worldController.score) 
		{
			long shakeAlpha = System.currentTimeMillis() % 360;
			float shakeDist = 1.5f;
			offsetX += MathUtils.sinDeg(shakeAlpha * 2.2f) * shakeDist;
			offsetY += MathUtils.sinDeg(shakeAlpha * 2.9f) * shakeDist;
		}
		batch.draw(Assets.instance.banana.banana, x, y, offsetX,offsetY, 100, 100, 0.35f, -0.35f, 0);
		Assets.instance.fonts.defaultBig.draw(batch,"" + (int)worldController.scoreVisual,x + 75, y + 37);
	}

	/**
	 * to draw the extrea lives in the gui
	 * @param batch
	 */
	private void renderGuiExtraLive (SpriteBatch batch) 
	{
		float x = cameraGUI.viewportWidth - 50 - Constants.LIVES_START * 50;
		float y = -15;
		for (int i = 0; i < Constants.LIVES_START; i++) 
		{
			if (worldController.lives <= i)
				batch.setColor(0.5f, 0.5f, 0.5f, 0.5f);
			batch.draw(Assets.instance.monkey.monkey,x + i * 50, y, 50, 50, 120, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
		}

		//draw monkey  icon that is changed in its alpha
		//color, scale, and rotation over time to create the animation.
		if (worldController.lives>= 0&&worldController.livesVisual>worldController.lives) 
		{
			int i = worldController.lives;
			float alphaColor = Math.max(0, worldController.livesVisual- worldController.lives - 0.5f);
			float alphaScale = 0.35f * (2 + worldController.lives- worldController.livesVisual) * 2;
			float alphaRotate = -45 * alphaColor;
			batch.setColor(1.0f, 0.7f, 0.7f, alphaColor);
			batch.draw(Assets.instance.monkey.monkey,x + i * 50, y, 50, 50, 120, 100, alphaScale, -alphaScale,
					alphaRotate);
			batch.setColor(1, 1, 1, 1);
		}
	}

	private void renderGuiFpsCounter (SpriteBatch batch) 
	{
		float x = cameraGUI.viewportWidth - 55;
		float y = cameraGUI.viewportHeight - 15;
		int fps = Gdx.graphics.getFramesPerSecond();
		BitmapFont fpsFont = Assets.instance.fonts.defaultNormal;

		if (fps >= 45)
		{
			// 45 or more FPS show up in green (good)
			fpsFont.setColor(0, 1, 0, 1);
		}
		else if (fps >= 30) 
		{
			// 30 or more FPS show up in yellow(average)
			fpsFont.setColor(1, 1, 0, 1);
		} 
		else 
		{
			// less than 30 FPS show up in red (bad)
			fpsFont.setColor(1, 0, 0, 1);
		}
		fpsFont.draw(batch, "FPS: " + fps, x, y);
		fpsFont.setColor(1, 1, 1, 1); // white
	}

	/**
	 *  to draw the gui
	 */
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
			renderGuiFpsCounter(batch);


		// draw game over text
		renderGuiGameOverMessage(batch);
		
		//draw high score 
		renderHighScore(batch);

	
		batch.end();
	}
	
	/**
	 * calculates the center of the GUI camera's viewport. 
	 * The text is rendered using the big font from our assets. 
	 * Its color is changed using the setColor() method of BitmapFont.
	 * @param batch
	 */
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
	
	/**
	 * draw highScore list 
	 * @param batch
	 */
	 public void renderHighScore(SpriteBatch batch) 
	    {
	        float x = cameraGUI.viewportWidth / 2;
	        float y = cameraGUI.viewportHeight / 2;
	        if (worldController.isGameOver()) {
	            BitmapFont fontGameOver = Assets.instance.fonts.defaultBig;
	            fontGameOver.setColor(1, 0.75f, 0.25f, 1);
	            fontGameOver.draw(batch, "1: " + GamePreferences.instance.highscore1, x, y + 30, 0, Align.center, true);
	            fontGameOver.draw(batch, "2: " + GamePreferences.instance.highscore2, x, y + 60, 0, Align.center, true);
	            fontGameOver.draw(batch, "3: " + GamePreferences.instance.highscore3, x, y + 90, 0, Align.center, true);
	            fontGameOver.draw(batch, "4: " + GamePreferences.instance.highscore4, x, y + 120, 0, Align.center, true);
	            fontGameOver.setColor(1, 1, 1, 1);
	        }
	    }

	/**
	 * This method first checks whether there is still time left for the pineapple power-up effect to end. 
	 * Only if this is the case, a pineapple icon is drawn in the top-left corner under the banana icon.
	 * @param batch
	 */
	private void renderGuiFeatherPowerup (SpriteBatch batch) 
	{
		float x = -15;
		float y = 30;
		float timeLeftPineApplePowerup = worldController.level.monkey.timeLeftPineApplePowerup;

		if (timeLeftPineApplePowerup > 0) 
		{
			// Start icon fade in/out if the left power-up time
			// is less than 4 seconds. The fade interval is set
			// to 5 changes per second.
			if (timeLeftPineApplePowerup < 4) 
			{
				if (((int)(timeLeftPineApplePowerup * 5) % 2) != 0) 
				{
					batch.setColor(1, 1, 1, 0.5f);
				}
			}
			batch.draw(Assets.instance.pineApple.pineApple,x, y, 50, 50, 100, 100, 0.35f, -0.35f, 0);
			batch.setColor(1, 1, 1, 1);
			Assets.instance.fonts.defaultSmall.draw(batch,"" + (int)timeLeftPineApplePowerup, x + 60, y + 57);
		}
	}

	
	/**
	 * call it to free the allocated memory.
	 */
	@Override
	public void dispose() 
	{		
		batch.dispose();
	}

}
