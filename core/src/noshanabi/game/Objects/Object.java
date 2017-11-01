package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 25/09/2017.
 */

//parent class of any other objects, such as player, enemy, etc.
public class Object extends Sprite {

    protected World world; //the world that this object is belonged to
    protected Body body; //the body of this object
    protected Array<Transform> positions; //the list of positions that this object was going through - this is for rewinding purpose
    protected Array<Vector2> velocities; //the list of velocities that this object was going through - this is for rewinding purpose
    protected boolean isRewinding; //is rewinding time or not?
    protected float maximumRewindingTime;

    public Object(World world) {
        this.world = world;
        positions = new Array<Transform>();
        velocities = new Array<Vector2>();
        isRewinding = false;
        maximumRewindingTime = 5f;
    }

    //this function resize this object to be used more appropriate with Box2D
    protected void usePixelPerMeter() {

        setPosition(getX() / GameManager.PPM, getY() / GameManager.PPM);
        setSize(getWidth() / GameManager.PPM, getHeight() / GameManager.PPM);

    }

    //record positions that this object was going through
    protected void recordPositions(float dt) {

        if(body==null) return;

        if(isRewinding == false) {
            //
            if(positions.size > (int)(maximumRewindingTime/dt))
            {
                positions.removeIndex(positions.size-1);
                velocities.removeIndex(velocities.size-1);
            }
            positions.insert(0, new Transform(body.getPosition(), body.getAngle()));
            velocities.insert(0, new Vector2(body.getLinearVelocity()));
        }
    }

    //start rewinding - this fuction will automatically stop rewinding when it reaches maximum rewinding time
    public void startRewinding () {

        if(body==null) return;

        if (positions.size >0) {
            isRewinding = true;
            //body.setType(BodyDef.BodyType.KinematicBody);
            body.setTransform(positions.first().getPosition(), positions.first().getRotation());
            body.setLinearVelocity(velocities.first());
            positions.removeIndex(0);
            velocities.removeIndex(0);
        }
        else
        {
            stopRewinding();
        }
    }

    //stop rewinding time
    public void stopRewinding () {
        isRewinding = false;
        //body.setType(BodyDef.BodyType.DynamicBody);
    }

    protected void defineObject()
    {

    }


    public void update(float dt)
    {
        recordPositions(dt);
    }

    public Body getBody()
    {
        return body;
    }

    public void dispose()
    {
        if(getTexture()!=null)
        {
            getTexture().dispose();
        }

    }


}
