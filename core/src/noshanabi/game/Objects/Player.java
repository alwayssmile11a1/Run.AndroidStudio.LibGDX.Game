package noshanabi.game.Objects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 27/09/2017.
 */

public class Player extends Sprite {

    public static final short PLAYER_BIT = 2;
    public static final short FOOT_BIT = 4;

    private World world; //the world that this object is belonged to
    private Body body; //the body of this object
    private Array<Transform> positions; //the list of positions that this object was going through - this is for rewinding purpose
    private Array<Vector2> velocities; //the list of velocities that this object was going through - this is for rewinding purpose
//    private boolean isRewinding; //is rewinding time or not?
//    private float maximumRewindingTime = 5f;

    private int reviewingIndex =0;
    private int checkpointIndex = 0;

    private boolean isRecording = true;
    private boolean isReviewing = false;

    private Body foot;
    public boolean isGrounded = false;
    public boolean isDoubleJumped = false;

    private Vector2 checkPoint;
    private Vector2 instantiatePoint;
    private boolean returnToCheckPoint = false;
    private boolean hitFinishPoint = false;




    public Player(World world, float x, float y) {

        this.world = world;
        positions = new Array<Transform>();
        velocities = new Array<Vector2>();
//        isRewinding = false;

        Texture playerTexture= new Texture(Resourses.Player1);
        set(new Sprite(playerTexture));
        //setColor(0f,0.4f,1f,1f);

        checkPoint = new Vector2();
        instantiatePoint = new Vector2();

        //set Position
        setPosition(x,y);

        //set Size
        setSize(40f/ Resourses.PPM,40f/Resourses.PPM);

        //set this to rotate object in the center
        setOriginCenter();

        //defineObject
        defineObject();
    }

    public void returnToCheckPoint()
    {
        returnToCheckPoint = true;

        if(!hitFinishPoint) {
            positions.removeRange(checkpointIndex, positions.size - 1);
            velocities.removeRange(checkpointIndex, velocities.size - 1);
        }
    }

    public void setCheckPoint(float x, float y)
    {
        checkPoint.set(x, y);
        checkpointIndex = positions.size;
    }


    public void setInstantiatePoint(float x, float y)
    {
        instantiatePoint.set(x, y);
        checkPoint.set(x, y);
    }

    public void OnHitFinishPoint()
    {
        this.hitFinishPoint = true;
    }

    public boolean isHitFinishPoint()
    {
        return hitFinishPoint;
    }

    private void defineObject() {
        //Create a main body
        BodyDef bDef = new BodyDef();
        bDef.position.set(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2);
        bDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bDef);

        //create the shape of body
        FixtureDef fDef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(this.getWidth()/2,this.getHeight()/2);
        fDef.shape = bodyShape;
        fDef.filter.categoryBits = PLAYER_BIT;
        fDef.filter.maskBits = Ground.GROUND_BIT| Checkpoint.CHECKPOINT_BIT|DeadGround.DEAD_BIT |FinishPoint.FINISHPOINT_BIT;
        fDef.density = 2f;
        fDef.friction = 0.1f;
        body.createFixture(fDef).setUserData(this);


        //Create foot
        bDef.position.set(this.getX()+this.getWidth()/2,this.getY()+this.getHeight());
        bDef.type = BodyDef.BodyType.DynamicBody;
        foot = world.createBody(bDef);
        foot.setGravityScale(0);

        bodyShape.setAsBox(this.getWidth()/2-0.05f,0.08f);
        fDef.shape = bodyShape;
        fDef.isSensor = true;
        fDef.filter.categoryBits = FOOT_BIT;
        fDef.filter.maskBits = Ground.GROUND_BIT;
        foot.createFixture(fDef).setUserData(this);
    }

    public void update(float dt) {

        if(returnToCheckPoint)
        {
            body.setTransform(checkPoint,0);
            setRotation(0);
            returnToCheckPoint = false;
            return;
        }

        recordPositions(dt);

        //update texture position
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        //rotate box2d
        body.setAngularVelocity(-body.getLinearVelocity().x * 3);

        foot.setTransform(new Vector2(body.getPosition().x,body.getPosition().y-this.getHeight()/2),0);

        //rotate the texture corresponding to the body
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

    }

    //record positions that this object was going through
    private void recordPositions(float dt) {

        if (body == null) return;

//        if(isRewinding == false) {
//            //
//            if(positions.size > (int)(maximumRewindingTime/dt))
//            {
//                positions.removeIndex(positions.size-1);
//                velocities.removeIndex(velocities.size-1);
//            }
//            positions.insert(0, new Transform(body.getPosition(), body.getAngle()));
//            velocities.insert(0, new Vector2(body.getLinearVelocity()));
//        }

        if (isRecording && !isReviewing) {
            positions.add(new Transform(body.getPosition(), body.getAngle()));
            velocities.add(new Vector2(body.getLinearVelocity()));
        }


    }

//    //start rewinding - this fuction will automatically stop rewinding when it reaches maximum rewinding time
//    public void startRewinding () {
//
//        if(body==null) return;
//
//        if (positions.size >0) {
//            isRewinding = true;
//            //body.setType(BodyDef.BodyType.KinematicBody);
//            body.setTransform(positions.first().getPosition(), positions.first().getRotation());
//            body.setLinearVelocity(velocities.first());
//            positions.removeIndex(0);
//            velocities.removeIndex(0);
//        }
//        else
//        {
//            stopRewinding();
//        }
//    }
//
//    //stop rewinding time
//    public void stopRewinding () {
//        isRewinding = false;
//        //body.setType(BodyDef.BodyType.DynamicBody);
//    }


    public void reviewing()
    {

        if(isReviewing==false) return;

        if (reviewingIndex < positions.size) {
            //body.setType(BodyDef.BodyType.KinematicBody);
            body.setTransform(positions.get(reviewingIndex).getPosition(), positions.get(reviewingIndex).getRotation());
            body.setLinearVelocity(velocities.get(reviewingIndex));
            reviewingIndex++;
        }
        else
        {
            isReviewing = false;
            reviewingIndex = 0;
        }
    }

    public void startRecording()
    {
        isRecording = true;
    }

    public void stopRecording()
    {
        isRecording = false;
    }

    public boolean isReviewing()
    {
        return isReviewing;
    }

    //auto return false after finishing reviewing
    public void setReviewing(boolean reviewing)
    {
        isReviewing = reviewing;
    }

    public void setReviewingIndex(int index)
    {
        reviewingIndex = index;
    }

    public void reset()
    {
        isReviewing = false;
        hitFinishPoint = false;
        positions.clear();
        velocities.clear();
        reviewingIndex = 0;
        checkpointIndex = 0;
        checkPoint.set(instantiatePoint.x,instantiatePoint.y);
        body.setTransform(instantiatePoint,0);
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
