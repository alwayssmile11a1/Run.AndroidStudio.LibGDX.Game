package noshanabi.game.WorldCreator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import noshanabi.game.Objects.Checkpoint;
import noshanabi.game.Objects.DeadGround;
import noshanabi.game.Objects.FinishPoint;
import noshanabi.game.Objects.Ground;
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


        //create Platforms
        Array<RectangleMapObject> platforms = map.getLayers().get("Platforms").getObjects().getByType(RectangleMapObject.class);
        for(MapObject mapObject:platforms)
        {
            //create rigid body
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            Ground ground = new Ground(world,
                    rectangle.getX()/Resourses.PPM,
                    rectangle.getY()/Resourses.PPM,
                    rectangle.getWidth()/Resourses.PPM,
                    rectangle.getHeight()/Resourses.PPM);

            grounds.add(ground);

        }

        //create Dead Platforms
        Array<RectangleMapObject> deadPlatforms = map.getLayers().get("DeadPlatforms").getObjects().getByType(RectangleMapObject.class);
        for(MapObject mapObject:deadPlatforms)
        {
            //create rigid body
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            DeadGround deadGround = new DeadGround(world,
                    rectangle.getX()/Resourses.PPM,
                    rectangle.getY()/Resourses.PPM,
                    rectangle.getWidth()/Resourses.PPM,
                    rectangle.getHeight()/Resourses.PPM);

            deadGrounds.add(deadGround);

        }

        //create Checkpoint
        Array<RectangleMapObject> checkPoints = map.getLayers().get("CheckPoints").getObjects().getByType(RectangleMapObject.class);
        for(MapObject mapObject:checkPoints)
        {
            //create rigid body
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            Checkpoint checkpoint = new Checkpoint(world,
                    rectangle.getX()/Resourses.PPM,
                    rectangle.getY()/Resourses.PPM,
                    rectangle.getWidth()/Resourses.PPM,
                    rectangle.getHeight()/Resourses.PPM);
        }

        //create finish position
        FinishPoint finishPoint = new FinishPoint(world, finishRect.getX()/Resourses.PPM, finishRect.getY()/Resourses.PPM,
                                                            finishRect.getWidth()/Resourses.PPM,finishRect.getHeight()/Resourses.PPM);


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

    public void update(OrthographicCamera camera)
    {
        mapRenderer.setView(camera);
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
    }

}
