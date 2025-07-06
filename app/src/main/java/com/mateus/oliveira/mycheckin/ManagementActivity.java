package com.mateus.oliveira.mycheckin;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class ManagementActivity extends AppCompatActivity {

    private CheckinDAO checkinDAO;
    private LinearLayout layoutConteudo;
    private LinearLayout layoutDeletar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_management);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.management), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        checkinDAO = new CheckinDAO(this);
        layoutConteudo = findViewById(R.id.layoutConteudo);
        layoutDeletar = findViewById(R.id.layoutDeletar);

        populateCheckinList();
    }

    private void populateCheckinList() {
        checkinDAO.open();
        List<CheckinData> checkins = checkinDAO.getAllCheckins();
        checkinDAO.close();

        // Limpa as views antigas antes de adicionar novas
        layoutConteudo.removeAllViews();
        layoutDeletar.removeAllViews();

        for (CheckinData checkin : checkins) {
            // Cria e configura o TextView para o nome do local
            TextView tvLocal = new TextView(this);
            tvLocal.setText(checkin.getLocal());
            tvLocal.setTextSize(18);
            tvLocal.setPadding(0, 16, 0, 16);
            layoutConteudo.addView(tvLocal);

            // Cria e configura o ImageButton para deletar
            ImageButton btnDelete = new ImageButton(this);
            btnDelete.setImageResource(android.R.drawable.ic_delete);
            btnDelete.setBackground(null); // Remove o fundo padrão do botão
            btnDelete.setTag(checkin.getLocal()); // Usa o nome do local como Tag para identificação
            btnDelete.setOnClickListener(deleteListener);

            // Adiciona o botão ao layout de exclusão
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.gravity = Gravity.CENTER_VERTICAL;
            params.height = tvLocal.getLineHeight() + 32; // Tenta alinhar a altura
            btnDelete.setLayoutParams(params);

            layoutDeletar.addView(btnDelete);
        }
    }

    private final View.OnClickListener deleteListener = v -> {
        // Pega o nome do local que foi armazenado na Tag do botão
        final String localToDelete = (String) v.getTag();

        // Cria um diálogo de confirmação
        new AlertDialog.Builder(this)
                .setTitle("Exclusão")
                .setMessage("Tem certeza que deseja excluir " + localToDelete + "?")
                .setPositiveButton("SIM", (dialog, which) -> {
                    // Se o usuário clicar em "SIM", deleta o registro
                    checkinDAO.open();
                    checkinDAO.deleteCheckin(localToDelete);
                    checkinDAO.close();
                    Toast.makeText(ManagementActivity.this, localToDelete + " excluído.", Toast.LENGTH_SHORT).show();
                    // Recarrega a tela para atualizar a lista
                    finish();
                    startActivity(getIntent());
                })
                .setNegativeButton("NÃO", null) // Não faz nada se clicar em "NÃO"
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    };

    // --- Lógica do Menu ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.management_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_voltar_management) {
            finish(); // Fecha a tela e volta para a anterior
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
