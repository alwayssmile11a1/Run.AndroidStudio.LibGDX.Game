package noshanabi.game.Objects;

import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by 2SMILE2 on 21/11/2017.
 */

public class DeadGround extends Ground{

    public static final short DEAD_BIT = 32;

    public DeadGround(World world, float x, float y, float width, float height)
    {
        super(world,x,y,width,height);

    }

    @Override
    protected void defineObject() {
        //body definition
        BodyDef bDef = new BodyDef();
        bDef.position.set(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2);
        bDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bDef);

        //create the shape of body
        FixtureDef fDef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(this.getWidth()/2,this.getHeight()/2);
        fDef.shape = bodyShape;
        fDef.filter.categoryBits = DEAD_BIT;
        fDef.filter.maskBits = Player.PLAYER_BIT;
        body.createFixture(fDef).setUserData(this);
    }

    @Override
    public void dispose() {
        if(getTexture()!=null)
        {
            getTexture().dispose();
        }
    }


}
