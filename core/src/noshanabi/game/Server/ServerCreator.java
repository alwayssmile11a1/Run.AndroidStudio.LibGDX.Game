package noshanabi.game.Server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import noshanabi.game.GameManager;
import noshanabi.game.Objects.FriendPlayer;
import noshanabi.game.Objects.Player;

/**
 * Created by 2SMILE2 on 15/10/2017.
 */

public class ServerCreator {

    private boolean createServer = true;

    private GameManager gameManager;

    //multiplayer things
    private Socket socket;
    private HashMap<String,FriendPlayer> otherPlayers;
    private Player mainPlayer;
    private Array<FriendPlayer> playersToDispose;

    private Array<ServerListener> serverListeners;

    public ServerCreator(GameManager gameManager)
    {
        this.gameManager = gameManager;
        if(!createServer) return;
        otherPlayers = new HashMap<String, FriendPlayer>();
        serverListeners = new Array<ServerListener>();
        playersToDispose = new Array<FriendPlayer>();
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

        });

        //disconnect
        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Disconnected");

            }

        });

        //just for testing
        socket.on("connected", new Emitter.Listener() {
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


        });

        socket.on("getRooms", new Emitter.Listener() {
            @Override
            public void call(Object... args) {

                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnGetRooms(args);
                }
            }
        });

        socket.on("getMaxPlayersInRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnGetMaxPlayersInRoom(args);
                }
            }
        });

        //get all other players
        socket.on("getOtherPlayers", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnGetOtherPlayers(args);
                }
				handleGetOtherPlayersEvent(args);
            }
        });

        //other player moved
        socket.on("playerMoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                handlePlayerMovedEvent(args);
            }
        });

        socket.on("roomCreated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnRoomCreated(args);
                }


            }
        });

        socket.on("roomRemoved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnRoomRemoved(args);
                }

            }
        });

        socket.on("socketRoomCreated", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "You created a room ");
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnSocketRoomCreated(args);
                }
            }
        });

        socket.on("roomExisted", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "this room is existed");
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnRoomExisted(args);
                }
            }
        });

        socket.on("socketRoomJoined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnSocketRoomJoined(args);
                }

                try {
                    JSONObject data = (JSONObject) args[0];
                    String roomName = data.getString("roomName");
                    Gdx.app.log("SocketIO", "You joined room " + roomName );

                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error joining room");
                    e.printStackTrace();
                }
            }
        });

        socket.on("roomJoined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnRoomJoined(args);
                }
                handleRoomJoinedEvent(args);
            }
        });


        socket.on("socketRoomLeaved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnSocketRoomLeaved(args);
                }

                try {
                    JSONObject data = (JSONObject) args[0];
                    String roomName = data.getString("roomName");
                    Gdx.app.log("SocketIO", "You leaved room " + roomName );

                } catch (JSONException e) {
                    Gdx.app.log("SocketIO", "Error leaving room");
                    e.printStackTrace();
                }
            }
        });

        socket.on("roomLeaved", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnRoomLeaved(args);
                }

                handleRoomLeavedEvent(args);

            }
        });

        socket.on("unableToJoinRoom", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Gdx.app.log("SocketIO", "Room is unable to join");

                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnUnableToJoinRoom(args);
                }
            }
        });

        socket.on("gameJoined", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnGameJoined(args);
                }
            }
        });

        socket.on("mapTransitioned", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnMapTransitioned(args);
                }
            }
        });

        socket.on("playersCountChanged", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnPlayersCountChanged(args);
                }
            }
        });

        socket.on("roomStateChanged", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                for(ServerListener serverListener:serverListeners) {
                    serverListener.OnRoomStateChanged(args);
                }
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

    public void handleRoomJoinedEvent(Object... args) {
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

    public void handleRoomLeavedEvent(Object... args) {
        try {
            JSONObject data = (JSONObject) args[0];
            String id = data.getString("id");
            Gdx.app.log("SocketIO", "Player Leaved: " + id);
            playersToDispose.add(otherPlayers.remove(id));

        } catch (JSONException e) {
            Gdx.app.log("SocketIO", "Error getting player ID");
            e.printStackTrace();
        }

    }


    public void updateServer(float dt) {
        if (!createServer) return;

//        if (playersToRemove.size > 0) {
//            for(String playerID:playersToRemove) {
//                otherPlayers.remove(playerID).dispose();
//            }
//            playersToRemove.clear();
//        }

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

    public void setMainPlayer(Player player)
    {
        mainPlayer = player;
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

    public Socket getSocket() {
        return socket;
    }

    public void addServerListener(ServerListener serverListener) {
        serverListeners.add(serverListener);
    }

    public void dispose()
    {

        for(HashMap.Entry<String,FriendPlayer> entry : otherPlayers.entrySet())
        {
            entry.getValue().dispose();
        }

        for(FriendPlayer player:playersToDispose) {
           player.dispose();
        }
    }


}
