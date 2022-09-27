package dev.manuel.events;

import dev.manuel.ui.CustomDialog;

public class DeviceDialogListener implements DialogListener{
  @Override
  public void sendPressed(Object[] selectedItems, String message, CustomDialog dialog) {
    System.out.println(message);

    for (Object obj: selectedItems) {

    }
  }
}
