package pl.trayz.drugscanner;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import pl.trayz.drugscanner.utils.HttpUtil;

import java.util.concurrent.Executors;

public class ScannerActivity extends AppCompatActivity {

    private CodeScanner codeScanner;
    private Long lastPress = 0L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);

        final CodeScannerView scannerView = findViewById(R.id.scanner);
        final EditText gtinCode = findViewById(R.id.gtin_code);

        this.codeScanner = new CodeScanner(this, scannerView);
        this.codeScanner.setDecodeCallback(result -> runOnUiThread(() -> gtinCode.setText(result.getText())));

        scannerView.setOnClickListener(view -> this.codeScanner.startPreview());
        findViewById(R.id.done).setOnClickListener(view -> {
            String gtin = gtinCode.getText().toString();

            if(gtin.length() > 13 || gtin.isEmpty()) {
                Toast.makeText(this, R.string.enter_gtin, Toast.LENGTH_SHORT).show();
                return;
            }

            if(System.currentTimeMillis() - this.lastPress < 1000) return;

            this.lastPress = System.currentTimeMillis();

            //send request to api
            Executors.newSingleThreadExecutor().execute(() -> {
                String result = HttpUtil.readGetRequest("https://rejestry.ezdrowie.gov.pl/api/rpl/medicinal-products/search/public?eanGtin="+gtin+"&subjectRolesIds=1&isAdvancedSearch=false&size=30&page=0&sort=name,ASC");

                runOnUiThread(()-> {
                    if(result == null || result.startsWith("{\"content\":[]")) {
                        Toast.makeText(this, R.string.no_drug, Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent = new Intent(this, ScannerResultActivity.class);
                    intent.putExtra("result", result);
                    startActivity(intent);
                });
            });
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.codeScanner.releaseResources();
    }
}