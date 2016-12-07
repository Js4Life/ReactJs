package com.parabole.feed.contentparser;

import com.parabole.feed.contentparser.models.common.CharacterFormatInfo;
import com.parabole.feed.contentparser.models.common.TextFormatInfo;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFontDescriptor;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anish on 7/25/2016.
 */
public class PDFContentTagger extends PDFTextStripper {
    /**
     * Instantiate a new PDFTextStripper object.
     *
     * @throws IOException If there is an error loading the properties.
     */
    public PDFContentTagger( IDocIndexBuilder indexBuilder) throws IOException {
        super();
        isParaStart = true;
        docIndexBuilder = indexBuilder;
    }

    @Override
    protected void startDocument(PDDocument document) throws IOException {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endDocument(PDDocument document) throws IOException
    {
    }

    /**
     * Write something (if defined) at the start of a page.
     *
     * @throws IOException if something went wrong
     */
    protected void writePageStart() throws IOException
    {
        System.out.println("Start Page :: " + this.getCurrentPageNo());
    }

    /**
     * Write something (if defined) at the end of a page.
     *
     * @throws IOException if something went wrong
     */
    protected void writePageEnd() throws IOException
    {
        System.out.println("End Page :: " + this.getCurrentPageNo());
    }


    /**
     * Write out the article separator (div tag) with proper text direction
     * information.
     *
     * @param isLTR true if direction of text is left to right
     * @throws IOException
     *             If there is an error writing to the stream.
     */
    @Override
    protected void startArticle(boolean isLTR) throws IOException
    {

    }

    /**
     * Write out the article separator.
     *
     * @throws IOException
     *             If there is an error writing to the stream.
     */
    @Override
    protected void endArticle() throws IOException
    {

    }

    /**
     * Writes the paragraph end "&lt;/p&gt;" to the output. Furthermore, it will also clear the font state.
     *
     * {@inheritDoc}
     */
    @Override
    protected void writeParagraphStart() throws IOException
    {
        isParaStart = true;
    }

    /**
     * Writes the paragraph end "&lt;/p&gt;" to the output. Furthermore, it will also clear the font state.
     *
     * {@inheritDoc}
     */
    @Override
    protected void writeParagraphEnd() throws IOException
    {
        System.out.println();
        System.out.println("End Paragraph :: ********************* " + this.getCurrentPageNo());
        isParaStart = false;
    }

    /**
     * Write a string to the output stream, maintain font state, and escape some HTML characters.
     * The font state is only preserved per word.
     *
     * @param text The text to write to the stream.
     * @param textPositions the corresponding text positions
     * @throws IOException If there is an error writing to the stream.
     */
    @Override
    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
    {
        //System.out.println("Text :: " + text);
        TextFormatInfo formatInfo = new TextFormatInfo();
        formatInfo.setPageNum(this.getCurrentPageNo());
        formatInfo.setTextPositions(textPositions);
        if(!text.isEmpty() && text.length()>0) {
            populateTextFormattingInfo(text, textPositions, formatInfo);
            docIndexBuilder.addChunk(text, formatInfo, isParaStart);
        }
        /*if( isParaStart )
            isParaStart = false;*/
    }

    private void populateTextFormattingInfo(String text,  List<TextPosition> textPositions, TextFormatInfo formatInfo) {
        //We have all the positions
        int numBoldChars = 0, numItalicChars = 0;
        int normalChars = 0;
        int charsToCheck = text.length() == textPositions.size() ? text.length() : textPositions.size();
        float totXHeight = 0;
        List<CharacterFormatInfo> formatInfos = new ArrayList<>();
        for(int i = 0; i < charsToCheck;i++)
        {
            CharacterFormatInfo characterFormatInfo = new CharacterFormatInfo();
            formatInfos.add(characterFormatInfo);
            TextPosition textPosition = textPositions.get(i);
            PDFontDescriptor descriptor = textPosition.getFont().getFontDescriptor();

            float xHeight = textPosition.getFontSizeInPt();//descriptor.getXHeight();
            characterFormatInfo.setHeight(xHeight);
            totXHeight += xHeight;
            if( isBold(descriptor)) {
                numBoldChars++;
                characterFormatInfo.setIsBold(true);
            }
            else{
                if(isItalic(descriptor)) {
                    numItalicChars++;
                    characterFormatInfo.setIsItalics(true);
                }
                else
                    normalChars++;
            }
        }
        formatInfo.setCharacterFormatInfos(formatInfos);
        if( numBoldChars > normalChars)
            formatInfo.setIsBold(true);
        else
            formatInfo.setIsBold(false);

        if( numItalicChars > normalChars)
            formatInfo.setIsItalics(true);
        else
            formatInfo.setIsItalics(false);

        formatInfo.setAverageTextHeight(totXHeight/text.length());
    } ///////////// DEBUG POINT WAS HREEE
    /**
     * Write a string to the output stream and escape some HTML characters.
     *
     * @param chars String to be written to the stream
     * @throws IOException
     *             If there is an error writing to the stream.
     */
    @Override
    protected void writeString(String chars) throws IOException
    {
    }



    /**
     * Write the line separator value to the output stream.
     *
     * @throws IOException If there is a problem writing out the lineseparator to the document.
     */
    protected void writeLineSeparator() throws IOException
    {
    }

    /**
     * Write the word separator value to the output stream.
     *
     * @throws IOException If there is a problem writing out the wordseparator to the document.
     */
    protected void writeWordSeparator() throws IOException
    {
    }

    private boolean isBold(PDFontDescriptor descriptor)
    {
        if (descriptor.isForceBold())
        {
            return true;
        }
        return descriptor.getFontName().contains("Bold");
    }

    private boolean isItalic(PDFontDescriptor descriptor)
    {
        if (descriptor.isItalic())
        {
            return true;
        }
        return descriptor.getFontName().contains("Italic");
    }

    private boolean isParaStart;
    private IDocIndexBuilder docIndexBuilder;
}
