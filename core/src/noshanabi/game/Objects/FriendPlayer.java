package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 17/10/2017.
 */

public class FriendPlayer extends Sprite {

    public static final short FRIEND_PLAYER_BIT = 8;

    private World world; //the world that this object is belonged to

    private Body body; //the body of this object

    private Vector2 tempPosition;
    private float tempRotation;

    public FriendPlayer()
    {

        tempPosition = new Vector2();
        tempRotation = 0;


    }

    public void SetTexture() {

        Texture texture = new Texture(Resourses.Player1);

        set(new Sprite(texture));

        setBounds(200/ Resourses.PPM,200/ Resourses.PPM, 40f/Resourses.PPM,40f/Resourses.PPM);

        //set this to rotate object in the center
        setOriginCenter();

    }




    public void defineObject(World world) {

        this.world = world;

        //body definition
        BodyDef bDef = new BodyDef();
        bDef.position.set(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2);
        bDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bDef);
        body.setGravityScale(0f);

        //create the shape of body
        FixtureDef fDef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(this.getWidth()/2,this.getHeight()/2);
        fDef.shape = bodyShape;
        fDef.filter.categoryBits = FRIEND_PLAYER_BIT;
        fDef.filter.maskBits = FRIEND_PLAYER_BIT|Player.PLAYER_BIT|Player.FOOT_BIT;
        fDef.density = 2f;
        body.createFixture(fDef).setUserData(this);

    }

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
//        body.setTransform(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2,this.getRotation()/ MathUtils.radiansToDegrees);

        setRotation(tempRotation);
        setPosition(tempPosition.x,tempPosition.y);


    }

    public Body getBody()
    {
        return body;
    }

    public void dispose() {
        if (this.getTexture() != null) {
            this.getTexture().dispose();
        }
        if (body != null)
            world.destroyBody(body);

    }


}
