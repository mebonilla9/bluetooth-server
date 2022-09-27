package dev.manuel.application;

import dev.manuel.entities.BluetoothDevice;
import dev.manuel.events.DeviceDialogListener;
import dev.manuel.services.RemoteDeviceDiscoverer;
import dev.manuel.ui.CustomDialog;

import javax.bluetooth.*;
import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;


public class BluetoothServiceSearcher {

  private String tempDeviceName;
  private CustomDialog dialog;
  private RemoteDeviceDiscoverer deviceDiscoverer;

  private final UUID OBEX_OBJECT_PUSH = new UUID(0x1105);
  private final ArrayList<String> serviceFound;

  public BluetoothServiceSearcher() {
    this.serviceFound = new ArrayList<>();
  }

  private void start(String... args) {

    try {
      dialog = new CustomDialog(
        "Bluetooth multi-sender",
        null,
        new DeviceDialogListener()
      );

      deviceDiscoverer = new RemoteDeviceDiscoverer();

      dialog.setStatus("Searching devices, please wait");
      deviceDiscoverer.searchDevices();

      serviceFound.clear();

      UUID serviceUUID = OBEX_OBJECT_PUSH;
      if (args != null && args.length > 0) {
        serviceUUID = new UUID(args[0], false);
      }

      final Object serviceSearchCompletedEvent = new Object();

      DiscoveryListener listener = new DiscoveryListener() {
        @Override
        public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {

        }

        @Override
        public void servicesDiscovered(int transId, ServiceRecord[] serviceRecords) {
          for (int i = 0; i < serviceRecords.length; i++) {
            String connectionURL = serviceRecords[i].getConnectionURL(
              ServiceRecord.NOAUTHENTICATE_NOENCRYPT, false
            );
            if (connectionURL == null) {
              continue;
            }
            serviceFound.add(connectionURL);

            DataElement serviceName = serviceRecords[i].getAttributeValue(0x0100);
            if (serviceName != null) {
              System.out.println("Service " + serviceName.getValue() + " found " + connectionURL);
            } else {
              System.out.println("Service found " + connectionURL);
            }
            dialog.addItem(new BluetoothDevice(tempDeviceName, connectionURL));
          }
        }

        @Override
        public void serviceSearchCompleted(int transid, int responseCode) {
          System.out.println("Service search completed!");
          synchronized (serviceSearchCompletedEvent) {
            serviceSearchCompletedEvent.notifyAll();
          }
        }

        @Override
        public void inquiryCompleted(int i) {

        }
      };

      UUID[] searchUuidSet = new UUID[]{serviceUUID};
      int[] attrIds = new int[]{0x0100};

      int size = deviceDiscoverer.getDevicesDiscovered().size();
      dialog.setStatus(size + " devices found. Now checking which is supported.");
      for (Enumeration<RemoteDevice> en = Collections.enumeration(deviceDiscoverer.getDevicesDiscovered()); en.hasMoreElements(); ) {
        RemoteDevice remoteDevice = en.nextElement();
        synchronized (serviceSearchCompletedEvent) {
          String friendlyName = remoteDevice.getFriendlyName(false);
          tempDeviceName = friendlyName;
          System.out.println("Search services on " + remoteDevice.getBluetoothAddress() + " " + friendlyName);
          LocalDevice.getLocalDevice()
            .getDiscoveryAgent()
            .searchServices(
              attrIds,
              searchUuidSet,
              remoteDevice,
              listener
            );
          serviceSearchCompletedEvent.wait();
        }
      }
      dialog.setStatus("Search finished. Send a message");
    } catch (InterruptedException | IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
    new BluetoothServiceSearcher().start(args);
  }
}
