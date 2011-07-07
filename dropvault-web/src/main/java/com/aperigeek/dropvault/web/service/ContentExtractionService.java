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
package com.aperigeek.dropvault.web.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
public class ContentExtractionService {
    
    public Map<String, String> extractContent(String fileName, InputStream data, String contentType) throws ContentExtractionException {
        try {
            Parser parser = new AutoDetectParser();
            BodyContentHandler bodyHandler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            parser.parse(data, 
                    bodyHandler, 
                    metadata, 
                    context);
            System.out.println(metadata.toString());
            System.out.println(bodyHandler.toString());
            
            Map<String, String> results = new HashMap<String, String>();
            results.put("title", metadata.get("title"));
            results.put("author", metadata.get("Author"));
            results.put("body", bodyHandler.toString());
            
            return results;
        } catch (IOException ex) {
            throw new ContentExtractionException("IO exception during content extraction", ex);
        } catch (SAXException ex) {
            throw new ContentExtractionException("SAX exception during content extraction", ex);
        } catch (TikaException ex) {
            throw new ContentExtractionException("Tika exception during content extraction", ex);
        }
    }
    
}
