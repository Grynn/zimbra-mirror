
package com.zimbra.soap.admin.wsimport.generated;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for flushCacheRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="flushCacheRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cache" type="{urn:zimbraAdmin}cacheEntrySelector" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "flushCacheRequest", propOrder = {
    "cache"
})
public class FlushCacheRequest {

    protected CacheEntrySelector cache;

    /**
     * Gets the value of the cache property.
     * 
     * @return
     *     possible object is
     *     {@link CacheEntrySelector }
     *     
     */
    public CacheEntrySelector getCache() {
        return cache;
    }

    /**
     * Sets the value of the cache property.
     * 
     * @param value
     *     allowed object is
     *     {@link CacheEntrySelector }
     *     
     */
    public void setCache(CacheEntrySelector value) {
        this.cache = value;
    }

}
