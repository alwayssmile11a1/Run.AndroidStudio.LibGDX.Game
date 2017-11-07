package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
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

    //the background image
    Array<Image> mapImages;
    Array<Texture> mapTextures;


    public MapSelectionScreen(GameManager _gameManager)
    {
        //set up constructor variables
        this.gameManager = _gameManager;

        //-----------------VIEW RELATED VARIABLES-----------------//
        mapSelectionViewport = new StretchViewport(GameManager.WORLDWIDTH, GameManager.WORLDHEIGHT);
        stage = new Stage(mapSelectionViewport,gameManager.batch);
        Gdx.input.setInputProcessor(stage);

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
                    dispose();
                    return true;
                }

            });
        }


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

        //color to clear this screen
        Gdx.gl.glClearColor(0,0,0,1);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        mapSelectionViewport.update(width, height);
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

    }

}
