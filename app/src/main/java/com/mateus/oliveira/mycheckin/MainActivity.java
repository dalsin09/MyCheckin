package com.mateus.oliveira.mycheckin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mateus.oliveira.mycheckin.CheckinDAO;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private AutoCompleteTextView actvLocal;
    private Spinner spinnerCategoria;
    private TextView tvLatitude, tvLongitude;
    private Button btnCheckin;

    private CheckinDAO checkinDAO;
    private FusedLocationProviderClient fusedLocationClient;

    private String currentLatitude = null;
    private String currentLongitude = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializa o DAO e o provedor de localização
        checkinDAO = new CheckinDAO(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Associa os componentes da interface
        actvLocal = findViewById(R.id.actvLocal);
        spinnerCategoria = findViewById(R.id.spinnerCategoria);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        btnCheckin = findViewById(R.id.btnCheckin);

        // Carrega os dados do banco nos componentes
        loadLocalNames();
        loadCategories();

        // Solicita a localização
        requestLocation();

        // Configura o clique do botão de check-in
        btnCheckin.setOnClickListener(v -> handleCheckin());
    }

    private void requestLocation() {
        // Verifica se a permissão de localização foi concedida
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Se não, solicita a permissão ao usuário
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        // Se a permissão já foi concedida, obtém a última localização conhecida
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                currentLatitude = String.valueOf(location.getLatitude());
                currentLongitude = String.valueOf(location.getLongitude());
                tvLatitude.setText(currentLatitude);
                tvLongitude.setText(currentLongitude);
            } else {
                Toast.makeText(MainActivity.this, "Não foi possível obter a localização.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Se o usuário concedeu a permissão, tenta obter a localização novamente
                requestLocation();
            } else {
                Toast.makeText(this, "Permissão de localização negada.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadLocalNames() {
        checkinDAO.open();
        List<String> localNames = checkinDAO.getAllLocalNames();
        checkinDAO.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, localNames);
        actvLocal.setAdapter(adapter);
    }

    private void loadCategories() {
        checkinDAO.open();
        // Recupera os nomes das categorias em ordem crescente de chave primária [cite: 55]
        List<String> categories = checkinDAO.getAllCategoryNames();
        checkinDAO.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoria.setAdapter(adapter);
    }

    private void handleCheckin() {
        String local = actvLocal.getText().toString().trim();

        // Validação dos campos [cite: 61]
        if (local.isEmpty()) {
            Toast.makeText(this, "Por favor, digite o nome de um local.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (spinnerCategoria.getSelectedItem() == null) {
            Toast.makeText(this, "Por favor, escolha uma categoria.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (currentLatitude == null || currentLongitude == null) {
            Toast.makeText(this, "Aguardando obtenção da posição do usuário.", Toast.LENGTH_SHORT).show();
            return;
        }

        // O ID da categoria no banco de dados é a posição no Spinner + 1 (pois o ID começa em 1)
        long categoryId = spinnerCategoria.getSelectedItemPosition() + 1;

        checkinDAO.open();
        // Verifica se o local já existe no banco
        boolean exists = checkinDAO.checkinExists(local);

        if (exists) {
            // Se já existe, atualiza o registro incrementando o contador de visitas [cite: 64]
            checkinDAO.incrementCheckin(local);
            Toast.makeText(this, "Check-in atualizado para " + local, Toast.LENGTH_SHORT).show();
        } else {
            // Se for um novo local, insere um novo registro com qtdVisitas = 1 [cite: 59, 60]
            checkinDAO.insertNewCheckin(local, (int) categoryId, currentLatitude, currentLongitude);
            Toast.makeText(this, "Novo check-in salvo para " + local, Toast.LENGTH_SHORT).show();
        }

        checkinDAO.close();

        // Recarrega a tela para dar a sensação de atualização [cite: 58]
        finish();
        startActivity(getIntent());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Usamos if/else if para verificar qual item do menu foi clicado
        if (item.getItemId() == R.id.menu_mapa) {
            // Verifica se a localização já foi obtida antes de ir para o mapa
            if (currentLatitude == null || currentLongitude == null) {
                Toast.makeText(this, "Aguardando localização para abrir o mapa.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, MapActivity.class);
                // Envia a localização atual para a tela de mapa como parâmetros
                intent.putExtra("LATITUDE", currentLatitude);
                intent.putExtra("LONGITUDE", currentLongitude);
                startActivity(intent);
            }
            return true;

        } else if (item.getItemId() == R.id.menu_gestao) {
            Intent intent = new Intent(this, ManagementActivity.class);
            startActivity(intent);
            return true;

        } else if (item.getItemId() == R.id.menu_relatorio) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}