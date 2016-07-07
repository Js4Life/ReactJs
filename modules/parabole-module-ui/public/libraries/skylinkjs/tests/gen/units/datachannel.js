/*! skylinkjs - v1.0.0 - Thu Oct 15 2015 13:13:10 GMT+0800 (SGT) */

//mocha.bail();
//mocha.run();

var expect = chai.expect;
var assert = chai.assert;
var should = chai.should;

/* Test timeouts */
var testTimeout = 35000;
var gUMTimeout = 25000;
var testItemTimeout = 4000;

var util = require('./util');

/* Template */
describe('datachannel', function () {
  this.timeout(testTimeout + 2000);
  this.slow(2000);

  
describe('DataChannel', function() {

	describe('CoreDataChannel', function(){

		it('should be constructed successfully', function(){

		});

	});

	describe('TransferChannel', function(){

		it('should be constructed successfully', function(){

		});

	});

	describe('MainChannel', function(){

		it('should be constructed successfully', function(){

		});

	});

});


});