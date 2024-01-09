package humble.wolf.volume_changer_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {

    private String ip;
    private String port;
    private String sessionID;

    EditText ipText;
    EditText portText;
    EditText idText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_main);

        ipText = (EditText) findViewById(R.id.ip);
        portText = (EditText) findViewById(R.id.port);
        idText = (EditText) findViewById(R.id.sessionId);


        findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ip = ipText.getText().toString();
                port = portText.getText().toString();
                sessionID = idText.getText().toString();

                changeActivity();
            }
        });
    }

    void changeActivity() {
        final TcpClient.TcpClientListener listener = new TcpClient.TcpClientListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(message.equals("The Session ID is incorrect")){
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Intent intent = new Intent(StartActivity.this, MainActivity.class);

                        intent.putExtra("ip", ip);
                        intent.putExtra("port", Integer.parseInt(port));
                        intent.putExtra("sessionID", sessionID);

                        // Start the new activity
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                Looper.prepare();
                Toast.makeText(getApplicationContext(), "Can't connect to server", Toast.LENGTH_SHORT).show();
            }
        };
        // Replace these values with your actual IP address, port, and message
        String messageToSend = sessionID + " get";

        // Send the message using the TcpClient
        TcpClient.sendMessage(ip, Integer.parseInt(port), messageToSend, listener);
    }
}
