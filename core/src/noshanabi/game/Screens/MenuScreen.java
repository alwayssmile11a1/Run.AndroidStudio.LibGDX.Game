package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;

import noshanabi.game.ButtonPrefabs.CreditButton;
import noshanabi.game.ButtonPrefabs.MultiPlayButton;
import noshanabi.game.ButtonPrefabs.SinglePlayButton;
import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 25/09/2017.
 */

public class MenuScreen implements Screen{

    //GameManager
    GameManager gameManager;

    //-----------------VIEW RELATED VARIABLES-----------------//
    //how well we want to see our map
    private Viewport menuViewPort;

    //gameStage manage UI on it
    private Stage stage;

    //----------------TEXTURE RELATED VARIABLES------------//
    SinglePlayButton singlePlayerButton;
    MultiPlayButton multiPlayerButton;
    CreditButton creditButton;

    //choose player table
    private Stage chooseCharacterStage;
    private VisTable chooseCharacterTable;
    private Texture chooseCharacterBackgroundTexture;
    private Image chooseCharacterBackground;

    VisImage playerImage;
    Table table;


    //map as background
    TiledMap map;
    OrthogonalTiledMapRenderer mapRenderer;
    OrthographicCamera camera;
    MapLayer movableLayer;
    float mapWidth=0;
    float movableLayerSpeed =0.5f;
    Sprite sampleCharacter;
    Array<TextureRegion> characterRegions;
    float randomY;

    public MenuScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;

        //-----------------VIEW RELATED VARIABLES-----------------//
        camera = new OrthographicCamera();
        menuViewPort = new StretchViewport(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT, camera);
        stage = new Stage(menuViewPort, gameManager.batch);
        camera.position.set(new Vector2(Resourses.WORLDWIDTH/2,Resourses.WORLDHEIGHT/2),0);

        //map0 as back ground
        //get map from file
        map = new TmxMapLoader().load("maps/map0/map.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map);
        float tileWidth = Float.parseFloat(map.getProperties().get("tilewidth").toString());
        mapWidth = Float.parseFloat(map.getProperties().get("width").toString()) * tileWidth;

        for(MapLayer mapLayer:map.getLayers())
        {
            mapLayer.setOffsetY(75f);
        }

        //get movable layer
        movableLayer = map.getLayers().get("MovableBackGround");



        //Table help us to easily arrange UI, such as labels, texts, etc.
        table = new Table();
        table.center();
        table.setFillParent(true);


        //singleplayer TouchableImage
        singlePlayerButton = new SinglePlayButton(gameManager);
        table.add(singlePlayerButton).row();

        //multiplayer TouchableImage
        multiPlayerButton = new MultiPlayButton(gameManager);

        //add to table
        table.add(multiPlayerButton).padTop(5f).row();


        //--------CHARACTER SELECT BUTTON --------------
        setupChooseCharacterTable();

        playerImage = new VisImage(gameManager.getCurrentCharacter());
        playerImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(chooseCharacterStage);
                chooseCharacterTable.setVisible(true);
                return true;
            }

        });
        playerImage.setPosition(Resourses.WORLDWIDTH/2 - 200,Resourses.WORLDHEIGHT/2);

        table.addActor(playerImage);

        //get sample character
        sampleCharacter = new Sprite( characterRegions.get(MathUtils.random(0,characterRegions.size-1)));
        sampleCharacter.setPosition(1000,-10);
        sampleCharacter.setSize(32,32);
        sampleCharacter.setOriginCenter();


        //-----------CREDIT BUTTON---------------------
        creditButton = new CreditButton(gameManager);
        table.add(creditButton).padTop(20f);


        //add to gameStage
        stage.addActor(table);
    }

    public void setupChooseCharacterTable()
    {
        chooseCharacterStage = new Stage(menuViewPort, gameManager.batch);

        //------------------CHOOSE CHARACTER TABLE ---------------------//
        //get all player texture
        chooseCharacterTable = new VisTable();
        chooseCharacterTable.setFillParent(true);
        chooseCharacterTable.center();

        //background
        chooseCharacterBackgroundTexture = new Texture(Resourses.GameFinishedBackground);
        chooseCharacterBackground = new VisImage(chooseCharacterBackgroundTexture);
        chooseCharacterBackground.setSize(Resourses.WORLDWIDTH-25,Resourses.WORLDHEIGHT-25);
        chooseCharacterBackground.setPosition(Resourses.WORLDWIDTH/2- chooseCharacterBackground.getWidth()/2,Resourses.WORLDHEIGHT/2- chooseCharacterBackground.getHeight()/2);
        chooseCharacterBackground.setColor(0, 0, 0, 0.5f);
        chooseCharacterTable.addActor(chooseCharacterBackground);


        characterRegions = gameManager.getCharacterRegions();
        int index = 0;
        for(final TextureRegion region:characterRegions) {

            VisImage characterImage = new VisImage(region);
            characterImage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameManager.setCurrentCharacter(region);
                    chooseCharacterTable.setVisible(false);
                    Gdx.input.setInputProcessor(stage);
                    playerImage.setDrawable(new TextureRegionDrawable(region));
                    return true;
                }

            });
            chooseCharacterTable.add(characterImage).expandX();
            index++;
            if (index >= 4) {
                index = 0;
                chooseCharacterTable.row().padTop(50f);
            }
        }

        chooseCharacterStage.addActor(chooseCharacterTable);
        chooseCharacterTable.setVisible(false);


    }

    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if(movableLayer!=null) {
            if ((movableLayer.getOffsetX() + mapWidth - 5f) > (camera.position.x - camera.viewportWidth / 2)) {
                movableLayer.setOffsetX(movableLayer.getOffsetX() - movableLayerSpeed);
            } else {

                movableLayer.setOffsetX(camera.position.x + camera.viewportWidth / 2);
            }
        }

        mapRenderer.setView(camera);
        mapRenderer.render();

        if(sampleCharacter.getX() > camera.position.x+Resourses.WORLDWIDTH/2) {
            sampleCharacter.setRegion(characterRegions.get(MathUtils.random(0,characterRegions.size-1)));
            sampleCharacter.setPosition(MathUtils.random(-1000f,-500f),MathUtils.random(100.0f,Resourses.WORLDHEIGHT-50.0f));
            sampleCharacter.setRotation(MathUtils.random(30,180));
            randomY = MathUtils.random(-1.0f,1.0f);
        }
        else
        {
            sampleCharacter.setPosition(sampleCharacter.getX()+3,sampleCharacter.getY()+randomY);
            sampleCharacter.setRotation(sampleCharacter.getRotation()-2);
        }

        gameManager.batch.begin();
        sampleCharacter.draw(gameManager.batch);
        gameManager.batch.end();

        stage.draw();
        stage.act();
        chooseCharacterStage.draw();
        chooseCharacterStage.act();

        singlePlayerButton.update(delta);
        multiPlayerButton.update(delta);
        creditButton.update(delta);

    }

    @Override
    public void resize(int width, int height) {
        menuViewPort.update(width,height);
        creditButton.resize(width,height);
    }

    public Stage getStage() {
        return stage;
    }

    @Override
    public void show() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        if (stage != null) {
            stage.dispose();
        }

        if (singlePlayerButton != null)
            singlePlayerButton.dispose();

        if (multiPlayerButton != null)
            multiPlayerButton.dispose();

        if(creditButton!=null)
            creditButton.dispose();

        if (chooseCharacterStage != null)
            chooseCharacterStage.dispose();


        if (mapRenderer != null)
            mapRenderer.dispose();

        if (map != null)
            map.dispose();

    }


}
