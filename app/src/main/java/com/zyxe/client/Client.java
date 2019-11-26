package com.zyxe.client;

//      Developed from C Drive.
//      If Error Code 3: Can't find ...Client then delete app from Android phone.
//      Don't just simple delete icon.
//      Go into applications & Uninstall.
//      11/25/2019  Add Button to clear terminal.


import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


/**
 * The Client class is run from a remote (local) client that will
 * connect via a socket to the SocketServer class.
 */

public class Client extends AppCompatActivity
{
    private static final String TAG = "SAM";

    Thread Thread1 = null;
    EditText etIP, etPort;
    TextView tvMessages;
    EditText etMessage;
    Button btnSend;
    String SERVER_IP;
    int SERVER_PORT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        tvMessages = findViewById(R.id.tvMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        tvMessages.setMovementMethod( new ScrollingMovementMethod());

        Button btnConnect = findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                tvMessages.setText("");
                //SERVER_IP = etIP.getText().toString().trim();
                SERVER_IP = "10.0.0.227";
                //SERVER_PORT = Integer.parseInt(etPort.getText().toString().trim());
                SERVER_PORT = 7000;
                Thread1 = new Thread(new Thread1());
                Thread1.start();
            }
        });
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String message = etMessage.getText().toString().trim();
                if (!message.isEmpty()) {
                    new Thread(new Thread3(message)).start();
                }
            }
        });
    }


    PrintWriter output;
    InputStreamReader in;
    BufferedReader br;

    class Thread1 implements Runnable {
        public void run() {
            Socket socket;
            try {
                socket = new Socket(SERVER_IP, SERVER_PORT);
                output = new PrintWriter(socket.getOutputStream());

                in = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(in);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvMessages.setText("Connected\n");
                    }
                });
                new Thread(new Thread2()).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Thread2 implements Runnable
    {
        @Override
        public void run() {
            while (true) {
                try {
                    final String message = br.readLine();


                    Log.i(TAG, "message=" + message);


                    final String newMessage = message.replaceAll("▼", "\n");

                    Log.i(TAG, "newMessage=" + newMessage);



                    if (newMessage != null) {
                        runOnUiThread(new Runnable()
                        {
                            @Override
                            public void run() {
                                tvMessages.append("server: " + newMessage + "\n");
                            }
                        });
                    }
                    else
                    {
                        Thread1 = new Thread(new Thread1());
                        Thread1.start();
                        return;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Thread3 implements Runnable
    {
        private String message;

        Thread3(String message) {
            this.message = message;
        }

        @Override
        public void run()
        {
            output.println(message);
            output.flush();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tvMessages.append("client: " + message + "\n");
                    etMessage.setText("");
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

}