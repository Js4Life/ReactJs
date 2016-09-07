package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.models.common.DocMetaInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

/**
 * Created by anish on 8/22/2016.
 */
public abstract class AbstractDocBuilder {

    protected PDDocument document = null;
    protected String fPath;
    protected DocMetaInfo docMeta = null;

    protected AbstractDocBuilder(String path) throws IOException {
        document = PDDocument.load(new File( path ), "");
        fPath = path;
        AccessPermission ap = document.getCurrentAccessPermission();
        if( ! ap.canExtractContent() )
        {
            throw new IOException( "You do not have permission to extract text" );
        }
    }

    protected void startStriping() throws IOException {
        PDFTextStripper stripper = new PDFContentTagger((IDocIndexBuilder)this);
        stripper.setStartPage( docMeta.getStartPage() );
        stripper.setEndPage( docMeta.getEndPage() );
        stripper.writeText(document,null);
    }

    public String getFileName() {
        return fPath;
    }

}
