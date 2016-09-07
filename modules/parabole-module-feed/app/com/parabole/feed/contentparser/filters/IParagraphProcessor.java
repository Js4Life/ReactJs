package com.parabole.feed.contentparser.filters;

import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;

/**
 * Created by anish on 7/26/2016.
 */
public interface IParagraphProcessor {
    String IsNewParagraphStart( String text );
    boolean IsEndOfParagraph( String text  , ParagraphElement para, TextFormatInfo formatInfo);
}
