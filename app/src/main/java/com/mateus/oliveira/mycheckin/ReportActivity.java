package com.mateus.oliveira.mycheckin;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class ReportActivity extends AppCompatActivity {

    private CheckinDAO checkinDAO;
    private LinearLayout layoutReportLocal;
    private LinearLayout layoutReportVisitas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_report);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.report), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkinDAO = new CheckinDAO(this);
        layoutReportLocal = findViewById(R.id.layoutReportLocal);
        layoutReportVisitas = findViewById(R.id.layoutReportVisitas);

        populateReportList();
    }

    private void populateReportList() {
        checkinDAO.open();
        List<CheckinData> checkins = checkinDAO.getCheckinsOrderByVisits();
        checkinDAO.close();

        // Limpa as views antigas
        layoutReportLocal.removeAllViews();
        layoutReportVisitas.removeAllViews();

        for (CheckinData checkin : checkins) {
            // Cria e adiciona o TextView para o nome do local
            TextView tvLocal = new TextView(this);
            tvLocal.setText(checkin.getLocal());
            tvLocal.setTextSize(18);
            tvLocal.setPadding(0, 16, 0, 16);
            layoutReportLocal.addView(tvLocal);

            // Cria e adiciona o TextView para a quantidade de visitas
            TextView tvVisitas = new TextView(this);
            tvVisitas.setText(String.valueOf(checkin.getQtdVisitas()));
            tvVisitas.setTextSize(18);
            tvVisitas.setGravity(Gravity.CENTER);
            tvVisitas.setPadding(16, 16, 16, 16);
            layoutReportVisitas.addView(tvVisitas);
        }
    }

    // --- Lógica do Menu ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.report_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_voltar_report) {
            finish(); // Fecha a tela e volta para a anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
