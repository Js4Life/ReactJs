package com.parabole.feed.contentparser.contextparser.helpers;

import com.parabole.feed.application.global.CCAppConstants;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Rajdeep on 06-Jan-17.
 */

public class TokenDetect {
    private String sentence;
    static TokenizerME tokenizerME;
    static {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(CCAppConstants.TOKEN);
            TokenizerModel tokenizerModel = new TokenizerModel(inputStream);
            tokenizerME = new TokenizerME(tokenizerModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public TokenDetect(String sentence) {
        this.sentence = sentence;
    }

    public String[] getAllTokens() throws IOException {
        return tokenizerME.tokenize(sentence);
    }
}
