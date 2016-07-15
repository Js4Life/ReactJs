package com.parabole.feed.platform.exceptions;

import java.text.MessageFormat;
import java.util.Properties;
import com.parabole.feed.platform.utils.AppUtils;

public final class AppException extends Exception {

    private static final long serialVersionUID = -221927109584074396L;
    private static Properties errorMessageProperties;
    private final String errorMessage;

    static {
        try {
            errorMessageProperties = AppUtils.loadProperties("error.properties");
        } catch (final AppException appEx) {
            throw new ExceptionInInitializerError();
        }
    }

    public AppException(final AppErrorCode errorCode, final Object[]... errorMessageParams) {
        String userMessage = errorMessageProperties.getProperty(errorCode.name());
        if (errorMessageParams.length > 0) {
            final MessageFormat messageFormat = new MessageFormat(userMessage);
            userMessage = messageFormat.format(errorMessageParams);
        }
        this.errorMessage = userMessage;
    }

    @Override
    public String getMessage() {
        return this.errorMessage;
    }
}