package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import noshanabi.game.GameManager;
import noshanabi.game.Objects.Player;
import noshanabi.game.PlayScreenUI.PlayScreenUI;
import noshanabi.game.Server.ServerCreator;
import noshanabi.game.WorldCreator.MapCreator;
import noshanabi.game.WorldCreator.WorldListener;

/**
 * Created by 2SMILE2 on 25/09/2017.
 */

//Game activity mostly here
public class PlayScreen implements Screen{

    //GameManager
    private GameManager gameManager;

    //-----------------VIEW RELATED VARIABLES-----------------//
    //how well we want to see our map
    private Viewport gameViewPort;
    //world width and height
    private float worldWidth;
    private float worldHeight;
    //a camera to view our world
    private OrthographicCamera mainCamera;


    //----------------TEXTURE RELATED VARIABLES------------//


    //----------------OBJECT RELATED VARIABLES------------//
    //world simulate collision, physics, etc.
    private World world;
    private WorldListener worldListener;

    private Player player;

    //this variable helps us to see the virtual shape of our world (virtual shape of all objects for example)
    //this variable should be eliminated when public the game
    private Box2DDebugRenderer b2DebugRenderer;


    //----------------WORLD RELATED VARIABLES------------//
    //manipulate world step - the bigger the worldStepSpeed, the faster the game simulates its physics
    private float worldStepSpeed = 1f;
    //the length of time that the world have to take to be able to get back to its normal speed if it's being slowed down
    private float slowdownLength = 5f;

    //map related variables
    private MapCreator mapCreator;
    //RayHandler rayHandler;
    //PointLight pointLight1;



    //----------------CONTROLLER RELATED VARIABLES------------//
    private PlayScreenUI playScreenUI;
    private boolean gamePaused = false;
    private float previousWorldStepSpeed;

    //----------------SERVER RELATED VARIABLES------------//
    ServerCreator server;


    //-------------------OTHERS------------------------
    Stage stage;
    VisLabel countDownLabel;
    float countDownTime = 3;
    Viewport stageViewport;


    public PlayScreen(GameManager gameManager, String mapName) {
        //set up constructor variables
        this.gameManager = gameManager;
        this.worldWidth = gameManager.WORLDWIDTH / gameManager.PPM;
        this.worldHeight = gameManager.WORLDHEIGHT / gameManager.PPM;

        //-----------------VIEW RELATED VARIABLES-----------------//
        //initialize a new camera
        mainCamera = new OrthographicCamera();
        //initialze gameViewPort
        gameViewPort = new StretchViewport(worldWidth, worldHeight, mainCamera);
        //gameViewPort = new FitViewport(worldWidth,worldHeight,mainCamera);
        //set mainCamera position to the center of gameviewport
        mainCamera.position.set(gameViewPort.getWorldWidth() / 2, gameViewPort.getWorldHeight() / 2, 0);


        //----------------WORLD VARIABLES------------//
        //initialize world with the gravity of -9.8f
        world = new World(new Vector2(0f, -9.8f), true);
        worldListener = new WorldListener();
        world.setContactListener(worldListener);

        //initialize box2DDebugRenderer
        b2DebugRenderer = new Box2DDebugRenderer();


        //----------------MAP RELATED VARIABLES------------//
        //create map
        mapCreator = new MapCreator(world, mapName);

        //rayHandler = new RayHandler(world);
        //pointLight1 = new PointLight(rayHandler, 500, Color.GRAY, 100, 4f, 4f);
        //pointLight1.setSoftnessLength(50f);
        //System.out.print(pointLight.getSoftShadowLength());


        //initialize player
        player = new Player(world, mapCreator.getInstantiatePosition().x,mapCreator.getInstantiatePosition().y);
        player.setCheckPoint(mapCreator.getInstantiatePosition().x,mapCreator.getInstantiatePosition().y);

        //--------------------------UI -----------------------------
        playScreenUI = new PlayScreenUI(gameManager);
        Gdx.input.setInputProcessor(playScreenUI.getInGameStage());



        //-------------------------OTHERS------------------------------
        stageViewport = new StretchViewport(gameManager.WORLDWIDTH,gameManager.WORLDHEIGHT);
        stage = new Stage(stageViewport,gameManager.batch);
        VisTable table = new VisTable();
        table.setFillParent(true);
        table.top();

        countDownLabel = new VisLabel(""+(int)countDownTime);
        table.add(countDownLabel).padTop(100f);
        stage.addActor(table);


    }

    public void setServer(ServerCreator server)
    {
        this.server = server;

        server.setMainPlayer(player);

    }

    public void handleInput(float delta) {
        if (playScreenUI.isPauseButtonPressed() && !gamePaused) {
            previousWorldStepSpeed = worldStepSpeed;
            worldStepSpeed = 0;
            gamePaused = true;
        }

        if (playScreenUI.isContinueButtonPressed() && gamePaused) {
            worldStepSpeed = previousWorldStepSpeed;
            gamePaused = false;
            countDownTime = 2f;
            return;
        }

        if (gamePaused) return;

        if (countDownTime > 0) {
            countDownLabel.setText("" + (int) countDownTime);
            countDownTime -= 0.02f;
            return;
        } else {
            countDownTime = 0;
            countDownLabel.setText("");
        }

        //player.getBody().setLinearVelocity(1.5f,player.getBody().getLinearVelocity().y);
        if(player.getBody().getLinearVelocity().x <1.5f) {
            player.getBody().applyLinearImpulse(0.1f, 0f, player.getBody().getPosition().x, player.getBody().getPosition().y, true);
        }

        if (Gdx.input.justTouched()) {
            if (player.isGrounded) {
                player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 4f);
                player.isGrounded = false;
                player.isDoubleJumped = false;
            } else {
                if (!player.isDoubleJumped) {
                    player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 4f);
                    player.isDoubleJumped = true;
                }
            }

        }

        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            player.startRewinding();
        } else {
            player.stopRewinding();
        }

        //slow down the time
        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            worldStepSpeed = 0.05f;
        } else {
            worldStepSpeed = worldStepSpeed + 1 / (slowdownLength / delta);
            worldStepSpeed = MathUtils.clamp(worldStepSpeed, 0f, 1f);
        }

//        if(playScreenUI.isPauseButtonPressed())
//        {
//            player.getBody().setLinearVelocity(1.5f,player.getBody().getLinearVelocity().y);
//        }

    }


    public void update(float delta)
    {
        handleInput(delta);

        if(countDownTime==0) {
            //update world
            world.step(1 / 60f * worldStepSpeed, 6, 2);
        }

        //update player
        player.update(delta);

        if(server!=null) {
            //update server
            server.updateServer(delta);
        }

        //update camera to follow this player
        mainCamera.position.x = MathUtils.clamp(player.getBody().getPosition().x + 1,
                                                gameViewPort.getWorldWidth()/2,
                                                 mapCreator.getFinishPosition().x-2f);

        mainCamera.position.y = MathUtils.clamp(player.getBody().getPosition().y ,gameViewPort.getWorldHeight()/2,gameViewPort.getWorldHeight()/2+3f);
        mainCamera.update();

        mapCreator.update(mainCamera);

    }

    public Stage getStage()
    {
        return playScreenUI.getInGameStage();
    }

    //render textures, maps, etc..
    @Override
    public void render(float delta) {

        if (playScreenUI.isMenuButtonPressed()) {
            gameManager.addToDisposeScreens(this);

            if (server == null) {
                Gdx.input.setInputProcessor(gameManager.getMapSelectionScreen().getStage());
                gameManager.setScreen(gameManager.getMapSelectionScreen());

            } else {
                if (server != null) {
                    server.getSocket().emit("leaveRoom");
                    Gdx.input.setInputProcessor(gameManager.getModeSelectionScreen().getStage());
                    gameManager.setScreen(gameManager.getModeSelectionScreen());
                }

            }

            return;
        }


        //call update
        update(delta);

        //clear background color to a specified color
        Gdx.gl.glClearColor(0,0,0,1f);
        //Gdx.gl.glClearColor(0.85f, 0.85f, 0.85f, 0);

        //clear background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapCreator.renderMap();

        //-----------DRAW-----------------//
        //set camera to be used by this batch
        gameManager.batch.setProjectionMatrix(mainCamera.combined);

        //draw things to batch
        gameManager.batch.begin();

        if(server !=null) {
            server.drawOtherPlayers(gameManager.batch);
        }

        player.draw(gameManager.batch);

        //end of draw
        gameManager.batch.end();

        playScreenUI.draw();

        if(countDownTime!=0) {
            stage.draw();
        }

        //render box2DDebug
        b2DebugRenderer.render(world,mainCamera.combined);

        //rayHandler.setCombinedMatrix(mainCamera);
        //rayHandler.updateAndRender();
    }

    @Override
    public void resize(int width, int height) {
        //resize viewport if we resize our game world
        gameViewPort.update(width,height);
        playScreenUI.resize(width,height);
        stageViewport.update(width,height);
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

        if (player != null) {
            player.dispose();
        }

        if (mapCreator != null) {
            mapCreator.dispose();
        }

        //if(rayHandler!=null) {
        //    rayHandler.dispose();
        //}
        if(playScreenUI !=null)
            playScreenUI.dispose();

        stage.dispose();

        Gdx.app.log("DISPOSE","Play Screen");

    }
}
