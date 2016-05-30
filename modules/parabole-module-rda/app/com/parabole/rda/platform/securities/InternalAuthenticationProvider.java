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
package com.parabole.rda.platform.securities;

import com.google.inject.Inject;
import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.application.services.CoralUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import play.Logger;

import java.text.SimpleDateFormat;

/**
 * AuthenticationProvider for Internal Users.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class InternalAuthenticationProvider implements AuthenticationProvider {

    @Inject
    private CoralUserService coralUserService;
   // private static final License license = new License();
    private static final SimpleDateFormat licenseDateFormat = new SimpleDateFormat("dd-MM-yyyy");

/*    static {
        try {
            license.loadKeyRingFromResource("pubring.gpg", null);
            final InputStream licenseFileInputStream = AppUtils.getClasspathFileInputStream(RdaAppConstants.LICENSE_FILE);
            license.setLicenseEncoded(licenseFileInputStream);
        } catch (final Exception ex) {
            Logger.error("GPG Exception: ", ex);
        }
    }*/

    @Override
    public boolean authenticate(final String username, final String password) {
        Validate.notBlank(username, "'username' cannot be null!");
        Validate.notBlank(password, "'password' cannot be null!");
        try {
            final String storedPassword = coralUserService.getSpecificDocumentUsingIdAndColumnName(username, RdaAppConstants.ATTR_DATABASE_PASSWORD_COLUMN_NAME);
            if (StringUtils.isNotBlank(storedPassword)) {
                return PasswordManager.check(password, storedPassword);
            }
        } catch (final Exception appEx) {
            Logger.error("Login Error", appEx);
        }
        return false;
    }

/*    public boolean checkLicense() throws ParseException {
        final String edition = license.getFeature("Edition");
        if (edition.equalsIgnoreCase("Trial")) {
            final Date today = new Date();
            final Date expiryDate = licenseDateFormat.parse(license.getFeature("Expiry"));
            if (today.after(expiryDate)) {
                return false;
            }
        }
        return true;
    }*/
}
