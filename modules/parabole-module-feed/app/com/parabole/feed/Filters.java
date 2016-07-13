package com.parabole.feed;

import com.parabole.feed.filters.ExampleFilter;
import play.Environment;
import play.Mode;
import play.http.HttpFilters;
import play.mvc.EssentialFilter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class configures com.parabole.feed.filters that run on every request. This
 * class is queried by Play to get a list of com.parabole.feed.filters.
 *
 * Play will automatically use com.parabole.feed.filters from any class called
 * <code>com.parabole.feed.Filters</code> that is placed the root package. You can load com.parabole.feed.filters
 * from a different class by adding a `play.http.com.parabole.feed.filters` setting to
 * the <code>ui.conf</code> configuration file.
 */
@Singleton
public class Filters implements HttpFilters {

    private final Environment env;
    private final EssentialFilter exampleFilter;

    /**
     * @param env Basic environment settings for the current application.
     * @param exampleFilter A demonstration filter that adds a header to
     */
    @Inject
    public Filters(Environment env, ExampleFilter exampleFilter) {
        this.env = env;
        this.exampleFilter = exampleFilter;
    }

    @Override
    public EssentialFilter[] filters() {
      // Use the example filter if we're running development mode. If
      // we're running in production or test mode then don't use any
      // com.parabole.feed.filters at all.
      if (env.mode().equals(Mode.DEV)) {
          return new EssentialFilter[] { exampleFilter };
      } else {
         return new EssentialFilter[] {};      
      }
    }

}
