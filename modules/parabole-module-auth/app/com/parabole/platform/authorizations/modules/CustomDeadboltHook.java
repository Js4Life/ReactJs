package com.parabole.platform.authorizations.modules;



import be.objectify.deadbolt.java.DeadboltExecutionContextProvider;
import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DefaultDeadboltExecutionContextProvider;
import be.objectify.deadbolt.java.TemplateFailureListener;
import be.objectify.deadbolt.java.cache.HandlerCache;
import com.parabole.platform.authorizations.security.*;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

import javax.inject.Singleton;

/**
 * @author Sagiruddin Mondal
 */
public class CustomDeadboltHook extends Module
{
    @Override
    public Seq<Binding<?>> bindings(final Environment environment,
                                    final Configuration configuration)
   {
        return seq(bind(TemplateFailureListener.class).to(MyCustomTemplateFailureListener.class).in(Singleton.class),
                   // it's not necessary to make this execution context provider binding, this is just for testing
                bind(DeadboltExecutionContextProvider.class).to(DefaultDeadboltExecutionContextProvider.class).in(Singleton.class),
                bind(DeadboltHandler.class).qualifiedWith(HandlerQualifiers.MainHandler.class).to(MyDeadboltHandler.class).in(Singleton.class),
                bind(DeadboltHandler.class).qualifiedWith(HandlerQualifiers.SomeOtherHandler.class).to(SomeOtherDeadboltHandler.class).in(Singleton.class),
                bind(HandlerCache.class).to(MyHandlerCache.class).in(Singleton.class),
                bind(CompositeConstraints.class).toSelf().eagerly());
    }



/*        @Override
        public Seq<Binding<?>> bindings(final Environment environment, final Configuration configuration) {
            return seq(bind(HandlerCache.class).to(MyHandlerCache.class).in(Singleton.class),
                    bind(HandlerCache.class).to(MyHandlerCache.class).in(Singleton.class));
        }*/
}
