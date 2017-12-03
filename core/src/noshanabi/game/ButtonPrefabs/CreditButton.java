package noshanabi.game.ButtonPrefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;

import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 03/12/2017.
 */

public class CreditButton extends TextButton{

    Stage stage;
    Viewport viewport;
    private boolean isPressed = false;
    private float stateTime = 0;
    private float maxTime = 0.3f;
    GameManager gameManager;

    //table
    VisTable table;
    VisImage backGround;
    Texture backgroundTexture;


    public CreditButton(final GameManager gameManager)
    {
        super("Credit",new Skin(Gdx.files.internal(Resourses.ButtonSkinJSON2),new TextureAtlas(Resourses.ButtonSkinTextureAtlas2)));
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {

                isPressed = true;
                Gdx.app.log("ads","");
                return true;
            }

        });

        this.gameManager = gameManager;
        viewport = new StretchViewport(Resourses.WORLDWIDTH,Resourses.WORLDHEIGHT);
        stage = new Stage(viewport);

        //get all player texture
        table = new VisTable();
        table.setFillParent(true);
        table.center();
        table.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                Gdx.input.setInputProcessor(gameManager.getMenuScreen().getStage());
                table.setVisible(false);
                return true;
            }

        });

        //background
        backgroundTexture = new Texture(Resourses.WhiteRectangle);
        backGround = new VisImage(backgroundTexture);
        backGround.setSize(Resourses.WORLDWIDTH-25,Resourses.WORLDHEIGHT-25);
        backGround.setPosition(Resourses.WORLDWIDTH/2- backGround.getWidth()/2,Resourses.WORLDHEIGHT/2- backGround.getHeight()/2);
        backGround.setColor(0, 0, 0, 0.5f);
        table.addActor(backGround);


        table.add(new Label("THANK YOU FOR:",getSkin())).row();
        table.add(new Label("LIBGDX AND ANDROID STUDIO",getSkin())).padTop(30f).row();
        table.add(new Label("SKIN COMPOSER - HIERO - PARTICAL EDITOR",getSkin())).padTop(15f).row();
        table.add(new Label("ART: Kenney Vleugels (www.kenney.nl)",getSkin())).padTop(20f).row();

        stage.addActor(table);
        table.setVisible(false);
    }

    public Stage getStage()
    {
        return stage;
    }

    public void resize(int x, int y)
    {
       viewport.update(x,y);
    }

    public void update(float dt) {

        stage.draw();
        stage.act();

        if (isPressed) {
            stateTime += dt;
            if (stateTime > maxTime) {
                Gdx.input.setInputProcessor(stage);
                table.setVisible(true);
                isPressed = false;
                stateTime = 0;
            }
        }



    }

    public void dispose()
    {
        getSkin().dispose();

        if(backgroundTexture!=null)
            backgroundTexture.dispose();

        if(stage!=null)
            stage.dispose();
    }

}
