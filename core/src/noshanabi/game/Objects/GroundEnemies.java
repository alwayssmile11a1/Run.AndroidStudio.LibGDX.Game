package noshanabi.game.Objects;

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
import com.badlogic.gdx.physics.box2d.Transform;
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
        private Array<Transform> positions; //the list of positions that this object was going through - this is for rewinding purpose

        private int reviewingIndex = 0;
        private int checkpointIndex = 0;

        float stateTime = 0;
        float playTime = 0;

        Animation<TextureRegion> animation;

        float moveDistance = 1f;
        float moveSpeed = 1f;

        private Vector2 move;

        private float initX;
        private float previousX;

        private boolean isRecording = true;

        private boolean isReviewing = false;

        private boolean playerHitFinishPoint = false;


        public GroundEnemy(World world, Array<TextureRegion> textures, float x, float y, float width, float height) {

            this.world = world;

            move = new Vector2();

            positions = new Array<Transform>();

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
            if(body.isActive() && !isReviewing) {

                playTime +=dt;

                move.set(initX + moveDistance * MathUtils.sin(playTime * moveSpeed), body.getPosition().y);

                body.setTransform(move, 0);

            }

            stateTime += dt;
            setRegion(animation.getKeyFrame(stateTime, true));

            //record position
            recordPositions();

            //update texture position
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

            if (body.getPosition().x-previousX > 0) {

                this.flip(true, false);
            }
            else
            {
                this.flip(false, false);
            }

            previousX = body.getPosition().x;

        }

        public void onPlayerDead() {
            if (!playerHitFinishPoint)
                positions.removeRange(checkpointIndex, positions.size - 1);
        }

        public void onPlayerHitCheckPoint()
        {
            checkpointIndex = positions.size;
        }

        public void onPlayerHitFinishPoint()
        {
            playerHitFinishPoint = true;
        }


        //record positions that this object was going through
        private void recordPositions() {

            if (body == null) return;

            if (isRecording && !isReviewing) {
                positions.add(new Transform(body.getPosition(), body.getAngle()));
            }

        }

        public void reviewing()
        {

            if(isReviewing==false) return;

            if (reviewingIndex < positions.size) {
                body.setTransform(positions.get(reviewingIndex).getPosition(), positions.get(reviewingIndex).getRotation());
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
            positions.clear();
            reviewingIndex = 0;
            checkpointIndex = 0;
            playerHitFinishPoint = false;
            playTime = 0;
            stateTime = 0;
        }


        public void setActive(boolean actived)
        {
            if(body.isActive()^actived) {
                body.setActive(actived);
            }

            setRecording(actived);
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
            groundEnemy = new GroundEnemy(world, textureRegions1, x, y, width, height);
        }
        else {
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

    public void onPlayerDead() {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.onPlayerDead();
        }
    }

    public void onPlayerHitCheckPoint() {
        for (GroundEnemy enemy : groundEnemies) {
            enemy.onPlayerHitCheckPoint();
        }
    }

    public void onPlayerHitFinishPoint()
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.onPlayerHitFinishPoint();
        }
    }

    public void reviewing()
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.reviewing();
        }
    }

    public void setRecording(boolean recording)
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.setRecording(recording);
        }
    }

    //auto return false after finishing reviewing
    public void setReviewing(boolean reviewing)
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.setReviewing(reviewing);
        }
    }

    public void reset()
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.reset();
        }
    }


    public void update(float dt)
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.update(dt);
        }
    }

    public void setActive(boolean actived)
    {
        for(GroundEnemy enemy: groundEnemies)
        {
            enemy.setActive(actived);
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
