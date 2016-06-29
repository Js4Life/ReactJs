package com.parabole.ccar.application.controllers;

import play.mvc.Result;

/**
 * Dispatch the UI element by Direct Actions
 *
 * @author Sagiruddin Mondal
 * @since v1.0
 */
public class DispatcherController extends BaseController {

    @Override
    public Result login() {
        return ok(com.parabole.ccar.application.views.html.main.render("Manager", "Yet to be decided"));
    }

    public Result jsonHTTP() {
        return ok(com.parabole.ccar.application.views.html.JsonHTTP.render());
    }

    public Result landing() {
        return ok(com.parabole.ccar.application.views.html.landing.render());
    }

    public Result home() {
        return ok(com.parabole.ccar.application.views.html.dashboard.render());
    }

    public Result dashboardProfile() {
        return ok(com.parabole.ccar.application.views.html.dashboardProfile.render());
    }

    public Result dashboardGraphProfile() {
        return ok(com.parabole.ccar.application.views.html.dashboardGraphProfile.render());
    }

    public Result status() {
        return ok(com.parabole.ccar.application.views.html.status.render());
    }

    public Result schedule() {
        return ok(com.parabole.ccar.application.views.html.schedule.render());
    }

    public Result gapdetails() {
        return ok(com.parabole.ccar.application.views.html.gapdetails.render());
    }

    public Result statusProfile() {
        return ok(com.parabole.ccar.application.views.html.statusProfile.render());
    }

    public Result statusReport() {
        return ok(com.parabole.ccar.application.views.html.statusReport.render());
    }

    public Result userBuilder() {
        return ok(com.parabole.ccar.application.views.html.user.render());
    }

    public Result userDetail() {
        return ok(com.parabole.ccar.application.views.html.userdetail.render());
    }

    public Result userGroupDetail() {
        return ok(com.parabole.ccar.application.views.html.usergroupdetail.render());
    }

    public Result datasourceBuilder() {
        return ok(com.parabole.ccar.application.views.html.datasourcebuilder.render());
    }

    public Result dashboardSelector() {
        return ok(com.parabole.ccar.application.views.html.dashboardSelector.render());
    }

    public Result pageArchive() {
        return ok(com.parabole.ccar.application.views.html.pageArchive.render());
    }

    public Result ewgReport() {
        return ok(com.parabole.ccar.application.views.html.ewgReport.render());
    }

    public Result ewgIssue() {
        return ok(com.parabole.ccar.application.views.html.ewgIssue.render());
    }

    public Result glossary() {
        return ok(com.parabole.ccar.application.views.html.glossary.render());
    }

    public Result heatMap() {
        return ok(com.parabole.ccar.application.views.html.heatMap.render());
    }

    public Result scheduleTable() {
        return ok(com.parabole.ccar.application.views.html.scheduleTable.render());
    }
}