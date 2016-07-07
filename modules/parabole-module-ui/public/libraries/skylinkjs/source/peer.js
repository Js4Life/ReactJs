var Peer = function (socketRef, config) {

  'use strict';

  // Reference object
  var self = this;

  // Event hook to object for triggering
  Event._mixin(self);



};

/**
 * 
Peer.prototype.

/*
Peer.prototype._doOffer = function () {
  var self = this;
  self._connection.createOffer(function (offer) {

  }, function (error) {
    throw error;
  });
};

Peer.prototype._doAnswer = function () {
  var self = this;

};


Peer.prototype.addStream = function (stream) {

};

Peer.prototype.removeStream = function (streamId) {

};

Peer.prototype.connect = function () {
  var self = this;
  var peer = new RTCPeerConnection({
    iceServers: self._iceServers,
    //bundlePolicy: "balanced",
    //iceTransportPolicy: "all",
    //peerIdentity: null
  });

  self._connection = new RTCPeerConnection({
    iceServers: self._iceServers,
    //bundlePolicy: 'balanced',
    //iceTransportPolicy: 'all',
    //peerIdentity: null
  })

};*/