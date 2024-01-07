package humble.wolf.volume_changer_app;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TcpClient {
    public interface TcpClientListener {
        void onMessageReceived(String message);
        void onError(Exception e);
    }

    public static void sendMessage(final String ipAddress, final int port, final String message, final TcpClientListener listener) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket socket = null;
                try {
                    // Create a socket with the given IP address and port
                    socket = new Socket(ipAddress, port);

                    // Get the output stream of the socket
                    OutputStream outputStream = socket.getOutputStream();
                    InputStream inputStream = socket.getInputStream();

                    // Convert the message to bytes and send it
                    byte[] messageBytes = message.getBytes();
                    outputStream.write(messageBytes);
                    outputStream.flush();

                    // Read the response from the InputStream
                    byte[] buffer = new byte[1024];
                    int bytesRead = inputStream.read(buffer);
                    if (bytesRead != -1) {
                        String response = new String(buffer, 0, bytesRead);
                        // Notify the listener with the received message
                        if (listener != null) {
                            listener.onMessageReceived(response);
                        }
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    if (listener != null) {
                        listener.onError(e);
                    }
                } finally {
                    try {
                        // Close the socket when done
                        if (socket != null) {
                            socket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}