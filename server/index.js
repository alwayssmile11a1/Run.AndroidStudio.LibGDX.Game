var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);

//store all players
var players = {};

app.set('port',(process.env.PORT||5000));

server.listen(app.get('port'), function(){
	console.log("Server is now running...");
});

var rooms = [];



io.on('connection',function(socket){

    //player connected
	console.log("Player Connected!");
    setInterval(() => io.emit('time', new Date().toTimeString()), 100);

    //automatically turn on if a player disconnected
    socket.on('disconnect',function(){
        console.log("Player Disconnected!");

        //emit to all other players but the client's socket
        socket.broadcast.emit('playerDisconnected',{id: socket.id});

        //delete the client socket player from hash table
        delete players[socket.id];

    });

    //emit the id to the client's socket only
	socket.emit('socketID',{id: socket.id });

	//emit all other players to client's socket only
	socket.emit('getOtherPlayers' ,players);

    //push a new player to players hash table, so other new connected players can get all the connected players
    players[socket.id] = new player(socket.id,2,2,0);

    //emit new player event to everyone, but the client's socket
    socket.broadcast.emit('newPlayer', {id: socket.id});

    //when the client emits this event, emit the position, rotation,..etc.. to other players
    socket.on('socketPlayerMoved',function(data){
        //put the id in for the sake of quick searching
        data.id = socket.id;

        //emit to all other players but the client's socket
        socket.broadcast.emit('playerMoved',data);

        //update the position and ..etc.. in hash table
        var socketPlayer = players[socket.id];
        socketPlayer.x = data.x;
        socketPlayer.y = data.y;
        socketPlayer.rotation = data.rotation;


    });

    socket.on('roomCreated', function(data))
    {
        if(rooms.indexOf(data.roomName) >= 0)
        {
            rooms.push(data.roomName);
        }
        else
        {
            socket.emit('roomExisted');
        }
    });






});

//player struct
function player(id,x,y,rotation){
    this.id = id;
    this.x= x;
    this.y= y;
    this.rotation = rotation;
}