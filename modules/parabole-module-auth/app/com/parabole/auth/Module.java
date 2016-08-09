package com.parabole.auth;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.parabole.auth.services.ApplicationTimer;
import com.parabole.auth.services.AtomicCounter;
import com.parabole.auth.services.Counter;
import com.parabole.platform.authorizations.securities.AuthenticationProvider;
import com.parabole.platform.authorizations.securities.InternalAuthenticationProvider;


import java.time.Clock;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `com.parabole.auth.Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `ui.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        // Use the system clock as the default implementation of Clock
        bind(Clock.class).toInstance(Clock.systemDefaultZone());
        // Ask Guice to create an instance of ApplicationTimer when the
        // application starts.
        bind(ApplicationTimer.class).asEagerSingleton();
        // Set AtomicCounter as the implementation for Counter.
        bind(Counter.class).to(AtomicCounter.class);

        final Multibinder<AuthenticationProvider> actionBinder = Multibinder.newSetBinder(binder(), AuthenticationProvider.class);
        actionBinder.addBinding().to(InternalAuthenticationProvider.class);
    }

}
