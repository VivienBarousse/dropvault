/*  
 * This file is part of dropvault.
 *
 * dropvault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * dropvault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with dropvault.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.aperigeek.dropvault.android.dav;

import com.aperigeek.dropvault.android.Resource;
import com.aperigeek.dropvault.android.Resource.ResourceType;
import com.aperigeek.dropvault.android.dav.http.HttpPropfind;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author Vivien Barousse
 */
public class DAVClient {
    
    public static final Namespace DAV_NS = Namespace.getNamespace("DAV:");
    
    private HttpClient client;

    public DAVClient() {
        client = new DefaultHttpClient();
    }
    
    public Resource getResource(String uri) throws DAVException {
        HttpPropfind propfind = new HttpPropfind(uri);
        propfind.addHeader("Depth", "0");
        
        try {
            HttpResponse response = client.execute(propfind);
            
            InputStream in = response.getEntity().getContent();
            
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(in);
            Element element = document.getRootElement()
                    .getChild("response", DAV_NS);
            
            return buildResource(element);
        } catch (JDOMException ex) {
            throw new DAVException("Error in XML returned by server", ex);
        } catch (IOException ex) {
            throw new DAVException(ex);
        }
    }
    
    public List<Resource> getResources(Resource parent) throws DAVException {
        return getResources(parent, 1);
    }
    
    public List<Resource> getResources(Resource parent, int depth) throws DAVException {
        return getResources(parent, 
                depth < 0 ? "Infinity" : Integer.toString(depth));
    }
    
    protected List<Resource> getResources(Resource parent, String depth) throws DAVException {
        HttpPropfind propfind = new HttpPropfind(parent.getHref());
        propfind.addHeader("Depth", depth);
        
        try {
            HttpResponse response = client.execute(propfind);
            
            InputStream in = response.getEntity().getContent();
            
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(in);
            List<Element> elements = document.getRootElement()
                    .getChildren("response", DAV_NS);
            
            List<Resource> resources = buildResources(elements);
            resources.remove(parent);
            
            return resources;
        } catch (JDOMException ex) {
            throw new DAVException("Error in XML returned by server", ex);
        } catch (IOException ex) {
            throw new DAVException(ex);
        }
    }
    
    protected List<Resource> buildResources(List<Element> resps) {
        List<Resource> resources = new ArrayList<Resource>();
        for (Element resp : resps) {
            resources.add(buildResource(resp));
        }
        return resources;
    }
    
    protected Resource buildResource(Element resp) {
        Resource r = new Resource();
        
        r.setHref(URLDecoder.decode(resp.getChild("href", DAV_NS).getTextTrim()));
        
        Element prop = resp.getChild("propstat", DAV_NS)
                .getChild("prop", DAV_NS);
        
        r.setName(prop.getChild("displayname", DAV_NS).getTextTrim());
        r.setType(isFolder(prop) ? ResourceType.FOLDER : ResourceType.FILE);
        
        if (r.getType() == ResourceType.FILE) {
            r.setContentType(prop.getChild("getcontenttype", DAV_NS).getTextTrim());
        }
        
        return r;
    }
    
    protected boolean isFolder(Element prop) {
        Element restype = prop.getChild("resourcetype", DAV_NS);
        if (restype == null) {
            return false;
        }
        
        Element collection = restype.getChild("collection", DAV_NS);
        if (collection == null) {
            return false;
        }
        
        return true;
    }
    
}
