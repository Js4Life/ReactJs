// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// ReportAction.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.application.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.parabole.rda.application.global.RdaAppConstants;
import com.parabole.rda.platform.exceptions.AppErrorCode;
import com.parabole.rda.platform.exceptions.AppException;
import com.parabole.rda.platform.reports.pdf.PdfReportUtils;
import play.Logger;
import play.Play;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.Security;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.parabole.rda.platform.reports.pdf.PdfReport;

/**
 * Play Framework Action Controller dedicated for Reporting features.
 *
 * @author Koushik Chatterjee
 * @since v1.0
 */
@Security.Authenticated(ActionAuthenticator.class)
public class ReportAction extends BaseAction {

    @Inject
    protected PdfReportUtils pdfReportUtils;

    @BodyParser.Of(BodyParser.Json.class)
    public Result reportGenerator() throws AppException {
        final JsonNode reportData = request().body().asJson();
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            final PdfReport pdfReport = mapper.readValue(reportData.toString(), PdfReport.class);
            final byte[] pdfDataBytes = pdfReportUtils.pdfReportInfoToPdfFileData(pdfReport);
            final String userId = session().get(RdaAppConstants.USER_ID);
            final String path = Play.application().path().getAbsolutePath();
            final String reportDirFullPath = path + File.separator + RdaAppConstants.RDA_CFG_TYPE_REPORT;
            new File(reportDirFullPath).mkdir();
            final String userReportDirFullPath = reportDirFullPath + File.separator + userId;
            new File(userReportDirFullPath).mkdir();
            final String reportId = UUID.randomUUID().toString();
            final Map<String, String> reportObject = new HashMap<String, String>();
            reportObject.put("reportId", reportId);
            final String pdfFullPath = userReportDirFullPath + File.separator + reportId + ".pdf";
            Files.write(Paths.get(pdfFullPath), pdfDataBytes);
            response().setContentType(RdaAppConstants.MIME_JSON);
            return Results.ok(Json.toJson(reportObject));
        } catch (final IOException ioEx) {
            Logger.error("PDF report generation error", ioEx);
            throw new AppException(AppErrorCode.PDF_REPORT_ERROR);
        }
    }

    public Result reportDownload(final String reportId) throws AppException {
        final String userId = session().get(RdaAppConstants.USER_ID);
        final String absoluteDirPath = Play.application().path().getAbsolutePath();
        final String reportDirFullPath = absoluteDirPath + File.separator + RdaAppConstants.RDA_CFG_TYPE_REPORT + File.separator + userId;
        final String reportFileName = reportId + ".pdf";
        final File retFile = new java.io.File(reportDirFullPath + File.separator + reportFileName);
        response().setContentType(RdaAppConstants.MIME_PDF);
        return Results.ok(retFile);
    }
}
