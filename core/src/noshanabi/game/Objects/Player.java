package noshanabi.game.Objects;


import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import noshanabi.game.GameManager;

/**
 * Created by 2SMILE2 on 27/09/2017.
 */

public class Player extends Object {

    public static final short PLAYER_BIT = 2;
    public static final short FOOT_BIT = 4;

    private Body foot;
    public boolean isGrounded = false;
    public boolean isDoubleJumped = false;

    private Vector2 checkPoint;
    private boolean returnToCheckPoint = false;

    public Player(World world, float x, float y) {
        super(world);

        Texture playerTexture= new Texture("images/alienYellow_square.png");
        set(new Sprite(playerTexture));
        //setColor(0f,0.4f,1f,1f);

        checkPoint = new Vector2();

        //set Position
        setPosition(x,y);

        //set Size
        setSize(40f/ GameManager.PPM,40f/GameManager.PPM);

        //usePixelPerMeter();

        //set this to rotate object in the center
        setOriginCenter();

        //defineObject
        defineObject();
    }

    public void returnToCheckPoint()
    {
        returnToCheckPoint = true;
    }

    public void setCheckPoint(float x, float y)
    {
        checkPoint.set(x, y);
    }

    @Override
    protected void defineObject() {
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
        fDef.filter.maskBits = Ground.GROUND_BIT|Checkpoint.CHECKPOINT_BIT|DeadGround.DEADGROUND_BIT;
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
    public void update(float dt) {

        super.update(dt);

        if(returnToCheckPoint)
        {
            body.setTransform(checkPoint,0);
            setRotation(0);
            returnToCheckPoint = false;
            return;
        }

        //update texture position
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        //rotate box2d
        body.setAngularVelocity(-body.getLinearVelocity().x * 3);

        foot.setTransform(new Vector2(body.getPosition().x,body.getPosition().y-this.getHeight()/2),0);

        //rotate the texture corresponding to the body
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

    }

    @Override
    public void dispose() {
        super.dispose();
    }
}
