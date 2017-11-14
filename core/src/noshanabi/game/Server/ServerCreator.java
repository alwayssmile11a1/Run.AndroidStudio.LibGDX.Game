package noshanabi.game.Server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import noshanabi.game.Objects.FriendPlayer;
import noshanabi.game.Objects.Player;

/**
 * Created by 2SMILE2 on 15/10/2017.
 */

public class ServerCreator {

    private boolean createServer = true;

    //multiplayer things
    private Socket socket;
    private HashMap<String,FriendPlayer> otherPlayers;
    private Player mainPlayer;
    private String disconnectedPlayerID;


    public ServerCreator()
    {
        if(!createServer) return;
        otherPlayers = new HashMap<String, FriendPlayer>();

    }

    public void SetMainPlayer(Player mainPlayer)
    {
        this.mainPlayer = mainPlayer;
    }

    public void connectSocket()
    {
        if(!createServer) return;

        try
        {
            //Connect to server (server is the index.js file, kind of ..)
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
        }).on("getOtherPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

				handleGetOtherPlayersEvent(args);
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

    public void handleGetOtherPlayersEvent(Object... args) {
        try {

            //clear
            otherPlayers.clear();

            JSONObject objects = (JSONObject) args[0];

            //get all the keys
            Iterator<String> iter = objects.keys();

            //loop through all
            while (iter.hasNext()) {
                String otherPlayerID = iter.next();

                //get value
                JSONObject value = new JSONObject(objects.get(otherPlayerID).toString());

                //create a new friend player
                FriendPlayer otherPlayer = new FriendPlayer();
                Vector2 position = new Vector2();

                //get position
                position.x = ((Double) value.getDouble("x")).floatValue();
                position.y = ((Double) value.getDouble("y")).floatValue();
                float rotation = ((Double) value.getDouble("rotation")).floatValue();

                otherPlayer.setPosition(position.x, position.y);
                otherPlayer.setRotation(rotation);

                otherPlayers.put(otherPlayerID, otherPlayer);

            }

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error handling get player event");
        }

    }


    public void updateServer(float dt) {
        if (!createServer) return;

        if (disconnectedPlayerID != null) {
            otherPlayers.remove(disconnectedPlayerID).dispose();
            disconnectedPlayerID = null;
        }

        if(mainPlayer==null) return;

        //send the position of main player to other players
        JSONObject data = new JSONObject();
        try {
            data.put("x", mainPlayer.getX());
            data.put("y", mainPlayer.getY());
            data.put("rotation", mainPlayer.getRotation());
            socket.emit("socketPlayerMoved", data);
        } catch (JSONException e) {
            Gdx.app.log("SOCKET.IO", "Error sending update data");
        }

//        //update body position of other players
//        for(HashMap.Entry<String,FriendPlayer> entry : otherPlayers.entrySet())
//        {
//
//            if(entry.getValue().getBody()==null)
//            {
//                entry.getValue().defineObject(world);
//            }
//
//            entry.getValue().update(dt);
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


    public void dispose()
    {

        for(HashMap.Entry<String,FriendPlayer> entry : otherPlayers.entrySet())
        {
            entry.getValue().dispose();
        }
    }
}
