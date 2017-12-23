var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

//store all the current rooms. In each room, store all the players
var rooms = {};
var maxPlayersInRoom = 4;

app.get('/', (req, res) => res.send('Welcome to Run Game Hosting!!!'));

app.set('port',(process.env.PORT||5000));

server.listen(app.get('port'), function(){
	console.log("Server is now running...");
});

io.on('connection',function(socket){

    //player connected
	console.log("Player Connected!");
    //setInterval(() => io.emit('time', new Date().toTimeString()), 100);

    //emit the id to the client's socket only
    socket.emit('connected',{id: socket.id });

    socket.emit('getRooms', rooms);

    socket.emit('getMaxPlayersInRoom', {max:maxPlayersInRoom});

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
            rooms[data.roomName] = new room({},"Waiting");

            //push socket client player to players hash table, so other new connected players know about socket client player
            rooms[data.roomName].players[socket.id] = new player(socket.id,2,2,0);


        }
        else
        {
            socket.emit('roomExisted');
        }
    });

    socket.on('joinRoom', function(data){

        var room;
        var playersCount;

        if(rooms.hasOwnProperty(data.roomName))
        {
            room = rooms[data.roomName];
            playersCount = getRoomLength(data.roomName);
        }

        if(room && room.state != "InGame" && playersCount < maxPlayersInRoom)
        {
            // store the room name in the socket session for this client
            socket.room = data.roomName;

            //send the client the this room
            socket.join(data.roomName);

            socket.emit('socketRoomJoined',{roomName: data.roomName});

            socket.broadcast.to(data.roomName).emit('roomJoined',{id: socket.id});

            //emit all other players in this room to client's socket only
            socket.emit('getOtherPlayers' ,room.players);

            //push socket client player to players hash table, so other connected players know about socket client player
            room.players[socket.id] = new player(socket.id,2,2,0);

            //players count changed
            io.sockets.emit('playersCountChanged', {roomName: socket.room, playersCount: playersCount+1});

        }
        else
        {
            socket.emit('unableToJoinRoom');
        }
    });

    socket.on('leaveRoom', function(){

        //delete the client socket player from hash table
        if(rooms.hasOwnProperty(socket.room))
        {

            delete rooms[socket.room].players[socket.id];

            socket.emit('socketRoomLeaved', {roomName: socket.room});

            socket.broadcast.to(socket.room).emit('roomLeaved',{id: socket.id});

            var playersCount = getRoomLength(socket.room);

            //if there is no one left in this room, delete it
            if(playersCount<=0)
            {
                delete rooms[socket.room];
                console.log("Room "+ socket.room + " removed");

                io.sockets.emit('roomRemoved', {roomName: socket.room});

            }
            else
            {
                io.sockets.emit('playersCountChanged', {roomName: socket.room, playersCount: playersCount});
            }


            //leave room
            socket.leave(socket.room);
        }

    });

    socket.on('joinGame',function(){

       if(rooms.hasOwnProperty(socket.room))
       {
            socket.broadcast.to(socket.room).emit('gameJoined');
            rooms[socket.room].state = "InGame";
            io.sockets.emit('roomStateChanged', {roomName: socket.room, state: "InGame"});
       }
    });

    socket.on('transitionMap', function(data){
        if(rooms.hasOwnProperty(socket.room))
        {
            socket.broadcast.to(socket.room).emit('mapTransitioned', {transitionUp: data});
        }
    });


    //when the client emits this event, emit the position, rotation,..etc.. to other players
    socket.on('socketPlayerMoved',function(data){

        if(data) //if !=null
        {
            //put the id in for the sake of quick searching
            data.id = socket.id;

            //emit to all other players but the client's socket
            socket.broadcast.to(socket.room).emit('playerMoved',data);

            //update the position and ..etc.. in hash table
            var room = rooms[socket.room];

            var socketPlayer;

            if(room)
            {
               socketPlayer = room.players[socket.id];
            }

            if(socketPlayer) //if !=null
            {
                socketPlayer.x = data.x;
                socketPlayer.y = data.y;
                socketPlayer.rotation = data.rotation;
            }
        }

    });

    //automatically turn on if a player disconnected
    socket.on('disconnect',function(){
        console.log("Player Disconnected!");

        //emit to all other players but the client's socket
        socket.broadcast.to(socket.room).emit('playerDisconnected',{id: socket.id});

        //delete the client socket player from hash table
        if(rooms.hasOwnProperty(socket.room))
        {
            delete rooms[socket.room].players[socket.id];

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
    var players = rooms[roomName].players;
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

//room struct
function room(players, state){
    this.players = players;
    this.state = state;
}