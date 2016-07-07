/*! skylinkjs - v1.0.0 - Thu Oct 15 2015 13:13:10 GMT+0800 (SGT) */

var sharedConfig = require('../../config/browsers/firefox.conf.js');

module.exports = function(config) {

  sharedConfig(config);

  config.files.push('../units/streamtrack.js');
  config.files.push('../../../publish/skylink.complete.js');
  config.files.push('../../util/util.js');

  config.preprocessors['../../../publish/skylink.complete.js'] = ['coverage'];
  config.preprocessors['../units/streamtrack.js'] = ['browserify'];

  // generate random port
  config.port = 50113;
};