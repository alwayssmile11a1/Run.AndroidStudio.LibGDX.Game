package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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

import noshanabi.game.ButtonPrefabs.ReturnScreenButton;
import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 07/11/2017.
 */

public class MapSelectionScreen implements Screen {

    //viewport
    private Viewport mapSelectionViewport;

    //stage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;

    //map count
    private int mapCount = 0;


    private Sprite backGround;

    //
    Array<Image> mapImages;
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
        backGround.setSize(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);

        transitionLeft = -1;
        transitionDistance = 0;
        transitionCount = 0;
        transitionSpeed = 50;

        //-----------------VIEW RELATED VARIABLES-----------------//
        mapSelectionViewport = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(mapSelectionViewport,gameManager.batch);


        //Table help us to easily arrange UI, such as labels, texts, etc.
        Group mapGroup = new Group();


        mapTextures = new Array<Texture>();
        mapImages = new Array<Image>();

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
            Image mapImage = new Image(mapTextures.get(i));
            final String mapName = "maps/map" + i + "/map.tmx";
            mapImages.add(mapImage);
            mapImage.setBounds(0, 0, mapTextures.get(i).getWidth(), mapTextures.get(i).getHeight());
            mapImage.setTouchable(Touchable.enabled);
            mapImage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameManager.setScreen(new PlayScreen(gameManager, mapName));
                    return true;
                }

            });
            mapImage.setPosition(100 + i * gameManager.WORLDWIDTH, 50);
            mapImage.setSize(gameManager.WORLDWIDTH - 200, gameManager.WORLDHEIGHT - 100);
            mapGroup.addActor(mapImages.get(i));

        }



        //Next map button
        nextMapTexture = new Texture("images/nextarrow.png");
        nextMapButton = new Image(nextMapTexture);
        nextMapButton.setBounds(0,0,nextMapTexture.getWidth(),nextMapTexture.getHeight());
        nextMapButton.setTouchable(Touchable.enabled);
        nextMapButton.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if(transitionDistance==0 && transitionCount < mapCount -1 )
                {
                    transitionLeft = 1;
                    transitionCount++;
                }
                return true;
            }

        });

        nextMapButton.setSize(50,50);
        nextMapButton.setPosition(gameManager.WORLDWIDTH-80,mapImages.first().getY() + mapImages.first().getHeight()/2- nextMapButton.getHeight()/2);

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

        //add to stage
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
                for (Image map : mapImages) {
                    map.setPosition(map.getX() + transitionSpeed, map.getY());
                }

            } else {

                for (Image map : mapImages) {
                    map.setPosition(map.getX() - transitionSpeed, map.getY());

                }
            }
            transitionDistance += transitionSpeed;

            if (transitionDistance >= gameManager.WORLDWIDTH) {
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
