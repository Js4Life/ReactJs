package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.filters.FASBParagraphProcessor;
import com.parabole.feed.contentparser.models.FASBDocMeta;
import com.parabole.feed.contentparser.models.FASBIndexedDocument;
import com.parabole.feed.contentparser.models.ParagraphElement;
import com.parabole.feed.contentparser.models.TextFormatInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by anish on 7/26/2016.
 */
public class FASBDocIndexBuilder implements IDocIndexBuilder {

    public FASBDocIndexBuilder( String path , List<String> con) throws IOException {
        document = PDDocument.load(new File( path ), "");
        concepts = con;
        AccessPermission ap = document.getCurrentAccessPermission();
        if( ! ap.canExtractContent() )
        {
            throw new IOException( "You do not have permission to extract text" );
        }
    }

    //Build the Document
    public FASBIndexedDocument buildFASBIndex() throws IOException {
        indexedDocument = new FASBIndexedDocument();
        docMeta = getFASBMetadata();
        initializeParagraphBuilder(docMeta);
        PDFTextStripper stripper = new PDFContentTagger(this);
        stripper.setStartPage( docMeta.getStartPage() );
        stripper.setEndPage( docMeta.getEndPage() );
        stripper.writeText(document,null);
        indexedDocument.setParagraphs(paragraphBuilder.getAllParagraphs());
        return indexedDocument;
    }

    @Override
    public void addChunk(String text, TextFormatInfo format , boolean isNewPara) {
        if( isNewPara )
            paragraphBuilder.buildParagraph( text , format);
        else
            paragraphBuilder.addToCurrentParagraph(text);
    }

    private void indexParagraphByConcepts(String concept, ParagraphElement paragraph){
        Set<String> paraIds = null;
        HashMap<String,Set<String>> indexListMap = indexedDocument.getConceptIndex();
        if( indexListMap.containsKey(concept))
            paraIds = indexListMap.get(concept);
        else{
            paraIds = new TreeSet<>();
            indexListMap.put(concept,paraIds);
        }
        if(paragraph.getBodyText().toUpperCase().indexOf(concept.toUpperCase()) != -1) {
            if(!paraIds.contains(paragraph.getId()))
                paraIds.add(paragraph.getId());
        }
    }

    private void initializeParagraphBuilder(FASBDocMeta docMeta) {
        paragraphBuilder = new FASBParagraphBuider(new FASBParagraphProcessor(docMeta));
        paragraphBuilder.addPostProcessParagraph( this::postProcessParagraph );
    }

    private void postProcessParagraph(ParagraphElement para){
        //Is Ihe Para Obsolete
        isParagraphObsolete(para);
        for(String concept : this.concepts){
            indexParagraphByConcepts(concept,para) ;
        }
    }

    private boolean isParagraphObsolete(ParagraphElement para) {
        String firstLine = para.getFirstLine();
        String[] words = firstLine.split(" ");
        for(String word : words){
            if( word.compareToIgnoreCase(docMeta.getParaIgnore()) == 0){
                para.setWillIgnore(true);
                return true;
            }
        }
        return false;
    }

    private FASBDocMeta getFASBMetadata() {
        FASBDocMeta fasbDocMeta = new FASBDocMeta();
        fasbDocMeta.setStartPage(16);
        fasbDocMeta.setEndPage(Integer.MAX_VALUE);
        fasbDocMeta.setParaStartRegEx("\\w{1,3}[-]\\w{1,3}[-]\\w{1,3}[-]\\w{1,3}");
        fasbDocMeta.setParaIgnore("superseded");
        return fasbDocMeta;
    }

    FASBIndexedDocument indexedDocument;
    List<String> concepts;
    IParagraphBuilder paragraphBuilder;
    PDDocument document = null;
    FASBDocMeta docMeta = null;
}
