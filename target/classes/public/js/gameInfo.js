//Establish the WebSocket connection and set up event handlers
console.log("File Loading Correctly.");
var webSocket = new WebSocket("ws://localhost:1816/updates");
var slider = id("speedRange");
var output = id("speedLabel");

webSocket.onmessage = function (msg) {

updateInfo(msg);
console.log('Server: ' + msg.data);

};
webSocket.onclose = function () { console.log("WebSocket connection closed"); };
webSocket.onopen = function (event) {
    console.log("Connection established");
    webSocket.send('Ping');
};

slider.oninput = function() {
  var socketString = "SPEED:";
  switch(this.value) {
    case "1":
        output.textContent="Very Slow";
        socketString = socketString.concat("1");
        break;
    case "2":
        output.textContent="Slow";
        socketString = socketString.concat("2");
        break;
    case "3":
        output.textContent="Normal";
        socketString = socketString.concat("3");
        break;
    case "4":
        output.textContent="Fast";
        socketString = socketString.concat("4");
        break;
    case "5":
        output.textContent="Very Fast";
        socketString = socketString.concat("5");
        break;
  }

  webSocket.send(socketString);
  console.log(socketString);
};


//Update the info-panel
function updateInfo(msg) {
    var data = JSON.parse(msg.data);
    switch(data.phase) {
        case 1:

            break;
        case 2:

            break;
        case 3:
            id("cardsLeft").innerHTML = data.cardsLeft;
            id("topCard").src = "/cards/" + data.topCard;
            break;
        default:
            console.log("Error parsing phase data from web socket.");
    }

}

//Helper function for inserting HTML as the first child of an element
function insert(targetId, message) {
    id(targetId).insertAdjacentHTML("afterbegin", message);
}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}