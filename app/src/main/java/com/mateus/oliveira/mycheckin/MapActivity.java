package com.mateus.oliveira.mycheckin;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private CheckinDAO checkinDAO;
    private String userLatitude, userLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.map), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Recebe a localização do usuário vinda da MainActivity
        Intent intent = getIntent();
        userLatitude = intent.getStringExtra("LATITUDE");
        userLongitude = intent.getStringExtra("LONGITUDE");

        checkinDAO = new CheckinDAO(this);

        // Obtém o SupportMapFragment e notifica quando o mapa estiver pronto para ser usado.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Chamado quando o mapa está pronto.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Centraliza o mapa na localização atual do usuário
        try {
            double lat = Double.parseDouble(userLatitude);
            double lon = Double.parseDouble(userLongitude);
            LatLng userLocation = new LatLng(lat, lon);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15f)); // 15f é um bom nível de zoom para cidades
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Não foi possível obter a localização do usuário.", Toast.LENGTH_SHORT).show();
        }

        // Carrega e exibe os marcadores de check-in
        loadCheckinMarkers();
    }

    private void loadCheckinMarkers() {
        checkinDAO.open();
        List<CheckinData> checkins = checkinDAO.getAllCheckins();
        checkinDAO.close();

        for (CheckinData checkin : checkins) {
            try {
                double lat = Double.parseDouble(checkin.getLatitude());
                double lon = Double.parseDouble(checkin.getLongitude());
                LatLng checkinLocation = new LatLng(lat, lon);

                String snippet = "Categoria: " + checkin.getCategoriaNome() + " Visitas: " + checkin.getQtdVisitas();

                mMap.addMarker(new MarkerOptions()
                        .position(checkinLocation)
                        .title(checkin.getLocal())
                        .snippet(snippet));
            } catch (NumberFormatException e) {
                // Ignora marcadores com coordenadas inválidas
            }
        }
    }

    // --- Lógica do Menu ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.menu_voltar) {
            finish(); // Fecha a tela atual e volta para a anterior (MainActivity)
            return true;
        } else if (itemId == R.id.menu_gestao_map) {
            startActivity(new Intent(this, ManagementActivity.class));
            return true;
        } else if (itemId == R.id.menu_relatorio_map) {
            startActivity(new Intent(this, ReportActivity.class));
            return true;
        } else if (itemId == R.id.menu_mapa_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        } else if (itemId == R.id.menu_mapa_hibrido) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
