package br.com.tairoroberto.testeopenstreetmapii;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Address;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.bonuspack.overlays.Marker;
import org.osmdroid.bonuspack.overlays.MarkerInfoWindow;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.Projection;
import org.osmdroid.views.overlay.Overlay;
import org.osmdroid.views.overlay.PathOverlay;

import java.io.IOException;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity implements LocationListener{
    //MapView OSM
    private MapView mapView;
    private MapController mapController;

    //LocationManager para o GPS
    private LocationManager locationManager;

    private PathOverlay pathOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView)findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);

        //habilita o zoom
        mapView.setBuiltInZoomControls(true);

        //habilita o touch
        mapView.setMultiTouchControls(true);

        //Inicializa o controller do mapa
        mapController = (MapController) mapView.getController();

        //seta o ponto de localização do mapa = equivalente ao LatLng da Api do coogleMaps
        GeoPoint geoPoint = new GeoPoint(-20.1698,-40.2487);

        //configura o zoom no mapa
        mapController.setZoom(10);

        //Sem animação do mapa
        // mapController.setCenter(geoPoint);

        //com animação do mapa
        mapController.animateTo(geoPoint);

        //adiciona o stilo e borda da rota do mapView com PathOverlay
        initPathOverlay();

        //Adiciona o marcador no mapa
        addMarker(geoPoint);

        mapView.setMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent scrollEvent) {
                Log.i("Script","onScroll()");
                return false;
            }

            @Override
            public boolean onZoom(ZoomEvent zoomEvent) {
                Log.i("Script","onZoom()");
                return false;
            }
        });

    }

    //Metodo de inicialização do PatghOverlay
    public void initPathOverlay(){
        pathOverlay = new PathOverlay(0,this);
        Paint paint = new Paint();
        //seta a cor do paint
        paint.setColor(Color.RED);
        //seta o stilo paint
        paint.setStyle(Paint.Style.STROKE);
        //define a largura do Stroke
        paint.setStrokeWidth(5);
        pathOverlay.setPaint(paint);

    }

    public PathOverlay addPointsLine(GeoPoint geoPoint){
        pathOverlay.addPoint(geoPoint);
        return (pathOverlay);
    }


    public void addMarker(GeoPoint geoPoint){
        Marker marker = new Marker(mapView);

        //configura a posição do marcador
        marker.setPosition(geoPoint);
        marker.setAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_BOTTOM);

        //Habiliza o drag and drop do marcador
        marker.setDraggable(true);

        //Define um titulo para o marcador
        marker.setTitle("Marker");

        //Define uma descrição para o marcador
        marker.setSnippet("Snippet Marker");

        //Define uma Subdescrição pro marcador
        marker.setSubDescription("Subdescrição do marcador");

        //Adiciona a classe que ouve os evento do InfoWindow do marcador
        marker.setInfoWindow(new CustonMarkerInfoWindow(mapView));

        //Configura o posicionamento do infoWindow na tela
        marker.setInfoWindowAnchor(Marker.ANCHOR_CENTER,Marker.ANCHOR_TOP);




        //Adiciona um icone ao mapa para o marcador
        //marker.setIcon(getResources().getDrawable(R.drawable.ic_launcher));

        //adiciona o evento de clique ao marcador
        marker.setOnMarkerClickListener(new Marker.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker, MapView mapView) {
                Log.i("Script","setOnMarkerClickListener()");
                marker.showInfoWindow();
                return false;
            }
        });

        //Adiciona o evento de drag and drop
        marker.setOnMarkerDragListener(new Marker.OnMarkerDragListener() {
            @Override
            public void onMarkerDrag(Marker marker) {
                Log.i("Script","setOnMarkerDragListener()");
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.i("Script","onMarkerDragEnd()");
            }

            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.i("Script","onMarkerDragStart()");
            }
        });
        //limpa todos os marcadores do mapa, para poder adicionar apenas o nosso
        mapView.getOverlays().clear();

        //Adiciona o PathOverlay para a rota do mapView
        mapView.getOverlays().add(addPointsLine(geoPoint));
        //Adiciona o evento de clique ao mapView
        mapView.getOverlays().add(new MapOverlay(this));

        //Adiciona nosso marcador ao mapa
        mapView.getOverlays().add(marker);

        //Dá um refresh no mapa para aparecer o marcador
        mapView.invalidate();

        //configura o GPS
        // locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Pega as atualizaçãoes do GPS
        // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);

    }


    //Classe para pegar o evento de clique no botão do inforWindow do marcador
    public class CustonMarkerInfoWindow extends MarkerInfoWindow{
        //Nesse contrutor utilizamos o layout padrao
        public CustonMarkerInfoWindow(MapView mapView) {
            super(R.layout.my_buble_infowindow,mapView);
        }

        @Override
        public void onOpen(Object item) {
            super.onOpen(item);

            Marker marker = (Marker) item;
            //Pega a ImagemView do infoWindow
            ImageView imageView = (ImageView) mView.findViewById(R.id.bubble_image);
            imageView.setImageResource(R.drawable.ic_launcher);

            //Pega a TextView de Titulo do infoWindow
            TextView title = (TextView) mView.findViewById(R.id.bubble_title);
            title.setText(marker.getTitle());

            //Pega a TextView de descrição do infoWindow
            TextView snippet = (TextView) mView.findViewById(R.id.bubble_description);
            snippet.setText(marker.getSnippet());

            //Pega a TextView de Subdescrição do infoWindow
            TextView subDescription = (TextView) mView.findViewById(R.id.bubble_subdescription);
            subDescription.setText(marker.getSubDescription());

            //Pega o Botão do InfoWindow e seu evento de clique
           /* Button button = (Button) mView.findViewById(R.id.bubble_moreinfo);
            button.setVisibility(View.VISIBLE);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this,"Botão do infoWindow do Marcador..!! ",Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }


    //Quando a localização mudar adicionamos novamente o marcador ao mapView
    @Override
    public void onLocationChanged(Location location) {
        GeoPoint geoPoint = new GeoPoint(location.getLatitude(),location.getLongitude());
        mapController.animateTo(geoPoint);
        addMarker(geoPoint);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager  != null){
            locationManager.removeUpdates(this);
        }
    }


    //Classe para pegar o evento de clique no mapa
    class MapOverlay extends Overlay{

        public MapOverlay(Context ctx) {
            super(ctx);
        }

        @Override
        protected void draw(Canvas canvas, MapView mapView, boolean b) {

        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent motionEvent, MapView mapView) {
            //Projection permite usar a posição relativa ao mapView
            Projection projection = mapView.getProjection();

            //Pega as posiçãoes X e Y do mapView
            GeoPoint geoPoint = (GeoPoint) projection.fromPixels((int)motionEvent.getX(),(int)motionEvent.getY());
            //Adiciona os pontos do clique no marcador
            //addMarker(geoPoint);

            return super.onSingleTapConfirmed(motionEvent, mapView);
        }
    }

    //Metodo paara pegar a rota de um ponto a outro
    public void getRoute(View view){
        EditText edtOrigem = (EditText)findViewById(R.id.edtOrigem);
        EditText edtDestino = (EditText)findViewById(R.id.edtDestino);

        final String origem = edtOrigem.getText().toString();
        final String destino = edtDestino.getText().toString();

        new Thread(){
            public void run(){
                GeoPoint geoPointStart = getLocation(origem);
                GeoPoint geoPointEnd = getLocation(destino);

                //Se a localização do endereço foe diferente de null desenhamos a rota
                if (geoPointStart != null && geoPointEnd != null){
                    drawRoute(geoPointStart,geoPointEnd);
                }else{
                    Toast.makeText(MainActivity.this,"Falha ao buscar rota de endereço",Toast.LENGTH_SHORT).show();
                }
            }
        }.start();

    }

    public GeoPoint getLocation(String location){
        GeocoderNominatim geocoderNominatim = new GeocoderNominatim(MainActivity.this);
        GeoPoint geoPoint = null;
        List<Address> addresses = new ArrayList<Address>();

        try {
            addresses = geocoderNominatim.getFromLocationName(location,1);

            if (addresses != null && addresses.size() > 0 ){
                Log.i("Script","Rua :"+addresses.get(0).getThoroughfare());
                Log.i("Script","Cidade :"+addresses.get(0).getSubAdminArea());
                Log.i("Script","Estado :"+addresses.get(0).getAdminArea());
                Log.i("Script","País :"+addresses.get(0).getCountryName());

                geoPoint = new GeoPoint(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
            }
        }catch (IOException e){
            e.printStackTrace();
        }


        return geoPoint;
    }


    public void drawRoute(GeoPoint start,GeoPoint end){
        RoadManager roadManager = new OSRMRoadManager();
        ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
        points.add(start);
        points.add(end);
        Road road = roadManager.getRoad(points);

        final Polyline roadOverlay = RoadManager.buildRoadOverlay(road,MainActivity.this);

        //Abre a Thread principal para desenhar a rota
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapView.getOverlays().add(roadOverlay);
            }
        });
    }
}
