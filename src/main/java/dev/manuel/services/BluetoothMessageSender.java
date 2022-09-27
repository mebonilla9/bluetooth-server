package dev.manuel.services;

import dev.manuel.ui.CustomDialog;

import javax.microedition.io.Connector;
import javax.obex.ClientSession;
import javax.obex.HeaderSet;
import javax.obex.Operation;
import javax.obex.ResponseCodes;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BluetoothMessageSender {

  public static boolean sendMessage(
    String deviceUrl,
    String message,
    CustomDialog dialog) throws IOException {
    System.out.println("Connecting to: " + deviceUrl);
    dialog.setStatus("Connecting to: " + deviceUrl);

    ClientSession clientSession = (ClientSession) Connector.open(deviceUrl);

    HeaderSet hsConnectReply = clientSession.connect(null);

    if (hsConnectReply.getResponseCode() != ResponseCodes.OBEX_HTTP_OK) {
      System.out.println("Failed to connect");
      dialog.setStatus("Failed to connect");
      return false;
    }

    HeaderSet hsOperation = clientSession.createHeaderSet();
    hsOperation.setHeader(HeaderSet.NAME, "Names.txt");
    hsOperation.setHeader(HeaderSet.TYPE, "text");
    Operation putOperation = clientSession.put(hsOperation);

    byte[] data = message.getBytes(StandardCharsets.UTF_8);
    OutputStream os = putOperation.openOutputStream();
    os.write(data);
    os.close();

    putOperation.close();
    clientSession.disconnect(null);
    clientSession.close();

    return true;

  }
}
