package dev.manuel.events;

import dev.manuel.ui.CustomDialog;

public interface DialogListener {

  void sendPressed(Object[] selectedItems, String message, CustomDialog dialog);

}
