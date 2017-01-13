package com.parabole.feed.contentparser.contextparser.helpers;

import com.parabole.feed.application.global.CCAppConstants;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Rajdeep on 06-Jan-17.
 */

public class SentenceDetect {
    private String paragraph;
    static SentenceDetectorME sDetector;
    static {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(CCAppConstants.SENT);
            SentenceModel sentenceModel = new SentenceModel(inputStream);
            sDetector = new SentenceDetectorME(sentenceModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SentenceDetect(String paragraph) {
        this.paragraph = paragraph;
    }

    public String[] getAllSentences() throws IOException {
        String [] sentences = sDetector.sentDetect(paragraph);
        return sentences;
    }

}
