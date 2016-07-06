// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// DbModelAttribute.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.assimilation.rdbms;

import com.parabole.cecl.platform.BaseDTO;

/**
 * DbModel Database Attribute.
 *
 * @author Atanu Mallick
 * @since v1.0
 */
public class DbModelAttribute extends BaseDTO {

    private static final long serialVersionUID = 8305530436542908769L;
    private String text;
    private String type;
    private String dataType;
    private String path;

    public DbModelAttribute() {
    }

    public DbModelAttribute(final String name, final String dataType) {
        this.text = name;
        this.dataType = dataType;
    }

    public String getText() {
        return text;
    }

    public void setText(final String name) {
        this.text = name;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(final String dataType) {
        this.dataType = dataType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
