package sn.esmt.developpementmobile;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//package sn.esmt.developpementmobile;
//
//public class WelcomeActivity {
//}
// On ajoute "extends AppCompatActivity" pour qu'Android comprenne que c'est une page

import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // On récupère le bouton grâce à l'ID que tu as mis dans ton XML
        Button btnReady = findViewById(R.id.btnReady);

        // On crée l'action du clic
        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // L'Intent pour voyager de Welcome vers Login
                Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}