
package com.betfair.publicapi.types.exchange.v5;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ArrayOfVolumeInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfVolumeInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="VolumeInfo" type="{http://www.betfair.com/publicapi/types/exchange/v5/}VolumeInfo" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfVolumeInfo", propOrder = {
    "volumeInfo"
})
public class ArrayOfVolumeInfo {

    @XmlElement(name = "VolumeInfo", namespace = "http://www.betfair.com/publicapi/types/exchange/v5/", nillable = true)
    protected List<VolumeInfo> volumeInfo;

    /**
     * Gets the value of the volumeInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the volumeInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVolumeInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VolumeInfo }
     * 
     * 
     */
    public List<VolumeInfo> getVolumeInfo() {
        if (volumeInfo == null) {
            volumeInfo = new ArrayList<VolumeInfo>();
        }
        return this.volumeInfo;
    }

}
