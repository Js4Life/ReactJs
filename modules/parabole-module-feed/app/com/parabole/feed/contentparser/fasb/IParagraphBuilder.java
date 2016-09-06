package com.parabole.feed.contentparser.fasb;

import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by anish on 7/26/2016.
 */
public interface IParagraphBuilder {

    void buildParagraph( String text , TextFormatInfo format );
    void addToCurrentParagraph( String text );
    void addPostProcessParagraph( Consumer<ParagraphElement> functor);
    Map<String,ParagraphElement> getAllParagraphs();
}
