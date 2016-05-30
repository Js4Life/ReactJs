// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// OperationResult.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.assimilation;

import com.parabole.ccar.platform.BaseDTO;

/**
 * Back-end Operations Result Handler.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class OperationResult extends BaseDTO {

    private static final long serialVersionUID = -6343301279206981492L;

    private final boolean status;
    private final String errorMessage;

    public OperationResult(final boolean status) {
        this.status = status;
        this.errorMessage = null;
    }

    public OperationResult(final boolean status, final String errorMessage) {
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return status ? true : false;
    }

    public boolean isStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
