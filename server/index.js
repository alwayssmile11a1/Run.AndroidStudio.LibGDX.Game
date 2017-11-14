var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

var players = {};

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

    socket.on('roomCreating', function(data){

        //if this room haven't existed, add to room list
        if(!rooms.hasOwnProperty(data.roomName))
        {
            socket.emit('roomCreated');

            // store the room name in the socket session for this client
            socket.room = data.roomName;

            //send the client the this room
            socket.join(data.roomName);

            socket.emit('roomJoined',{roomName: data.roomName});

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

        // store the room name in the socket session for this client
        socket.room = data.roomName;

        //send the client the this room
        socket.join(data.roomName);

        socket.emit('roomJoined',{roomName: data.roomName});

        //emit all other players in this room to client's socket only
        socket.emit('getOtherPlayers' ,rooms[data.roomName]);

        //push socket client player to players hash table, so other connected players know about socket client player
        rooms[data.roomName][socket.id] = new player(socket.id,2,2,0);

        //emit new player event to everyone, but the client's socket
        socket.broadcast.to(data.roomName).emit('newPlayer', {id: socket.id});

    });


//	//emit all other players to client's socket only
//	socket.emit('getOtherPlayers' ,players);

//    //push a new player to players hash table, so other new connected players can get all the connected players
//    players[socket.id] = new player(socket.id,2,2,0);

//    //emit new player event to everyone, but the client's socket
//    socket.broadcast.emit('newPlayer', {id: socket.id});

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

            //if there is no one left in this room, delete it
            if(rooms[socket.room].length<=0)
            {
                delete rooms[socket.room];
            }

        }

        //leave room
        socket.leave(socket.room);

    });


});

//player struct
function player(id,x,y,rotation){
    this.id = id;
    this.x= x;
    this.y= y;
    this.rotation = rotation;
}