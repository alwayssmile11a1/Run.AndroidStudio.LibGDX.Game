package noshanabi.game.WorldCreator;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import noshanabi.game.GameManager;
import noshanabi.game.Objects.Ground;

/**
 * Created by 2SMILE2 on 29/09/2017.
 */

public class MapCreator {

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private Array<Ground> grounds;

    public MapCreator(World world, String fileName)
    {
        grounds = new Array<Ground>();
        //get map from file
        map = new TmxMapLoader().load(fileName);
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ GameManager.PPM);


        //create objects given in the map
        for(MapObject mapObject:map.getLayers().get("Platforms").getObjects().getByType(RectangleMapObject.class))
        {
            //create rigid body
            Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
            Ground ground = new Ground(world,
                    rectangle.getX(),
                    rectangle.getY(),
                    rectangle.getWidth(),
                    rectangle.getHeight(),
                    0);

            grounds.add(ground);

        }

    }

    public Array<Ground> getGrounds()
    {
        return grounds;
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
