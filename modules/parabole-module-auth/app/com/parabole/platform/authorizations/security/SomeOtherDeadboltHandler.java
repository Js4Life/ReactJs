package com.parabole.platform.authorizations.security;

import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import be.objectify.deadbolt.java.ExecutionContextProvider;
import be.objectify.deadbolt.java.models.Subject;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Example handler just to show how to handle DI of multiple implementations of DeadboltHandler.
 *
 * @author Steve Chaloner (steve@objectify.be)
 */
@HandlerQualifiers.SomeOtherHandler
public class SomeOtherDeadboltHandler extends AbstractDeadboltHandler
{
    @Inject
    public SomeOtherDeadboltHandler(final ExecutionContextProvider ecProvider)
    {
        super(ecProvider);
    }

    @Override
    public CompletionStage<Optional<? extends Subject>> getSubject(final Http.Context context)
    {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<Result>> beforeAuthCheck(final Http.Context context)
    {
        return CompletableFuture.completedFuture(Optional.empty());
    }

    @Override
    public CompletionStage<Optional<DynamicResourceHandler>> getDynamicResourceHandler(final Http.Context context)
    {
        return CompletableFuture.completedFuture(Optional.empty());
    }
}
