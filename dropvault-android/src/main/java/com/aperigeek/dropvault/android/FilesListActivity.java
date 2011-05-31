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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import com.aperigeek.dropvault.R;
import com.aperigeek.dropvault.android.Resource.ResourceType;
import com.aperigeek.dropvault.android.service.FilesService;
import com.aperigeek.dropvault.android.service.SyncException;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class FilesListActivity extends ListActivity {

    public static final String BASE_URI = "http://thom.aperigeek.com:8080/dropvault/rs/dav/viv/";
    
    private static final Logger logger = Logger.getLogger(FilesListActivity.class.getName());

    private FilesService service;
    
    private Resource current;
    
    private FilesListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        service = new FilesService(BASE_URI, this);
        current = service.getRoot();
    }
    
    @Override
    protected void onResume() {
        super.onResume();

        registerAdapter();
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
                updateDB();
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
            startActivity(intent);
        }
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

    protected void updateDB() {
        try {
            service.sync();
            current = service.getRoot();
            registerAdapter();
        } catch (SyncException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}
