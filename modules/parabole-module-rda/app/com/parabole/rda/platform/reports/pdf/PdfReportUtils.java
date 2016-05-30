// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// PdfReportUtils.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.reports.pdf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.parabole.rda.platform.exceptions.AppErrorCode;
import com.parabole.rda.platform.exceptions.AppException;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import play.Logger;
import com.google.inject.Singleton;
import com.itextpdf.awt.geom.Dimension;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.ElementHandler;
import com.itextpdf.tool.xml.Writable;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.itextpdf.tool.xml.pipeline.WritableElement;

/**
 * PDF Report Utility.
 *
 * @author Koushik Chatterjee
 * @since v1.0
 */
@Singleton
public class PdfReportUtils {

    public static enum PdfLayoutPattern {
        ONE_BY_ONE("1by1"), ONE_BY_TWO("1by2"), TWO_BY_ONE("2by1"), TWO_BY_TWO("2by2");
        private final String style;

        private PdfLayoutPattern(final String style) {
            this.style = style;
        }

        @Override
        public String toString() {
            return style;
        }

        public static PdfLayoutPattern get(final String inputStyle) {
            for (final PdfLayoutPattern pdfLayoutPattern : PdfLayoutPattern.values()) {
                if (inputStyle.equalsIgnoreCase(pdfLayoutPattern.style)) {
                    return pdfLayoutPattern;
                }
            }
            return null;
        }
    }

    public static enum PdfHeight {
        LARGE_MODE_ROW(640), SMALL_MODE_ROW(320);
        private final int height;

        private PdfHeight(final int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }
    }

    public byte[] pdfReportInfoToPdfFileData(final PdfReport pdfReport) throws AppException {
        final Document document = new Document(PageSize.A4, 50, 50, 100, 80);
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, byteStream);
            document.open();
            setReportName(document, pdfReport.getName());
            for (final PdfReportPage pdfReportPage : pdfReport.getPages()) {
                final PdfReportLayout pdfReportLayout = pdfReportPage.getLayout();
                final PdfLayoutPattern pdfPatternLayout = PdfLayoutPattern.get(pdfReportLayout.getName());
                if (null != pdfPatternLayout) {
                    reOrderCells(pdfReportLayout, pdfPatternLayout);
                    appendPageToDocument(document, pdfReportLayout, pdfPatternLayout);
                }
            }
            document.close();
            return byteStream.toByteArray();
        } catch (final Exception ex) {
            Logger.error("Could not create Pdf File data", ex);
            throw new AppException(AppErrorCode.PDF_REPORT_ERROR);
        }
    }

    private void setReportName(final Document document, final String reportName) throws DocumentException {
        final PdfPTable headerTable = new PdfPTable(1);
        final Font font = FontFactory.getFont("Arial", 28f);
        font.setStyle("bold");
        final PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(5);
        cell.setBorderColor(BaseColor.BLUE);
        cell.setPaddingTop(300);
        cell.setFixedHeight(650f);
        final Paragraph paragraphTitle = new Paragraph();
        paragraphTitle.setAlignment(Element.ALIGN_CENTER);
        paragraphTitle.add(new Chunk(reportName));
        paragraphTitle.setFont(font);
        cell.addElement(paragraphTitle);
        font.setColor(BaseColor.BLUE);
        headerTable.addCell(cell);
        document.add(headerTable);
    }

    private void reOrderCells(final PdfReportLayout pdfReportLayout, final PdfLayoutPattern pdfPatternLayout) {
        switch (pdfPatternLayout) {
            case ONE_BY_ONE:
                break;
            case ONE_BY_TWO:
                break;
            case TWO_BY_ONE:
                final List<PdfReportCell> pdfReportCells = new ArrayList<PdfReportCell>();
                pdfReportCells.add(pdfReportLayout.getCells().get(0));
                pdfReportCells.add(pdfReportLayout.getCells().get(2));
                pdfReportCells.add(pdfReportLayout.getCells().get(1));
                pdfReportLayout.setCells(pdfReportCells);
                break;
            case TWO_BY_TWO:
                break;
            default:
                break;
        }
    }

    private void appendPageToDocument(final Document document, final PdfReportLayout pdfReportLayout, final PdfLayoutPattern pdfPatternLayout) throws DocumentException, MalformedURLException, IOException, TranscoderException {
        document.newPage();
        final int totalColumns = (pdfPatternLayout == PdfLayoutPattern.ONE_BY_ONE) ? 1 : 2;
        final PdfPTable table = new PdfPTable(totalColumns);
        table.setWidthPercentage(100);
        final List<PdfReportCell> pdfReportCells = pdfReportLayout.getCells();
        for (final PdfReportCell pdfReportCell : pdfReportCells) {
            final Dimension dim = calculateDimention(pdfPatternLayout, pdfReportCell.getIndex());
            final PdfPCell cell = createPdfCell(pdfReportCell, dim);
            table.addCell(cell);
        }
        document.add(table);
    }

    private Dimension calculateDimention(final PdfLayoutPattern pdfPatternLayout, final int boxIndex) {
        switch (pdfPatternLayout) {
            case ONE_BY_TWO:
                if (boxIndex == 0) {
                    return new Dimension(0, PdfHeight.LARGE_MODE_ROW.getHeight());
                } else {
                    return new Dimension(0, PdfHeight.SMALL_MODE_ROW.getHeight());
                }
            case TWO_BY_ONE:
                if ((boxIndex == 0) || (boxIndex == 2)) {
                    return new Dimension(0, PdfHeight.SMALL_MODE_ROW.getHeight());
                } else {
                    return new Dimension(0, PdfHeight.LARGE_MODE_ROW.getHeight());
                }
            default:
                return new Dimension(0, PdfHeight.SMALL_MODE_ROW.getHeight());
        }
    }

    private PdfPCell createPdfCell(final PdfReportCell pdfReportCell, final Dimension dim) throws BadElementException, MalformedURLException, IOException, TranscoderException {
        final PdfPCell cell = new PdfPCell();
        final Paragraph prCellData = new Paragraph();
        cell.setFixedHeight((float) dim.getHeight());
        prCellData.setAlignment(Element.ALIGN_CENTER);
        if (dim.getHeight() == PdfHeight.LARGE_MODE_ROW.getHeight()) {
            cell.setPaddingTop(200);
        } else {
            cell.setPaddingTop(10);
        }
        prCellData.add(Chunk.NEWLINE);
        final String graphSvg = pdfReportCell.getGraphSvg();
        if (StringUtils.isNotBlank(graphSvg)) {
            final Image image = decodeSvgStringToITextImage(graphSvg);
            image.scalePercent(40f, 50f);
            prCellData.add(new Chunk(image, 0, 0, true));
            prCellData.add(Chunk.NEWLINE);
        }
        final String graphDesc = pdfReportCell.getGraphDesc();
        if (StringUtils.isNotBlank(graphDesc)) {
            final StringReader strReader = new StringReader(graphDesc);
            final Paragraph paragraph = new Paragraph();
            XMLWorkerHelper.getInstance().parseXHtml(new ElementHandler() {
                @Override
                public void add(final Writable w) {
                    if (w instanceof WritableElement) {
                        final List<Element> elements = ((WritableElement) w).elements();
                        for (final Element element : elements) {
                            paragraph.add(element);
                        }
                    }

                }
            }, strReader);
            paragraph.setAlignment(Element.ALIGN_CENTER);
            prCellData.add(paragraph);
        }
        if (dim.getHeight() == PdfHeight.LARGE_MODE_ROW.getHeight()) {
            cell.setRowspan(2);
        }
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell.addElement(prCellData);
        return cell;
    }

    private Image decodeSvgStringToITextImage(final String svgContent) throws IOException, TranscoderException, MalformedURLException, BadElementException {
        final Reader stringReader = new StringReader(svgContent);
        final TranscoderInput transcoderInput = new TranscoderInput(stringReader);
        final PNGTranscoder transcoder = new PNGTranscoder();
        final ByteArrayOutputStream byteOpStream = new ByteArrayOutputStream();
        final TranscoderOutput transcoderOutput = new TranscoderOutput(byteOpStream);
        transcoder.transcode(transcoderInput, transcoderOutput);
        byteOpStream.flush();
        final byte[] bytes = byteOpStream.toByteArray();
        IOUtils.closeQuietly(byteOpStream);
        return Image.getInstance(bytes);
    }
}
