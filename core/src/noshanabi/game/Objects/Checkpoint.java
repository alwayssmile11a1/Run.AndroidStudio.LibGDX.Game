package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 21/11/2017.
 */

public class Checkpoint extends Sprite {

    public static final short CHECKPOINT_BIT = 16;

    private Body body;

    private World world;

    public Checkpoint(World world, float x, float y, float width, float height) {

        this.world = world;

        setPosition(x,y);

        setSize(width,height);

        //convert to PPM
        usePixelPerMeter();

        defineObject();

    }

    //this function resize this object to be used more appropriate with Box2D
    protected void usePixelPerMeter() {

        setPosition(getX() / GameManager.PPM, getY() / GameManager.PPM);
        setSize(getWidth() / GameManager.PPM, getHeight() / GameManager.PPM);

    }

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
        fDef.isSensor = true;
        fDef.filter.categoryBits = CHECKPOINT_BIT;
        fDef.filter.maskBits = Player.PLAYER_BIT;
        body.createFixture(fDef).setUserData(this);
    }


    public void update(float dt) {

        //update texture position
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);

    }


    public void dispose() {

    }


}
