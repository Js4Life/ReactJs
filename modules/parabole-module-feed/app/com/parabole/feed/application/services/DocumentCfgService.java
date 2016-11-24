package com.parabole.feed.application.services;



import play.Configuration;
import play.api.Play;

import javax.inject.Inject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;

/**
 * Created by parabole on 11/22/2016.
 */
public class DocumentCfgService {

    @Inject
    Configuration configuration;

    public List<HashMap<String, String>> getFeedFileNames(){
        List<HashMap<String, String>> files = new ArrayList<>();
        String subPath = configuration.getString("application.feedFilePath");
        String path = Play.current().path().getAbsolutePath().concat(subPath);
        File folder = new File(path);
        File[] allFiles = folder.listFiles();
        for (File file: allFiles) {
            HashMap<String, String> aFile = new HashMap<>();
            if(file.isFile()){
                String fileName = file.getName();
                String id = fileName.substring(0, fileName.indexOf('.'));
                String fileType = fileName.substring(fileName.indexOf('.')+1, fileName.length());
                aFile.put("id", id);
                aFile.put("name", fileName);
                aFile.put("type", fileType);
                files.add(aFile);
            }
        }
        return files;
    }

    public Boolean uploadFeedFile(String fileName, String fileData){
        String subPath = configuration.getString("application.feedFilePath");
        String path = Play.current().path().getAbsolutePath().concat(subPath).concat("/").concat(fileName);
        try {
            String temp = fileData.substring(fileData.indexOf(',') + 1);
            byte[] bytes = Base64.getDecoder().decode(temp);
            FileOutputStream fos = new FileOutputStream(new File(path));
            fos.write(bytes);
        } catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
