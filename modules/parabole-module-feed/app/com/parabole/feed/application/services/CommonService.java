package com.parabole.feed.application.services;

import com.google.inject.Inject;
import play.Configuration;

/**
 * Created by Sagir on 25-08-2016.
 */
public class CommonService {


    @Inject
    Configuration configuration;


    public String getBaseUrl(){

        return configuration.getString("application.baseUrl");
    }

}
