package humble.wolf.volume_changer_app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    private Button upVolume;
    private Button downVolume;
    private TextView volumeText;

    private FloatingActionButton muteButton;

    private boolean muted = false;
    private int volume = 0;
    private String sessionID = "";
    private String ipAddress = "";
    private int port = 0;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();

        // Retrieve the extra data from the Intent
        ipAddress = intent.getStringExtra("ip");
        port = intent.getIntExtra("port", 6369);
        sessionID = intent.getStringExtra("sessionID");

        // Initialize UI components
        upVolume = findViewById(R.id.upButton);
        downVolume = findViewById(R.id.okButton);
        volumeText = findViewById(R.id.volumeLevel);
        muteButton = findViewById(R.id.mute);

        getCurrentVolume();

        upVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vol = volume + 1;
                updateVolume(vol);
            }
        });

        downVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int vol = volume - 1;
                updateVolume(vol);
            }
        });

        muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mute();
            }
        });
    }

    void getCurrentVolume() {
        final TcpClient.TcpClientListener listener = new TcpClient.TcpClientListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        volume = Integer.parseInt(message);
                        volumeText.setText(message);
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                // Handle errors
                Log.e("TcpClient", "Error: " + e.getMessage());

                // You can update UI elements or perform other actions based on the error
            }
        };
        String messageToSend = sessionID + " get";

        // Send the message using the TcpClient
        TcpClient.sendMessage(ipAddress, port, messageToSend, listener);
    }

    void updateVolume(int _volume) {
        final TcpClient.TcpClientListener listener = new TcpClient.TcpClientListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            volume = Integer.parseInt(message);
                            volumeText.setText(message);
                            System.out.println(message);
                        }
                        catch(Exception e){
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                // Handle errors
                Log.e("TcpClient", "Error: " + e.getMessage());

                // You can update UI elements or perform other actions based on the error
            }
        };
        String messageToSend = sessionID + " set " + _volume;

        // Send the message using the TcpClient
        TcpClient.sendMessage(ipAddress, port, messageToSend, listener);
    }

    void mute() {
        final TcpClient.TcpClientListener listener = new TcpClient.TcpClientListener() {
            @Override
            public void onMessageReceived(String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(muted) {
                            volumeText.setText(String.valueOf(volume));
                        }
                        else{
                            volumeText.setText("MUTED");
                        }
                        muted = !muted;
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                // Handle errors
                Log.e("TcpClient", "Error: " + e.getMessage());

                // You can update UI elements or perform other actions based on the error
            }
        };
        String messageToSend = sessionID + (muted ? " unmute" : " mute");

        // Send the message using the TcpClient
        TcpClient.sendMessage(ipAddress, port, messageToSend, listener);
    }
}
