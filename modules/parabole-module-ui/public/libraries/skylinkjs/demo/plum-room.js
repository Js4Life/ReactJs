var App = {};
App.Room = {
  skylinkAppKey: '<%= ENV["SKYLINK_APP_KEY"] %>',
  sendMessage: function(skylink) {
    var input;
    input = document.getElementById('message');
    if (input.value) {
      skylink.sendP2PMessage(input.value);
    }
    return input.value = '';
  },
  addMessage: function(message, className) {
    var chatbox, div;
    chatbox = document.getElementById('chatbox');
    div = document.createElement('div');
    div.className = className;
    div.textContent = message;
    chatbox.appendChild(div);
    return chatbox.scrollTop = chatbox.scrollHeight;
  },
  addVideo: function(peerId, stream, muted) {
    var peerVideo;
    peerVideo = document.createElement('video');
    peerVideo.id = peerId;
    peerVideo.autoplay = true;
    // EDIT: Added the flag to mute the video based on the flag passed in.
    if (muted) {
      peerVideo.muted = true;
    }
    console.log('addVideo', peerId, stream, muted);
    document.getElementById('video').appendChild(peerVideo);
    return attachMediaStream(peerVideo, stream);
  }
};