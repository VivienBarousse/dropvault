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

import android.database.DataSetObserver;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import com.aperigeek.dropvault.R;
import java.util.List;

/**
 *
 * @author Vivien Barousse
 */
class FilesListAdapter implements ListAdapter {

    private LayoutInflater inflater;
    
    private List<Resource> files;

    public FilesListAdapter(LayoutInflater inflater, List<Resource> files) {
        this.inflater = inflater;
        this.files = files;
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
        return files.size();
    }

    public Object getItem(int i) {
        return files.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int i, View oldView, ViewGroup parent) {
        
        Resource resource = files.get(i);
        
        // TODO: Reuse old view
        LinearLayout element = (LinearLayout) inflater.inflate(R.layout.files_list_item, parent, false);
        TextView fileName = (TextView) element.findViewById(R.id.file_name);
        ImageView fileIcon = (ImageView) element.findViewById(R.id.file_icon);
        fileName.setText(resource.getName());
        
        int icon = resource.getType() == Resource.ResourceType.FOLDER
                ? R.drawable.folder
                : MimetypeIconMap.getIcon(resource.getContentType());
        fileIcon.setImageBitmap(BitmapFactory.decodeResource(element.getResources(), icon));
        
        return element;
    }

    public int getItemViewType(int i) {
        return 0;
    }

    public int getViewTypeCount() {
        return 1;
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }
}
