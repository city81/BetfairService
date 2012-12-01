
package com.betfair.publicapi.v3.bfglobalservice;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import com.betfair.publicapi.types.global.v3.SubmitLIMBMessageResp;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://www.betfair.com/publicapi/types/global/v3/}SubmitLIMBMessageResp"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "result"
})
@XmlRootElement(name = "submitLIMBMessageResponse")
public class SubmitLIMBMessageResponse {

    @XmlElement(name = "Result", required = true, nillable = true)
    protected SubmitLIMBMessageResp result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link SubmitLIMBMessageResp }
     *     
     */
    public SubmitLIMBMessageResp getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link SubmitLIMBMessageResp }
     *     
     */
    public void setResult(SubmitLIMBMessageResp value) {
        this.result = value;
    }

}
