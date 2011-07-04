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
package com.aperigeek.dropvault.android.service;

import com.aperigeek.dropvault.service.AbstractFilesService;
import com.aperigeek.dropvault.service.FilesService;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import com.aperigeek.dropvault.android.dao.AndroidFilesDAO;
import java.io.File;

/**
 *
 * @author Vivien Barousse
 */
public class AndroidFilesService extends AbstractFilesService implements FilesService {

    private Context context;
    
    public AndroidFilesService(Context context) {
        super(new AndroidFilesDAO(context));
        this.context = context;
    }

    public AndroidFilesService(String username, String password, Context context) {
        super(username, password, new AndroidFilesDAO(context));
        this.context = context;
    }

    @Override
    protected File getStorageDirectory() {
        File folder = Environment.getExternalStorageDirectory();
        folder = new File(folder, "DropVault");
        return folder;
    }
    
    protected String getBaseURI() {
        SharedPreferences prefs = context.getSharedPreferences("URI_PREFS", 0);
        return prefs.getString("baseURI", null);
    }
    
    protected void setBaseURI(String baseUri) {
        SharedPreferences prefs = context.getSharedPreferences("URI_PREFS", 0);
        prefs.edit()
                .putString("baseURI", baseUri)
                .commit();
    }
    
}
