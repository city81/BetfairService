
package com.betfair.publicapi.v3.bfglobalservice;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.7-b01-
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "BFGlobalService", targetNamespace = "http://www.betfair.com/publicapi/v3/BFGlobalService/", wsdlLocation = "https://api.betfair.com/global/v3/BFGlobalService.wsdl")
public class BFGlobalService_Service
    extends Service
{

    private final static URL BFGLOBALSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(com.betfair.publicapi.v3.bfglobalservice.BFGlobalService_Service.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = com.betfair.publicapi.v3.bfglobalservice.BFGlobalService_Service.class.getResource(".");
            url = new URL(baseUrl, "https://api.betfair.com/global/v3/BFGlobalService.wsdl");
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'https://api.betfair.com/global/v3/BFGlobalService.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        BFGLOBALSERVICE_WSDL_LOCATION = url;
    }

    public BFGlobalService_Service(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public BFGlobalService_Service() {
        super(BFGLOBALSERVICE_WSDL_LOCATION, new QName("http://www.betfair.com/publicapi/v3/BFGlobalService/", "BFGlobalService"));
    }

    /**
     * 
     * @return
     *     returns BFGlobalService
     */
    @WebEndpoint(name = "BFGlobalService")
    public BFGlobalService getBFGlobalService() {
        return super.getPort(new QName("http://www.betfair.com/publicapi/v3/BFGlobalService/", "BFGlobalService"), BFGlobalService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns BFGlobalService
     */
    @WebEndpoint(name = "BFGlobalService")
    public BFGlobalService getBFGlobalService(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.betfair.com/publicapi/v3/BFGlobalService/", "BFGlobalService"), BFGlobalService.class, features);
    }

}
