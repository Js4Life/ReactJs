// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// GuiceModule.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.global;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.parabole.rda.platform.securities.InternalAuthenticationProvider;
import com.parabole.rda.platform.securities.AuthenticationProvider;

/**
 * Guice Dependency Injection Registration.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class GuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        final Multibinder<AuthenticationProvider> actionBinder = Multibinder.newSetBinder(binder(), AuthenticationProvider.class);
        actionBinder.addBinding().to(InternalAuthenticationProvider.class);
    }
}
