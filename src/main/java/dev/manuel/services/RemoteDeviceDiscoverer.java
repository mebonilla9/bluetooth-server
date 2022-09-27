package dev.manuel.services;

import javax.bluetooth.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteDeviceDiscoverer {

  private final ArrayList<RemoteDevice> devicesDiscovered;

  public RemoteDeviceDiscoverer() {
    this.devicesDiscovered = new ArrayList<>();
  }

  public void searchDevices() throws BluetoothStateException, InterruptedException {
    final Object inquiryCompletedEvent = new Object();

    devicesDiscovered.clear();

    DiscoveryListener listener = new DiscoveryListener() {
      @Override
      public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        System.out.println("Device "+ remoteDevice.getBluetoothAddress() + " found");
        devicesDiscovered.add(remoteDevice);
        try {
          System.out.println("-- Name: "+ remoteDevice.getFriendlyName(false));
        } catch (IOException e) {
          System.out.println(e.getMessage());
        }
      }

      @Override
      public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {

      }

      @Override
      public void serviceSearchCompleted(int i, int i1) {

      }

      @Override
      public void inquiryCompleted(int discType) {
        System.out.println("Device Inquiry completed!");
        synchronized (inquiryCompletedEvent){
          inquiryCompletedEvent.notify();
        }
      }
    };

    synchronized (inquiryCompletedEvent){
      boolean started = LocalDevice.getLocalDevice()
        .getDiscoveryAgent()
        .startInquiry(DiscoveryAgent.GIAC, listener);
      if (started){
        inquiryCompletedEvent.wait();
        System.out.println(devicesDiscovered.size() + " device(s) found");
      }
    }

  }

  public ArrayList<RemoteDevice> getDevicesDiscovered(){
    return devicesDiscovered;
  }

}
