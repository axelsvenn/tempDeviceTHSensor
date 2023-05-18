package com.example.tempdevicethsensor;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    static final UUID UUID_CONSTANT = UUID.fromString("fa916458-bbce-42f5-a016-f6e5e95a62eb");

    Button sendMessage, updateSocket;
    EditText editText;
    TextView socket, device;
    BluetoothServerSocket bluetoothServerSocket;
    OutputStream outputStream;
    BluetoothService bluetoothService;
    BluetoothSocket bluetoothSocket;

    public MainActivity() throws IOException {
        BluetoothAdapter madapter = BluetoothAdapter.getDefaultAdapter();

//        BluetoothDevice mdevice = madapter.getRemoteDevice(((BluetoothDevice)
//                madapter.getBondedDevices().toArray()[0]).getAddress());

        bluetoothServerSocket = madapter.listenUsingRfcommWithServiceRecord("device", UUID_CONSTANT);
        bluetoothService = new BluetoothService();
        bluetoothService.execute();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendMessage = findViewById(R.id.send);
        updateSocket = findViewById(R.id.update);
        editText = findViewById(R.id.editText);
        socket = findViewById(R.id.socket);
        device = findViewById(R.id.device);



        sendMessage.setOnClickListener(view -> {
            try {
                outputStream.write(editText.getText().toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        updateSocket.setOnClickListener(view -> {
            bluetoothService = new BluetoothService();
            bluetoothService.execute();
        });
    }

    public void setStatusSocketTV() {
        String socketConnect = bluetoothSocket.isConnected() ? "on" : "off";

        socket.setText(socket.getText().toString() + " " + socketConnect);
    }

    public void setDeviceTV() {
        BluetoothDevice bluetoothDevice = bluetoothSocket.getRemoteDevice();
        String deviceName = (bluetoothDevice == null ? "null" : bluetoothDevice.getName());
        device.setText(device.getText().toString() + deviceName);

    }

    public class BluetoothService extends AsyncTask<Void, Void, Void> {

        @SuppressLint("MissingPermission")
        @Override
        protected Void doInBackground(Void[] objects) {

            {
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                    System.out.println("connected");
                } catch (IOException exception) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        }

        @Override
        protected void onPostExecute(Void result) {

            setDeviceTV();
            setStatusSocketTV();
            try {
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);

        }
    }
}