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
    DocumentElement lastInsertedParentNode;
    DocumentElement lastInsertedChildNode;

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

    /*private List<DocumentElement> buildTree(List<ParagraphElement> paraList) {            //coordinate wise indentation
        for(ParagraphElement aPara : paraList) {
            LineElement lineElement = aPara.getLines().get(0);
            float currentStartX = lineElement.getLineStart();
            DocumentElement temp = buildDocElement(aPara, currentStartX);

            flatParaList.add(temp);
            if(lastInsertedParentNode != null){
                if(currentStartX > lastInsertedParentNode.getStartX()){
                    if((currentStartX > lastInsertedChildNode.getStartX()) && (lastInsertedParentNode.getStartX() != lastInsertedChildNode.getStartX())){
                        lastInsertedChildNode.addChildren(temp);
                        lastInsertedParentNode = lastInsertedChildNode;
                        lastInsertedChildNode = temp;
                    } else {
                        lastInsertedChildNode = temp;
                        lastInsertedParentNode.addChildren(lastInsertedChildNode);
                    }
                } else {
                    List<DocumentElement> siblingList = getSiblingsAtPreviousLevel(treeData, currentStartX);
                    if(siblingList != null){
                        lastInsertedChildNode = lastInsertedParentNode = temp;
                        siblingList.add(lastInsertedParentNode);
                    }
                }
            } else {
                lastInsertedChildNode = lastInsertedParentNode = temp;
                treeData.add(lastInsertedParentNode);
            }
        }
        return treeData;
    }   */

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
                            //else temp.setElementType(DocumentElement.ElementTypes.OTHER);
                            else
                                break;                                                                      //Concidering upto level 3 (SECTION)
                            List<DocumentElement> siblingList = getSiblingsAtPreviousLevel(treeData, level);
                            temp.setIndex(siblingList.size());
                            siblingList.add(temp);
                            break;
                        }
                    }
                }
                if(aSentence.toString().startsWith(docMeta.getEndText()))
                    return treeData;
            }
        }
        return treeData;
    }

    private Boolean isParagraphSelectorLevel(int level){
        int[] paraSelectorLevels = docMeta.getParagraphSelectorLevel();
        return IntStream.of(paraSelectorLevels).anyMatch(x -> x == level);
    }

    private List<DocumentElement> getSiblingsAtPreviousLevel(List<DocumentElement> tempTreeData, int level){
        if (tempTreeData.size() > 0) {
            for (int i = 1; i < level; i++) {
                try {
                    List<DocumentElement> childeren = tempTreeData.get(tempTreeData.size() - 1).getChildren();
                    tempTreeData = childeren;
                }catch (Exception e){
                    return tempTreeData;
                }
            }
        }
        return tempTreeData;
    }

    /*private List<DocumentElement> getSiblingsAtPreviousLevel(List<DocumentElement> tempTreeData, float currentStartX){
        for (DocumentElement documentElement : tempTreeData) {
            if (currentStartX == documentElement.getStartX()) {
                return tempTreeData;
            } else {
                if(documentElement.getChildren().size() > 0)
                    return getSiblingsAtPreviousLevel(documentElement.getChildren(), currentStartX);
            }
        }
        return tempTreeData;
    }

    private DocumentElement buildDocElement(ParagraphElement paragraphElement, float currentStartX){
        DocumentElement documentElement = new DocumentElement();
        documentElement.setStartX(currentStartX);
        documentElement.setName(paragraphElement.toString().replaceAll(docMeta.getParaEndRegEx(), ""));
        return documentElement;
    }*/

    private DocumentElement buildDocElement(LineElement lineElement){
        DocumentElement documentElement = new DocumentElement();
        documentElement.setContent(lineElement.toString().replaceAll(docMeta.getParaEndRegEx(), ""));
        return documentElement;
    }

    private BaselDocMeta getGlossaryMetadata() {
        BaselDocMeta baselDocMeta = new BaselDocMeta();
        baselDocMeta.setStartPage(5);
        baselDocMeta.setEndPage(10);
        baselDocMeta.setStartText("Part 2: The First Pillar – Minimum Capital Requirements");
        baselDocMeta.setEndText("9. Model validation standards");

        int[] paraSelectorLevels = {2, 3};
        baselDocMeta.setParagraphSelectorLevel(paraSelectorLevels);
        baselDocMeta.setParaEndRegEx("[.]{5,}[0-9]{1,}|[.]{5,}|\\d+");

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
