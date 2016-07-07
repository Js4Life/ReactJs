// Globals used by test


describe('- Methods', function () {

  describe('#_parseServer', function() {
    it('is typeof "string"', function(done) {
      this.timeout(testItemTimeout);
      assert.typeOf(peer.id, 'string');
      done();
    });
    it('matches given ID', function(done) {
      this.timeout(testItemTimeout);
      expect(peer.id).to.equal(peerId);
      done();
    });
  });


});