package nshrapnel.geoinformaticaassignment2;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


class GeoHelper {

    private static final String TAG = GeoHelper.class.getSimpleName();

    static String getAddressFromLocation(double latitude, double longitude, final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String result = null;
        try {
            List<Address> list = geocoder.getFromLocation(latitude, longitude, 1);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                result = address.getAddressLine(0) + ", " + address.getLocality();
            }
        } catch (IOException e) {
            Log.e(TAG, "Impossible to connect to Geocoder", e);
        }
        return result;
    }

    static LatLng getLocationFromAddress(String addressName, final Context context) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        LatLng location = null;
        try {
            List<Address> list = geocoder.getFromLocationName(addressName, 1);
            if (list != null && list.size() > 0) {
                Address address = list.get(0);
                location = new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (IOException e) {
            Log.e(TAG, "Impossible to connect to Geocoder", e);
        }
        return location;
    }
}
