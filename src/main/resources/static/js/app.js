var stompClient = null;

window.onload = function() {
    connect();
};

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

// function connectSubject() {
//     var socket = new SockJS('/gs-guide-websocket');
//     stompClient = Stomp.over(socket);
//     stompClient.connect({}, function (frame) {
//         setConnected(true);
//         console.log('Connected: ' + frame);
//         // stompClient.subscribe('/topic/greetings', function (greeting) {
//         //     showGreeting(JSON.parse(greeting.body).content);
//         //     location.reload(true);
//         // });
//     });
// }

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/refresh', function (greeting) {
            //  showGreeting(JSON.parse(greeting.body).content);
            location.reload(true);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}
// @{/reservation/submit/{id}(id=${id})}
function sendName() {
    location.href='/reservation/submit/'.concat($("#id").val());//{idAuthor}(idAuthor = ${"idAuthor"}.val())';
    stompClient.send("/app/hello", {}, JSON.stringify({'name':"smith"})); // $("#title").val()
}
function sendUpdate() {
    location.href='/articles/update/submit/'.concat($("#idAuthor").val());//{idAuthor}(idAuthor = ${"idAuthor"}.val())';
    stompClient.send("/app/hello", {}, JSON.stringify({'name':"smith"})); // $("#title").val()
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    // $("form").on('submit', function (e) {
    //     e.preventDefault();
    // });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    $( "#update" ).click(function() { sendUpdate(); });
});