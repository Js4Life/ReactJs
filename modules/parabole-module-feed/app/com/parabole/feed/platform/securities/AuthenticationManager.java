// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// AuthenticationManager.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.securities;

import java.util.Set;
import org.apache.commons.lang3.Validate;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * AuthenticationManager to work over multiple Authentication Provider.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */

@Singleton
public class AuthenticationManager {

    final Set<AuthenticationProvider> authenticationProviders;

    @Inject
    public AuthenticationManager(final Set<AuthenticationProvider> authenticationProviders) {
        Validate.notEmpty(authenticationProviders, "authenticationProviders can not be Empty");
        this.authenticationProviders = authenticationProviders;
    }

    public boolean authenticate(final String username, final String password) {
        for (final AuthenticationProvider authenticationProvider : authenticationProviders) {
            final boolean authenticationResult = authenticationProvider.authenticate(username, password);
            if (authenticationResult) {
                return true;
            }
        }
        return false;
    }
}
