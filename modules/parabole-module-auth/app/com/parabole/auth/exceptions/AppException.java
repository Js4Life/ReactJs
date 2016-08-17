package com.parabole.auth.exceptions;

import com.parabole.auth.utils.AppUtils;

import java.text.MessageFormat;
import java.util.Properties;

/**
 * Base Exception Class.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public final class AppException extends Exception {

    private static final long serialVersionUID = -221927109584074396L;
    private static Properties errorMessageProperties;
    private final String errorMessage;



    public AppException(final AppErrorCode errorCode, final Object[]... errorMessageParams) {
        String userMessage = errorMessageProperties.getProperty(errorCode.name());
        if (errorMessageParams.length > 0) {
            final MessageFormat messageFormat = new MessageFormat(userMessage);
            userMessage = messageFormat.format(errorMessageParams);
        }
        this.errorMessage = userMessage;
    }

}