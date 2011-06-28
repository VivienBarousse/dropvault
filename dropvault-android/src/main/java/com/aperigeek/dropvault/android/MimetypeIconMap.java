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
package com.aperigeek.dropvault.android;

import com.aperigeek.dropvault.R;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Vivien Barousse
 */
public class MimetypeIconMap {

    private static final Integer defaultIcon = R.drawable.mimetype_application_octet_stream;
    
    private static final Map<String, Integer> icons = buildMap();

    private static Map<String, Integer> buildMap() {
        Map<String, Integer> icons = new HashMap<String, Integer>();
        icons.put("application/pdf", R.drawable.mimetype_application_pdf);
        icons.put("text/plain", R.drawable.mimetype_text_plain);
        icons.put("text/html", R.drawable.mimetype_text_html);
        icons.put("application/rtf", R.drawable.mimetype_application_rtf);
        icons.put("application/msword", R.drawable.mimetype_application_msword);
        icons.put("application/zip", R.drawable.mimetype_application_zip);
        icons.put("image/png", R.drawable.mimetype_image_generic);
        icons.put("image/gif", R.drawable.mimetype_image_generic);
        icons.put("image/jpeg", R.drawable.mimetype_image_generic);
        icons.put("image/x-ms-bmp", R.drawable.mimetype_image_generic);
        icons.put("application/octet-stream", R.drawable.mimetype_application_octet_stream);
        return Collections.unmodifiableMap(icons);
    }
    
    public static int getIcon(String mimetype) {
        Integer icon = icons.get(mimetype);
        return icon != null ? icon : defaultIcon;
    }
    
}
