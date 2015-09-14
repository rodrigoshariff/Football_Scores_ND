package barqsoft.footballscores.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

public class WidgetRemoteViewService extends RemoteViewsService {
    public WidgetRemoteViewService() {
    }

    private static final String[] SCORE_COLUMNS = {
            DatabaseContract.scores_table._ID,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL
    };
    // these indices must match the projection
    static final int INDEX_ID = 0;
    static final int INDEX_DATE_COL = 1;
    static final int INDEX_MATCH_ID = 2;
    static final int INDEX_LEAGUE_COL = 3;
    static final int INDEX_HOME_GOALS_COL = 4;
    static final int INDEX_AWAY_GOALS_COL = 5;


    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                Uri weatherForLocationUri = DatabaseContract.scores_table.buildScores();
                data = getContentResolver().query(weatherForLocationUri,
                        SCORE_COLUMNS,
                        null,
                        null,
                        DatabaseContract.scores_table.DATE_COL + " DESC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                String formattedScore =Utilies.getScores(data.getInt(INDEX_HOME_GOALS_COL), data.getInt(INDEX_AWAY_GOALS_COL))
                ;
/*                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }*/
                views.setTextViewText(R.id.widget_score, formattedScore);
//                views.setTextViewText(R.id.widget_description, description);
//                views.setTextViewText(R.id.widget_high_temperature, formattedMaxTemperature);
//                views.setTextViewText(R.id.widget_low_temperature, formattedMinTemperature);

/*                final Intent fillInIntent = new Intent();
                String locationSetting =
                        Utility.getPreferredLocation(WidgetRemoteViewService.this);
                Uri weatherUri = DatabaseContract.scores_table.buildWeatherLocationWithDate(
                        locationSetting,
                        dateInMillis);
                fillInIntent.setData(weatherUri);
                views.setOnClickFillInIntent(R.id.widget, fillInIntent);*/
                return views;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }


            @Override
            public long getItemId(int position) {

                return position;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public boolean hasStableIds() {
                 return true;
             }




        };
    }
}

