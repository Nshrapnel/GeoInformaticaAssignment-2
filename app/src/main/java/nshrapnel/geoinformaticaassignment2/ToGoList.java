package nshrapnel.geoinformaticaassignment2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;


public class ToGoList extends AppCompatActivity {

    private ArrayList<String> addresses = new ArrayList<>();
    private LocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        adapter = new LocationAdapter(this, R.layout.location, addresses);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView listLocations = (RecyclerView) findViewById(R.id.listLocations);
        int verticalSpacing = 20;
        VerticalSpaceItemDecorator itemDecorator = new VerticalSpaceItemDecorator(verticalSpacing);
        ShadowVerticalSpaceItemDecorator shadowItemDecorator = new ShadowVerticalSpaceItemDecorator(
                this, R.drawable.drop_shadow);
        listLocations.setHasFixedSize(true);
        listLocations.setLayoutManager(layoutManager);
        listLocations.addItemDecoration(shadowItemDecorator);
        listLocations.addItemDecoration(itemDecorator);
        listLocations.setAdapter(adapter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ToGoList.this);
                final EditText input = new EditText(ToGoList.this);
                builder.setTitle("Add an address?");
                builder.setView(input);
                builder.setCancelable(false);
                builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String text = input.getText().toString();
                        if (!text.isEmpty() && !text.equals("\n") && !text.equals(" ")) {
                            addresses.add(input.getText().toString());
                            adapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.go_to_map:
                Intent intent = new Intent(this, Map.class);
                if (!addresses.isEmpty()) {
                    intent.putStringArrayListExtra("Addresses", addresses);
                }
                this.startActivity(intent);
                break;
            case R.id.instructions:
                LayoutInflater inflater = getLayoutInflater();
                AlertDialog.Builder builder = new AlertDialog.Builder(ToGoList.this);
                View view = inflater.inflate(R.layout.instructions, null);
                builder.setTitle("Instructions");
                builder.setView(view);
                builder.setCancelable(false);
                builder.setPositiveButton("Got it.", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
