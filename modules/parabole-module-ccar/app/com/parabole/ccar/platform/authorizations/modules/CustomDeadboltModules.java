package com.parabole.ccar.platform.authorizations.modules;

import be.objectify.deadbolt.java.TemplateFailureListener;
import be.objectify.deadbolt.java.cache.HandlerCache;
import com.parabole.ccar.platform.authorizations.security.MyHandlerCache;
import com.parabole.ccar.platform.authorizations.security.MyCustomTemplateFailureListener;
import play.api.Configuration;
import play.api.Environment;
import play.api.inject.Binding;
import play.api.inject.Module;
import scala.collection.Seq;

import javax.inject.Singleton;


/**
 * Created by Sagiruddin on 2/25/2016.
 */

public class CustomDeadboltModules extends Module {

    @Override
    public Seq<Binding<?>> bindings(final Environment environment,
                                    final Configuration configuration)
    {
        return seq(bind(TemplateFailureListener.class).to(MyCustomTemplateFailureListener.class).in(Singleton.class),
                bind(HandlerCache.class).to(MyHandlerCache.class).in(Singleton.class));
    }

}