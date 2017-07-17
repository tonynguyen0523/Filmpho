package com.android.tonynguyen0523.filmpho.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Tony Nguyen on 1/13/2017.
 */

public class FilmphoAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private FilmphoAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new FilmphoAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}

