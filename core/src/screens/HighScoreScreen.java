package screens;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import util.Constants;
import util.GamePreferences;

/**
 * This class holds all the methods needed to create a new screen to 
 * show the high scores.
 * @author Mohammed Almaslamani
 *
 */
public class HighScoreScreen extends AbstractGameScreen
{
    private Stage stage;
    private Skin skinCanyonBunny;
    //options
    private Window winHighScores;
    //go to menu button
    private TextButton btnGoToMenu;
    private Skin skinLibgdx;
    GamePreferences prefs = GamePreferences.instance;

    /**
     * Score Screen Constructor
     * @param game
     * @param score
     */
    public HighScoreScreen(Game game, int score)
    {
        super(game);
    }

    /**
     * build the stage for the high score list.
     */
    private void rebuildStage()
    {
        skinCanyonBunny = new Skin(Gdx.files.internal(Constants.SKIN_CANYONBUNNY_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_UI));
        skinLibgdx = new Skin(Gdx.files.internal(Constants.SKIN_LIBGDX_UI), new TextureAtlas(Constants.TEXTURE_ATLAS_LIBGDX_UI));
        Table layerHighScoreWindow = buildHighScoreLayer();
        stage.clear();
        Stack stack = new Stack();
        stage.addActor(stack);
        stack.setSize(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT);
        stage.addActor(layerHighScoreWindow);
    }

    /**
     * Build the Whole layer for the highScores
     * @return
     */
    private Table buildHighScoreLayer()
    {
        winHighScores = new Window("High Scores", skinLibgdx);
        winHighScores.add(buildHighScoreListWindow()).row();
        
        //button
        winHighScores.add(buildGoToMenuButton()).pad(10, 0, 10, 0);
      
        //set the color of the list 
        winHighScores.setColor(1, 1, 1, 0.8f);
        
        //hide options window by default
        winHighScores.setVisible(true);

        //change position and get correct size
        winHighScores.pack();
        
        //set position to the middle
        winHighScores.setPosition(Constants.VIEWPORT_GUI_WIDTH /2.4f, 100);
        return winHighScores;
    }

    /**
     * To go back to menu when clicking go back to menu
     */
    protected void backToMenu()
    {
        game.setScreen(new MenuScreen(game));
    }

    /**
     * drawing the highscore list widnow
     */
    @Override
    public void render(float deltaTime)
    {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        stage.act(deltaTime);
        stage.draw();
    }

    /**
     * building the highscores window
     * @return
     */
    private Table buildHighScoreListWindow()
    {

        Table tbl = new Table();
        //+Title
        tbl.pad(15, 10, 0, 10);
        tbl.add(new Label("High Scores", skinLibgdx,"default-font", Color.ORANGE)).colspan(2);
        tbl.row();
      
        //high score 1
        tbl.add(new Label("1:", skinLibgdx));
        tbl.add(new Label(""+prefs.highscore1,skinLibgdx));
        tbl.row();
        
        //high score 2
        tbl.add(new Label("2:", skinLibgdx));
        tbl.add(new Label(""+prefs.highscore2,skinLibgdx));
        tbl.row();
        
        //high score 3
        tbl.add(new Label("3:", skinLibgdx));
        tbl.add(new Label(""+prefs.highscore3,skinLibgdx));
        tbl.row();
        
        //high score 4
        tbl.add(new Label("4:", skinLibgdx));
        tbl.add(new Label(""+prefs.highscore4,skinLibgdx));
        tbl.row();

        return tbl;

    }

    /**
     * Build the go to Menu button withing the list of the high scores
     * @return
     */
    private Table buildGoToMenuButton()
    {
        Table tbl = new Table();
        
        //cancel button with event handler
        btnGoToMenu = new TextButton("Go To Menu Screen", skinLibgdx);
        tbl.add(btnGoToMenu);
        btnGoToMenu.addListener(new ChangeListener()
        {
            @Override
            public void changed(ChangeEvent event, Actor actor)
            {
                backToMenu();
            }
        });
        return tbl;
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide()
    {
        stage.dispose();
        skinCanyonBunny.dispose();
        skinLibgdx.dispose();
    }

    @Override
    public void show()
    {
        stage = new Stage(new StretchViewport(Constants.VIEWPORT_GUI_WIDTH, Constants.VIEWPORT_GUI_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        rebuildStage();
    }

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

}