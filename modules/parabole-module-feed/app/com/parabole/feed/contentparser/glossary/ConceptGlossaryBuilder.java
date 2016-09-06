package com.parabole.feed.contentparser.glossary;

import com.parabole.feed.contentparser.AbstractDocBuilder;
import com.parabole.feed.contentparser.IDocIndexBuilder;
import com.parabole.feed.contentparser.PDFContentTagger;
import com.parabole.feed.contentparser.filters.SentenceProcessor;
import com.parabole.feed.contentparser.models.common.*;
import com.parabole.feed.contentparser.models.fasb.FASBDocMeta;
import com.parabole.feed.contentparser.models.glossary.Glossary;
import com.parabole.feed.contentparser.models.glossary.GlossaryDocMeta;
import org.apache.pdfbox.text.PDFTextStripper;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 8/22/2016.
 */
public class ConceptGlossaryBuilder {


    public Glossary buildGlossary() throws IOException {
        /*docMeta = getGlossaryMetadata();
        startStriping();
        //System.out.print(lineElementList);
        System.out.println("*************************");
        paragraphElementList.stream().forEach(a -> {
            System.out.println(a);
            System.out.println("____________________");
        });*/
        return glossary;
    }

    private DocMetaInfo getGlossaryMetadata() {
        GlossaryDocMeta glossaryDocMeta = new GlossaryDocMeta();
        glossaryDocMeta.setStartPage(1);
        glossaryDocMeta.setEndPage(2);
        return glossaryDocMeta;
    }


    Glossary glossary;
}
