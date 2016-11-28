package com.parabole.feed.contentparser.fasb;

import com.parabole.feed.contentparser.AbstractDocBuilder;
import com.parabole.feed.contentparser.IDocIndexBuilder;
import com.parabole.feed.contentparser.PDFContentTagger;
import com.parabole.feed.contentparser.filters.FASBParagraphProcessor;
import com.parabole.feed.contentparser.models.common.DocMetaInfo;
import com.parabole.feed.contentparser.models.fasb.FASBDocMeta;
import com.parabole.feed.contentparser.models.fasb.FASBIndexedDocument;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by anish on 7/26/2016.
 */
public class FASBDocIndexBuilder extends AbstractDocBuilder implements IDocIndexBuilder {

    private JSONObject nFASBDocMetaFromJSON = new JSONObject();

    public FASBDocIndexBuilder(String path , List<String> con, JSONObject fasbMETA) throws IOException {
        super(path);
        concepts = con;
        this.nFASBDocMetaFromJSON = fasbMETA;
    }

    //Build the Document
    public FASBIndexedDocument buildFASBIndex() throws IOException {
        indexedDocument = new FASBIndexedDocument();
        docMeta = getFASBMetadata();
        initializeParagraphBuilder((FASBDocMeta)docMeta);
        PDFTextStripper stripper = new PDFContentTagger(this);
        stripper.setStartPage( docMeta.getStartPage() );
        stripper.setEndPage( docMeta.getEndPage() );
        stripper.writeText(document,null);
        indexedDocument.setParagraphs(paragraphBuilder.getAllParagraphs());
        return indexedDocument;
    }

    @Override
    public List<ParagraphElement> startProcessing(DocMetaInfo metaInfo) throws IOException {
        return null;
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
            if( word.compareToIgnoreCase(((FASBDocMeta)docMeta).getParaIgnore()) == 0){
                para.setWillIgnore(true);
                return true;
            }
        }
        return false;
    }

    private FASBDocMeta getFASBMetadata() {
        FASBDocMeta fasbDocMeta = new FASBDocMeta();
        /*if(this.nFASBDocMetaFromJSON != null){
            fasbDocMeta.setStartPage(16);
            fasbDocMeta.setEndPage(219);
            fasbDocMeta.setParaStartRegEx(this.nFASBDocMetaFromJSON.getString("paraStartRegEx"));
            fasbDocMeta.setParaIgnore(this.nFASBDocMetaFromJSON.getString("paraIgnore"));
        }else{*/
            fasbDocMeta.setStartPage(16);
            fasbDocMeta.setEndPage(Integer.MAX_VALUE);
            fasbDocMeta.setParaStartRegEx("\\w{1,3}[-]\\w{1,3}[-]\\w{1,3}[-]\\w{1,3}");
            fasbDocMeta.setParaIgnore("superseded");
        /*}*/

        return fasbDocMeta;
    }


    FASBIndexedDocument indexedDocument;
    List<String> concepts;
    IParagraphBuilder paragraphBuilder;
}
