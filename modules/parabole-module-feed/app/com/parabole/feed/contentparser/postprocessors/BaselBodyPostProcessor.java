package com.parabole.contentparser.postprocessors;

import com.parabole.contentparser.IDocIndexBuilder;
import com.parabole.contentparser.models.basel.BaselDocMeta;
import com.parabole.contentparser.models.basel.DocumentElement;
import com.parabole.contentparser.models.common.DocMetaInfo;
import com.parabole.contentparser.models.common.ParagraphElement;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by parabole on 10/14/2016.
 */
public class BaselBodyPostProcessor implements IPostProcessor {

    IDocIndexBuilder docIndexBuilder;
    BaselDocMeta docMeta;
    Map<String, List<DocumentElement>> treeData;
    DocumentElement startTocPivot;
    DocumentElement endTocPivot;
    int paraIndexPivot;

    public BaselBodyPostProcessor(IDocIndexBuilder docIndexBuilder){
        this.docIndexBuilder = docIndexBuilder;
        this.docMeta = getGlossaryMetadata();
        this.treeData = new LinkedHashMap<>();
        this.startTocPivot = this.endTocPivot = null;
        this.paraIndexPivot = 0;
    }

    public Map<String, List<DocumentElement>> buildItemTree(List<DocumentElement> toc) throws IOException {
        List<ParagraphElement> paras = docIndexBuilder.startProcessing(docMeta);
        buildTree(paras, toc);
        return treeData;
    }

    //Boolean inChild = false;

    /*private void buildTree(List<ParagraphElement> paras, List<DocumentElement> toc){
        for(int i = 0; i < toc.size(); i++){
            if(!inChild) {
                startTocPivot = toc.get(i);
                if(startTocPivot.getChildren().size() > 0){
                    inChild = true;
                    buildTree(paras, startTocPivot.getChildren());
                } else {
                    inChild = false;
                    endTocPivot = toc.get(i+1);
                    treeData.put(startTocPivot.getName(), getParagraphs(paras));
                }
            } else {
                inChild = false;
                endTocPivot = toc.get(i);
                treeData.put(startTocPivot.getName(), getParagraphs(paras));
            }
        }
    }*/

    private void buildTree(List<ParagraphElement> paras, List<DocumentElement> toc){
        for(int i = 0; i < toc.size()-1; i++){
            startTocPivot = toc.get(i);
            endTocPivot = toc.get(i+1);
            treeData.put(startTocPivot.getName(), getParagraphs(paras));
        }
    }

    private List<DocumentElement> getParagraphs(List<ParagraphElement> paras){
        Boolean fetchFlag = false;
        List<DocumentElement> tempParas = new ArrayList<>();
        for(int i = paraIndexPivot; i < paras.size(); i++){
            ParagraphElement aPara = paras.get(i);
            if(fetchFlag){
                if(endTocPivot.getName().trim().equalsIgnoreCase(aPara.toString().trim())){
                    paraIndexPivot = i;
                    return tempParas;
                } else {
                    Pattern p = Pattern.compile(docMeta.getParaStartRegEx());
                    Matcher m = p.matcher(aPara.toString());
                    if(m.find())
                        tempParas.add(buildDocElement(aPara));
                }
            }
            if(!fetchFlag && startTocPivot.getName().trim().equalsIgnoreCase(aPara.toString().trim())){
                fetchFlag = true;
            }
        }
        return tempParas;
    }

    private DocumentElement buildDocElement(ParagraphElement paragraphElement){
        DocumentElement documentElement = new DocumentElement();
        documentElement.setContent(paragraphElement.toString());
        documentElement.setElementType(DocumentElement.ElementTypes.PARAGRAPH);
        return documentElement;
    }

    private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setStartPage(26);
        baselDocMeta.setEndPage(184);
        baselDocMeta.setParaStartRegEx("^[1-9].");
        return baselDocMeta;
    }
}
