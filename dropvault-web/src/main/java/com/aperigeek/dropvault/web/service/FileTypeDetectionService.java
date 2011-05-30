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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;

/**
 *
 * @author Vivien Barousse
 */
@Stateless
public class FileTypeDetectionService {

    private static final Logger logger = Logger.getLogger(FileTypeDetectionService.class.getName());

    /**
     * Tries to determine the content type of a given file depending on its 
     * content and name.
     * 
     * The detection occurs according to the following process:
     * <ul>
     *     <li>The content type is guessed based on the content of the file</li>
     *     <li>
     *         If the content based detection fails, a new guess is attempted 
     *         using the file name
     *     </li>
     *     <li>
     *         If file name based detection fails, "application/octet-stream"
     *         is returned
     *     </li>
     * </ul>
     * 
     * @param name File name
     * @param data File content
     * @return Detected file type
     */
    public String detectFileType(String name, byte[] data) {
        // Content-based detection
        String contentType = null;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            contentType = URLConnection.guessContentTypeFromStream(in);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unexpected IO exception on internal stream", ex);
        }

        // File name based detection
        if (contentType == null) {
            contentType = URLConnection.guessContentTypeFromName(name);
        }

        // Default value
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return contentType;
    }
}
