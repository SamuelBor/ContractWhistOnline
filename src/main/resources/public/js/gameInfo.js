//Establish the WebSocket connection and set up event handlers
console.log("File Loading Correctly.");
var webSocket = new WebSocket("ws://localhost:1816/updates");
var slider = id("speedRange");
var output = id("speedLabel");
var newPlayer = "player1";
var winner = "player1";
var chosenCard;

webSocket.onmessage = function (msg) {
    console.log('Server: ' + msg.data);
    updateInfo(msg);
};
webSocket.onclose = function () { console.log("WebSocket Connection Closed"); };
webSocket.onopen = function (event) {
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
function updateInfo(msg) {
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
            showCards(hand.length);9
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
function showCards(cardCount) {
    var id;
    var element;

    for(var player= 1; player<4; player++){
        for (var i = 1; i < (cardCount+1); i++) {
            id = "player" + player + "card" + i;
            element = document.getElementById(id);
            element.style.borderColor='#346029';
            element.style.borderWidth = '2px';
            element.style.display = 'block';
        }
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