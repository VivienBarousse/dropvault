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
package com.aperigeek.dropvault.android.activity;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Toast;
import com.aperigeek.dropvault.android.FilesListActivity;
import com.aperigeek.dropvault.android.Resource;
import com.aperigeek.dropvault.android.dao.FilesDAO;
import com.aperigeek.dropvault.android.service.FilesService;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class ReceiveFileActivity extends ListActivity {

    private SharedPreferences prefs;
    
    private FilesService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        String username = prefs.getString("username", null);
        String password = prefs.getString("password", null);
        
        service = new FilesService(username, password, this);
    }
    
    @Override
    protected void onResume() {
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            Uri uri = extras.getParcelable(Intent.EXTRA_STREAM);
            String mime = intent.getType();
            String name = extras.getString(Intent.EXTRA_TITLE);
            
            try {
                service.importFile(uri, mime, name);
            } catch (IOException ex) {
                Toast toast = new Toast(this);
                toast.setText("Unable to import file");
                toast.show();
            }
        }
        startActivity(new Intent(this, FilesListActivity.class));
    }

    @Override
    protected void onDestroy() {
        service.close();
    }
    
}
