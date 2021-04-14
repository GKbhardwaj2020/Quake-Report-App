package com.example.android.quakereport;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.nfc.tech.NdefFormatable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class quakeAdapter extends ArrayAdapter<EarthQuake> {
    private Context mContext;
    int mResource;
    private String locationOffset;
    private String primaryLocation;
    private String originalLocation;
    private static final  String LOCATION_SEPARATOR="of";

    public quakeAdapter(@NonNull Context context, int resource, @NonNull ArrayList<EarthQuake> objects) {
        super(context, resource, objects);
        mContext=context;
        mResource=resource;
    }

    private String formatDate(Date dateobject){
        SimpleDateFormat DateFormat=new SimpleDateFormat("LLL dd, yyyy");
        return DateFormat.format(dateobject);
    }
    private String formatTime(Date timeObject){
        SimpleDateFormat timeFormat=new SimpleDateFormat("hh:mm a");
        return timeFormat.format(timeObject);
    }
    private String Dformat(double no){
        DecimalFormat df=new DecimalFormat("0.0");
        return df.format(no);
    }
    private int getMagnitudeColor(double magnitude){
        int magnitudeResourceId;
        int magnitudeFloor=(int)Math.floor(magnitude);
        switch (magnitudeFloor){
            case 0:
            case 1:
                magnitudeResourceId=R.color.magnitude1;
                break;
            case 2:
                magnitudeResourceId=R.color.magnitude2;
                break;
            case 3:
                magnitudeResourceId=R.color.magnitude3;
                break;
            case 4:
                magnitudeResourceId=R.color.magnitude4;
                break;
            case 5:
                magnitudeResourceId=R.color.magnitude5;
                break;
            case 6:
                magnitudeResourceId=R.color.magnitude6;
                break;
            case 7:
                magnitudeResourceId=R.color.magnitude7;
                break;
            case 8:
                magnitudeResourceId=R.color.magnitude8;
                break;
            case 9:
                magnitudeResourceId=R.color.magnitude9;
                break;
            default:
                magnitudeResourceId=R.color.magnitude10plus;
                break;
        }
        return ContextCompat.getColor(getContext(),magnitudeResourceId);

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        double magnitude=getItem(position).getMagnitude();
        String location=getItem(position).getLocation();
        String primaryLocation=getItem(position).getPrimarylocation();
        long dateandTime=getItem(position).getTimeInMilliSeconds();
        String url=getItem(position).getUrl();
        EarthQuake quake=new EarthQuake(magnitude,location,primaryLocation,dateandTime,url);

        String originalLocation=quake.getLocation();
        if(originalLocation.contains(LOCATION_SEPARATOR)){
            String[] parts=originalLocation.split(LOCATION_SEPARATOR);
            locationOffset=parts[0]+LOCATION_SEPARATOR;
            primaryLocation=parts[1];
        }
        else{
            locationOffset=getContext().getString(R.string.near_the);
            primaryLocation=originalLocation;
        }


        LayoutInflater inflater=LayoutInflater.from(mContext);
        convertView=inflater.inflate(R.layout.list_item,parent,false);



        TextView tvlocationOffset=(TextView)convertView.findViewById(R.id.place_offset);
        TextView tvprimarylocation=(TextView)convertView.findViewById(R.id.primary_place);

        Date dateObject=new Date(quake.getTimeInMilliSeconds());
        TextView dateView=(TextView)convertView.findViewById(R.id.Date);

        Time timeObject=new Time(quake.getTimeInMilliSeconds());
        TextView time=(TextView)convertView.findViewById(R.id.Time) ;

        TextView tvMagnitude=(TextView)convertView.findViewById(R.id.magnitude);
        String mag= Dformat(magnitude);
        tvMagnitude.setText(mag);
        GradientDrawable magnitudeCircle=(GradientDrawable)tvMagnitude.getBackground();

        int magnitudeColor=getMagnitudeColor(quake.getMagnitude());
        magnitudeCircle.setColor(magnitudeColor);


        tvlocationOffset.setText(locationOffset);
        tvprimarylocation.setText(primaryLocation);
        String formattedDate=formatDate(dateObject);
        dateView.setText(formattedDate);
        String formattedTime=formatTime(timeObject);
        time.setText(formattedTime);


        return  convertView;

    }
}
