/**
 *
 */
package com.parabole.feed.platform.reasoner;

import com.parabole.feed.platform.BaseDTO;

/**
 * @author ATANU
 *
 */
public class BaseBindObj extends BaseDTO {
    private static final long serialVersionUID = -1172613597422125119L;

    private String type;
    private String value;
    private String datatype;

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(final String datatype) {
        this.datatype = datatype;
    }

}
