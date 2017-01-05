package com.parabole.feed.application.services;

import com.google.common.io.ByteSource;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.parabole.feed.application.exceptions.AppException;
import com.parabole.feed.application.utils.AppUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import play.Configuration;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static org.apache.tools.ant.types.resources.MultiRootFileSet.SetType.file;

/**
 * Created by Sagir on 25-08-2016.
 */
public class CommonService {

    @Inject
    Configuration configuration;

    @Inject
    LightHouseService lightHouseService;


    public String getBaseUrl(){

        return configuration.getString("application.baseUrl");
    }

    public String updateParagraphContentsFromExcel(String fName) throws IOException, AppException {

        InputStream file = AppUtils.getClasspathFileInputStream("files//"+fName);

        XSSFWorkbook workbook = new XSSFWorkbook(file);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Map<String, String> mapOfParagraphsAgainstParagraphID = new HashMap<>();
        Iterator<Row> rowIterator = sheet.iterator();
        while (rowIterator.hasNext())
        {
            Row row = rowIterator.next();
            mapOfParagraphsAgainstParagraphID.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
        }

        lightHouseService.updateParagraphsByIDs(mapOfParagraphsAgainstParagraphID);

        file.close();
        return "{status : ok}";
    }

}
