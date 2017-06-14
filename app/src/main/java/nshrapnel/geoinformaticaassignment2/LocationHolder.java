package nshrapnel.geoinformaticaassignment2;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;


class LocationHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

    private final TextView tvAddress;
    private String location;
    private List<String> locations;
    private Context context;
    private LocationAdapter adapter;

    LocationHolder(Context context, View itemView, List<String> locations, LocationAdapter adapter) {
        super(itemView);
        this.tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
        this.context = context;
        this.locations = locations;
        this.adapter = adapter;
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    void bindLocation(String location) {
        this.location = location;
        this.tvAddress.setText(location);
    }


    @Override
    public void onClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final EditText input = new EditText(context);
        builder.setTitle("Rewrite '" + location + "'?");
        builder.setView(input);
        builder.setCancelable(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String text = input.getText().toString();
                if (!text.isEmpty() && !text.equals("\n") && !text.equals(" ")) {
                    int index = locations.indexOf(location);
                    locations.remove(location);
                    locations.add(index, input.getText().toString());
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public boolean onLongClick(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete '" + location + "'?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                locations.remove(location);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
        return false;
    }
}
