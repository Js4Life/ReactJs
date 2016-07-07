/*! skylinkjs - v1.0.0 - Thu Oct 15 2015 13:13:10 GMT+0800 (SGT) */

var sharedConfig = require('../../config/browsers/chrome.conf.js');

module.exports = function(config) {

  sharedConfig(config);

  config.files.push('../units/peer.js');
  config.files.push('../../../publish/skylink.complete.js');
  config.files.push('../../util/util.js');

  config.preprocessors['../../../publish/skylink.complete.js'] = ['coverage'];
  config.preprocessors['../units/peer.js'] = ['browserify'];

  // generate random port
  config.port = 5007;
};