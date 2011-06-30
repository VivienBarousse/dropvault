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

import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;
import com.aperigeek.dropvault.R;
import com.aperigeek.dropvault.android.Resource.ResourceType;
import com.aperigeek.dropvault.android.service.FilesService;
import com.aperigeek.dropvault.android.settings.SettingsActivity;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class FilesListActivity extends ListActivity {

    private static final Logger logger = Logger.getLogger(FilesListActivity.class.getName());

    private FilesService service;
    
    private Resource current;
    
    private FilesListAdapter adapter;
    
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        service = new FilesService(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        service.setUsername(prefs.getString("username", null));
        service.setPassword(prefs.getString("password", null));
        current = service.getRoot();
        
        registerAdapter();
        
        registerForContextMenu(getListView());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        service.close();
    }

    private void registerAdapter() {
        List<Resource> resources 
                = current != null 
                ? service.getChildren(current) 
                : Collections.EMPTY_LIST;
        
        adapter = new FilesListAdapter(
                (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE),
                resources);

        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.files_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.files_menu_refresh:
                startService(new Intent(this, SyncService.class));
                return true;
            case R.id.files_menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Resource resource = (Resource) adapter.getItem(position);
        
        if (resource.getType() == ResourceType.FOLDER) {
            current = resource;
            registerAdapter();
        } else {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            File file = service.getFile(resource);
            intent.setDataAndType(Uri.fromFile(file), resource.getContentType());
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast toast = Toast.makeText(this, "No application can open this file", 10);
                toast.show();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.files_list_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        Resource resource = (Resource) adapter.getItem(info.position);
        
        switch (item.getItemId()) {
            case R.id.files_context_edit:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_EDIT);
                File file = service.getFile(resource);
                intent.setDataAndType(Uri.fromFile(file), resource.getContentType());
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Toast toast = Toast.makeText(this, "No application can edit this file", 10);
                    toast.show();
                }
                return true;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Resource parent = service.getParent(current);
        if (parent != null) {
            current = parent;
            registerAdapter();
        } else {
            super.onBackPressed();
        }
    }
}
