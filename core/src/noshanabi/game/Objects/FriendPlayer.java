package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 17/10/2017.
 */

public class FriendPlayer extends Sprite {

    public static final short FRIEND_PLAYER_BIT = 8;

    private World world; //the world that this object is belonged to

    private Body body; //the body of this object

    public FriendPlayer()
    {
        //set Texture
        //setTexture(new Texture("images/WhiteRectangle.png"));
        setColor(0.5f,0.4f,1f,1f);

        //set Position
        setPosition(200,200);

        //set Size
        setSize(30f,30f);

        usePixelPerMeter();

        //set this to rotate object in the center
        setOriginCenter();

    }

    //this function resize this object to be used more appropriate with Box2D
    protected void usePixelPerMeter() {

        setPosition(getX() / GameManager.PPM, getY() / GameManager.PPM);
        setSize(getWidth() / GameManager.PPM, getHeight() / GameManager.PPM);

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

    public void update(float dt) {

        if(body==null) return;

        //update texture position
        body.setTransform(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2,this.getRotation()/ MathUtils.radiansToDegrees);

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
