package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by 2SMILE2 on 23/11/2017.
 */

public class Saw extends Sprite {

    private World world; //the world that this object is belonged to
    private Body body; //the body of this object

    private float angularVelocity = 2f;

    public Saw(World world, Texture texture , float x, float y, float width, float height) {

        this.world = world;

        set(new Sprite(texture));

        setPosition(x,y);

        setSize(width,height);

        //this help us easily apply rotation
        setOriginCenter();

        defineObject();

    }

    protected void defineObject() {
        //body definition
        BodyDef bDef = new BodyDef();
        bDef.position.set(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2);
        bDef.type = BodyDef.BodyType.KinematicBody;
        body = world.createBody(bDef);

        //create the shape of body
        CircleShape bodyShape = new CircleShape();
        bodyShape.setRadius(this.getWidth()/2-0.1f);
        FixtureDef fDef = new FixtureDef();
        fDef.shape = bodyShape;
        fDef.filter.categoryBits = DeadGround.DEAD_BIT;
        fDef.filter.maskBits = Player.PLAYER_BIT;
        body.createFixture(fDef).setUserData(this);
    }


    public void update(float dt) {

        //update texture position
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);

        body.setAngularVelocity(angularVelocity);

        setRotation(body.getAngle()* MathUtils.radiansToDegrees);
    }

    public void setAngularVelocity(float angularVelocity)
    {
        this.angularVelocity = angularVelocity;
    }


    public void dispose() {
        if(getTexture()!=null)
        {
            getTexture().dispose();
        }
    }

}
