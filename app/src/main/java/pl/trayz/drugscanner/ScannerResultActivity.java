package pl.trayz.drugscanner;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class ScannerResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_result);

        JsonObject requestObject = new Gson().fromJson(getIntent().getStringExtra("result"), JsonObject.class);
        JsonObject result = requestObject.getAsJsonArray("content").get(0).getAsJsonObject();

        ((TextView)findViewById(R.id.product_name)).setText(result.get("medicinalProductName").getAsString());
        ((TextView)findViewById(R.id.product_common_name)).setText(result.get("commonName").getAsString());
        ((TextView)findViewById(R.id.product_pharmaceutical_name)).setText(result.get("pharmaceuticalFormName").getAsString());
        ((TextView)findViewById(R.id.product_expiration)).setText(result.get("expirationDateString").getAsString());

        findViewById(R.id.back).setOnClickListener(v -> finish());
        findViewById(R.id.see_leaflet).setOnClickListener(v -> {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra("url", "https://docs.google.com/gview?embedded=true&url=https://rejestry.ezdrowie.gov.pl/api/rpl/medicinal-products/"+result.get("id").getAsInt()+"/leaflet");
            startActivity(intent);
        });
    }
}