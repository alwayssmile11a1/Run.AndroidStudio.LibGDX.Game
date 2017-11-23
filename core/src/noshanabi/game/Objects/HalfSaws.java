package noshanabi.game.Objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 23/11/2017.
 */

public class HalfSaws {

    public static class HalfSaw extends Sprite {
        private World world; //the world that this object is belonged to
        private Body body; //the body of this object

        float stateTime = 0;

        Animation<Texture> animation;

        public HalfSaw(World world, Array<Texture> textures, float x, float y, float width, float height) {

            this.world = world;

            animation = new Animation<Texture>(0.025f, textures);

            set(new Sprite(textures.first()));

            setPosition(x, y);

            setSize(width, height);

            //this help us easily apply rotation
            setOriginCenter();

            defineObject();

        }

        protected void defineObject() {
            //body definition
            BodyDef bDef = new BodyDef();
            bDef.position.set(this.getX() + this.getWidth() / 2, this.getY());
            bDef.type = BodyDef.BodyType.KinematicBody;
            body = world.createBody(bDef);

            //create the shape of body

            CircleShape bodyShape = new CircleShape();
            bodyShape.setRadius(this.getWidth() / 2 - 0.1f);
            FixtureDef fDef = new FixtureDef();
            fDef.shape = bodyShape;
            fDef.filter.categoryBits = DeadGround.DEAD_BIT;
            fDef.filter.maskBits = Player.PLAYER_BIT;
            body.createFixture(fDef).setUserData(this);

        }


        public void update(float dt) {

            stateTime += dt;

            setTexture(animation.getKeyFrame(stateTime, true));

            //update texture position
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y);

        }

        public void setAngularVelocity(float angularVelocity) {
            animation.setFrameDuration(0.5f / angularVelocity);
        }
    }

    Array<Texture> textures;

    private Array<HalfSaw> halfSaws;

    public HalfSaws()
    {
        textures = new Array<Texture>();
        textures.add(new Texture(Resourses.SawHalf1));
        textures.add(new Texture(Resourses.SawHalf2));

        halfSaws = new Array<HalfSaw>();

    }

    public void addSaw(World world, float x, float y, float width, float height, float angularVelocity) {

        HalfSaw saw = new HalfSaw(world, textures, x, y, width, height);
        saw.setAngularVelocity(angularVelocity);
        halfSaws.add(saw);

    }

    public Array<HalfSaw> getSaws()
    {
        return halfSaws;
    }

    public void update(float dt)
    {
        for(HalfSaw saw:halfSaws)
        {
            saw.update(dt);
        }
    }

    public void draw(SpriteBatch batch)
    {
        for(HalfSaw saw:halfSaws)
        {
            saw.draw(batch);
        }
    }

    public void dispose() {

        for(Texture texture:textures)
        {
            texture.dispose();
        }

    }

}
