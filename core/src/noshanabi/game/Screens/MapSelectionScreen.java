package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisLabel;

import noshanabi.game.ButtonPrefabs.ReturnScreenButton;
import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 07/11/2017.
 */

public class MapSelectionScreen implements Screen {

    //viewport
    private Viewport mapSelectionViewport;

    //gameStage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;

    //map count
    private int mapCount = 0;


    private Sprite backGround;



    public static class MapInfo
    {
        public String mapName;
        public Image image;
        public VisLabel highScoreLabel;

        public void addToGroup(Group group)
        {
            group.addActor(image);
            group.addActor(highScoreLabel);
        }

    }

    //
    Array<MapInfo> mapInfos;
    Array<Texture> mapTextures;

    private ReturnScreenButton returnScreenButton;

    private Texture nextMapTexture;
    private Image nextMapButton;
    private Image previousMapButton;

    private int transitionLeft;
    private int transitionDistance;
    private int transitionCount;
    private int transitionSpeed;

    public MapSelectionScreen(GameManager _gameManager)
    {
        //set up constructor variables
        this.gameManager = _gameManager;

        backGround = new Sprite(new Texture("images/BlueBackground.png"));
        backGround.setSize(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);

        transitionLeft = -1;
        transitionDistance = 0;
        transitionCount = 0;
        transitionSpeed = 50;

        //-----------------VIEW RELATED VARIABLES-----------------//
        mapSelectionViewport = new StretchViewport(Resourses.WORLDWIDTH, Resourses.WORLDHEIGHT);
        stage = new Stage(mapSelectionViewport,gameManager.batch);


        //Table help us to easily arrange UI, such as labels, texts, etc.
        Group mapGroup = new Group();

        mapTextures = new Array<Texture>();
        mapInfos = new Array<MapInfo>();

        //map texture
        while (true) {
            String textureFileName = "maps/map" + mapCount + "/maptexture.png";
            try {
                Texture mapTexture = new Texture(textureFileName);
                mapTextures.add(mapTexture);
                mapCount++;

            } catch (GdxRuntimeException e) {
                break;
            }
        }

        //load map images
        for(int i=0;i<mapCount;i++) {
            final MapInfo mapInfo = new MapInfo();
            mapInfo.image = new Image(mapTextures.get(i));
            mapInfo.mapName = "maps/map" + i + "/map.tmx";
            final String backgroundMusicName = "maps/map" + i + "/backgroundmusic.mp3";


            mapInfo.image.setBounds(0, 0, mapTextures.get(i).getWidth(), mapTextures.get(i).getHeight());
            mapInfo.image.setTouchable(Touchable.enabled);
            mapInfo.image.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameManager.setScreen(new PlayScreen(gameManager, mapInfo, backgroundMusicName));
                    return true;
                }

            });

            mapInfo.image.setSize(Resourses.WORLDWIDTH - 200, Resourses.WORLDHEIGHT - 100);
            mapInfo.image.setPosition(100 + i * Resourses.WORLDWIDTH, 55);


            //highscore
            mapInfo.highScoreLabel = new VisLabel(""+ gameManager.getPreferences().getFloat(mapInfo.mapName, 0));
            mapInfo.highScoreLabel.setPosition(i * Resourses.WORLDWIDTH + Resourses.WORLDWIDTH/2-30, 20);

            mapInfos.add(mapInfo);

            mapInfo.addToGroup(mapGroup);
        }



        //Next map button
        nextMapTexture = new Texture(Resourses.NextMapButton);
        nextMapButton = new Image(nextMapTexture);
        nextMapButton.setBounds(0,0,nextMapTexture.getWidth(),nextMapTexture.getHeight());
        nextMapButton.setTouchable(Touchable.enabled);
        nextMapButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(transitionDistance==0 && transitionCount < mapCount -1 )
                {
                    gameManager.getAssetManager().get(Resourses.ClickSound, Sound.class).play();
                    transitionLeft = 1;
                    transitionCount++;
                }
                return true;
            }

        });

        nextMapButton.setSize(50,50);
        nextMapButton.setPosition(Resourses.WORLDWIDTH-80, mapInfos.first().image.getY() + mapInfos.first().image.getHeight()/2- nextMapButton.getHeight()/2);

        //add to table
        mapGroup.addActor(nextMapButton);


        //Previous map button
        previousMapButton = new Image(nextMapTexture);
        previousMapButton.setBounds(0,0,nextMapTexture.getWidth(),nextMapTexture.getHeight());
        previousMapButton.setTouchable(Touchable.enabled);
        previousMapButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if(transitionDistance==0 && transitionCount >0) {
                    gameManager.getAssetManager().get(Resourses.ClickSound, Sound.class).play();
                    transitionLeft = 0;
                    transitionCount--;
                }
                return true;
            }

        });

        previousMapButton.setSize(50,50);
        previousMapButton.setOrigin(previousMapButton.getWidth()/2, previousMapButton.getHeight()/2);
        previousMapButton.setRotation(-180);
        previousMapButton.setPosition(20,nextMapButton.getY());

        //add to group
        mapGroup.addActor(previousMapButton);

        //add to gameStage
        stage.addActor(mapGroup);


        //------------RETURN SCREEN BUTTON ------------------
        //Group allow to place an actor wherever we want
        Group group = new Group();

        returnScreenButton = new ReturnScreenButton();
        returnScreenButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getMenuScreen().getStage());
               gameManager.setScreen(gameManager.getMenuScreen());
                return true;
            }

        });
        //add to group
        group.addActor(returnScreenButton);


        //add to actor
        stage.addActor(group);


    }

    @Override
    public void render(float delta) {
        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //transition map
        if (transitionLeft !=-1) {
            if (transitionLeft == 0) {
                for (MapInfo map : mapInfos) {
                    map.image.setPosition(map.image.getX() + transitionSpeed, map.image.getY());
                    map.highScoreLabel.setPosition(map.highScoreLabel.getX() + transitionSpeed, map.highScoreLabel.getY());
                }

            } else {

                for (MapInfo map : mapInfos) {
                    map.image.setPosition(map.image.getX() - transitionSpeed, map.image.getY());
                    map.highScoreLabel.setPosition(map.highScoreLabel.getX() - transitionSpeed, map.highScoreLabel.getY());
                }
            }
            transitionDistance += transitionSpeed;

            if (transitionDistance >= Resourses.WORLDWIDTH) {
                transitionDistance = 0;
                transitionLeft = -1;
            }

        }

        //draw sprite
        gameManager.batch.begin();

        backGround.draw(gameManager.batch);

        gameManager.batch.end();

        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        mapSelectionViewport.update(width, height);
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

        for (int i = 0; i < mapCount; i++) {
            mapTextures.get(i).dispose();
        }

        if (backGround.getTexture() != null) {
            backGround.getTexture().dispose();
        }

        if (returnScreenButton != null)
            returnScreenButton.dispose();

        if (nextMapTexture != null)
            nextMapTexture.dispose();
    }

}
