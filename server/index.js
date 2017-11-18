var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

//store all the current rooms. In each room, store all the players
var rooms = {};


app.set('port',(process.env.PORT||5000));

server.listen(app.get('port'), function(){
	console.log("Server is now running...");
});




io.on('connection',function(socket){

    //player connected
	console.log("Player Connected!");
    setInterval(() => io.emit('time', new Date().toTimeString()), 100);

    //emit the id to the client's socket only
    socket.emit('connected',{id: socket.id });

    socket.emit('getRooms', rooms);

    socket.on('createRoom', function(data){

        //if this room haven't existed, add to room list
        if(!rooms.hasOwnProperty(data.roomName))
        {

            // store the room name in the socket session for this client
            socket.room = data.roomName;

            socket.broadcast.emit('roomCreated', {roomName: data.roomName});

            socket.emit('socketRoomCreated', {roomName: data.roomName});

            //send the client the this room
            socket.join(data.roomName);

            socket.emit('socketRoomJoined',{roomName: data.roomName});

            //a room contain a hash table of players in that room
            rooms[data.roomName] = {};

            //push socket client player to players hash table, so other new connected players know about socket client player
            rooms[data.roomName][socket.id] = new player(socket.id,2,2,0);


        }
        else
        {
            socket.emit('roomExisted');
        }
    });

    socket.on('joinRoom', function(data){

        if(getRoomLength(data.roomName)<4)
        {
            // store the room name in the socket session for this client
            socket.room = data.roomName;

            //send the client the this room
            socket.join(data.roomName);

            socket.emit('socketRoomJoined',{roomName: data.roomName});

            socket.broadcast.to(data.roomName).emit('roomJoined',{id: socket.id});

            //emit all other players in this room to client's socket only
            socket.emit('getOtherPlayers' ,rooms[data.roomName]);

            //push socket client player to players hash table, so other connected players know about socket client player
            rooms[data.roomName][socket.id] = new player(socket.id,2,2,0);

            //emit new player event to everyone, but the client's socket
            socket.broadcast.to(data.roomName).emit('newPlayer', {id: socket.id});

        }
        else
        {
            socket.emit('roomFull');
        }
    });

    socket.on('leaveRoom', function(){

        //delete the client socket player from hash table
        if(rooms.hasOwnProperty(socket.room))
        {

            delete rooms[socket.room][socket.id];

            socket.emit('socketRoomLeaved', {roomName: socket.room});

            socket.broadcast.to(socket.room).emit('roomLeaved',{id: socket.id});

            //if there is no one left in this room, delete it
            if(getRoomLength(socket.room)<=0)
            {
                delete rooms[socket.room];
                console.log("Room "+ socket.room + " removed");

                io.sockets.emit('roomRemoved', {roomName: socket.room});

            }


            //leave room
            socket.leave(socket.room);
        }

    });

    //when the client emits this event, emit the position, rotation,..etc.. to other players
    socket.on('socketPlayerMoved',function(data){
        //put the id in for the sake of quick searching
        data.id = socket.id;

        //emit to all other players but the client's socket
        socket.broadcast.to(socket.room).emit('playerMoved',data);

        //update the position and ..etc.. in hash table
        var socketPlayer = rooms[socket.room][socket.id];
        socketPlayer.x = data.x;
        socketPlayer.y = data.y;
        socketPlayer.rotation = data.rotation;


    });

    //automatically turn on if a player disconnected
    socket.on('disconnect',function(){
        console.log("Player Disconnected!");

        //emit to all other players but the client's socket
        socket.broadcast.to(socket.room).emit('playerDisconnected',{id: socket.id});

        //delete the client socket player from hash table
        if(rooms.hasOwnProperty(socket.room))
        {
            delete rooms[socket.room][socket.id];

            socket.emit('socketRoomLeaved', {roomName: socket.room});

            socket.broadcast.to(socket.room).emit('roomLeaved',{id: socket.id});

            //if there is no one left in this room, delete it
            if(getRoomLength(socket.room)<=0)
            {
                delete rooms[socket.room];
                console.log("Room "+ socket.room + " removed");

                io.sockets.emit('roomRemoved', {roomName: socket.room});
            }

            //leave room
            socket.leave(socket.room);

        }

    });


});

function getRoomLength(roomName)
{
    //get the length of hash table
    var count = 0;
    var i;
    var players = rooms[roomName];
    for (i in players) {
        if (players.hasOwnProperty(i)) {
            count++;
        }
    }

    return count;

}

//player struct
function player(id,x,y,rotation){
    this.id = id;
    this.x= x;
    this.y= y;
    this.rotation = rotation;
}