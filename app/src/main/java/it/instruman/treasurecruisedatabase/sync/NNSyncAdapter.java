package it.instruman.treasurecruisedatabase.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import it.instruman.treasurecruisedatabase.nakama.network.NNHelper;
import it.instruman.treasurecruisedatabase.nakama.network.QueriedId;
import it.instruman.treasurecruisedatabase.nakama.network.Ship;
import it.instruman.treasurecruisedatabase.nakama.network.Team;

/**
 * Created by infan on 20/02/2018.
 */

class NNSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String TAG = NNSyncAdapter.class.getSimpleName();
    private static final Object syncAdapterLock = new Object();

    NNSyncAdapter(final Context context, final boolean autoInitialize) {
        super(context, autoInitialize);
    }

    NNSyncAdapter(final Context context, final boolean autoInitialize,
                  final boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority,
                              final ContentProviderClient provider, final SyncResult syncResult) {
        Log.i(TAG, "onPerformSync() was called");

        /* This is where you would put any code you want to run in the background.
           Such as fetching data from a server! */
        Realm realm = Realm.getDefaultInstance();
        try {
            //  QUERIES SEARCHED IDS (THE CHARACTERS IN WHICH YOU OPENED THE TEAM FIELD),
            //  REPLACES OR UPDATES WITH NEW DATA LOADED FROM THE SERVER
            NNHelper nnHelper = new NNHelper();
            RealmResults<QueriedId> queriedResults = realm.where(QueriedId.class).findAll();
            if (queriedResults.size() > 0) {
                try {
                    realm.beginTransaction();
                    for (QueriedId id : queriedResults) {
                        List<Team> teams = nnHelper.getTeamsByLeaderIdUsingNetwork(id.getLeaderId());
                        realm.insertOrUpdate(teams);
                    }
                    realm.commitTransaction();
                } catch (Exception e) {
                    realm.cancelTransaction();
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "TEAMS UPDATED");
            // UPDATE SHIPS
            if (realm.where(Ship.class).count() > 0) {
                List<Ship> ships = NNHelper.getShipList();
                if (ships != null && ships.size() > 0) {
                    realm.beginTransaction();
                    realm.insertOrUpdate(ships);
                    realm.commitTransaction();
                }
            }
            Log.i(TAG, "SHIPS UPDATED");
            // UPDATE STAGES
            // \\\ NOT NEEDED RIGHT NOW. A STAGE SHOULD NOT CHANGE IN TIME AND
            // \\\ NEW STAGES ARE AUTOMATICALLY ADDED
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (realm != null)
                realm.close();
        }
    }
}