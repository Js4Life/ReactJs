// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// GraphDbLinkDefinition.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.ccar.platform.graphdb;

import com.parabole.ccar.platform.BaseDTO;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 *
 * @author Subhasis Sanyal
 * @since v1.0
 */
public class GraphDbLinkDefinition extends BaseDTO {

    private static final long serialVersionUID = -6343301212346981492L;
    private String name;
    private String fromClass;
    private String toClass;
    private String fromProperty;
    private String toProperty;
    private int cardinality;
    private GraphDbLinkDefinition parentLink;
    private List<GraphDbLinkDefinition> childLinks;

    public String getFromClass() {
        return fromClass;
    }

    public void setFromClass(final String fromClass) {
        this.fromClass = fromClass;
    }

    public String getToClass() {
        return toClass;
    }

    public void setToClass(final String toClass) {
        this.toClass = toClass;
    }

    public String getFromProperty() {
        return fromProperty;
    }

    public void setFromProperty(final String fromProperty) {
        this.fromProperty = fromProperty;
    }

    public String getToProperty() {
        return toProperty;
    }

    public void setToProperty(final String toProperty) {
        this.toProperty = toProperty;
    }

    public int getCardinality() {
        return cardinality;
    }

    public void setCardinality(final int cardinality) {
        this.cardinality = cardinality;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public GraphDbLinkDefinition getParentLink() {
        return parentLink;
    }

    public void setParentLink(final GraphDbLinkDefinition parentLink) {
        this.parentLink = parentLink;
    }

    public void addChildLink(final GraphDbLinkDefinition lDef) {
        if (childLinks == null) {
            childLinks = new ArrayList<GraphDbLinkDefinition>();
        }
        lDef.setParentLink(this);
        childLinks.add(lDef);
    }

    public List<GraphDbLinkDefinition> getChildLinks() {
        return childLinks;
    }

    public String getLinkPath() {
        if ((parentLink != null) && (parentLink.getName().trim().length() > 0)) {
            return parentLink.getName() + "." + getName();
        } else {
            return getName();
        }
    }
}
