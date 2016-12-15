package screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import util.AudioManager;
import util.CharacterSkin;
import util.Constants;
import util.GamePreferences;
import com.almaslamanigdx.game.Assets;
import com.almaslamanigdx.game.WorldController;
import com.almaslamanigdx.game.WorldRenderer;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.Touchable;

public class MenuScreen extends AbstractGameScreen
{
	private static final String TAG = MenuScreen.class.getName();
	private Stage stage;
	private Skin skinCanyonBunny;
	// menu
	private Image imgBackground;
	private Image imgLogo;
	private Image imgInfo;
	private Image imgCoins;
	private Image imgBunny;
	private Button btnMenuPlay;
	private Button btnMenuOptions;
	private TextButton btnHighScore;
	// options
	private Window winOptions;
	private TextButton btnWinOptSave;
	private TextButton btnWinOptCancel;
	private CheckBox chkSound;
	private Slider sldSound;
	private CheckBox chkMusic;
	private Slider sldMusic;
	private SelectBox<CharacterSkin> selCharSkin;
	private Image imgCharSkin;
	private CheckBox chkShowFpsCounter;
	// debug
	private final float DEBUG_REBUILD_INTERVAL = 5.0f;
	private boolean debugEnabled = false;
	private float debugRebuildStage;

	private Skin skinLibgdx;
	public WorldController wc;
	public WorldRenderer worldRenderer;
	public int score;

	public MenuScreen (Game game)
	{
		super(game);
	}

	/**
	 * clear the screen by filling it solid black
	 * and checks if the screen has been touched or not.
	 */
	@Override
	public void render (float deltaTime)
	{
		Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (debugEnabled)
		{
			debugRebuildStage -= deltaTime;
			if (debugRebuildStage <= 0)
			{
				debugRebuildStage = DEBUG_REBUILD_INTERVAL;
				rebuildStage();
			}
		}
		stage.act(deltaTime);
		stage.draw();
		stage.setDebugAll(false);//to draw debug lines on the menu screen, have to be true.
	
	}

	/**
	 * sets the viewport size of the stage.
	 */
	@Override public void resize (int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}

	/**
	 * called when the screen is shown,initializes the stage, and ready to receive inputs
	 */
	@Override public void show ()
	{
		stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH,Constants.VIEWPORT_GUI_HEIGHT));
		Gdx.input.setInputProcessor(stage);
		rebuildStage();
	}

	/**
	 * free the allocated resources when the screen is hidden
	 */
	@Override public void hide ()
	{
		stage.dispose();
		skinCanyonBunny.dispose();
	}

	@Override public void pause () { }

	/**
	 * will make up the final scene of our menu screen.
	 * This method is implemented in a way so that it can be
	 * called in a repeated manner
	 */
	private void rebuildStage ()
	{
		skinCanyonBunny = new Skin(Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI),new TextureAtlas(Constants.TEXTURE_ATLAS_UI));

		skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI),new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));

		// build all layers
		Table layerBackground = buildBackgroundLayer();
		Table layerObjects = buildObjectsLayer();
		Table layerLogos = buildLogosLayer();
		Table layerControls = buildControlsLayer();
		Table layerOptionsWindow = buildOptionsWindowLayer();

		// assemble stage for menu screen
		stage.clear();
		Stack stack = new Stack();
		stage.addActor(stack);
		stack.setSize(Constants.VIEWPORT_GUI_WIDTH,Constants.VIEWPORT_GUI_HEIGHT);
		stack.add(layerBackground);
		stack.add(layerObjects);
		stack.add(layerLogos);
		stack.add(layerControls);
		stage.addActor(layerOptionsWindow);
	}

	/**
	 * background image drawn to the scene of the menu screen
	 * @return tbl
	 */
	private Table buildBackgroundLayer ()
	{
		Table layer = new Table();
		// + Background
		imgBackground = new Image(skinCanyonBunny, "background");
		layer.add(imgBackground);
		return layer;
	}

	/**
	 * an image of some coins and another image of a huge bunny head TO BE CHANGED
	 * @return tbl
	 */
	private Table buildObjectsLayer ()
	{
		Table layer = new Table();
		// + Banana
		imgCoins = new Image(skinCanyonBunny, "banana");
		layer.addActor(imgCoins);
		imgCoins.setOrigin(imgCoins.getWidth() / 2,
				imgCoins.getHeight() / 2);
		imgCoins.addAction(sequence(moveTo(135, -20),scaleTo(0, 0),fadeOut(0),
				delay(2.5f),
				parallel(moveBy(0, 100, 0.5f, Interpolation.swingOut),
						scaleTo(1.0f, 1.0f, 0.25f, Interpolation.linear),
						alpha(1.0f, 0.5f))));
		// + Monkey
		imgBunny = new Image(skinCanyonBunny, "monkey");
		layer.addActor(imgBunny);
		imgBunny.addAction(sequence(
				moveTo(655, 510),
				delay(4.0f),
				moveBy(-70, -100, 0.5f, Interpolation.fade),
				moveBy(-100, -50, 0.5f, Interpolation.fade),
				moveBy(-150, -300, 1.0f, Interpolation.elasticIn)));
		return layer;
	}
	/**
	 * in the top-left corner of the screen
	 * @return tbl
	 */
	private Table buildLogosLayer ()
	{
		Table layer = new Table();
		layer.left().top();

		// + Game Logo
		imgLogo = new Image(skinCanyonBunny, "logo");
		layer.add(imgLogo);
		layer.row().expandY();

		// + Info Logos
		imgInfo = new Image(skinCanyonBunny, "info");
		layer.add(imgInfo).bottom();

		//draw debug layers if enabled
		if (debugEnabled)
			layer.debug();
		return layer;
	}

	/**
	 * in the bottom-right corner of the screen. A new button widget is added using the Play style
	 * @return tbl
	 */
	private Table buildControlsLayer ()
	{
		Table layer = new Table();
		layer.right().bottom();

		// + Play Button
		btnMenuPlay = new Button(skinCanyonBunny, "play");
		layer.add(btnMenuPlay);
		btnMenuPlay.addListener(new ChangeListener()
		{
			@Override
			public void changed (ChangeEvent event, Actor actor)
			{
				onPlayClicked();
			}
		});
		layer.row();

		// + Options Button
		btnMenuOptions = new Button(skinCanyonBunny, "options");
		layer.add(btnMenuOptions);
		btnMenuOptions.addListener(new ChangeListener()
		{
			@Override
			public void changed (ChangeEvent event, Actor actor)
			{
				onOptionsClicked();
			}
		});
		layer.row();


		if (debugEnabled)
			layer.debug();
		return layer;
	}

	/**
	 * initializes the Options window.
	 *  It builds each part of the menu using the build methods
	 * @return tbl
	 */
	private Table buildOptionsWindowLayer ()
	{
		winOptions = new Window("Options", skinLibgdx);
		// + Audio Settings: Sound/Music CheckBox and Volume Slider
		winOptions.add(buildOptWinAudioSettings()).row();

		// + Character Skin: Selection Box (White, Gray, Brown)
		winOptions.add(buildOptWinSkinSelection()).row();

		// + Debug: Show FPS Counter
		winOptions.add(buildOptWinDebug()).row();

		// + Separator and Buttons (Save, Cancel)
		winOptions.add(buildOptWinButtons()).pad(10, 0, 10, 0);

		// Make options window slightly transparent
		winOptions.setColor(1, 1, 1, 0.8f);

		// Hide options window by default
		showOptionsWindow(false, false);

		// Let TableLayout recalculate widget sizes and positions
		winOptions.pack();

		// Move options window to bottom right corner
		winOptions.setPosition
		(Constants.VIEWPORT_GUI_WIDTH - winOptions.getWidth() - 50,50);

		return winOptions;
	}

	/**
	 * builds a table containing the audio settings labels and checkbox
	 * @return tbl
	 */
	private Table buildOptWinAudioSettings ()
	{
		Table tbl = new Table();

		// + Title: "Audio"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Audio", skinLibgdx, "default-font",
				Color.ORANGE)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);

		// + Checkbox, "Sound" label, sound volume slider
		chkSound = new CheckBox("", skinLibgdx);
		tbl.add(chkSound);
		tbl.add(new Label("Sound", skinLibgdx));
		sldSound = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldSound);
		tbl.row();

		// + Checkbox, "Music" label, music volume slider
		chkMusic = new CheckBox("", skinLibgdx);
		tbl.add(chkMusic);
		tbl.add(new Label("Music", skinLibgdx));
		sldMusic = new Slider(0.0f, 1.0f, 0.1f, false, skinLibgdx);
		tbl.add(sldMusic);
		tbl.row();
		return tbl;
	}


	/**
	 * builds a table that contains the character skin selection via a drop-down box and a preview image next to it
	 * @return tbl
	 */
	private Table buildOptWinSkinSelection ()
	{
		Table tbl = new Table();

		// + Title: "Character Skin"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Character Skin", skinLibgdx,
				"default-font", Color.ORANGE)).colspan(2);
		tbl.row();

		// + Drop down box filled with skin items
		selCharSkin = new SelectBox<CharacterSkin>(skinLibgdx);
		selCharSkin.setItems(CharacterSkin.values());
		selCharSkin.addListener(new ChangeListener()
		{
			@Override
			public void changed(ChangeEvent event, Actor actor)
			{
				onCharSkinSelected(((SelectBox<CharacterSkin>)actor).getSelectedIndex());
			}
		});
		tbl.add(selCharSkin).width(120).padRight(20);

		// + Skin preview image
		imgCharSkin = new Image(Assets.instance.monkey.monkey);
		tbl.add(imgCharSkin).width(50).height(50);
		return tbl;
	}

	//builds a table that contains the debug settings.
	private Table buildOptWinDebug ()
	{
		Table tbl = new Table();

		// + Title: "Debug"
		tbl.pad(10, 10, 0, 10);
		tbl.add(new Label("Debug", skinLibgdx, "default-font",
				Color.RED)).colspan(3);
		tbl.row();
		tbl.columnDefaults(0).padRight(10);
		tbl.columnDefaults(1).padRight(10);

		// + Checkbox, "Show FPS Counter" label
		chkShowFpsCounter = new CheckBox("", skinLibgdx);
		tbl.add(new Label("Show FPS Counter", skinLibgdx));
		tbl.add(chkShowFpsCounter);
		tbl.row();
		return tbl;
	}

	//builds a table that contains a separator, and the Save and Cancel
	//buttons at the bottom of the Options window
	private Table buildOptWinButtons ()
	{
		Table tbl = new Table();

		// + Separator
		Label lbl = null;
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.75f, 0.75f, 0.75f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 0, 0, 1);
		tbl.row();
		lbl = new Label("", skinLibgdx);
		lbl.setColor(0.5f, 0.5f, 0.5f, 1);
		lbl.setStyle(new LabelStyle(lbl.getStyle()));
		lbl.getStyle().background = skinLibgdx.newDrawable("white");
		tbl.add(lbl).colspan(2).height(1).width(220).pad(0, 1, 5, 0);
		tbl.row();

		// + Save Button with event handler
		btnWinOptSave = new TextButton("Save", skinLibgdx);
		tbl.add(btnWinOptSave).padRight(30);
		btnWinOptSave.addListener(new ChangeListener()
		{
			@Override
			public void changed (ChangeEvent event, Actor actor)
			{
				onSaveClicked();
			}
		});

		// + Cancel Button with event handler
		btnWinOptCancel = new TextButton("Cancel", skinLibgdx);
		tbl.add(btnWinOptCancel);
		btnWinOptCancel.addListener(new ChangeListener()
		{
			@Override
			public void changed (ChangeEvent event, Actor actor)
			{
				onCancelClicked();
			}
		});

		// + High Score with event handler
		btnHighScore = new TextButton("HighScore", skinLibgdx);
		tbl.add(btnHighScore);
		btnHighScore.addListener(new ChangeListener()
		{
			@Override
			public void changed (ChangeEvent event, Actor actor)
			{
				onHighScoreClicked();
			}
		});
		return tbl;
	}


	/**
	 * used to translate back and forth between the values stored in the widgets
	 * and the instance of the GamePreferences class.
	 * for load() and save()
	 */
	private void loadSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.load();
		chkSound.setChecked(prefs.sound);
		sldSound.setValue(prefs.volSound);
		chkMusic.setChecked(prefs.music);
		sldMusic.setValue(prefs.volMusic);
		selCharSkin.setSelectedIndex(prefs.charSkin);
		onCharSkinSelected(prefs.charSkin);
		chkShowFpsCounter.setChecked(prefs.showFpsCounter);
	}
	private void saveSettings()
	{
		GamePreferences prefs = GamePreferences.instance;
		prefs.sound = chkSound.isChecked();
		prefs.volSound = sldSound.getValue();
		prefs.music = chkMusic.isChecked();
		prefs.volMusic = sldMusic.getValue();
		prefs.charSkin = selCharSkin.getSelectedIndex();
		prefs.showFpsCounter = chkShowFpsCounter.isChecked();
		prefs.save();
	}

	/**
	 * when the mouse is clicked it should set the screen (game screen)
	 */
	private void onPlayClicked ()
	{
		game.setScreen(new GameScreen(game));
	}

	/**
	 * allows the Options window to be opened,, setting loaded first
	 */
	private void onOptionsClicked ()
	{
		loadSettings();
		showMenuButtons(false);
		showOptionsWindow(true, true);
	}

	/**
	 * will update the preview image
	 */
	private void onCharSkinSelected(int index)
	{
		CharacterSkin skin = CharacterSkin.values()[index];
		imgCharSkin.setColor(skin.getColor());
	}
	/**saves the current settings of the Options window
	 * and swaps the Options to menu controls
	 */
	private void onSaveClicked()
	{
		saveSettings();
		onCancelClicked();

		/** when the Options menu is closed, the audio manager
		 * will start or stop the music depending on the current audio settings.
		 */
		AudioManager.instance.onSettingsUpdated();
	}

	/**
	 * only swaps the widgets, which also means that any changed settings will be discarded
	 */
	private void onCancelClicked()
	{
		showMenuButtons(true);
		showOptionsWindow(false, true);
		AudioManager.instance.onSettingsUpdated();
	}

	/**
	 * High score clicked 
	 */
	private void onHighScoreClicked()
	{
		game.setScreen(new HighScoreScreen(game,score));
	}

	private void showMenuButtons (boolean visible)
	{
		float moveDuration = 1.0f;
		Interpolation moveEasing = Interpolation.swing;
		float delayOptionsButton = 0.25f;
		float moveX = 300 * (visible ? -1 : 1);
		float moveY = 0 * (visible ? -1 : 1);
		final Touchable touchEnabled = visible ? Touchable.enabled: Touchable.disabled;

		btnMenuPlay.addAction(
				moveBy(moveX, moveY, moveDuration, moveEasing));
		btnMenuOptions.addAction(sequence(
				delay(delayOptionsButton),
				moveBy(moveX, moveY, moveDuration, moveEasing)));
		SequenceAction seq = sequence();

		if (visible)
			seq.addAction(delay(delayOptionsButton + moveDuration));
		seq.addAction(run(new Runnable()
		{
			public void run ()
			{
				btnMenuPlay.setTouchable(touchEnabled);
				btnMenuOptions.setTouchable(touchEnabled);
			}
		}));
		stage.addAction(seq);
	}
	private void showOptionsWindow (boolean visible,boolean animated)
	{
		float alphaTo = visible ? 0.8f : 0.0f;
		float duration = animated ? 1.0f : 0.0f;
		Touchable touchEnabled = visible ? Touchable.enabled: Touchable.disabled;
		winOptions.addAction(sequence(touchable(touchEnabled),alpha(alphaTo, duration)));
	}
}
