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
package com.parabole.ccar.platform.authorizations.security;

import be.objectify.deadbolt.core.models.Subject;
import be.objectify.deadbolt.java.AbstractDeadboltHandler;
import be.objectify.deadbolt.java.DynamicResourceHandler;
import com.parabole.ccar.application.global.CCAppConstants;
import com.parabole.ccar.platform.authorizations.models.UserModel;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;

import java.util.Optional;

import static play.mvc.Http.Context.Implicit.session;



public class BuggyDeadboltHandler extends AbstractDeadboltHandler
{
    public F.Promise<Optional<Result>> beforeAuthCheck(Http.Context context)
    {
        // returning null means that everything is OK.  Return a real result if you want a redirect to a login page or
        // somewhere else
        return F.Promise.promise(Optional::empty);
    }

    public F.Promise<Optional<Subject>> getSubject(Http.Context context)
    {
        // in a real application, the user name would probably be in the session following a login process
        return F.Promise.promise(() -> Optional.ofNullable(UserModel.findByUserName(session().get(CCAppConstants.USER_NAME))));
    }

    public F.Promise<Optional<DynamicResourceHandler>> getDynamicResourceHandler(Http.Context context)
    {
        return F.Promise.promise(() -> Optional.of(new MyDynamicResourceHandler()));
    }

    @Override
    public F.Promise<Result> onAuthFailure(Http.Context context,
                                           String content)
    {
        throw new RuntimeException("An exception occurred in onAuthFailure");
    }
}
