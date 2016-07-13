/*
 * Copyright 2012 Steve Chaloner
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.parabole.feed.platform.authorizations.security;

import be.objectify.deadbolt.java.DeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import play.libs.F;
import play.mvc.Http;


public abstract class AbstractDynamicResourceHandler implements DynamicResourceHandler
{
    public F.Promise<Boolean> checkPermission(final String permissionValue,
                                              final DeadboltHandler deadboltHandler,
                                              final Http.Context ctx)
    {
        return F.Promise.pure(false);
    }

    public F.Promise<Boolean> isAllowed(final String name,
                                        final String meta,
                                        final DeadboltHandler deadboltHandler,
                                        final Http.Context ctx)
    {
        return F.Promise.pure(false);
    }
}
