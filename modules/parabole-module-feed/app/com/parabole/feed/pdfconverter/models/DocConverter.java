package com.parabole.feed.pdfconverter.models;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by parabole on 12/23/2016.
 */
public class DocConverter {
    String filePath;

    public DocConverter(String filePath) {
        this.filePath = filePath;
    }

    public List<String> genarateImage(int fromPage, int toPage, String format){
        List<String> images = new ArrayList<>();

        try {
            PDDocument doc = PDDocument.load(new File(this.filePath));
            PDFRenderer renderer = new PDFRenderer(doc);
            for ( ; fromPage <= toPage ; fromPage++){
                File aFile = new File("page-" + fromPage + "." + format);
                OutputStream output = new FileOutputStream(aFile);
                BufferedImage bim = renderer.renderImageWithDPI(fromPage, 300, ImageType.RGB);
                /*Boolean status = ImageIOUtil.writeImage(bim, format, output, 300);
                images.add(output);
                System.out.println("status = " + status);*/
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write( bim, format, baos );
                baos.flush();
                byte[] imageInByte = baos.toByteArray();
                baos.close();
                String base64Img = new String(Base64.getEncoder().encode(imageInByte));
                base64Img = "data:image/" + format + ";base64," + base64Img;
                images.add(base64Img);
            }
            doc.close();
        } catch(Exception e){
            e.printStackTrace();
        }
        return images;
    }
}
