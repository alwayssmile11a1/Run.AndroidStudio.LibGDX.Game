package noshanabi.game.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import noshanabi.game.Resourses;

/**
 * Created by 2SMILE2 on 23/11/2017.
 */

public class GroundEnemies {

    public static final short ENEMY_BIT = 128;

    public static class GroundEnemy extends Sprite {
        private World world; //the world that this object is belonged to
        private Body body; //the body of this object

        float stateTime = 0;

        Animation<TextureRegion> animation;

        float moveDistance = 1f;
        float moveSpeed = 1f;

        private Vector2 move;

        private float initX;
        private float previousX;



        public GroundEnemy(World world, Array<TextureRegion> textures, float x, float y, float width, float height) {

            this.world = world;

            move = new Vector2();

            animation = new Animation<TextureRegion>(0.3f, textures);

            set(new Sprite(textures.first()));

            setPosition(x, y);

            setSize(width, height);

            //this help us easily apply rotation
            setOriginCenter();

            defineObject();

            initX = body.getPosition().x;
            previousX = initX;
        }

        protected void defineObject() {
            //body definition
            BodyDef bDef = new BodyDef();
            bDef.position.set(this.getX()+this.getWidth()/2,this.getY()+this.getHeight()/2);
            bDef.type = BodyDef.BodyType.DynamicBody;
            body = world.createBody(bDef);

            //create the shape of body
            FixtureDef fDef = new FixtureDef();
            PolygonShape bodyShape = new PolygonShape();
            bodyShape.setAsBox(this.getWidth()/2,this.getHeight()/2);
            fDef.shape = bodyShape;
            fDef.filter.categoryBits = ENEMY_BIT;
            fDef.filter.maskBits = Player.PLAYER_BIT|Ground.GROUND_BIT;
            body.createFixture(fDef).setUserData(this);

        }



        public void update(float dt) {

            stateTime += dt;

            setRegion(animation.getKeyFrame(stateTime, true));

            move.set(initX + moveDistance * MathUtils.sin(stateTime * moveSpeed), body.getPosition().y);

            body.setTransform(move, 0);

            //update texture position
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

            Gdx.app.log("",MathUtils.sin(stateTime * moveSpeed)+"");

            if (move.x-previousX > 0) {

                this.flip(true, false);
            }
            else
            {
                this.flip(false, false);
            }


            previousX = move.x;

        }

        public void setMoveDistance(float distance)
        {
            moveDistance = distance;
        }

        public void setMoveSpeed(float speed)
        {
            moveSpeed = speed;
        }

    }

    private Texture texture;
    Array<TextureRegion> textureRegions1;
    Array<TextureRegion> textureRegions2;

    private Array<GroundEnemy> groundEnemies;


    public GroundEnemies()
    {
        texture = new Texture(Resourses.EnemiesSpriteSheet);

        textureRegions1 = new Array<TextureRegion>();
        textureRegions1.add(new TextureRegion(texture,9,836,110,73));
        textureRegions1.add(new TextureRegion(texture,9,966,110,73));

        textureRegions2 = new Array<TextureRegion>();
        textureRegions2.add(new TextureRegion(texture,270,326,110,63));
        textureRegions2.add(new TextureRegion(texture,270,586,110,63));

        groundEnemies = new Array<GroundEnemy>();

    }

    public void addEnemy(World world, String name, float x, float y, float width, float height, float moveDistance, float moveSpeed) {

        GroundEnemy groundEnemy;

        if(name.equals("snail"))
        {
            Gdx.app.log("snail","");
            groundEnemy = new GroundEnemy(world, textureRegions1, x, y, width, height);
        }
        else {
            Gdx.app.log("not snail","");
            groundEnemy = new GroundEnemy(world, textureRegions2, x, y, width, height);
        }

        groundEnemy.setMoveDistance(moveDistance);
        groundEnemy.setMoveSpeed(moveSpeed);
        groundEnemies.add(groundEnemy);

    }

    public Array<GroundEnemy> getEnemy()
    {
        return groundEnemies;
    }

    public void update(float dt)
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.update(dt);
        }
    }

    public void draw(SpriteBatch batch)
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.draw(batch);
        }
    }

    public void dispose() {

        texture.dispose();

    }



}
