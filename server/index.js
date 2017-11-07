var app = require('express')();
var server = require('http').Server(app);
var io = require('socket.io')(server);
var players = [];

app.set('port',(process.env.PORT||5000));

server.listen(app.get('port'), function(){
	console.log("Server is now running...");
});


io.on('connection',function(socket){
	console.log("player Connected!");
    setInterval(() => io.emit('time', new Date().toTimeString()), 100);

	socket.emit('socketID',{id: socket.id });
	socket.emit('getPlayers' ,players);

    players.push(new player(socket.id,2,2,0));

    socket.broadcast.emit('newPlayer', {id: socket.id});

    socket.on('thisPlayerMoved',function(data){
        data.id = socket.id;

        socket.broadcast.emit('playerMoved',data);

        for(var i=0;i<players.length;i++)
        {
            if(players[i].id == data.id)
            {
                players[i].x = data.x;
                players[i].y = data.y;
                players[i].rotation = data.rotation;
                break;
            }
        }

    });

	socket.on('disconnect',function(){
		console.log("player Disconnected!");

        socket.broadcast.emit('playerDisconnected',{id: socket.id});

		for(var i=0;i<players.length;i++)
		{
		    if(players[i].id == socket.id)
		    {
		        players.splice(i,1);
		        break;
		    }
		}

	});


});

function player(id,x,y,rotation){
    this.id = id;
    this.x= x;
    this.y= y;
    this.rotation = rotation;
}