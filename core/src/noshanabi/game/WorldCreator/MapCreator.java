package noshanabi.game.WorldCreator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import noshanabi.game.Objects.Checkpoint;
import noshanabi.game.Objects.DeadGround;
import noshanabi.game.Objects.FinishPoint;
import noshanabi.game.Objects.Ground;
import noshanabi.game.Objects.GroundEnemies;
import noshanabi.game.Objects.HalfSaws;
import noshanabi.game.Objects.Saws;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 29/09/2017.
 */

public class MapCreator {

    private float movableLayerSpeed = 0.2f;

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private float mapWidth;

    private Array<Ground> grounds;
    private Array<DeadGround> deadGrounds;
    private Vector2 instantiatePosition;
    private Vector2 finishPosition;
    private Saws saws;
    private HalfSaws halfSaws;
    private GroundEnemies groundEnemies;

    private MapLayer movableLayer;


    public MapCreator(World world, String fileName) {
        grounds = new Array<Ground>();
        deadGrounds = new Array<DeadGround>();

        //get map from file
        map = new TmxMapLoader().load(fileName);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / Resourses.PPM);

        float tileWidth = Float.parseFloat(map.getProperties().get("tilewidth").toString()) / Resourses.PPM;
        mapWidth = Float.parseFloat(map.getProperties().get("width").toString()) * tileWidth;

        //get movable layer
        movableLayer = map.getLayers().get("MovableBackGround");


        //-------------GET INIT POINT
        instantiatePosition = new Vector2();
        if (map.getLayers().get("InstantiatePosition") != null) {
            Rectangle instantiateRect = map.getLayers().get("InstantiatePosition").getObjects().getByType(RectangleMapObject.class).first().getRectangle();
            instantiatePosition.set((instantiateRect.getX() + instantiateRect.getWidth() / 2) / Resourses.PPM,
                    (instantiateRect.getY() + instantiateRect.getHeight() / 2) / Resourses.PPM);
        }


        //-----------------CREATE FINISH POINT --------------------------------------------
        finishPosition = new Vector2();
        if (map.getLayers().get("FinishPosition") != null) {
            //get finish Position
            Rectangle finishRect = map.getLayers().get("FinishPosition").getObjects().getByType(RectangleMapObject.class).first().getRectangle();
            finishPosition.set((finishRect.getX() + finishRect.getWidth() / 2) / Resourses.PPM,
                    (finishRect.getY() + finishRect.getHeight() / 2) / Resourses.PPM);

            //-----------------CREATE FINISH POINT --------------------------------------------
            FinishPoint finishPoint = new FinishPoint(world, finishRect.getX() / Resourses.PPM, finishRect.getY() / Resourses.PPM,
                    finishRect.getWidth() / Resourses.PPM, finishRect.getHeight() / Resourses.PPM);
        }

        //--------------------CREATE PLATFORMS-------------------------------------
        if (map.getLayers().get("Platforms") != null) {
            Array<RectangleMapObject> platforms = map.getLayers().get("Platforms").getObjects().getByType(RectangleMapObject.class);
            for (RectangleMapObject platform : platforms) {

                Rectangle rectangle = platform.getRectangle();
                Ground ground = new Ground(world,
                        rectangle.getX() / Resourses.PPM,
                        rectangle.getY() / Resourses.PPM,
                        rectangle.getWidth() / Resourses.PPM,
                        rectangle.getHeight() / Resourses.PPM);

                grounds.add(ground);

            }
        }

        //----------------------CREATE DEAD PLATFORMS-------------------------------------
        if (map.getLayers().get("DeadPlatforms") != null) {
            Array<RectangleMapObject> deadPlatforms = map.getLayers().get("DeadPlatforms").getObjects().getByType(RectangleMapObject.class);
            for (RectangleMapObject deadPlatform : deadPlatforms) {

                Rectangle rectangle = deadPlatform.getRectangle();
                DeadGround deadGround = new DeadGround(world,
                        rectangle.getX() / Resourses.PPM,
                        rectangle.getY() / Resourses.PPM,
                        rectangle.getWidth() / Resourses.PPM,
                        rectangle.getHeight() / Resourses.PPM);

                deadGrounds.add(deadGround);

            }
        }

        //----------------------CREATE CHECKPOINTS-----------------------------------------
        if (map.getLayers().get("CheckPoints") != null) {
            Array<RectangleMapObject> checkPoints = map.getLayers().get("CheckPoints").getObjects().getByType(RectangleMapObject.class);
            for (RectangleMapObject checkpoint : checkPoints) {

                Rectangle rectangle = checkpoint.getRectangle();
                Checkpoint checkPoint = new Checkpoint(world,
                        rectangle.getX() / Resourses.PPM,
                        rectangle.getY() / Resourses.PPM,
                        rectangle.getWidth() / Resourses.PPM,
                        rectangle.getHeight() / Resourses.PPM);
            }
        }


        //------------------CREATE SAWS-----------------------------------------------
        saws = new Saws();
        if (map.getLayers().get("Saws") != null) {
            Array<EllipseMapObject> mapObjects = map.getLayers().get("Saws").getObjects().getByType(EllipseMapObject.class);

            for (EllipseMapObject object : mapObjects) {

                Ellipse ellipse = object.getEllipse();

                float angularVelocity = -2f;

                if (object.getProperties().get("angularVelocity") != null) {
                    angularVelocity = Float.parseFloat(object.getProperties().get("angularVelocity").toString());


                    saws.addSaw(world, ellipse.x / Resourses.PPM, ellipse.y / Resourses.PPM,
                            ellipse.width / Resourses.PPM, ellipse.height / Resourses.PPM, angularVelocity);

                }
            }
        }

        //------------------CREATE HALF SAWS-----------------------------------------------
        halfSaws = new HalfSaws();
        if (map.getLayers().get("HalfSaws") != null) {
            Array<EllipseMapObject> mapObjects = map.getLayers().get("HalfSaws").getObjects().getByType(EllipseMapObject.class);

            for (EllipseMapObject object : mapObjects) {

                Ellipse ellipse = object.getEllipse();

                float angularVelocity = 5f;

                if (object.getProperties().get("angularVelocity") != null) {
                    angularVelocity = Float.parseFloat(object.getProperties().get("angularVelocity").toString());

                    halfSaws.addSaw(world, ellipse.x / Resourses.PPM, (ellipse.y + ellipse.height / 2) / Resourses.PPM,
                            ellipse.width / Resourses.PPM, ellipse.height / Resourses.PPM,
                            angularVelocity);
                }
            }
        }

        //------------------CREATE GROUND ENEMIES-----------------------------------------------
        groundEnemies = new GroundEnemies();
        if (map.getLayers().get("GroundEnemies") != null) {
            Array<RectangleMapObject> mapObjects = map.getLayers().get("GroundEnemies").getObjects().getByType(RectangleMapObject.class);

            for (RectangleMapObject object : mapObjects) {

                Rectangle rectangle = object.getRectangle();

                String name = object.getProperties().get("name").toString();
                groundEnemies.addEnemy(world, name, rectangle.getX() / Resourses.PPM, rectangle.getY() / Resourses.PPM,
                        rectangle.getWidth() / Resourses.PPM, rectangle.getHeight() / Resourses.PPM,
                        1, 1);
            }
        }
    }


    public Vector2 getInstantiatePosition()
    {
        return instantiatePosition;
    }

    public Vector2 getFinishPosition()
    {
        return finishPosition;
    }

    public Array<Ground> getGrounds()
    {
        return grounds;
    }

    public Array<DeadGround> getDeadGrounds()
    {
        return deadGrounds;
    }


    public void renderMap(OrthographicCamera camera)
    {
        if(movableLayer!=null) {
            if ((movableLayer.getOffsetX() / Resourses.PPM + mapWidth - 5f) > (camera.position.x - camera.viewportWidth / 2)) {
                movableLayer.setOffsetX(movableLayer.getOffsetX() - movableLayerSpeed);
            } else {

                movableLayer.setOffsetX((camera.position.x + camera.viewportWidth / 2) * Resourses.PPM);
            }
        }


        mapRenderer.render();
    }

    public void draw(SpriteBatch batch)
    {
        saws.draw(batch);

        halfSaws.draw(batch);

        groundEnemies.draw(batch);
    }


    public void update(OrthographicCamera camera, float dt) {
        mapRenderer.setView(camera);

        saws.update(dt);

        halfSaws.update(dt);

        groundEnemies.update(dt);
    }

    public GroundEnemies getGroundEnemies()
    {
        return groundEnemies;
    }


    public void dispose() {
        if (map != null) {
            map.dispose();
        }
        if (mapRenderer != null) {
            mapRenderer.dispose();
        }

        for (Ground ground : grounds) {
            ground.dispose();
        }


        saws.dispose();

        halfSaws.dispose();

        groundEnemies.dispose();

    }

}
