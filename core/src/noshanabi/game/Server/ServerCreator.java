package noshanabi.game.Server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import noshanabi.game.Objects.FriendPlayer;
import noshanabi.game.Objects.Player;

/**
 * Created by 2SMILE2 on 15/10/2017.
 */

public class ServerCreator {

    private boolean createServer = false;

    //multiplayer things
    private Socket socket;
    private HashMap<String,FriendPlayer> otherPlayers;
    private World world;
    private float UPDATE_TIME = 1/60f;
    float timer;
    private Player mainPlayer;
    private String disconnectedPlayerID;


    public ServerCreator(World world, Player currentPlayer)
    {
        if(!createServer) return;
        this.world = world;
        otherPlayers = new HashMap<String, FriendPlayer>();
        mainPlayer = currentPlayer;
    }

    public void connectSocket()
    {
        if(!createServer) return;

        try
        {
            //Connect to server (server is the index.js file)
            socket = IO.socket("https://runandroidstudiolibgdx.herokuapp.com");
            //socket = IO.socket("http://localhost:5000");
            socket.connect();


        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public void configSocketEvents() {

        if(!createServer) return;

        //when we start connecting
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Connected");

            }

            //our ID
        }).on("socketID", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];

                try {
                    String id = data.getString("id");
                    Gdx.app.log("SocketIO", "My ID: " + id);

                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error getting ID");
                    e.printStackTrace();
                }

            }

            //new player connect event
        }).on("newPlayer", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handleNewPlayerEvent(args);

            }

            //when a player disconnected
        }).on("playerDisconnected", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
             handlePlayerDisconnectEvent(args);

            }

            //get all players
        }).on("getPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

				handleGetPlayerEvent(args);
            }
        }).on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                handlePlayerMovedEvent(args);
            }
        });
    }

    public void handlePlayerMovedEvent(Object... args) {



//        //Since all of the ApplicationListener methods are called on the same thread.
//        //This thread is the rendering thread on which OpenGL calls can be made
//        //we need to use a runnable thread to be able to access to rendering thread
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // post a Runnable to the rendering thread that processes the result
//                Gdx.app.postRunnable(new Runnable() {
//                    @Override
//                    public void run() {
        try {

            JSONObject data = (JSONObject) args[0];

            String id = data.getString("id");

            Double x = data.getDouble("x");
            Double y = data.getDouble("y");
            Double rotation = data.getDouble("rotation");

            FriendPlayer otherPlayer = otherPlayers.get(id);

            if (otherPlayer != null) {
                otherPlayer.setPosition(x.floatValue(), y.floatValue());
                otherPlayer.setRotation(rotation.floatValue());
            }
        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting disconnected player ID");
            e.printStackTrace();
        }
//                    }
//                });
//            }
//        }).start();

    }

    public void handleNewPlayerEvent(Object... args) {
        try {
            JSONObject data = (JSONObject) args[0];
            String id = data.getString("id");
            Gdx.app.log("SocketIO", "New Player Connected: " + id);


            otherPlayers.put(id, new FriendPlayer());

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting new player ID");
            e.printStackTrace();
        }

    }

    public void handlePlayerDisconnectEvent(Object... args)
    {

        try {
            JSONObject data = (JSONObject) args[0];

            disconnectedPlayerID = data.getString("id");

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting disconnected player ID");
            e.printStackTrace();
        }

    }

    public void handleGetPlayerEvent(Object... args) {
        try {
            JSONArray objects = (JSONArray) args[0];

            for (int i = 0; i < objects.length(); i++) {

                //get ID
                String otherPlayerID = objects.getJSONObject(i).getString("id");
                FriendPlayer otherPlayer = new FriendPlayer();
                Vector2 position = new Vector2();
                position.x = ((Double) objects.getJSONObject(i).getDouble("x")).floatValue();
                position.y = ((Double) objects.getJSONObject(i).getDouble("y")).floatValue();
                float rotation = ((Double) objects.getJSONObject(i).getDouble("rotation")).floatValue();

                otherPlayer.setPosition(position.x, position.y);
                otherPlayer.setRotation(rotation);

                otherPlayers.put(otherPlayerID, otherPlayer);

            }
        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error handling get player event");
        }

    }


    public void updateServer(float dt)
    {
        if(!createServer) return;

        if(disconnectedPlayerID !=null)
        {
            otherPlayers.remove(disconnectedPlayerID).dispose();
            disconnectedPlayerID = null;
        }

//        timer+=dt;
//        if(timer>=UPDATE_TIME)
//        {
            JSONObject data = new JSONObject();
            try
            {
                data.put("x",mainPlayer.getX());
                data.put("y",mainPlayer.getY());
                data.put("rotation",mainPlayer.getRotation());
                socket.emit("thisPlayerMoved",data);
            }
            catch (JSONException e)
            {
                Gdx.app.log("SOCKET.IO","Error sending update data");
            }
            timer = 0;
//        }
    }

    public HashMap<String,FriendPlayer> getPlayers()
    {
        return otherPlayers;
    }

    public void drawOtherPlayers(SpriteBatch batch)
    {
        if(!createServer) return;

        for(HashMap.Entry<String,FriendPlayer> entry : otherPlayers.entrySet())
        {
            if(entry.getValue().getTexture()==null)
            {
                entry.getValue().setTexture(new Texture("images/WhiteRectangle.png"));
            }

            entry.getValue().draw(batch);
        }
    }

    public void updateOtherPlayers(float dt)
    {
        if(!createServer) return;

        for(HashMap.Entry<String,FriendPlayer> entry : otherPlayers.entrySet())
        {

            if(entry.getValue().getBody()==null)
            {
                entry.getValue().defineObject(world);
            }

            entry.getValue().update(dt);
        }
    }

    public void dispose()
    {

        for(HashMap.Entry<String,FriendPlayer> entry : otherPlayers.entrySet())
        {
            entry.getValue().dispose();
        }
    }
}
