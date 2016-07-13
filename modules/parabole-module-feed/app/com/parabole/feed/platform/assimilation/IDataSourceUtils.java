// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// IDataSourceUtils.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.feed.platform.assimilation;

import com.parabole.feed.platform.exceptions.AppException;
import org.json.JSONObject;

/**
 * Interface for DB | Excel operations
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */
public interface IDataSourceUtils {
    JSONObject exploreDataSource() throws AppException;
}
