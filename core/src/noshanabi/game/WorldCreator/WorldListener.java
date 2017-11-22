package noshanabi.game.WorldCreator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

import noshanabi.game.Objects.Checkpoint;
import noshanabi.game.Objects.DeadGround;
import noshanabi.game.Objects.FinishPoint;
import noshanabi.game.Objects.Ground;
import noshanabi.game.Objects.Player;

/**
 * Created by 2SMILE2 on 06/11/2017.
 */

public class WorldListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {

        Fixture fixtureA =  contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if(fixtureA==null || fixtureB == null)
        {
            return;
        }

        beginContactPlayerHandler(fixtureA,fixtureB);

    }

    private void beginContactPlayerHandler(Fixture fixtureA, Fixture fixtureB)
    {
        //Gdx.app.log(fixtureA.getFilterData().categoryBits+"",fixtureB.getFilterData().categoryBits+"");

        switch (fixtureA.getFilterData().categoryBits*fixtureB.getFilterData().categoryBits)
        {
            //Player and ground hit each other
            case Player.FOOT_BIT * Ground.GROUND_BIT:

                if(fixtureA.getFilterData().categoryBits == Player.FOOT_BIT)
                {
                    ((Player)fixtureA.getUserData()).isGrounded = true;
                }
                else
                {
                    if (fixtureB.getFilterData().categoryBits == Player.FOOT_BIT)
                    {
                        ((Player)fixtureB.getUserData()).isGrounded = true;
                    }
                }
                break;

            case Player.PLAYER_BIT * Checkpoint.CHECKPOINT_BIT:

                Gdx.app.log("Checkpoint","");

                if(fixtureA.getFilterData().categoryBits == Player.PLAYER_BIT)
                {
                    Player player = ((Player)fixtureA.getUserData());
                    player.setCheckPoint(player.getBody().getPosition().x, player.getBody().getPosition().y);
                }
                else
                {
                    if (fixtureB.getFilterData().categoryBits == Player.PLAYER_BIT)
                    {
                        Player player = ((Player)fixtureB.getUserData());
                        player.setCheckPoint(player.getBody().getPosition().x, player.getBody().getPosition().y);
                    }
                }
                break;

            case Player.PLAYER_BIT* DeadGround.DEADGROUND_BIT:

                Gdx.app.log("Dead","");
                if(fixtureA.getFilterData().categoryBits == Player.PLAYER_BIT)
                {
                    ((Player)fixtureA.getUserData()).returnToCheckPoint();
                }
                else
                {
                    if (fixtureB.getFilterData().categoryBits == Player.PLAYER_BIT)
                    {
                        ((Player)fixtureB.getUserData()).returnToCheckPoint();
                    }
                }
                break;

            case Player.PLAYER_BIT* FinishPoint.FINISHPOINT_BIT:

                if(fixtureA.getFilterData().categoryBits == Player.PLAYER_BIT)
                {
                    ((Player)fixtureA.getUserData()).OnHitFinishPoint();
                }
                else
                {
                    if (fixtureB.getFilterData().categoryBits == Player.PLAYER_BIT)
                    {
                        ((Player)fixtureB.getUserData()).OnHitFinishPoint();
                    }
                }
                break;

        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixtureA =  contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if(fixtureA==null || fixtureB == null)
        {
            return;
        }

        //endContactPlayerHandler(fixtureA,fixtureB);
    }

    private void endContactPlayerHandler(Fixture fixtureA, Fixture fixtureB)
    {
        System.out.println(fixtureA.getFilterData().categoryBits);
        System.out.println(fixtureB.getFilterData().categoryBits);
        if(fixtureA.getUserData() instanceof Player && fixtureA.getFilterData().categoryBits == Player.FOOT_BIT)
        {
            ((Player)fixtureA.getUserData()).isGrounded = false;
        }
        else
        {
            if (fixtureB.getUserData() instanceof Player && fixtureB.getFilterData().categoryBits == Player.FOOT_BIT)
            {
                ((Player)fixtureA.getUserData()).isGrounded = false;
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
