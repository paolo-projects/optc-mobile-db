package it.instruman.treasurecruisedatabase.sync;

/**
 * Created by infan on 20/02/2018.
 */

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.ACCOUNT_SERVICE;

public class SyncAdapterManager {

    private static final String TAG = SyncAdapterManager.class.getSimpleName();
    public static final String AUTHORITY = "it.instruman.treasurecruisedatabase.provider";
    public static final String ACCOUNT_TYPE = "appmywod.com";
    public static final String ACCOUNT = "dummyaccount";

    private Account account;
    private Context context;

    public SyncAdapterManager(final Context context) {
        this.context = context;

        account = new Account(ACCOUNT, ACCOUNT_TYPE);
    }

    public void beginPeriodicSync(final long updateConfigInterval) {
        Log.i(TAG, "beginPeriodicSync() called with: updateConfigInterval = [" +
                updateConfigInterval + "]");

        final AccountManager accountManager = (AccountManager) context
                .getSystemService(ACCOUNT_SERVICE);

        if (accountManager!=null && !accountManager.addAccountExplicitly(account, null, null)) {
            account = accountManager.getAccountsByType(ACCOUNT_TYPE)[0];
        }

        setAccountSyncable();

        if(ContentResolver.getPeriodicSyncs(account, AUTHORITY).size()==0) {
            ContentResolver.addPeriodicSync(account, AUTHORITY,
                    Bundle.EMPTY, updateConfigInterval);

            ContentResolver.setSyncAutomatically(account, AUTHORITY, true);
        }
    }

    public void cancelPeriodicSyncs() {
        Log.i(TAG, "Cancelling all periodic syncs");

        final AccountManager accountManager = (AccountManager) context
                .getSystemService(ACCOUNT_SERVICE);

        if (accountManager!=null && !accountManager.addAccountExplicitly(account, null, null)) {
            account = accountManager.getAccountsByType(ACCOUNT_TYPE)[0];
        }

        setAccountSyncable();

        if(ContentResolver.getPeriodicSyncs(account, AUTHORITY).size()>0)
            ContentResolver.cancelSync(account, AUTHORITY);
    }

    public void syncImmediately() {
        Log.i(TAG, "Starting immediate sync");
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

        ContentResolver.requestSync(account, AUTHORITY, settingsBundle);
    }

    private void setAccountSyncable() {
        if (ContentResolver.getIsSyncable(account, AUTHORITY) == 0) {
            ContentResolver.setIsSyncable(account, AUTHORITY, 1);
        }
    }

}
