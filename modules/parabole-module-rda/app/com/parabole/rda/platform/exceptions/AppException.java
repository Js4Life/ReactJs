// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// AppException.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.exceptions;

import java.text.MessageFormat;
import java.util.Properties;

import com.parabole.rda.platform.utils.AppUtils;

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