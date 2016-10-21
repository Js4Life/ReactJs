package com.parabole.feed.contentparser.models.common;

import org.apache.pdfbox.text.TextPosition;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 8/23/2016.
 */
public class LineElement extends ContentElement{

    public LineElement(){
        wordList = new ArrayList<>();
    }

    public int getNumberOfWords(){
        return  wordList.size();
    }

    public void addToWord(char c , CharacterFormatInfo formatInfo) {
        if (currentWord == null || c == ' ') {
            currentWord = new WordElement();
            wordList.add(currentWord);
        }
        if( c != ' ')
            currentWord.addCharacter(c,formatInfo);
    }

    public float getLineStart(){
        WordElement firstWord = wordList.get(0);
        return firstWord.getFirstCharacterStartY();
    }

    public List<WordElement> getWordList(){
        return wordList;
    }

    public String getRemainingWords(int idx){
        StringBuilder sb = new StringBuilder();
        for(int i = idx; i < wordList.size();i++){
            sb.append(wordList.get(i));
            sb.append(" ");
        }
        return sb.toString().trim();
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        wordList.stream().forEach(a -> {
            sb.append(a.getWord());
            sb.append(" ");
        });
        return sb.toString().trim();
    }

    private WordElement currentWord;
    private List<WordElement> wordList;
}
