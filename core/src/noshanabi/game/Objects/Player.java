package noshanabi.game.Objects;


import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
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

import noshanabi.game.GameManager;
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
    private Vector2 deadPoint;

    private boolean returnToCheckPoint = false;
    private boolean hitFinishPoint = false;

    //sound
    private Sound explosionSound;
    private Sound checkPointSound;

    //effect
    private ParticleEffect checkpointEffect;
    private ParticleEffect deadEffect;
    private ParticleEffect finishEffect;


    public Player(GameManager gameManager, World world, float x, float y) {

        this.world = world;
        positions = new Array<Transform>();
        velocities = new Array<Vector2>();
//        isRewinding = false;

        //Texture playerTexture= new Texture(Resourses.Player1);
        set(new Sprite(gameManager.getCurrentCharacter()));
        //setColor(0f,0.4f,1f,1f);

        checkPoint = new Vector2();
        instantiatePoint = new Vector2();
        deadPoint = new Vector2();

        //set Position
        setPosition(x,y);

        //set Size
        setSize(40f/ Resourses.PPM,40f/Resourses.PPM);

        //set this to rotate object in the center
        setOriginCenter();

        //defineObject
        defineObject();


        //load Sound
        explosionSound = gameManager.getAssetManager().get(Resourses.ExplosionSound);
        checkPointSound = gameManager.getAssetManager().get(Resourses.CheckpointSound);

        //load effect
        checkpointEffect = gameManager.getAssetManager().get(Resourses.ExplosionEffect1);
        //checkpointEffect.scaleEffect(1/Resourses.PPM);
        checkpointEffect.setPosition(-10,-10);
        checkpointEffect.start();

        deadEffect = gameManager.getAssetManager().get(Resourses.ExplosionEffect2);
        //deadEffect.scaleEffect(1/Resourses.PPM);
        deadEffect.setPosition(-10,-10);
        deadEffect.start();

        finishEffect = gameManager.getAssetManager().get(Resourses.ExplosionEffect3);
        //finishEffect.scaleEffect(1/Resourses.PPM);
        finishEffect.setPosition(-10,-10);
        finishEffect.start();
    }

    public void onDead()
    {
        deadPoint.set(body.getPosition().x,body.getPosition().y);

        if(!hitFinishPoint) {
            returnToCheckPoint = true;
            positions.removeRange(checkpointIndex, positions.size - 1);
            velocities.removeRange(checkpointIndex, velocities.size - 1);


            //play sound
            explosionSound.play(0.3f);

            //play effect
            deadEffect.setPosition(body.getPosition().x + this.getWidth() / 2, body.getPosition().y);
            deadEffect.start();
        }
    }

    public void onHitCheckPoint(float x, float y)
    {
        checkPoint.set(x, y);
        checkpointIndex = positions.size-1;

        //play sound
        checkPointSound.play(1.0f);

        //play effect
        checkpointEffect.setPosition(body.getPosition().x+this.getWidth()/2,body.getPosition().y);
        checkpointEffect.start();
    }


    public void setInstantiatePoint(float x, float y)
    {
        instantiatePoint.set(x, y);
        checkPoint.set(x, y);
    }

    public void onHitFinishPoint()
    {
        this.hitFinishPoint = true;

        //play sound
        long id = checkPointSound.play(1.0f);
        checkPointSound.setPitch(id,2f);

        //play effect
        finishEffect.setPosition(body.getPosition().x+this.getWidth()/2,body.getPosition().y);
        finishEffect.start();

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
        fDef.filter.maskBits = Ground.GROUND_BIT| Checkpoint.CHECKPOINT_BIT|DeadGround.DEAD_BIT |FinishPoint.FINISHPOINT_BIT|GroundEnemies.ENEMY_BIT;
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

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
        checkpointEffect.draw(batch);
        finishEffect.draw(batch);
        deadEffect.draw(batch);
    }

    public void update(float dt) {


        if (returnToCheckPoint) {
            body.setTransform(checkPoint, 0);
            setRotation(0);
            returnToCheckPoint = false;

        }

        recordPositions();

        //update texture position
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        //rotate box2d
        body.setAngularVelocity(-body.getLinearVelocity().x * 3);

        foot.setTransform(new Vector2(body.getPosition().x, body.getPosition().y - this.getHeight() / 2), 0);

        //rotate the texture corresponding to the body
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        //update effect
        checkpointEffect.update(dt);
        finishEffect.update(dt);
        deadEffect.update(dt);
    }

    public Vector2 getCheckpoint()
    {
        return checkPoint;
    }

    public Vector2 getDeadPoint()
    {
        return deadPoint;
    }

    public void setActive(boolean actived)
    {
        if(body.isActive() ^ actived) {
            body.setActive(actived);
        }
        setRecording(actived);
    }


    //record positions that this object was going through
    private void recordPositions() {

        if (body == null) return;

        if (isRecording && !isReviewing) {
            positions.add(new Transform(body.getPosition(), body.getAngle()));
            velocities.add(new Vector2(body.getLinearVelocity()));
        }


    }



    public void reviewing()
    {

        if(isReviewing==false) return;

        if (reviewingIndex < positions.size) {
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

    public void setRecording(boolean recording)
    {
        isRecording = recording;
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
//        if(getTexture()!=null)
//        {
//            getTexture().dispose();
//        }

        //checkpointEffect.reset();
        //deadEffect.reset();
        //finishEffect.reset();
    }

}
