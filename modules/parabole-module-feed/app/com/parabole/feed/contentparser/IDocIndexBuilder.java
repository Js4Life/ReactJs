package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.models.common.DocMetaInfo;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;

import java.io.IOException;
import java.util.List;

/**
 * Created by anish on 7/26/2016.
 */
public interface IDocIndexBuilder {
    List<ParagraphElement> startProcessing(DocMetaInfo metaInfo) throws IOException;
    void addChunk( String text , TextFormatInfo format , boolean isNewPara);
    String getFileName();
}
