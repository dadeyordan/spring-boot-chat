'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;
var roomId = null;

var colors = [
  '#2196F3', '#32c787', '#00BCD4', '#ff5652',
  '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
  username = document.querySelector('#name').value.trim();
  roomId = document.querySelector('#roomId').value.trim();

  if (username && roomId) {
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');

    var socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);

  }
  event.preventDefault();
}

function onConnected() {
  // Subscribe to the Public Topic
  stompClient.subscribe('/topic/' + roomId, onMessageReceived);

  // Send username to the server
  stompClient.send("/app/chat/" + roomId + "/register",
      {},
      JSON.stringify({sender: username, type: 'JOIN'})
  );

  connectingElement.classList.add('hidden');
}

function onError(error) {
  connectingElement.textContent = 'Could not connect to WebSocket! Please refresh the page and try again or contact the administrator.';
  connectingElement.style.color = 'red';
}

function welcome() {
  var chatMessage = {
    sender: username,
    content: messageInput.value,
    type: 'CHAT'
  };

  stompClient.send("/app/chat/" + roomId + "/welcome", {},
      JSON.stringify(chatMessage));
  messageInput.value = '';
}

function send(event) {
  var messageContent = messageInput.value.trim();

  if (messageContent && stompClient) {
    var chatMessage = {
      sender: username,
      content: messageInput.value,
      type: 'CHAT'
    };

    stompClient.send("/app/chat/" + roomId + "/send", {},
        JSON.stringify(chatMessage));
    messageInput.value = '';
  }
  event.preventDefault();
}

function onMessageReceived(payload) {
  var message = JSON.parse(payload.body);

  var messageElement = document.createElement('li');

  if (message.type === 'JOIN') {
    messageElement.classList.add('event-message');
    message.content = message.sender + ' joined!';

    welcome();
  } else if (message.type === 'LEAVE') {
    messageElement.classList.add('event-message');
    message.content = message.sender + ' left!';
  } else {
    messageElement.classList.add('chat-message');

    var avatarElement = document.createElement('i');
    if (message.avatar) {
      setAvatarImage(avatarElement);
    } else {
      var avatarText = document.createTextNode(message.sender[0]);
      avatarElement.appendChild(avatarText);
      setAvatarColor(avatarElement, message.sender);
    }

    messageElement.appendChild(avatarElement);

    var usernameElement = document.createElement('span');
    var usernameText = document.createTextNode(message.sender);
    usernameElement.appendChild(usernameText);
    messageElement.appendChild(usernameElement);
  }

  var dateElement = document.createElement('p');
  dateElement.classList.add("chat-date-time");
  var messageDate = document.createTextNode(message.dateTime);
  dateElement.appendChild(messageDate);

  messageElement.appendChild(dateElement);

  var textElement = document.createElement('p');
  var messageText = document.createTextNode(message.content);
  textElement.appendChild(messageText);

  messageElement.appendChild(textElement);

  messageArea.appendChild(messageElement);
  messageArea.scrollTop = messageArea.scrollHeight;
}

function setAvatarImage(avatarElement) {
  avatarElement.style['background-image'] = "url(../image/admin.png)";
  avatarElement.style['background-size'] = "contain";
}

function setAvatarColor(avatarElement, messageSender) {
  var hash = 0;
  for (var i = 0; i < messageSender.length; i++) {
    hash = 31 * hash + messageSender.charCodeAt(i);
  }

  var index = Math.abs(hash % colors.length);

  avatarElement.style['background-color'] = colors[index];
}

usernameForm.addEventListener('submit', connect, true);
messageForm.addEventListener('submit', send, true);