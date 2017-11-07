package noshanabi.game.WorldCreator;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

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

        if(fixtureA.getUserData() instanceof Player && fixtureA.getFilterData().categoryBits == Player.FOOT)
        {
            ((Player)fixtureA.getUserData()).isGrounded = true;
        }
        else
        {
            if (fixtureB.getUserData() instanceof Player && fixtureB.getFilterData().categoryBits == Player.FOOT)
            {
                ((Player)fixtureA.getUserData()).isGrounded = true;
            }
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
        if(fixtureA.getUserData() instanceof Player && fixtureA.getFilterData().categoryBits == Player.FOOT)
        {
            ((Player)fixtureA.getUserData()).isGrounded = false;
        }
        else
        {
            if (fixtureB.getUserData() instanceof Player && fixtureB.getFilterData().categoryBits == Player.FOOT)
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
