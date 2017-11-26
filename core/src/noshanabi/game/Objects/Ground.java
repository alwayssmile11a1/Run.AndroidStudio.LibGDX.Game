package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by 2SMILE2 on 28/09/2017.
 */

public class Ground extends Sprite {

    protected World world; //the world that this object is belonged to
    protected Body body; //the body of this object
    public static final short GROUND_BIT = 1;

    public Ground(World world, float x, float y, float width, float height) {

        this.world = world;

        //set Texture
        //setTexture(new Texture("images/WhiteRectangle.png"));
        //setColor(0.4f,0.4f,1f,1f);
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
        FixtureDef fDef = new FixtureDef();
        PolygonShape bodyShape = new PolygonShape();
        bodyShape.setAsBox(this.getWidth()/2,this.getHeight()/2);
        fDef.shape = bodyShape;
        fDef.friction = 0.1f;
        fDef.filter.categoryBits = GROUND_BIT;
        fDef.filter.maskBits = Player.PLAYER_BIT|Player.FOOT_BIT|GroundEnemies.ENEMY_BIT;
        body.createFixture(fDef).setUserData(this);
    }


    public void update(float dt) {

        //update texture position
        setPosition(body.getPosition().x-getWidth()/2,body.getPosition().y-getHeight()/2);

        //setRotation(body.getAngle()*MathUtils.radiansToDegrees);
    }


    public void dispose() {
        if(getTexture()!=null)
        {
            getTexture().dispose();
        }
    }
}
