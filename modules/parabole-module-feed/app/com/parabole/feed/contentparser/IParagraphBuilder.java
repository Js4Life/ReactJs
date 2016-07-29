package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.models.ParagraphElement;
import com.parabole.feed.contentparser.models.TextFormatInfo;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by anish on 7/26/2016.
 */
public interface IParagraphBuilder {

    void buildParagraph(String text, TextFormatInfo format);
    void addToCurrentParagraph(String text);
    void addPostProcessParagraph(Consumer<ParagraphElement> functor);
    Map<String,ParagraphElement> getAllParagraphs();
}
