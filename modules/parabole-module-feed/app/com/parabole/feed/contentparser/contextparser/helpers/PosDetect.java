package com.parabole.feed.contentparser.contextparser.helpers;

import com.parabole.feed.application.global.CCAppConstants;
import com.parabole.feed.contentparser.contextparser.models.VerbForm;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rajdeep on 06-Jan-17.
 */
public class PosDetect {
    static POSTaggerME posTaggerME;
    static {
        InputStream inputStream;
        try {
            inputStream = new FileInputStream(CCAppConstants.POS_MAXENT);
            POSModel posModel = new POSModel(inputStream);
            posTaggerME = new POSTaggerME(posModel);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<VerbForm> getVerbForms(String sentence){
        List<VerbForm> verbFormList = new ArrayList<>();

        String whitespaceTokenizerLine[] = WhitespaceTokenizer.INSTANCE
                .tokenize(sentence);
        String[] tags = posTaggerME.tag(whitespaceTokenizerLine);

        for (int i = 0; i < whitespaceTokenizerLine.length; i++) {
            String word = whitespaceTokenizerLine[i].trim();
            String tag = tags[i].trim();
            if (tag.equals("VB") || tag.equals("VBD") || tag.equals("VBG") || tag.equals("VBN") || tag.equals("VBP") || tag.equals("VBZ") ){

                verbFormList.add(new VerbForm(word,tag));
            }
        }

        return verbFormList;
    }
}