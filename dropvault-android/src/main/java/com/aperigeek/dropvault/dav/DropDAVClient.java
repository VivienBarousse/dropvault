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
package com.aperigeek.dropvault.dav;

import com.aperigeek.dropvault.Resource;
import com.aperigeek.dropvault.service.HashService;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.text.MessageFormat;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

/**
 *
 * @author Vivien Barousse
 */
public class DropDAVClient extends DAVClient {
    
    public static final String LOGIN_URL = "http://thom.aperigeek.com:8080/dropvault/rs/login?username={0}&password={1}";
    
    private String username;
    
    private String password;
    
    private String baseUri;
    
    private HashService hash;

    public DropDAVClient(String username, String password) throws InvalidPasswordException, DAVException {
        super(username, password);
        this.username = username;
        this.password = password;
        hash = new HashService();
        login();
    }
    
    private void login() throws InvalidPasswordException, DAVException {
        String url = MessageFormat.format(LOGIN_URL,
                URLEncoder.encode(this.username), 
                hash.hash(this.password));
        HttpGet get = new HttpGet(url);
        try {
            HttpResponse response = client.execute(get);
            
            if (response.getStatusLine().getStatusCode() == 401) {
                throw new InvalidPasswordException();
            }
            
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new DAVException("Server error");
            }
            
            InputStream in = response.getEntity().getContent();
            StringWriter writer = new StringWriter();
            Reader reader = new InputStreamReader(in);
            char[] buffer = new char[128];
            int readed;
            while ((readed = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, readed);
            }
            in.close();
            baseUri = writer.toString().trim();
        } catch (IOException ex) {
            throw new DAVException("Login URL unavailable", ex);
        }
    }
    
    public Resource getRootResource() throws DAVException {
        return getResource(baseUri);
    }
    
    public String getBaseURI() {
        return baseUri;
    }
    
}
