package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 13/11/2017.
 */

public class FindRoomScreen implements Screen{

    //viewport
    private Viewport viewport;

    //stage manage UI on it
    private Stage stage;

    //game manager
    private GameManager gameManager;

    //map count
    private int mapCount = 0;


    private Sprite backGround;

    //map
    Array<Image> mapImages;
    Array<Texture> mapTextures;

    private Image returnImage;
    private Texture returnTexture;

    private Image signOutImage;
    private Texture signOutTexture;

    private Texture nextMapTexture;
    private Image nextMapButton;
    private Image previousMapButton;

    private int transitionUp;
    private int transitionDistance;
    private int transitionCount;
    private int transitionSpeed;

    public FindRoomScreen(GameManager _gameManager) {
        //set up constructor variables
        this.gameManager = _gameManager;

        //color to clear this screen
        Gdx.gl.glClearColor(0, 0, 0, 1);

        transitionUp = -1;
        transitionDistance = 0;
        transitionCount = 0;
        transitionSpeed = 20;

        backGround = new Sprite(new Texture("images/BlueBackground.png"));
        backGround.setSize(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);

        //-----------------VIEW RELATED VARIABLES-----------------//
        viewport = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(viewport, gameManager.batch);


        //Create group
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
        for (int i = 0; i < mapCount; i++) {
            Image mapImage = new Image(mapTextures.get(i));
            final String mapName = "maps/map" + i + "/map.tmx";
            mapImages.add(mapImage);
            mapImage.setBounds(0, 0, mapTextures.get(i).getWidth(), mapTextures.get(i).getHeight());
            mapImage.setTouchable(Touchable.enabled);
            mapImage.setPosition(250, 75 - gameManager.WORLDHEIGHT * i);
            mapImage.setSize(gameManager.WORLDWIDTH - 300, gameManager.WORLDHEIGHT - 150);
            mapImage.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameManager.setScreen(new PlayScreen(gameManager, mapName));
                    return true;
                }

            });

            mapGroup.addActor(mapImages.get(i));

        }


        //Next map button
        nextMapTexture = new Texture("images/nextarrow.png");
        nextMapButton = new Image(nextMapTexture);
        nextMapButton.setBounds(0, 0, nextMapTexture.getWidth(), nextMapTexture.getHeight());
        nextMapButton.setTouchable(Touchable.enabled);
        nextMapButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (transitionDistance == 0 && transitionCount > 0) {
                    transitionUp = 1;
                    transitionCount--;
                }
                return true;
            }

        });

        nextMapButton.setSize(50, 50);
        nextMapButton.setOrigin(nextMapButton.getWidth() / 2, nextMapButton.getHeight() / 2);
        nextMapButton.setRotation(90);
        nextMapButton.setPosition(mapImages.get(0).getX() + mapImages.get(0).getWidth() / 2 - nextMapButton.getWidth() / 2, gameManager.WORLDHEIGHT - 70);

        //add to group
        mapGroup.addActor(nextMapButton);


        //Previous map button
        //Next map button
        previousMapButton = new Image(nextMapTexture);
        previousMapButton.setBounds(0, 0, nextMapTexture.getWidth(), nextMapTexture.getHeight());
        previousMapButton.setTouchable(Touchable.enabled);
        previousMapButton.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (transitionDistance == 0 && transitionCount < mapCount - 1) {
                    transitionUp = 0;
                    transitionCount++;
                }
                return true;
            }

        });

        previousMapButton.setSize(50, 50);
        previousMapButton.setOrigin(previousMapButton.getWidth() / 2, previousMapButton.getHeight() / 2);
        previousMapButton.setRotation(-90);
        previousMapButton.setPosition(nextMapButton.getX(), 20);

        //add to group
        mapGroup.addActor(previousMapButton);


        //add to stage
        stage.addActor(mapGroup);


        //Group allow to place an actor wherever we want
        Group group = new Group();

        //the return button
        returnTexture = new Texture("images/rightarrow.png");
        returnImage = new Image(returnTexture);
        returnImage.setBounds(0, 0, returnTexture.getWidth(), returnTexture.getHeight());
        returnImage.setTouchable(Touchable.enabled);
        returnImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
                gameManager.setScreen(gameManager.getModeSelectionScreen());
                return true;
            }

        });

        returnImage.setSize(50, 50);
        returnImage.setOrigin(returnImage.getWidth() / 2, returnImage.getHeight() / 2);
        returnImage.setScaleX(-1);
        returnImage.setPosition(10, gameManager.WORLDHEIGHT - 60);
        //add to group
        group.addActor(returnImage);

        //------------------SIGN OUT BUTTON ------------------------
        signOutTexture = new Texture("images/signout.png");
        signOutImage = new Image(signOutTexture);
        signOutImage.setBounds(0, 0, signOutTexture.getWidth(), signOutTexture.getHeight());
        signOutImage.setTouchable(Touchable.enabled);
        signOutImage.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                if (gameManager.getPlayerServices() != null) {
                    gameManager.getPlayerServices().signOut();
                }
                Gdx.input.setInputProcessor(gameManager.getLoginScreen().getStage());
                gameManager.setScreen(gameManager.getLoginScreen());
                return true;
            }

        });
        //set position and size
        signOutImage.setPosition(gameManager.WORLDWIDTH - 60, gameManager.WORLDHEIGHT - 60);
        signOutImage.setSize(50, 50);

        //------------------USER INFORMATION ----------------------
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Label userNameLabel = new Label("USER NAME", labelStyle);
        if (gameManager.getPlayerServices() != null) {
            userNameLabel.setText(gameManager.getPlayerServices().getUserName());
        }
        userNameLabel.setPosition(gameManager.WORLDWIDTH - userNameLabel.getWidth() - 100, returnImage.getY() + 15);

        group.addActor(userNameLabel);


        //add to actor
        stage.addActor(group);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        //transition map
        if (transitionUp !=-1) {
            if (transitionUp == 0) {
                for (Image map : mapImages) {
                    map.setPosition(map.getX(), map.getY() + transitionSpeed);
                }

            } else {

                for (Image map : mapImages) {
                    map.setPosition(map.getX(), map.getY() - transitionSpeed);

                }
            }
            transitionDistance += transitionSpeed;

            if (transitionDistance >= gameManager.WORLDHEIGHT) {
                transitionDistance = 0;
                transitionUp = -1;
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
        viewport.update(width, height);
    }

    public Stage getStage() {
        return stage;
    }

    private void transitionMap(int mode, int step) {


        for (Image map: mapImages) {

            if (mode == 0) //Up
            {
                map.setPosition(map.getX(), map.getY()+step);
            } else //Down
            {
                map.setPosition(map.getX(), map.getY()-step);
            }
        }

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
        if(stage!=null)
        {
            stage.dispose();
        }

        for(int i=0;i<mapCount;i++) {
            mapTextures.get(i).dispose();
        }

        if(backGround.getTexture()!=null)
        {
            backGround.getTexture().dispose();
        }

        if(returnTexture!=null)
            returnTexture.dispose();

        if(nextMapTexture!=null)
            nextMapTexture.dispose();

        if(signOutTexture!=null)
            signOutTexture.dispose();

    }

}
