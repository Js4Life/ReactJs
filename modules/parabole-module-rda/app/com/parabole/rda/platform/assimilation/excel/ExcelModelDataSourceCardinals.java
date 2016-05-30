// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ExcelModelDataSourceCardinals.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.assimilation.excel;

import com.parabole.rda.platform.assimilation.BaseDataSourceCardinals;

/**
 * TODO
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */
public class ExcelModelDataSourceCardinals extends BaseDataSourceCardinals {

    private static final long serialVersionUID = -3268666847608776202L;
    private String fileName;
    private byte[] bytes;

    public ExcelModelDataSourceCardinals(final String dsName, final String fileName, final byte[] bytes) {
        this.fileName = fileName;
        this.bytes = bytes;
        this.dsName = dsName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(final byte[] bytes) {
        this.bytes = bytes;
    }
}
