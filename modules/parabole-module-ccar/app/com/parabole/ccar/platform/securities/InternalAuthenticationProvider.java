// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// InternalAuthenticationProvider.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.securities;

import com.parabole.ccar.application.services.CoralUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import play.Logger;
import com.google.inject.Inject;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.platform.exceptions.AppException;

/**
 * AuthenticationProvider for Internal Users.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class InternalAuthenticationProvider implements AuthenticationProvider {

    @Inject
    private CoralUserService coralUserService;

    @Override
    public boolean authenticate(final String username, final String password) {
        Validate.notBlank(username, "'username' cannot be null!");
        Validate.notBlank(password, "'password' cannot be null!");
        try {
            final String storedPassword = coralUserService.getSpecificDocumentUsingIdAndColumnName(username, CCAppConstants.ATTR_DATABASE_PASSWORD_COLUMN_NAME);
            if (StringUtils.isNotBlank(storedPassword)) {
                return PasswordManager.check(password, storedPassword);
            }
        } catch (final AppException appEx) {
            Logger.error("Login Error", appEx);
        }
        return false;
    }
}
