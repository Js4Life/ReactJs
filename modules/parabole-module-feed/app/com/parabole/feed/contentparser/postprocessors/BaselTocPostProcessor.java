package com.parabole.feed.contentparser.postprocessors;

import com.parabole.feed.contentparser.IDocIndexBuilder;
import com.parabole.feed.contentparser.models.basel.BaselDocMeta;
import com.parabole.feed.contentparser.models.basel.DocumentElement;
import com.parabole.feed.contentparser.models.common.LineElement;
import com.parabole.feed.contentparser.models.common.ParagraphElement;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

/**
 * Created by parabole on 10/19/2016.
 */
public class BaselTocPostProcessor implements IPostProcessor {
    IDocIndexBuilder docIndexBuilder;
    BaselDocMeta docMeta;
    List<DocumentElement> treeData;
    List<DocumentElement> flatParaList;
    StringBuilder levelId;

    public BaselTocPostProcessor(IDocIndexBuilder docIndexBuilder) {
        this.docIndexBuilder = docIndexBuilder;
        this.docMeta = getGlossaryMetadata();
        this.treeData = new ArrayList<>();
        this.flatParaList = new ArrayList<>();
    }

    public List<DocumentElement> buildItemTree() throws IOException {
        List<ParagraphElement> paraList = docIndexBuilder.startProcessing(docMeta);
        return buildTree(paraList);
    }

    private List<DocumentElement> buildTree(List<ParagraphElement> paraList) {            //predefined pattern match wise indentation
        Map<Integer, String> levelSelector = docMeta.getLevelSelector();
        Boolean startParsing = false;
        for(ParagraphElement aPara : paraList) {
            for(LineElement aSentence : aPara.getLines()){
                if(aSentence.toString().startsWith(docMeta.getStartText())){
                    startParsing = true;
                }
                if(startParsing) {
                    DocumentElement temp = buildDocElement(aSentence);
                    for (Integer level : levelSelector.keySet()) {
                        Pattern p = Pattern.compile(levelSelector.get(level));
                        Matcher m = p.matcher(temp.getContent());
                        if (m.find()) {
                            if(isParagraphSelectorLevel(level))
                                flatParaList.add(temp);
                            temp.setLevel(level);
                            if(level == 1){
                                temp.setElementType(DocumentElement.ElementTypes.TOPIC);
                                int index = temp.getContent().indexOf(':');
                                if(index != -1) {
                                    String name = temp.getContent().substring(0, index);
                                    temp.setName(name);
                                }
                            }
                            else if(level == 2){
                                temp.setElementType(DocumentElement.ElementTypes.SUBTOPIC);
                                int index = temp.getContent().indexOf('.');
                                if(index != -1) {
                                    String name = temp.getContent().substring(0, index);
                                    temp.setName(name);
                                }
                            }
                            else if(level == 3){
                                temp.setElementType(DocumentElement.ElementTypes.SECTION);
                                int index = temp.getContent().indexOf('.');
                                if(index != -1) {
                                    String name = temp.getContent().substring(0, index);
                                    temp.setName(name);
                                }
                            }
                            else
                                break;                                                                      //Concidering upto level 3 (SECTION)
                            List<DocumentElement> siblingList = getSiblingsAtPreviousLevel(treeData, level);
                            temp.setIndex(siblingList.size()+1);
                            levelId.append(temp.getIndex());
                            temp.setLevelId(levelId.toString());
                            siblingList.add(temp);
                            break;
                        }
                    }
                }
                if(aSentence.toString().startsWith(docMeta.getEndText())) {
                    return treeData;
                }
            }
        }
        return treeData;
    }

    private Boolean isParagraphSelectorLevel(int level){
        int[] paraSelectorLevels = docMeta.getParagraphSelectorLevel();
        return IntStream.of(paraSelectorLevels).anyMatch(x -> x == level);
    }

    private List<DocumentElement> getSiblingsAtPreviousLevel(List<DocumentElement> tempTreeData, int level){
        levelId = new StringBuilder(docMeta.getMetaDocName()).append("-");
        if (tempTreeData.size() > 0) {
            for (int i = 1; i < level; i++) {
                try {
                    DocumentElement documentElement = tempTreeData.get(tempTreeData.size() - 1);
                    List<DocumentElement> childeren = documentElement.getChildren();
                    tempTreeData = childeren;
                    levelId.append(documentElement.getIndex()).append("-");
                }catch (Exception e){
                    return tempTreeData;
                }
            }
        }
        return tempTreeData;
    }

    private DocumentElement buildDocElement(LineElement lineElement){
        DocumentElement documentElement = new DocumentElement();
        documentElement.setContent(lineElement.toString().replaceAll(docMeta.getParaEndRegEx(), ""));
        return documentElement;
    }

    private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setMetaDocName("basel1");
        baselDocMeta.setStartPage(5);
        baselDocMeta.setEndPage(10);
        baselDocMeta.setStartText("Part 2: The First Pillar – Minimum Capital Requirements");
        baselDocMeta.setEndText("6. Interest rate risk in the banking book");

        int[] paraSelectorLevels = {2, 3};
        baselDocMeta.setParagraphSelectorLevel(paraSelectorLevels);
        baselDocMeta.setParaEndRegEx("[.]{5,}[0-9]{1,}|[.]{5,}|\\d+$");

        Map<Integer, String> levelSelector = new HashMap<>();
        levelSelector.put(1, "^Part[ ]{1,}[0-9]{1,}:");
        levelSelector.put(2, "^(I{1,}[a-z]|I{1,}|V{1,}|X{1,}|I{1,}V{1,}|V{1,}I{1,}).");
        levelSelector.put(3, "^[A-H].");
        levelSelector.put(4, "^[1-9].");

        baselDocMeta.setLevelSelector(levelSelector);

        return baselDocMeta;
    }

    public List<DocumentElement> getFlatParaList() {
        return flatParaList;
    }
}
