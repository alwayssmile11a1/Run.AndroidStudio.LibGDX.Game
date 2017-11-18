package noshanabi.game.Server;

/**
 * Created by 2SMILE2 on 17/11/2017.
 */

public interface ServerListener {

    public void OnSocketRoomCreated(Object... args);

    public void OnRoomExisted(Object... args);

    public void OnRoomJoined(Object... args);

    public void OnGetRooms(Object... args);

    public void OnRoomCreated(Object... args);

    public void OnRoomRemoved(Object... args);

    public void OnGetOtherPlayers(Object... args);

    public void OnSocketRoomJoined(Object... args);

    public void OnSocketRoomLeaved(Object... args);

    public void OnRoomLeaved(Object... args);

    public void OnRoomFull(Object... args);

    public void OnGameJoined(Object... args);

}
