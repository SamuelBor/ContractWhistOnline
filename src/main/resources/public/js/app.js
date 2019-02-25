//Establish the WebSocket connection and set up event handlers
console.log("File Loading Correctly.");
var webSocket = new WebSocket("ws://localhost:1816/updates");
var slider = id("speedRange");
var output = id("speedLabel");
var newPlayer = "player1";
var winner = "player1";
var chosenCard;
var p1AgentType = 0;
var p2AgentType = 0;
var p3AgentType = 0;

webSocket.onmessage = function (msg) {
    console.log('Server: ' + msg.data);
    parseMsg(msg);
};

webSocket.onclose = function () { console.log("WebSocket Connection Closed"); };

webSocket.onopen = function () {
    console.log("Connection established");
    webSocket.send('Connection established');
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
/**
 * @param msg               Raw message passed from the back end
 * @param msg.data          Data part of the message passed from the back end
 * @param data              Data passed from the back end to be reflected to the front end.
 * @param data.phase        Describes the type of message that is being passed
 * @param data.trump        Shows what the current trump is to display in the information panel
 * @param data.playerID     Contains the playerID to show which of the three players a change should be applied to
 * @param data.cardsLeft    The number of cards left to be displayed in the information panel
 * @param data.prediction   The prediction passed from the backend to be displayed in the info panel
 * @param data.score        The player's current score
 * @param data.current      The current number of hands the agent has won
 * @param data.winnerID     The ID of the winning agent of a round
 * @param data.topCard      The card most recently played, shown at the top of the screen
 * @param data.cardID       The Id of the select
 */

function parseMsg(msg) {
    var data = JSON.parse(msg.data);
    switch(data.phase) {
        case 1:
            id("trumpLabel").innerHTML = data.trump;
            switch(data.trump){
                case "♣":
                    id("trumpLabel").style.color = "black";
                    break;
                case "♠":
                    id("trumpLabel").style.color = "black";
                    break;
                case "♥":
                    id("trumpLabel").style.color = "red";
                    break;
                case "♦":
                    id("trumpLabel").style.color = "red";
                    break;
            }

            id("cardsLeft").innerHTML = data.cardsLeft;
            id(winner).style.borderColor='#346029';
            newPlayer = "player" + data.playerID;
            id(newPlayer).style.borderColor='#552960';
            break;
        case 2:
            var idString = "player" + data.playerID + "card" + data.cardID;
            chosenCard = id(idString);
            chosenCard.classList.add("selectedCard");
            break;
        case 3:
            chosenCard.classList.remove("selectedCard");

            id("topCard").src = "/cards/" + data.topCard;
            id("topCard").style.display = 'block';

            var hand = data.hand;
            handLayout(hand.length, hand, data);
            id(newPlayer).style.borderColor='#346029';
            break;
        case 4:
            id("topCard").style.display = 'none';
            winner = "player" + data.winnerID;
            id(winner).style.borderColor='#FFD700';
            break;
        case 5:
            // New Hand - Display all the cards and the hands
            var hand = data.hand;

            handLayout(hand.length, hand, data);
            showCards(hand.length, data.playerID);
            break;
        case 6:
            // Player Submitting a prediction
            var playerID = data.playerID;
            var prediction = data.prediction;
            var spanLabel = "player" + playerID + "Prediction";

            id(spanLabel).innerHTML = prediction;
            break;
        case 7:
            // Updating the current hands won
            var playerID = data.playerID;
            var current = data.current;
            var spanLabel = "player" + playerID + "Current";

            id(spanLabel).innerHTML = current;
            break;
        case 8:
            // Updating a Player's Score
            var playerID = data.playerID;
            var score = data.score;
            var spanLabel = "player" + playerID + "Score";

            id(spanLabel).innerHTML = score;

            for (var i = 1; i < 4; i++) {
                spanLabel = "player" + i + "Prediction";
                var element = document.getElementById(spanLabel);
                element.innerHTML = '';
            }
            break;
        default:
            console.log("Error parsing phase data from web socket - Have you missed a break in the case statement?");
    }
}

function handLayout(numberOfCards, hand, data) {
    switch(numberOfCards) {
        case 0:
            EmptyLayout(data.playerID, hand);
            break;
        case 1:
            oneCardLayout(data.playerID, hand);
            break;
        case 2:
            twoCardLayout(data.playerID, hand);
            break;
        case 3:
            threeCardLayout(data.playerID, hand);
            break;
        case 4:
            fourCardLayout(data.playerID, hand);
            break;
        case 5:
            fiveCardLayout(data.playerID, hand);
            break;
        case 6:
            sixCardLayout(data.playerID, hand);
            break;
        case 7:
            sevenCardLayout(data.playerID, hand);
            break;
    }
}

//Layout function for organising a hand with 7 cards
function sevenCardLayout(player, hand) {
    var element;

    element = id("player" + player + "card1");
    element.src = "/cards/" + hand[0];
    element.className = "fourRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card2");
    element.src = "/cards/" + hand[1];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card3");
    element.src = "/cards/" + hand[2];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card4");
    element.src = "/cards/" + hand[3];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card5");
    element.src = "/cards/" + hand[4];
    element.className = "threeRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card6");
    element.src = "/cards/" + hand[5];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card7");
    element.src = "/cards/" + hand[6];
    element.className = "card";
    element.style.marginTop = "4%";
}

//Layout function for organising a hand with 6 cards
function sixCardLayout(player, hand) {
    hideCards(player, hand);
    var element;

    element = id("player" + player + "card1");
    element.src = "/cards/" + hand[0];
    element.className = "threeRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card2");
    element.src = "/cards/" + hand[1];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card3");
    element.src = "/cards/" + hand[2];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card4");
    element.src = "/cards/" + hand[3];
    element.className = "threeRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card5");
    element.src = "/cards/" + hand[4];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card6");
    element.src = "/cards/" + hand[5];
    element.className = "card";
    element.style.marginTop = "4%";
}

//Layout function for organising a hand with 5 cards
function fiveCardLayout(player, hand) {
    hideCards(player, hand);
    var element;

    element = id("player" + player + "card1");
    element.src = "/cards/" + hand[0];
    element.className = "threeRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card2");
    element.src = "/cards/" + hand[1];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card3");
    element.src = "/cards/" + hand[2];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card4");
    element.src = "/cards/" + hand[3];
    element.className = "twoRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card5");
    element.src = "/cards/" + hand[4];
    element.className = "card";
    element.style.marginTop = "4%";
}

//Layout function for organising a hand with 4 cards
function fourCardLayout(player, hand) {
    hideCards(player, hand);
    var element;

    element = id("player" + player + "card1");
    element.src = "/cards/" + hand[0];
    element.className = "twoRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card2");
    element.src = "/cards/" + hand[1];
    element.className = "card";
    element.style.marginTop = "4%";

    element = id("player" + player + "card3");
    element.src = "/cards/" + hand[2];
    element.className = "twoRow";
    element.style.marginTop = "4%";

    element = id("player" + player + "card4");
    element.src = "/cards/" + hand[3];
    element.className = "card";
    element.style.marginTop = "4%";
}

//Layout function for organising a hand with 3 cards
function threeCardLayout(player, hand) {
    hideCards(player, hand);
    var element;

    element = id("player" + player + "card1");
    element.src = "/cards/" + hand[0];
    element.className = "threeRow";
    element.style.marginTop = "18%";

    element = id("player" + player + "card2");
    element.src = "/cards/" + hand[1];
    element.className = "card";
    element.style.marginTop = "18%";

    element = id("player" + player + "card3");
    element.src = "/cards/" + hand[2];
    element.className = "card";
    element.style.marginTop = "18%";
}

//Layout function for organising a hand with 2 cards
function twoCardLayout(player, hand) {
    hideCards(player, hand);
    var element;

    element = id("player" + player + "card1");
    element.src = "/cards/" + hand[0];
    element.className = "twoRow";
    element.style.marginTop = "18%";

    element = id("player" + player + "card2");
    element.src = "/cards/" + hand[1];
    element.className = "card";
    element.style.marginTop = "18%";
}

//Layout function for organising a hand with 1 cards
function oneCardLayout(player, hand) {
    hideCards(player, hand);
    var element;

    element = id("player" + player + "card1");
    element.src = "/cards/" + hand[0];
    element.className = "oneRow";
    element.style.marginTop = "18%";
}

//Layout function for organising a hand with 0 cards
function EmptyLayout(player, hand) {
    hideCards(player, hand);
}

//Hides card elements not active in the current hand
function hideCards(player, hand) {
    var id;
    var element;

    for (var i = (hand.length+1); i < 8; i++) {
        id = "player" + player + "card" + i;
        element = document.getElementById(id);
        element.style.display = 'none';
    }
}

//Shows card elements at the start of each new round
function showCards(cardCount, player) {
    var id;
    var element;

    for (var i = 1; i < (cardCount+1); i++) {
        id = "player" + player + "card" + i;
        element = document.getElementById(id);
        console.log(id);
        element.style.borderColor='#346029';
        element.style.borderWidth = '2px';
        element.style.display = 'block';
    }

}

//Helper function for selecting element by id
function id(id) {
    return document.getElementById(id);
}

function dropDown(id) {
    document.getElementById("myDropdown" + id).classList.toggle("show");
}

function agentSelect(agentType, player) {
    var agentString;

    switch(agentType){
        case "1":
            agentString = "Maximum";
            break;
        case "2":
            agentString = "Minimax";
            break;
        case "3":
            agentString = "Random";
            break;
        case "4":
            agentString = "Trump %";
            break;
        case "5":
            agentString = "Monte Carlo";
            break;
        default:
            agentString = "Unknown."
    }

    id("dropbtn" + player).innerHTML = agentString.concat(" ▼");

    switch (player) {
        case 1:
            p1AgentType = agentType;
            break;
        case 2:
            p2AgentType = agentType;
            break;
        case 3:
            p3AgentType = agentType;
            break;
    }

}

function startClicked(){
    var accept = 1;

    if ( p1AgentType===0 || p2AgentType===0 || p3AgentType===0){
        alert("Please Ensure all 3 agent types have been assigned.");
        accept = 0;
    }

    var p1Name = id("agentNameBox1").value;
    var p2Name = id("agentNameBox2").value;
    var p3Name = id("agentNameBox3").value;

    if ( p1Name==="" || p2Name==="" || p3Name===""){
        alert("Please Ensure all 3 agent have been given a name.");
        accept = 0;
    }

    if (accept === 1) {
        // Remove setup elements
        var setups = document.getElementsByClassName("gameSetup");
        var i;
        for (i = 0; i < setups.length; i++) {
            var openSetup = setups[i];
            openSetup.style.display = "none";
        }
        id("start-btn").style.display = "none";

        // Bring back game elements
        var starters = document.getElementsByClassName("gameStarted");
        for (i = 0; i < starters.length; i++) {
            var openStartup = starters[i];
            openStartup.style.display = "block";
        }

        id("lowerGameArea").style.marginTop = "260px";

        // Change Player Name Labels
        changePlayerName(1, p1Name);
        changePlayerName(2, p2Name);
        changePlayerName(3, p3Name);

        // Initialise string to send to webserver
        var socketString = "AGENTS:";
        socketString = socketString.concat(p1AgentType + "," + p1Name + ",");
        socketString = socketString.concat(p2AgentType + "," + p2Name + ",");
        socketString = socketString.concat(p3AgentType + "," + p3Name);

        // Communicate with web server and log message in the console
        webSocket.send(socketString);
        console.log(socketString);
    }
}

function changePlayerName(player, name){
    var labels = document.getElementsByClassName("player" + player + "Name");
    var i;
    for (i = 0; i < labels.length; i++) {
        var label = labels[i];
        label.innerHTML = name;
    }
}

// Close the dropdown if the user clicks outside of it
// https://www.w3schools.com/howto/tryit.asp?filename=tryhow_css_js_dropdown
window.onclick = function(event) {
    if (!event.target.matches('.dropbtn')) {
        var dropdowns = document.getElementsByClassName("dropdown-content");
        var i;
        for (i = 0; i < dropdowns.length; i++) {
            var openDropdown = dropdowns[i];
            if (openDropdown.classList.contains('show')) {
                openDropdown.classList.remove('show');
            }
        }
    }
};