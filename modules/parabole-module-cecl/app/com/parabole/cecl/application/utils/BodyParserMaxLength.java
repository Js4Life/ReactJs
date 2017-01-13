package com.parabole.cecl.application.utils;

import play.http.HttpErrorHandler;
import play.mvc.BodyParser;
import javax.inject.Inject;

/**
 * Created by parabole on 10/5/2016.
 */

public final class BodyParserMaxLength extends BodyParser.Json {
    @Inject
    public BodyParserMaxLength(HttpErrorHandler errorHandler) {
        super(4096 * 4096, errorHandler);
    }
}
