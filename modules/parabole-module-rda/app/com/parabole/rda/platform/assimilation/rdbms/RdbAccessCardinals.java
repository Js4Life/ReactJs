// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// RdbAccessCardinals.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.assimilation.rdbms;

import com.parabole.rda.platform.BaseDTO;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class RdbAccessCardinals extends BaseDTO {

    private static final long serialVersionUID = 6159703397759619316L;
    private String driver;
    private String url;
    private String userId;
    private String pwd;

    public RdbAccessCardinals() {
    }

    public RdbAccessCardinals(final String driver, final String url, final String userId, final String pwd) {
        super();
        this.driver = driver;
        this.url = url;
        this.userId = userId;
        this.pwd = pwd;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(final String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(final String pwd) {
        this.pwd = pwd;
    }
}
