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

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.aperigeek.dropvault.R;
import com.aperigeek.dropvault.android.FilesListActivity;
import com.aperigeek.dropvault.android.service.AndroidFilesService;
import com.aperigeek.dropvault.service.FilesService;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class ReceiveFileActivity extends Activity implements OnClickListener {
    
    private static final Logger logger = Logger.getLogger(ReceiveFileActivity.class.getName());

    private SharedPreferences prefs;
    
    private FilesService service;
    
    private Uri uri;
    
    private String mime;
    
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        String username = prefs.getString("username", null);
        String password = prefs.getString("password", null);
        
        service = new AndroidFilesService(username, password, this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        Intent intent = getIntent();
        if (Intent.ACTION_SEND.equals(intent.getAction())) {
            Bundle extras = intent.getExtras();
            uri = extras.getParcelable(Intent.EXTRA_STREAM);
            mime = intent.getType();
            name = uri.getLastPathSegment();
        }
        
        setContentView(R.layout.receive_file_activity);
        
        Button okButton = (Button) findViewById(R.id.receive_file_ok);
        okButton.setOnClickListener(this);
        
        EditText nameText = (EditText) findViewById(R.id.receive_file_name);
        nameText.setText(name);
    }
    
    public void onClick(View view) {
        if (view.getId() == R.id.receive_file_ok) {
            EditText nameText = (EditText) findViewById(R.id.receive_file_name);
            name = nameText.getText().toString();
            
            try {
                InputStream in = getContentResolver().openInputStream(uri);
                service.importFile(in, mime, name);
            } catch (IOException ex) {
                logger.log(Level.SEVERE, "IO error during file import", ex);
                Toast toast = Toast.makeText(this, "Unable to import file", 10);
                toast.show();
            }
            startActivity(new Intent(this, FilesListActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        service.close();
    }
    
}
