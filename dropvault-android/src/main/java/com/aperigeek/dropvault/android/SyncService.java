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

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import com.aperigeek.dropvault.R;
import com.aperigeek.dropvault.android.dav.DAVException;
import com.aperigeek.dropvault.android.dav.InvalidPasswordException;
import com.aperigeek.dropvault.android.service.FilesService;
import com.aperigeek.dropvault.android.service.SyncException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Vivien Barousse
 */
public class SyncService extends IntentService {
    
    private static final Logger logger = Logger.getLogger(SyncService.class.getName());
    
    public static final int ERROR_NOTIFICATION_ID = 1;
    
    public static final int ONGOING_NOTIFICATION_ID = 2;
    
    private SharedPreferences prefs;
    
    private FilesService service;

    public SyncService() {
        super("SynchronisationService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        service = new FilesService(this);
        service.setUsername(prefs.getString("username", null));
        service.setPassword(prefs.getString("password", null));
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        showOngoingNotification();
        
        NotificationManager notificationManager = getNotificationManager();
        int nIcon = R.drawable.icon;

        try {
            service.sync();
        } catch (SyncException ex) {
            logger.log(Level.SEVERE, null, ex);
            
            String tickerText = getString(R.string.sync_error_ticker);
            String title = getString(R.string.sync_error_title);
            String text = getString(R.string.sync_error_text);
            if (ex.getCause() instanceof InvalidPasswordException) {
                title = getString(R.string.sync_error_text_cred);
            } else if (ex.getCause() instanceof DAVException) {
                title = getString(R.string.sync_error_text_dav);
            } else if (ex.getCause() instanceof IOException) {
                title = getString(R.string.sync_error_text_io);
            }
            
            Notification n = new Notification(nIcon, tickerText, System.currentTimeMillis());
            n.setLatestEventInfo(this, 
                    title, 
                    text, 
                    PendingIntent.getActivity(this, 0, new Intent(this, FilesListActivity.class), 0));
            
            notificationManager.notify(ERROR_NOTIFICATION_ID, n);
        }
        
        hideOngoingNotification();
    }
    
    private void showOngoingNotification() {
        Notification notification = new Notification(R.drawable.icon,
                getString(R.string.sync_ongoing_ticker), 
                System.currentTimeMillis());
        notification.setLatestEventInfo(this, 
                getString(R.string.sync_ongoing_title), 
                getString(R.string.sync_ongoing_text),
                PendingIntent.getActivity(this, 0, new Intent(this, FilesListActivity.class), 0));
        
        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        
        getNotificationManager().notify(ONGOING_NOTIFICATION_ID, notification);
    }
    
    private void hideOngoingNotification() {
        getNotificationManager().cancel(ONGOING_NOTIFICATION_ID);
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }
    
}
