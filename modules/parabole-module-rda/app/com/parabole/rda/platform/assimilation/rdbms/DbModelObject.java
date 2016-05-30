// =============================================================================
// Copyright (C) 2014-2015, Parabole LLC
// Registered Address: 14302 Goodrow Ct, Princeton, NJ 08540, USA
// Web: http://www.mindparabole.com
// All Rights Reserved.
//
// DbModelObject.java
//
// This source code is available under the terms of the GNU Affero General
// Public License (AGPL) version 3. Please see LICENSE.txt for full licensing
// terms, including the availability of proprietary exceptions for closed-source
// commercial applications and Acuity Community IP Partnership Programme.
// =============================================================================
package com.parabole.rda.platform.assimilation.rdbms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import com.parabole.rda.platform.BaseDTO;

/**
 * DbModel DbModelObject(Base Class).
 *
 * @author Atanu Mallick
 * @since v1.0
 */
public class DbModelObject<T> extends BaseDTO {

    private static final long serialVersionUID = -5113508569778216552L;
    protected String text;
    protected String type;
    private final List<T> children = new ArrayList<T>();

    public String getText() {
        return text;
    }

    public void setText(final String name) {
        this.text = name;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public List<T> getChildren() {
        return Collections.unmodifiableList(children);
    }

    public void setChildren(final List<T> objects) {
        children.clear();
        appendChildren(objects);
    }

    public void appendChildren(final List<T> objects) {
        if (CollectionUtils.isNotEmpty(objects)) {
            children.addAll(objects);
        }
    }

    public void clearChildren() {
        this.children.clear();
    }

    public void addChild(final T object) {
        if (null != object) {
            this.children.add(object);
        }
    }

    public void removeChild(final T object) {
        if (null != object) {
            this.children.remove(object);
        }
    }
}
