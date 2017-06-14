package nshrapnel.geoinformaticaassignment2;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {

    private Context context;
    private List<String> locations;
    private int itemResource;

    LocationAdapter(Context context, int itemResource, List<String> locations) {
        this.context = context;
        this.locations = locations;
        this.itemResource = itemResource;
    }

    @Override
    public LocationHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new LocationHolder(this.context, view, locations, this);
    }

    @Override
    public void onBindViewHolder(LocationHolder holder, int position) {
        String location = this.locations.get(position);
        holder.bindLocation(location);
    }

    @Override
    public int getItemCount() {
        return this.locations.size();
    }
}
