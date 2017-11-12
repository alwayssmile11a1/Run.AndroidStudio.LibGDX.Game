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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

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
    private int mapCount = 1;


    private Sprite backGround;

    //
    Array<Image> mapImages;
    Array<Texture> mapTextures;

    private Image returnImage;
    private Texture returnTexture;


    public MapSelectionScreen(GameManager _gameManager)
    {
        //set up constructor variables
        this.gameManager = _gameManager;

        //color to clear this screen
        Gdx.gl.glClearColor(0,0,0,1);

        backGround = new Sprite(new Texture("images/BlueBackground.png"));
        backGround.setSize(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);

        //-----------------VIEW RELATED VARIABLES-----------------//
        mapSelectionViewport = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(mapSelectionViewport,gameManager.batch);


        //Table help us to easily arrange UI, such as labels, texts, etc.
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        mapTextures = new Array<Texture>();
        mapImages = new Array<Image>();


        //map texture
        for(int i=0;i<mapCount;i++) {
            String textureFileName = "maps/map"+i+"/maptexture.png";
            mapTextures.add(new Texture(textureFileName));
        }

        //load map images
        for(int i=0;i<mapCount;i++) {
            Image mapImage = new Image(mapTextures.get(i));
            final String mapName = "maps/map"+i+"/map.tmx";
            mapImages.add(mapImage);
            mapImage.setBounds(0,0,mapTextures.get(i).getWidth(),mapTextures.get(i).getHeight());
            mapImage.setTouchable(Touchable.enabled);
            mapImage.addListener(new InputListener()
            {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    gameManager.setScreen(new PlayScreen(gameManager,mapName));
                    return true;
                }

            });
        }

        //add to table
        for(int i=0;i<mapCount;i++)
        {
            if(i<5) {
                table.add(mapImages.get(i)).expandX().size(100, 100);
            }
            else
            {
                table.add(mapImages.get(i)).expandX().size(100, 100).padTop(50);
            }

            if((i+1)%5==0)
            {
                table.row();
            }
        }


        //add to stage
        stage.addActor(table);


        //Group allow to place an actor wherever we want
        Group group = new Group();

        //the return button
        returnTexture = new Texture("images/rightarrow.png");
        returnImage = new Image(returnTexture);
        returnImage.setBounds(0,0,returnTexture.getWidth(),returnTexture.getHeight());
        returnImage.setTouchable(Touchable.enabled);
        returnImage.addListener(new InputListener()
        {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getMenuScreen().getStage());
               gameManager.setScreen(gameManager.getMenuScreen());
                return true;
            }

        });

        returnImage.setScaleX(-1);
        returnImage.setPosition(70,gameManager.WORLDHEIGHT-70);
        returnImage.setSize(50,50);
        //add to group
        group.addActor(returnImage);

        //add to actor
        stage.addActor(group);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

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
    }

}
