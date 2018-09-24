package com.ndc.bus.Activity;

import android.databinding.DataBindingUtil;
import android.location.Location;

import com.ndc.bus.R;
import com.ndc.bus.databinding.ActivityMapBinding;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapActivity extends BaseActivity {
    private ActivityMapBinding binding;
    private MapView mapView;
    private MapPoint startPoint;
    private MapPoint endPoint;
    //private Location myGPS;
    //https://www.data.go.kr/subMain.jsp#/L3B1YnIvcG90L215cC9Jcm9zTXlQYWdlL29wZW5EZXZEZXRhaWxQYWdlJEBeMDgyTTAwMDAxMzBeTTAwMDAxMzUkQF5wdWJsaWNEYXRhRGV0YWlsUGs9dWRkaTozMjA1NjhiNS1jZDBmLTQyODAtOGI5Ny1iZjUxMmYxNWZlNDkkQF5wcmN1c2VSZXFzdFNlcU5vPTQ4NzAxNjkkQF5yZXFzdFN0ZXBDb2RlPVNUQ0QwMQ==


    @Override
    public void initSettings(){
        super.initSettings();
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        binding.setActivity(this);

        mapView = new MapView(this);
        mapView.setDaumMapApiKey("7a5fdba88e6b173eca9ee2561d81245d");
        binding.mapView.addView(mapView);
        //startPoint = MapPoint.mapPointWithGeoCoord(36.5655455, 127.01013969999997);

        setMapDefault();

    }


    private void setMapDefault() {

        addMakers();
        //endPoint = MapPoint.mapPointWithGeoCoord(37.527346, 127.02735499999994);

        // 중심점 변경
        //mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        //mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(myGPS.getLatitude(), myGPS.getLongitude()), true);

        // 줌 레벨 변경
        //mapView.setZoomLevel(7, true);
        mapView.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(33.41, 126.52), 9, true);
        mapView.zoomIn(true);
        mapView.zoomOut(true);
    }

    private void addMakers(){
        MapPOIItem marker = new MapPOIItem();
        marker.setItemName("Default Marker");
        marker.setTag(1);
        marker.setMapPoint(MapPoint.mapPointWithGeoCoord(33.41, 126.52));
        marker.setMarkerType(MapPOIItem.MarkerType.BluePin); // 기본으로 제공하는 BluePin 마커 모양.
        marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin); // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.

        mapView.addPOIItem(marker);
    }
}