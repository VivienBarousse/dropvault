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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import com.aperigeek.dropvault.R;
import com.aperigeek.dropvault.android.dav.DAVClient;
import com.aperigeek.dropvault.android.dav.DAVException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class FilesListActivity extends ListActivity {

    private static final Logger logger = Logger.getLogger(FilesListActivity.class.getName());
    
    private List<Resource> resources = Arrays.asList(
            new Resource("toto"),
            new Resource("tata"),
            new Resource("titi"),
            new Resource("tutu"));

    @Override
    protected void onResume() {
        super.onResume();

        registerAdapter();
    }

    private void registerAdapter() {
        FilesListAdapter adapter = new FilesListAdapter(
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

    protected void updateDB() {
        try {
            String baseHref = "http://thom.aperigeek.com:8080/dropvault/rs/dav/viv/";

            DAVClient client = new DAVClient();
            
            Resource root = client.getResource(baseHref);
            
            resources = client.getResources(root);

            registerAdapter();
        } catch (DAVException ex) {
            logger.log(Level.SEVERE, null, ex);
            // Notify sync failed
        }
    }
}
