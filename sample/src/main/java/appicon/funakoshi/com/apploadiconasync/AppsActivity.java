package appicon.funakoshi.com.apploadiconasync;

import android.content.*;
import android.content.pm.*;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.GridView;

import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.List;

/**
 * Created by max on 03.10.15.
 */
public class AppsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final PackageManager pm = getPackageManager();

        final GridView gridView = (GridView) findViewById(R.id.grid_view);

        new AsyncTask<Void, Void, List<ResolveInfo>>() {

            @Override
            protected List<ResolveInfo> doInBackground(Void... params) {
                return getResolveInfos(pm);
            }

            @Override
            protected void onPostExecute(List<ResolveInfo> resolveInfos) {
                super.onPostExecute(resolveInfos);
                AppsAdapter appsAdapter = new AppsAdapter(resolveInfos, AppsActivity.this);

                SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(appsAdapter);
                swingBottomInAnimationAdapter.setAbsListView(gridView);

                gridView.setAdapter(swingBottomInAnimationAdapter);
            }
        }.execute();
    }

    private List<ResolveInfo> getResolveInfos(PackageManager pm) {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<android.content.pm.ResolveInfo> appList = pm.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);
        return appList;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
