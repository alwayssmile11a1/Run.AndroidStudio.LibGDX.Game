package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import noshanabi.game.GameManager;
import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 17/10/2017.
 */

public class FriendPlayer extends Sprite {

    public static final short FRIENDPLAYER_BIT = 8;

    private World world; //the world that this object is belonged to

    private Body body; //the body of this object

    private Vector2 tempPosition;
    private float tempRotation;


//    //effect
//    private ParticleEffect checkpointEffect;
//    private ParticleEffect deadEffect;
//    private ParticleEffect finishEffect;

    public FriendPlayer()
    {
        tempPosition = new Vector2();
        tempRotation = 0;

    }

    public void create(World world, Texture texture, GameManager gameManager) {


        set(new Sprite(texture));

        setBounds(200/ Resourses.PPM,200/ Resourses.PPM, 40f/Resourses.PPM,40f/Resourses.PPM);

        //set this to rotate object in the center
        setOriginCenter();

        //defineObject(world);

//        //load effect
//        checkpointEffect = gameManager.getAssetManager().get(Resourses.ExplosionEffect1);
//
//        deadEffect = gameManager.getAssetManager().get(Resourses.ExplosionEffect2);
//
//        finishEffect = gameManager.getAssetManager().get(Resourses.ExplosionEffect3);

    }




    public void defineObject(World world) {

        this.world = world;

        //body definition
        BodyDef bDef = new BodyDef();
        bDef.position.set(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2);
        bDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bDef);
        body.setGravityScale(0f);

        //create the shape of body
        FixtureDef fDef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(this.getWidth()/2,this.getHeight()/2);
        fDef.shape = bodyShape;
        fDef.filter.categoryBits = FRIENDPLAYER_BIT;
        fDef.filter.maskBits = DeadGround.DEAD_BIT|FinishPoint.FINISHPOINT_BIT|Checkpoint.CHECKPOINT_BIT|GroundEnemies.ENEMY_BIT;
        body.createFixture(fDef).setUserData(this);

    }

//    public void onHitCheckPoint()
//    {
//        ///play effect
//        checkpointEffect.setPosition(body.getPosition().x+this.getWidth()/2,body.getPosition().y);
//        checkpointEffect.start();
//    }
//
//    public void onDead()
//    {
//        //play effect
//        deadEffect.setPosition(body.getPosition().x + this.getWidth() / 2, body.getPosition().y);
//        deadEffect.start();
//    }
//
//    public void onHitFinishPoint()
//    {
//        //play effect
//        finishEffect.setPosition(body.getPosition().x+this.getWidth()/2,body.getPosition().y);
//        finishEffect.start();
//    }

    public void setTempPosition(float x, float y)
    {
        tempPosition.set(x,y);
    }

    public void setTempRotation(float rotation)
    {
        this.tempRotation = rotation;
    }


    public void update(float dt) {

//        if(body==null) return;
//
//        //update texture position
        setRotation(tempRotation);
        setPosition(tempPosition.x,tempPosition.y);

//        body.setTransform(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2,this.getRotation()/ MathUtils.radiansToDegrees);
//
//        checkpointEffect.update(dt);
//        deadEffect.update(dt);
//        finishEffect.update(dt);
    }

    @Override
    public void draw(Batch batch) {
        super.draw(batch);
//        checkpointEffect.draw(batch);
//        finishEffect.draw(batch);
//        deadEffect.draw(batch);
    }

    public Body getBody()
    {
        return body;
    }

    public void dispose() {
//        if (body != null && world!=null) {
//            world.destroyBody(body);
//        }

    }


}
