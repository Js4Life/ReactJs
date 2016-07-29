package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.models.TextFormatInfo;

/**
 * Created by anish on 7/26/2016.
 */
public interface IDocIndexBuilder {
    void addChunk(String text, TextFormatInfo format, boolean isNewPara);
}
