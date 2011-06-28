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

import eu.medsea.util.MimeUtil;
import java.io.ByteArrayInputStream;
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
     * The detection process is delegated to the mime-util library.
     * 
     * @param name File name
     * @param data File content
     * @return Detected file type
     */
    public String detectFileType(String name, byte[] data) {
        return MimeUtil.getMimeType(new ByteArrayInputStream(data));
    }
}
