
package com.betfair.publicapi.types.global.v3;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfCouponLinks complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCouponLinks">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CouponLink" type="{http://www.betfair.com/publicapi/types/global/v3/}CouponLink" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCouponLinks", propOrder = {
    "couponLink"
})
public class ArrayOfCouponLinks {

    @XmlElement(name = "CouponLink", namespace = "http://www.betfair.com/publicapi/types/global/v3/", nillable = true)
    protected List<CouponLink> couponLink;

    /**
     * Gets the value of the couponLink property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the couponLink property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCouponLink().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CouponLink }
     * 
     * 
     */
    public List<CouponLink> getCouponLink() {
        if (couponLink == null) {
            couponLink = new ArrayList<CouponLink>();
        }
        return this.couponLink;
    }

}
