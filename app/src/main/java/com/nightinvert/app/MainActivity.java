package com.nightinvert.app;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final String ADB_CMD =
            "adb shell pm grant com.nightinvert.app android.permission.WRITE_SECURE_SETTINGS";

    private Switch toggle;
    private TextView status;
    private TextView help;
    private Button copyBtn;
    private Button settingsBtn;
    private boolean updating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int pad = dp(24);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(pad, pad * 2, pad, pad);
        root.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView title = new TextView(this);
        title.setText("Modo Noturno");
        title.setTextSize(28);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        toggle = new Switch(this);
        toggle.setTextSize(20);
        toggle.setText("Ativar   ");
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tlp.topMargin = dp(32);
        root.addView(toggle, tlp);

        status = new TextView(this);
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        slp.topMargin = dp(16);
        root.addView(status, slp);

        help = new TextView(this);
        help.setTextSize(14);
        help.setText("Para o botão funcionar, conceda a permissão uma única vez pelo computador "
                + "(com a depuração USB ativada no celular) executando:\n\n" + ADB_CMD
                + "\n\nDepois reabra este app. Enquanto isso, você pode usar o botão abaixo "
                + "e ligar \"Inversão de cores\" manualmente.");
        help.setTextIsSelectable(true);
        LinearLayout.LayoutParams hlp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        hlp.topMargin = dp(24);
        root.addView(help, hlp);

        copyBtn = new Button(this);
        copyBtn.setText("Copiar comando");
        copyBtn.setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("adb", ADB_CMD));
            Toast.makeText(this, "Comando copiado", Toast.LENGTH_SHORT).show();
        });
        root.addView(copyBtn);

        settingsBtn = new Button(this);
        settingsBtn.setText("Abrir ajustes de acessibilidade");
        settingsBtn.setOnClickListener(v ->
                startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)));
        root.addView(settingsBtn);

        setContentView(root);

        toggle.setOnCheckedChangeListener((button, checked) -> {
            if (updating) return;
            if (!NightMode.set(this, checked)) {
                Toast.makeText(this, "Permissão ainda não concedida", Toast.LENGTH_SHORT).show();
            }
            refresh();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        boolean can = NightMode.canControl(this);
        updating = true;
        toggle.setChecked(NightMode.isOn(this));
        toggle.setEnabled(can);
        updating = false;

        status.setText(NightMode.isOn(this)
                ? "Serviço ativado: tudo em fundo preto e letras brancas."
                : "Serviço desativado: Android em modo normal.");

        int vis = can ? View.GONE : View.VISIBLE;
        help.setVisibility(vis);
        copyBtn.setVisibility(vis);
        settingsBtn.setVisibility(vis);
    }

    private int dp(int v) {
        return Math.round(v * getResources().getDisplayMetrics().density);
    }
}
