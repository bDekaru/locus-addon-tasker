package falcosc.locus.addon.tasker;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import falcosc.locus.addon.tasker.intent.handler.UpdateContainerRequest;
import falcosc.locus.addon.tasker.utils.LocusCache;
import falcosc.locus.addon.tasker.utils.LocusField;
import locus.api.android.utils.LocusUtils;
import locus.api.android.utils.exceptions.RequiredVersionMissingException;
import locus.api.objects.extra.Track;

public class LocusSetGuideTrackActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocusCache locusCache = LocusCache.getInstance(this);

        try {
            Track track = LocusUtils.handleIntentTrackTools(this, getIntent());
            final Track suggestedTrack = getSuggestedTrack(track, locusCache);

            if (track != null && suggestedTrack != null) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.suggest_navigation_track);
                builder.setPositiveButton(suggestedTrack.getName(), (dialog, which) -> setTrack(suggestedTrack));
                builder.setNegativeButton(track.getName(), (dialog, which) -> setTrack(track));
                builder.create().show();
            } else {
                //track is null or we don't have a suggestion
                setTrack(track);
            }
        } catch (RequiredVersionMissingException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.calc_remain_elevation);


        Button closeButton = findViewById(R.id.btnClose);
        closeButton.setOnClickListener(v -> finish());
    }

    private Track getSuggestedTrack(Track currentSelection, LocusCache locusCache) throws RequiredVersionMissingException {
        if (currentSelection != null && !currentSelection.getName().equalsIgnoreCase(locusCache.navigationTrackName)) {
            return UpdateContainerRequest.searchNavigationTrack(locusCache, this);
        }

        return null;
    }

    private void setTrack(Track track) {
        LocusCache locusCache = LocusCache.getInstance(this);
        locusCache.setLastSelectedTrack(track);

        String trackName = "not found"; //NON-NLS it's just an workaround
        if (track != null) {
            trackName = track.getName();
            trackName += " (" + track.getId() + ")";
        }
        TextView text = findViewById(R.id.text_desc);

        String calcFieldText = "";

        LocusField field = locusCache.updateContainerFieldMap.get(LocusCache.CALC_REMAIN_UPHILL_ELEVATION);
        if (field != null) {
            calcFieldText = field.label + "(%" + field.taskerName + ")";
        }

        text.setText(getString(R.string.calc_remain_elev_description,
                trackName, getString(R.string.act_request_stats_sensors), calcFieldText));
    }


}
