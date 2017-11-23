package noshanabi.game.WorldCreator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import noshanabi.game.Objects.Saw;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 29/09/2017.
 */

public class MapCreator {

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Array<Ground> grounds;
    private Array<DeadGround> deadGrounds;
    private Vector2 instantiatePosition;
    private Vector2 finishPosition;
    private Array<Saw> saws;

    //texture
    private Texture sawTexture;


    public MapCreator(World world, String fileName)
    {
        grounds = new Array<Ground>();
        deadGrounds = new Array<DeadGround>();
        //get map from file
        map = new TmxMapLoader().load(fileName);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ Resourses.PPM);

        //get init Position
        Rectangle instantiateRect = map.getLayers().get("InstantiatePosition").getObjects().getByType(RectangleMapObject.class).first().getRectangle();
        instantiatePosition =  new Vector2((instantiateRect.getX()+instantiateRect.getWidth()/2)/Resourses.PPM,
                                            (instantiateRect.getY()+instantiateRect.getHeight()/2)/Resourses.PPM);

        //get finish Position
        Rectangle finishRect = map.getLayers().get("FinishPosition").getObjects().getByType(RectangleMapObject.class).first().getRectangle();
        finishPosition =  new Vector2((finishRect.getX()+finishRect.getWidth()/2)/Resourses.PPM,
                                        (finishRect.getY()+finishRect.getHeight()/2)/Resourses.PPM);


        //--------------------CREATE PLATFORMS-------------------------------------
        Array<RectangleMapObject> platforms = map.getLayers().get("Platforms").getObjects().getByType(RectangleMapObject.class);
        for(RectangleMapObject platform:platforms)
        {
            //create rigid body
            Rectangle rectangle = platform.getRectangle();
            Ground ground = new Ground(world,
                    rectangle.getX()/Resourses.PPM,
                    rectangle.getY()/Resourses.PPM,
                    rectangle.getWidth()/Resourses.PPM,
                    rectangle.getHeight()/Resourses.PPM);

            grounds.add(ground);

        }

        //----------------------CREATE DEAD PLATFORMS-------------------------------------
        Array<RectangleMapObject> deadPlatforms = map.getLayers().get("DeadPlatforms").getObjects().getByType(RectangleMapObject.class);
        for(RectangleMapObject deadPlatform:deadPlatforms)
        {
            //create rigid body
            Rectangle rectangle = deadPlatform.getRectangle();
            DeadGround deadGround = new DeadGround(world,
                    rectangle.getX()/Resourses.PPM,
                    rectangle.getY()/Resourses.PPM,
                    rectangle.getWidth()/Resourses.PPM,
                    rectangle.getHeight()/Resourses.PPM);

            deadGrounds.add(deadGround);

        }

        //----------------------CREATE CHECKPOINTS-----------------------------------------
        Array<RectangleMapObject> checkPoints = map.getLayers().get("CheckPoints").getObjects().getByType(RectangleMapObject.class);
        for(RectangleMapObject checkpoint:checkPoints)
        {
            //create rigid body
            Rectangle rectangle = checkpoint.getRectangle();
            Checkpoint checkPoint = new Checkpoint(world,
                    rectangle.getX()/Resourses.PPM,
                    rectangle.getY()/Resourses.PPM,
                    rectangle.getWidth()/Resourses.PPM,
                    rectangle.getHeight()/Resourses.PPM);
        }

        //-----------------CREATE FINISH POINT --------------------------------------------
        FinishPoint finishPoint = new FinishPoint(world, finishRect.getX()/Resourses.PPM, finishRect.getY()/Resourses.PPM,
                                                            finishRect.getWidth()/Resourses.PPM,finishRect.getHeight()/Resourses.PPM);



        //------------------CREATE SAWS-----------------------------------------------
        sawTexture = new Texture(Resourses.Saw);
        saws = new Array<Saw>();
        Array<EllipseMapObject> mapObjects = map.getLayers().get("Saws").getObjects().getByType(EllipseMapObject.class);
        for(EllipseMapObject object:mapObjects) {
            //create rigid body
            Ellipse ellipse = object.getEllipse();

            Saw saw = new Saw(world, sawTexture,
                    ellipse.x / Resourses.PPM,
                    ellipse.y / Resourses.PPM,
                    ellipse.width / Resourses.PPM,
                    ellipse.height / Resourses.PPM);

            if(object.getProperties().get("angularVelocity") != null) {
                saw.setAngularVelocity(Float.parseFloat(object.getProperties().get("angularVelocity").toString()));
            }
            else
            {
                saw.setAngularVelocity(-2f);
            }

            //add to array
            saws.add(saw);

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


    public void renderMap()
    {
        mapRenderer.render();
    }

    public void draw(SpriteBatch batch)
    {
        for(Saw saw:saws)
        {
            saw.draw(batch);
        }
    }


    public void update(OrthographicCamera camera, float dt)
    {
        mapRenderer.setView(camera);
        for(Saw saw:saws)
        {
            saw.update(dt);
        }
    }

    public void dispose()
    {
        if(map!=null)
        {
            map.dispose();
        }
        if(mapRenderer!=null)
        {
            mapRenderer.dispose();
        }

        for(Ground ground:grounds)
        {
            ground.dispose();
        }

        for(Saw saw:saws)
        {
            saw.dispose();
        }

        if(sawTexture!=null)
            sawTexture.dispose();

    }

}
