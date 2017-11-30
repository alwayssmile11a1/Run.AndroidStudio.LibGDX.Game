package noshanabi.game.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import noshanabi.game.GameManager;
import noshanabi.game.Objects.Player;
import noshanabi.game.PlayScreenUI.GameFinishedUI;
import noshanabi.game.PlayScreenUI.InGameUI;
import noshanabi.game.Resourses;
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



    //----------------UI RELATED VARIABLES------------//
    private InGameUI inGameUI;
    private boolean isGamePausing = false;
    //private float previousWorldStepSpeed;


    private GameFinishedUI gameFinishedUI;

    //----------------SERVER RELATED VARIABLES------------//
    ServerCreator server;


    //-------------------OTHERS------------------------
    //just for holding count down label
    //private boolean isGameStarting = true; //if game is staring, don't move anything
    private float playTime;
    private boolean gameEnded = false;


    private Music backgroundMusic;
    float playbackPosition=0;

    float deadTime = -1;


    public PlayScreen(GameManager gameManager, String mapName, String backgroundMusicName) {
        //set up constructor variables
        this.gameManager = gameManager;
        this.worldWidth = Resourses.WORLDWIDTH / Resourses.PPM;
        this.worldHeight = Resourses.WORLDHEIGHT / Resourses.PPM;

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
        player = new Player(gameManager, world, mapCreator.getInstantiatePosition().x,mapCreator.getInstantiatePosition().y);
        player.setInstantiatePoint(mapCreator.getInstantiatePosition().x,mapCreator.getInstantiatePosition().y);
        player.reset();

        //do this to avoid map being faded at the very beginning of the game
        world.step(1 / 600f * worldStepSpeed, 6, 2);

        //--------------------------UI -----------------------------
        inGameUI = new InGameUI(gameManager);
        Gdx.input.setInputProcessor(inGameUI.getStage());
        gameFinishedUI = new GameFinishedUI(gameManager);


        //-------------------------OTHERS------------------------------
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal(backgroundMusicName));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.4f);
    }

    public void setServer(ServerCreator server)
    {
        this.server = server;

        server.setMainPlayer(player);

        server.setWorld(world);

    }

    public Stage getGameStage()
    {
        return inGameUI.getStage();
    }


    private void updateCamera() {
        if (deadTime == -1) {
            //update camera to follow this player
            mainCamera.position.x = MathUtils.clamp(player.getBody().getPosition().x + 1,
                    gameViewPort.getWorldWidth() / 2,
                    mapCreator.getFinishPosition().x);

            mainCamera.position.y = MathUtils.clamp(player.getBody().getPosition().y, gameViewPort.getWorldHeight() / 2, gameViewPort.getWorldHeight() / 2 + 3f);

        } else {
            if (deadTime > 0) {
                deadTime -= 1 / 60f;
                //update camera to follow this player
                mainCamera.position.x = MathUtils.clamp(player.getDeadPoint().x + 1,
                        gameViewPort.getWorldWidth() / 2,
                        mapCreator.getFinishPosition().x);

                mainCamera.position.y = MathUtils.clamp(player.getDeadPoint().y, gameViewPort.getWorldHeight() / 2, gameViewPort.getWorldHeight() / 2 + 3f);
            } else {
                deadTime = -1;
            }
        }

        mainCamera.update();
    }


    private void handleInput(float delta) {

        if(!player.getBody().isActive() || gameEnded) return;

        //player.getBody().setLinearVelocity(1.5f,player.getBody().getLinearVelocity().y);
        if(player.getBody().getLinearVelocity().x <2f) {
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

//        //slow down the time
//        if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
//            worldStepSpeed = 0.05f;
//        } else {
//            worldStepSpeed = worldStepSpeed + 1 / (slowdownLength / delta);
//            worldStepSpeed = MathUtils.clamp(worldStepSpeed, 0f, 1f);
//        }

    }

    private void handleUI()
    {
        if (inGameUI.isMenuButtonPressed() || gameFinishedUI.isMenuButtonPressed()) {
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

        if (inGameUI.isPauseButtonPressed() && !isGamePausing) {
            isGamePausing = true;
        }

        if (inGameUI.isContinueButtonPressed() && isGamePausing) {
            isGamePausing = false;
            if(server==null) {
                inGameUI.setCountDownTime(1.5f);
            }
            return;
        }


        if(gameFinishedUI.isReviewButtonPressed())
        {
            //set reviewing to true, reviewing automatically return false if there is nothing left to review
            player.setReviewing(true);
            mapCreator.getGroundEnemies().setReviewing(true);
        }

        if(gameFinishedUI.isReplayButtonPressed() || inGameUI.isReplayButtonPressed())
        {
            resetGame();
        }

    }

    private void resetGame()
    {
        //isGameStarting = true;
        isGamePausing = false;
        inGameUI.setCountDownTime(3f);
        gameEnded = false;
        player.reset();
        mapCreator.getGroundEnemies().reset();
        playTime = 0;
        Gdx.input.setInputProcessor(inGameUI.getStage());
        gameFinishedUI.reset();
    }

    private void update(float delta) {

        if (player.isHitFinishPoint()) {
            gameEnded = true;
            gameFinishedUI.setPlayTimeText("" + ((int) (playTime * 1000)) / 1000f);
        }

        //if game isn't finished, continue to handle input
        if (!gameEnded) {

            //game pausing
            if (isGamePausing && server == null)
            {
                setWorldActive(false);
            }
            else
                {
                //if game is still countdowning, it means player just start the game or player just pause the game and continue the game again (if server is null)
                if (inGameUI.getCountDownTime() > 0) {
                    inGameUI.setCountDownTime(inGameUI.getCountDownTime() - 1 / 60f);
                    inGameUI.setCountDownText("" + (int) inGameUI.getCountDownTime());
                    setWorldActive(false);

                } else {
                    inGameUI.setCountDownTime(0f);
                    inGameUI.setCountDownText("");
                    playTime += 1 / 60f;
                    setWorldActive(true);
                }
            }
        }
        else // if game is finished and player want to review their game -> let's review
        {
            //stop recording
            player.setRecording(false);
            mapCreator.getGroundEnemies().setRecording(false);

            //review if necessary
            player.reviewing(); // reviewing if isReviewing == true
            mapCreator.getGroundEnemies().reviewing();


        }

        handleInput(delta);


        world.step(1 / 60f * worldStepSpeed, 6, 2);

        //update player
        player.update(delta);

        //update enemy
        if(!gameEnded) {
            if (worldListener.isPlayerDead()) {
                backgroundMusic.setPosition(playbackPosition);
                mapCreator.getGroundEnemies().onPlayerDead();
                deadTime = 0.5f;
            }

            if (worldListener.isPlayerHitCheckPoint()) {
                playbackPosition = backgroundMusic.getPosition();
                mapCreator.getGroundEnemies().onPlayerHitCheckPoint();
            }

            if (worldListener.isPlayerHitFinishPoint()) {
                mapCreator.getGroundEnemies().onPlayerHitFinishPoint();
            }
        }

        if (server != null) {
            //update server
            server.updateServer(delta);
        }

        //update camera
        updateCamera();

        //update map
        mapCreator.update(mainCamera, delta);


    }

    public void setWorldActive(boolean actived)
    {
        player.setActive(actived);
        mapCreator.getGroundEnemies().setActive(actived);

        if(actived==false)
        {
            if(backgroundMusic.isPlaying()) {
                playbackPosition = backgroundMusic.getPosition();
                backgroundMusic.stop();
            }
        }
        else
        {
            if(!backgroundMusic.isPlaying())
            {
                backgroundMusic.play();
                backgroundMusic.setPosition(playbackPosition);
            }
        }


    }

    //render textures, maps, etc..
    @Override
    public void render(float delta) {

        handleUI();

        //call update
        update(delta);

        //clear background color to a specified color
        Gdx.gl.glClearColor(0,0,0,1f);

        //clear background color
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapCreator.renderMap(mainCamera);

        //-----------DRAW-----------------//
        //set camera to be used by this batch
        gameManager.batch.setProjectionMatrix(mainCamera.combined);

        //draw things to batch
        gameManager.batch.begin();

        if(server !=null) {
            server.drawOtherPlayers(gameManager.batch);
        }

        player.draw(gameManager.batch);

        mapCreator.draw(gameManager.batch);

        //end of draw
        gameManager.batch.end();


        //----------------------------------------

        inGameUI.draw();

        if(gameEnded)
        {
            gameFinishedUI.draw();
            if(!player.isReviewing())
            {
                gameFinishedUI.setVisiable(true);
            }
        }
        else
        {
            inGameUI.draw();
        }

        worldListener.update();

        //render box2DDebug
        b2DebugRenderer.render(world,mainCamera.combined);

        //rayHandler.setCombinedMatrix(mainCamera);
        //rayHandler.updateAndRender();
    }

    @Override
    public void resize(int width, int height) {
        //resize viewport if we resize our game world
        gameViewPort.update(width,height);
        inGameUI.resize(width,height);
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
        if(inGameUI !=null)
            inGameUI.dispose();

        if(gameFinishedUI !=null)
            gameFinishedUI.dispose();

        if(backgroundMusic!=null)
        {
            backgroundMusic.dispose();
        }


        //Gdx.app.log("DISPOSE","Play Screen");

    }
}
