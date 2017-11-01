package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import noshanabi.game.ControllerSystem.MobileController;
import noshanabi.game.GameManager;
import noshanabi.game.Objects.Player;
import noshanabi.game.Server.ServerCreator;
import noshanabi.game.WorldCreator.MapCreator;

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

    private Player player;

    //this variable helps us to see the virtual shape of our world (virtual shape of all objects for example)
    //this variable should be eliminated when public the game
    //private Box2DDebugRenderer b2DebugRenderer;


    //----------------WORLD RELATED VARIABLES------------//
    //manipulate world step - the bigger the worldStepSpeed, the faster the game simulates its physics
    float worldStepSpeed = 1f;
    //the length of time that the world have to take to be able to get back to its normal speed if it's being slowed down
    float slowdownLength = 5f;

    //map related variables
    private MapCreator mapCreator;
    //RayHandler rayHandler;
    //PointLight pointLight1;



    //----------------CONTROLLER RELATED VARIABLES------------//
    MobileController mobileController;



    //----------------SERVER RELATED VARIABLES------------//
    ServerCreator server;


    public PlayScreen(GameManager gameManager) {
        //set up constructor variables
        this.gameManager = gameManager;
        this.worldWidth = gameManager.WORLDWIDTH / gameManager.PPM;
        this.worldHeight = gameManager.WORLDHEIGHT / gameManager.PPM;

        //clear background color to a specified color
        //Gdx.gl.glClearColor(0,0,0,1f);
        Gdx.gl.glClearColor(0.85f, 0.85f, 0.85f, 0);

        //-----------------VIEW RELATED VARIABLES-----------------//
        //initialize a new camera
        mainCamera = new OrthographicCamera();
        //initialze gameViewPort
        gameViewPort = new StretchViewport(worldWidth, worldHeight, mainCamera);
        //gameViewPort = new FitViewport(worldWidth,worldHeight,mainCamera);
        //set mainCamera position to the center of gameviewport
        mainCamera.position.set(gameViewPort.getWorldWidth() / 2, gameViewPort.getWorldHeight() / 2, 0);


        //----------------OBJECT RELATED VARIABLES------------//
        //initialize world with the gravity of -9.8f
        world = new World(new Vector2(0f, -9.8f), true);

        //initialize box2DDebugRenderer
        //b2DebugRenderer = new Box2DDebugRenderer();

        //initialize player
        player = new Player(world);


        //----------------WORLD RELATED VARIABLES------------//
        //create map
        mapCreator = new MapCreator(world, "maps/Map.tmx");

        //rayHandler = new RayHandler(world);
        //pointLight1 = new PointLight(rayHandler, 500, Color.GRAY, 100, 4f, 4f);
        //pointLight1.setSoftnessLength(50f);
        //System.out.print(pointLight.getSoftShadowLength());


        //----------------CONTROLLER RELATED VARIABLES------------//
        mobileController = new MobileController(gameManager);

        server = new ServerCreator(world, player);
        server.connectSocket();
        server.configSocketEvents();

    }

    public void handleInput(float delta)
    {

        //Jump
        if(Gdx.input.isKeyJustPressed(Input.Keys.W))
        {
            player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x,4f);
        }
        //Right
        if(Gdx.input.isKeyPressed(Input.Keys.D))
        {
            player.getBody().setLinearVelocity(1.5f,player.getBody().getLinearVelocity().y);
        }
        //Left
        if(Gdx.input.isKeyPressed(Input.Keys.A))
        {
            player.getBody().setLinearVelocity(-1.5f,player.getBody().getLinearVelocity().y);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.E)) {
            player.startRewinding();
        }
        else
        {
            player.stopRewinding();
        }

        //slow down the time
        if(Gdx.input.isKeyJustPressed(Input.Keys.Q))
        {
            worldStepSpeed = 0.05f;
        }
        else
        {
            worldStepSpeed = worldStepSpeed + 1/(slowdownLength/delta);
            worldStepSpeed = MathUtils.clamp(worldStepSpeed,0f,1f);
        }

        if(mobileController.isLeftScreenPressed())
        {
            player.getBody().setLinearVelocity(1.5f,player.getBody().getLinearVelocity().y);
        }

    }

    //update things related physics
    public void update(float delta)
    {
        handleInput(delta);

        //update world
        world.step(1/60f * worldStepSpeed,6,2);

        //update other players
        server.updateOtherPlayers(delta);

        //update player
        player.update(delta);

        server.updateServer(delta);

        //update camera to follow thí player
        mainCamera.position.x = MathUtils.clamp(player.getBody().getPosition().x + 1,gameViewPort.getWorldWidth()/2,100f);
        mainCamera.update();

        mapCreator.update(mainCamera);

    }

    //render textures, maps, etc..
    @Override
    public void render(float delta) {
        //call update
        update(delta);

        //clear background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapCreator.renderMap();

        //-----------DRAW-----------------//
        //set camera to be used by this batch
        gameManager.batch.setProjectionMatrix(mainCamera.combined);

        //draw things to batch
        gameManager.batch.begin();

        server.drawOtherPlayers(gameManager.batch);

        //backgroundSprite.draw(gameManager.batch);
        player.draw(gameManager.batch);


        //end of draw
        gameManager.batch.end();

        mobileController.draw();

        //render box2DDebug
        //b2DebugRenderer.render(world,mainCamera.combined);

        //rayHandler.setCombinedMatrix(mainCamera);
        //rayHandler.updateAndRender();
    }

    @Override
    public void resize(int width, int height) {
        //resize viewport if we resize our game world
        gameViewPort.update(width,height);
        mobileController.resize(width,height);
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

        if(gameManager!=null)
        {
            gameManager.dispose();
        }

        if(player!=null)
        {
            player.dispose();
        }

        if(mapCreator!=null)
        {
            mapCreator.dispose();
        }
        //if(rayHandler!=null) {
        //    rayHandler.dispose();
        //}
        if(mobileController!=null)
            mobileController.dispose();

        server.dispose();

    }
}
