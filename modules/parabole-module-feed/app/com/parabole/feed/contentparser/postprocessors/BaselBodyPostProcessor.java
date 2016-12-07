package com.parabole.feed.contentparser.postprocessors;

import com.parabole.feed.contentparser.IDocIndexBuilder;
import com.parabole.feed.contentparser.models.basel.BaselDocMeta;
import com.parabole.feed.contentparser.models.basel.DocumentElement;
import com.parabole.feed.contentparser.models.common.DocMetaInfo;
import com.parabole.feed.contentparser.models.common.LineElement;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import org.json.JSONObject;

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
    List<ParagraphElement> flatParaList;
    int paraIndexPivot;
    HashMap<String,Set<String>> conceptParaMap = new HashMap<>();
    List<String> concepts = new ArrayList<>();

    public HashMap<String, Set<String>> getConceptParaMap() {
        return conceptParaMap;
    }

    public BaselBodyPostProcessor(IDocIndexBuilder docIndexBuilder, List<String> extConcepts, JSONObject glossaryMetaData){
        this.concepts = extConcepts;
        this.docIndexBuilder = docIndexBuilder;
        if(glossaryMetaData != null){
            this.docMeta = getGlossaryMetadata(glossaryMetaData);
        } else {
            this.docMeta = getGlossaryMetadata();
        }
        this.treeData = new LinkedHashMap<>();
        this.startTocPivot = this.endTocPivot = null;
        this.paraIndexPivot = 0;
        this.flatParaList = new ArrayList<>();
    }

    public Map<String, List<DocumentElement>> buildItemTree(List<DocumentElement> toc) throws IOException {
        List<ParagraphElement> paras = docIndexBuilder.startProcessing(docMeta);
        buildTree(paras, toc);
        return treeData;
    }

    private void buildTree(List<ParagraphElement> paras, List<DocumentElement> toc){
        Map<String, List<ParagraphElement>> tempTree = new LinkedHashMap<>();
        toc.add(null);
        for(int i = 0; i < toc.size()-1; i++){
            startTocPivot = toc.get(i);
            endTocPivot = toc.get(i+1);
            tempTree.put(startTocPivot.getLevelId(), indexParagraphs(paras));
        }

        Iterator<String> it = tempTree.keySet().iterator();
        while(it.hasNext()){
            String key = it.next();
            List<ParagraphElement> paraList = tempTree.get(key);
            treeData.put(key, getParagraphs(paraList));
        }
    }

    private List<ParagraphElement> indexParagraphs(List<ParagraphElement> paras){
        Boolean fetchFlag = false;
        List<ParagraphElement> tempParas = new ArrayList<>();
        for(int i = paraIndexPivot; i < paras.size(); i++){
            ParagraphElement aPara = paras.get(i);
            if(fetchFlag){
                if(endTocPivot != null && (aPara.toString().toLowerCase().trim().startsWith(endTocPivot.getContent().toLowerCase().trim()) || endTocPivot.getContent().toLowerCase().trim().startsWith(aPara.toString().toLowerCase().trim()))){
                    paraIndexPivot = i;
                    return tempParas;
                } else {
                    aPara.setId(startTocPivot.getLevelId());
                    tempParas.add(aPara);
                }
            }
            if(!fetchFlag && (aPara.toString().toLowerCase().trim().startsWith(startTocPivot.getContent().toLowerCase().trim()) || startTocPivot.getContent().toLowerCase().trim().startsWith(aPara.toString().toLowerCase().trim()))){
                aPara.setId(startTocPivot.getLevelId());
                tempParas.add(aPara);
                fetchFlag = true;
            }
        }
        return tempParas;
    }

    int paraIdx;
    private List<DocumentElement> getParagraphs(List<ParagraphElement> paras){
        List<DocumentElement> tempParas = new ArrayList<>();
        for(paraIdx = 0; paraIdx < paras.size(); paraIdx++){
            ParagraphElement aPara = paras.get(paraIdx);
            removeBoldAndItalicPrefixIfExist(aPara);
            Pattern p = Pattern.compile(docMeta.getParaStartRegEx());
            Matcher m = p.matcher(aPara.toString());
            if(m.find()) {
                if(docMeta.getHasParaIdentifier()) {
                    int index = aPara.toString().indexOf('.');
                    if (index == -1)
                        continue;
                }
                int levelIndex = tempParas.size()+1;
                DocumentElement anElement = buildDocElement(paras, levelIndex);
                if(anElement != null) {
                    tempParas.add(anElement);
                }
            }
        }
        return tempParas;
    }

    private DocumentElement buildDocElement(List<ParagraphElement> paras, int levelIndex){
        ParagraphElement aPara = paras.get(paraIdx);
        String paraId = aPara.getId();
        StringBuilder sb = new StringBuilder(aPara.toString()).append('\n');

        ParagraphElement newPara = new ParagraphElement();
        addToParagraphElement(newPara, aPara);

        for(paraIdx = paraIdx+1; paraIdx < paras.size(); paraIdx++){
            aPara = paras.get(paraIdx);
            removeBoldAndItalicPrefixIfExist(aPara);
            Pattern p = Pattern.compile(docMeta.getParaStartRegEx());
            Matcher m = p.matcher(aPara.toString());
            if(!m.find()) {
                if(!aPara.toString().isEmpty()) {
                    sb.append(aPara.toString()).append('\n');
                    addToParagraphElement(newPara, aPara);
                }
            } else if(docMeta.getHasParaIdentifier()) {
                int index = aPara.toString().indexOf('.');
                if(index == -1)
                    continue;
                paraIdx = paraIdx - 1;
                break;
            } else {
                int index = sb.toString().lastIndexOf('.');
                if(index != sb.toString().trim().length()-1){
                    sb.append(aPara.toString());
                    addToParagraphElement(newPara, aPara);
                    continue;
                }
                paraIdx = paraIdx - 1;
                break;
            }
        }
        DocumentElement documentElement = new DocumentElement();
        documentElement.setContent(sb.toString());
        documentElement.setElementType(DocumentElement.ElementTypes.PARAGRAPH);
        paraId = paraId + "-P" + levelIndex;
        if(docMeta.getHasParaIdentifier()) {
            int index = documentElement.getContent().indexOf('.');
            if (index == -1 || index > 12)
                return null;
            String name = documentElement.getContent().substring(0, index);
            documentElement.setName(name);
        } else {
            documentElement.setName(paraId);
        }
        documentElement.setLevelId(paraId);
        newPara.setId(paraId);
        flatParaList.add(newPara);

        // TODO
        // concept mapping here with each ======> newPara
        postProcessParagraph(newPara);
        //end

        return documentElement;
    }

    private void indexParagraphByConcepts(String concept, ParagraphElement paragraph){
        Set<String> paraIds = null;
        if( this.conceptParaMap.containsKey(concept))
            paraIds = this.conceptParaMap.get(concept);
        else{
            paraIds = new TreeSet<>();
            this.conceptParaMap.put(concept,paraIds);
        }
        if(paragraph.toString().toUpperCase().indexOf(concept.toUpperCase()) != -1) {
            System.out.println("paraIds = " + paraIds);
            if(!paraIds.contains(paragraph.getId()))
                paraIds.add(paragraph.getId());
        }
    }

    private void postProcessParagraph(ParagraphElement para){
        //Is Ihe Para Obsolete
        for(String concept : this.concepts){
            indexParagraphByConcepts(concept,para) ;
        }
    }

    private void addToParagraphElement(ParagraphElement toPara, ParagraphElement fromPara){
        for (LineElement aSentence: fromPara.getLines()) {
            toPara.addSentence(aSentence);
        }
    }

    private void removeBoldAndItalicPrefixIfExist(ParagraphElement aPara){
        Iterator<LineElement> sentenceIt = aPara.getLines().iterator();
        while (sentenceIt.hasNext()){
            LineElement aSentence = sentenceIt.next();
            if(aSentence.getWordList().get(0).isBold()){
                sentenceIt.remove();
            } else if(aSentence.getWordList().get(0).isItaics()){
                sentenceIt.remove();
            } else if(aSentence.getWordList().get(0).getWordHeight() != docMeta.getParagraphFontSize()){
                sentenceIt.remove();
            }
        }
    }

    public List<ParagraphElement> getFlatParaList() {
        return flatParaList;
    }

    private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setStartPage(3);
        baselDocMeta.setEndPage(4);
        baselDocMeta.setParagraphFontSize(9);
        baselDocMeta.setParaStartRegEx("^[\\d+^[\\%\\s]].");
        return baselDocMeta;
    }

    private BaselDocMeta getGlossaryMetadata(JSONObject glossaryMetaData) {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setStartPage(glossaryMetaData.getInt("fromPage"));
        baselDocMeta.setEndPage(glossaryMetaData.getInt("toPage"));
        baselDocMeta.setParagraphFontSize(glossaryMetaData.getInt("fontSize"));
        baselDocMeta.setParaStartRegEx(glossaryMetaData.getString("paraStartRegex"));
        if(glossaryMetaData.has("hasParaIdentifier")){
            baselDocMeta.setHasParaIdentifier(glossaryMetaData.getBoolean("hasParaIdentifier"));
        } else {
            baselDocMeta.setHasParaIdentifier(true);
        }
        return baselDocMeta;
    }
}
