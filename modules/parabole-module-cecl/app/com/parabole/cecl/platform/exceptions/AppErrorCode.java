// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// AppErrorCode.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.cecl.platform.exceptions;

/**
 * Error Codes.
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public enum AppErrorCode {

    // System Exception
    SYSTEM_EXCEPTION,

    // Security Exception
    SECURITY_EXCEPTION,

    // Graph DB Operation Exception
    GRAPH_DB_OPERATION_EXCEPTION,

    // PDF Report Generation Error
    PDF_REPORT_ERROR,

    // Excel Operation Error
    EXCEL_OPERATION_ERROR,

    // RDBMS Operation Error
    RDBMS_OPERATION_ERROR,

    // File Not Found
    FILE_NOT_FOUND
}
