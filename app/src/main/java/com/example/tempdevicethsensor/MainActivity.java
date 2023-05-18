package com.example.tempdevicethsensor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button button;
    EditText editText;
    BluetoothSocket bluetoothSocket;
    OutputStream outputStream;

    public MainActivity() throws IOException {
        BluetoothAdapter madapter= BluetoothAdapter.getDefaultAdapter();

        BluetoothDevice mdevice = madapter.getRemoteDevice(((BluetoothDevice)
                madapter.getBondedDevices().toArray()[0]).getAddress());

        bluetoothSocket = mdevice.createRfcommSocketToServiceRecord(UUID.randomUUID());
        BluetoothService bluetoothService = new BluetoothService();
        bluetoothService.execute();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        editText = findViewById(R.id.editText);

        button.setOnClickListener(view -> {
            try {
                outputStream.write(editText.getText().toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    public class BluetoothService extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void[] objects) {

            {
                try {
                    bluetoothSocket.connect();
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
            try {
                outputStream = bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }
}