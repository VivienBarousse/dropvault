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
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.aperigeek.dropvault.R;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

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

    private class FilesListAdapter implements ListAdapter {

        private LayoutInflater inflater;

        public FilesListAdapter() {
            inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public boolean areAllItemsEnabled() {
            return true;
        }

        public boolean isEnabled(int i) {
            return true;
        }

        public void registerDataSetObserver(DataSetObserver dso) {
            // Data set is fixed for now. Observers aren't required in this case
        }

        public void unregisterDataSetObserver(DataSetObserver dso) {
            // Data set is fixed for now. Observers aren't required in this case
        }

        public int getCount() {
            return resources.size();
        }

        public Object getItem(int i) {
            return resources.get(i);
        }

        public long getItemId(int i) {
            return i;
        }

        public boolean hasStableIds() {
            return true;
        }

        public View getView(int i, View oldView, ViewGroup parent) {
            // TODO: Reuse old view
            LinearLayout element = (LinearLayout) inflater.inflate(R.layout.files_list_item, parent, false);

            TextView providerName = (TextView) element.findViewById(R.id.file_name);
            ImageView providerIcon = (ImageView) element.findViewById(R.id.file_icon);

            providerName.setText(resources.get(i).getName());
            providerIcon.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icon));

            return element;
        }

        public int getItemViewType(int i) {
            return 0;
        }

        public int getViewTypeCount() {
            return 1;
        }

        public boolean isEmpty() {
            return resources.isEmpty();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        setListAdapter(new FilesListAdapter());
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
            
            HttpClient client = new DefaultHttpClient();
            
            WebdavPropfind propfind = new WebdavPropfind();
            propfind.setURI(URI.create(baseHref));
            propfind.setHeader("Depth", "1");
            HttpResponse res = client.execute(propfind);
            
            InputStream in = res.getEntity().getContent();
            
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build(in);
            
            List<Element> responses = document.getRootElement()
                    .getChildren("response", Namespace.getNamespace("DAV:"));
            
            resources = new ArrayList<Resource>();
            
            System.out.println(responses.size());
            
            for (Element response : responses) {
                String href = URLDecoder.decode(response.getChild("href", Namespace.getNamespace("DAV:")).getTextTrim());
                String name = href.substring(baseHref.length());
                if (name.length() > 0) {
                    Resource resource = new Resource(name);
                    resource.setType(Resource.ResourceType.FOLDER);
                    resources.add(resource);
                }
            }
            
            setListAdapter(new FilesListAdapter());
        } catch (JDOMException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private static class WebdavPropfind extends HttpRequestBase {

        @Override
        public String getMethod() {
            return "PROPFIND";
        }
        
    }
    
}
