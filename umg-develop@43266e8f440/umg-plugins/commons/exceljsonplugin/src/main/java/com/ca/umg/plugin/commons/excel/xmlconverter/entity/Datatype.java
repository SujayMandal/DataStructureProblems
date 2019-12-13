package com.ca.umg.plugin.commons.excel.xmlconverter.entity;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "datatype")
public class Datatype {

    private ObjectDataType object;
    private PrimitiveDataType stringtype;
    private PrimitiveDataType integertype;
    private PrimitiveDataType longtype;
    private PrimitiveDataType bigintegertype;
    private PrimitiveDataType bigdecimaltype;
    private PrimitiveDataType doubletype;
    private PrimitiveDataType booleantype;
    private PrimitiveDataType datetype;
    private PrimitiveDataType datetime;
    private ArrayDataType arrayDatatype;

    public ObjectDataType getObject() {
        return object;
    }

    public void setObject(ObjectDataType object) {
        this.object = object;
    }

    @XmlElement(name = "string")
    public PrimitiveDataType getStringtype() {
        return stringtype;
    }

    public void setStringtype(PrimitiveDataType stringtype) {
        this.stringtype = stringtype;
    }

    @XmlElement(name = "integer")
    public PrimitiveDataType getIntegertype() {
        return integertype;
    }

    public void setIntegertype(PrimitiveDataType integertype) {
        this.integertype = integertype;
    }

    @XmlElement(name = "long")
    public PrimitiveDataType getLongtype() {
        return longtype;
    }

    public void setLongtype(PrimitiveDataType longtype) {
        this.longtype = longtype;
    }

    @XmlElement(name = "bigdecimal")
    public PrimitiveDataType getBigDecimaltype() {
        return bigdecimaltype;
    }

    public void setBigDecimaltype(PrimitiveDataType bigdecimaltype) {
        this.bigdecimaltype = bigdecimaltype;
    }

    @XmlElement(name = "biginteger")
    public PrimitiveDataType getBigIntegertype() {
        return bigintegertype;
    }

    public void setBigIntegertype(PrimitiveDataType bigintegertype) {
        this.bigintegertype = bigintegertype;
    }

    @XmlElement(name = "double")
    public PrimitiveDataType getDoubletype() {
        return doubletype;
    }

    public void setDoubletype(PrimitiveDataType doubletype) {
        this.doubletype = doubletype;
    }

    @XmlElement(name = "boolean")
    public PrimitiveDataType getBooleantype() {
        return booleantype;
    }

    public void setBooleantype(PrimitiveDataType booleantype) {
        this.booleantype = booleantype;
    }

    @XmlElement(name = "date")
    public PrimitiveDataType getDatetype() {
        return datetype;
    }

    public void setDatetype(PrimitiveDataType datetype) {
        this.datetype = datetype;
    }

    @XmlElement(name = "datetime")
    public PrimitiveDataType getDatetime() {
        return datetime;
    }

    public void setDatetime(PrimitiveDataType datetime) {
        this.datetime = datetime;
    }

    @XmlElement(name = "array")
    public ArrayDataType getArrayDatatype() {
        return arrayDatatype;
    }

    public void setArrayDatatype(ArrayDataType arrayDatatype) {
        this.arrayDatatype = arrayDatatype;
    }
}
