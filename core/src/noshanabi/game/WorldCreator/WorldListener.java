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
import noshanabi.game.Objects.FriendPlayer;
import noshanabi.game.Objects.Ground;
import noshanabi.game.Objects.GroundEnemies;
import noshanabi.game.Objects.Player;

/**
 * Created by 2SMILE2 on 06/11/2017.
 */

public class WorldListener implements ContactListener {

    private boolean playerDead = false;
    private boolean playerHitCheckPoint = false;
    private boolean playerHitFinishPoint = false;

    public void update()
    {
        playerDead = false;
        playerHitCheckPoint = false;
        playerHitFinishPoint = false;
    }

    public boolean isPlayerDead() {
        return playerDead;
    }

    public boolean isPlayerHitCheckPoint() {
        return playerHitCheckPoint;
    }


    public boolean isPlayerHitFinishPoint() {
        return playerHitFinishPoint;
    }


    @Override
    public void beginContact(Contact contact) {

        Fixture fixtureA =  contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        if(fixtureA==null || fixtureB == null)
        {
            return;
        }

        beginContactPlayerHandler(fixtureA,fixtureB);
        beginContactFriendPlayerHandler(fixtureA,fixtureB);

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
                if(fixtureA.getFilterData().categoryBits == Player.PLAYER_BIT)
                {
                    Player player = ((Player)fixtureA.getUserData());
                    player.onHitCheckPoint(player.getBody().getPosition().x, player.getBody().getPosition().y);
                    playerHitCheckPoint = true;
                }
                else
                {
                    if (fixtureB.getFilterData().categoryBits == Player.PLAYER_BIT)
                    {
                        Player player = ((Player)fixtureB.getUserData());
                        player.onHitCheckPoint(player.getBody().getPosition().x, player.getBody().getPosition().y);
                        playerHitCheckPoint = true;
                    }
                }
                break;

            case Player.PLAYER_BIT* DeadGround.DEAD_BIT:
            case Player.PLAYER_BIT* GroundEnemies.ENEMY_BIT:

                Gdx.app.log("Dead","");

                if(fixtureA.getFilterData().categoryBits == Player.PLAYER_BIT)
                {
                    playerDead = true;
                    Player player = ((Player)fixtureA.getUserData());
                    player.onDead();
                }
                else
                {
                    if (fixtureB.getFilterData().categoryBits == Player.PLAYER_BIT)
                    {
                        playerDead = true;
                        Player player = ((Player)fixtureB.getUserData());
                        player.onDead();
                    }
                }
                break;

            case Player.PLAYER_BIT* FinishPoint.FINISHPOINT_BIT:
                if(fixtureA.getFilterData().categoryBits == Player.PLAYER_BIT)
                {
                    playerHitFinishPoint = true;
                    ((Player)fixtureA.getUserData()).onHitFinishPoint();
                }
                else
                {
                    if (fixtureB.getFilterData().categoryBits == Player.PLAYER_BIT)
                    {
                        playerHitFinishPoint = true;
                        ((Player)fixtureB.getUserData()).onHitFinishPoint();
                    }
                }
                break;

        }

    }

    public void beginContactFriendPlayerHandler(Fixture fixtureA, Fixture fixtureB)
    {
        switch (fixtureA.getFilterData().categoryBits*fixtureB.getFilterData().categoryBits) {
            //FRIEND PLAYER
            case FriendPlayer.FRIENDPLAYER_BIT * FinishPoint.FINISHPOINT_BIT:
                if (fixtureA.getFilterData().categoryBits ==FriendPlayer.FRIENDPLAYER_BIT) {
                    ((FriendPlayer) fixtureA.getUserData()).onHitFinishPoint();
                } else {
                    if (fixtureB.getFilterData().categoryBits == FriendPlayer.FRIENDPLAYER_BIT) {

                        ((FriendPlayer) fixtureB.getUserData()).onHitFinishPoint();
                    }
                }
                break;
            case FriendPlayer.FRIENDPLAYER_BIT * Checkpoint.CHECKPOINT_BIT:
                if (fixtureA.getFilterData().categoryBits == FriendPlayer.FRIENDPLAYER_BIT) {

                    ((FriendPlayer) fixtureA.getUserData()).onHitCheckPoint();
                } else {
                    if (fixtureB.getFilterData().categoryBits == FriendPlayer.FRIENDPLAYER_BIT) {

                        ((FriendPlayer) fixtureB.getUserData()).onHitCheckPoint();
                    }
                }
                break;
            case FriendPlayer.FRIENDPLAYER_BIT * DeadGround.DEAD_BIT:
                Gdx.app.log("FriendPlayerDead","");
                if (fixtureA.getFilterData().categoryBits == FriendPlayer.FRIENDPLAYER_BIT) {

                    ((FriendPlayer) fixtureA.getUserData()).onDead();
                } else {
                    if (fixtureB.getFilterData().categoryBits == FriendPlayer.FRIENDPLAYER_BIT) {

                        ((FriendPlayer) fixtureB.getUserData()).onDead();
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

//    private void endContactPlayerHandler(Fixture fixtureA, Fixture fixtureB)
//    {
//        System.out.println(fixtureA.getFilterData().categoryBits);
//        System.out.println(fixtureB.getFilterData().categoryBits);
//        if(fixtureA.getUserData() instanceof Player && fixtureA.getFilterData().categoryBits == Player.FOOT_BIT)
//        {
//            ((Player)fixtureA.getUserData()).isGrounded = false;
//        }
//        else
//        {
//            if (fixtureB.getUserData() instanceof Player && fixtureB.getFilterData().categoryBits == Player.FOOT_BIT)
//            {
//                ((Player)fixtureA.getUserData()).isGrounded = false;
//            }
//        }
//    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
