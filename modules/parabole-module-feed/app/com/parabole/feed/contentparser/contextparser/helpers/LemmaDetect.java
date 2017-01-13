package com.parabole.feed.contentparser.contextparser.helpers;

import com.parabole.feed.application.global.CCAppConstants;
import opennlp.tools.lemmatizer.SimpleLemmatizer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Rajdeep on 10-Jan-17.
 */
public class LemmaDetect {

    static SimpleLemmatizer lemmatizer;
    static {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(CCAppConstants.LEMMATIZER);
            lemmatizer = new SimpleLemmatizer(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String lemmatize(String word, String posTag){
        return lemmatizer.lemmatize(word, posTag);
    }

}
