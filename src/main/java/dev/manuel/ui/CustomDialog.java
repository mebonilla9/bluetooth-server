package dev.manuel.ui;

import dev.manuel.entities.BluetoothDevice;
import dev.manuel.events.DialogListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomDialog extends JDialog implements ActionListener {

  private final JTextArea txtMessage;
  private final JList<BluetoothDevice> lstDevices;
  private final DialogListener listener;
  private BluetoothDevice[] items;
  private final JLabel lblStatus;

  public CustomDialog(String title, BluetoothDevice[] items, DialogListener listener) {
    super.setTitle(title);
    this.listener = listener;
    this.setSize(500, 300);
    lstDevices = new JList<>();
    lstDevices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    loadListData(items == null ? new BluetoothDevice[0] : items);

    this.items = items;

    JScrollPane scrollPane = new JScrollPane(lstDevices);
    scrollPane.setSize(255, 5);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(scrollPane, BorderLayout.WEST);

    txtMessage = new JTextArea("Enter message here");
    this.getContentPane().add(txtMessage, BorderLayout.CENTER);

    JButton btnSend = new JButton("Send");
    btnSend.addActionListener(this);
    this.getContentPane().add(btnSend, BorderLayout.SOUTH);

    lblStatus = new JLabel("Started");
    this.getContentPane().add(lblStatus, BorderLayout.NORTH);

    setLocationRelativeTo(null);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    this.setVisible(true);
  }

  public void addItem(BluetoothDevice item) {
    BluetoothDevice[] newItems = new BluetoothDevice[this.items.length + 1];
    System.arraycopy(items, 0, newItems, 0, items.length);
    newItems[newItems.length - 1] = item;

    items = newItems;
    loadListData(newItems);
  }

  public void setStatus(String text) {
    lblStatus.setText(text);
  }

  public void loadListData(BluetoothDevice[] items) {
    DefaultListModel<BluetoothDevice> listModel = new DefaultListModel<>();
    for (BluetoothDevice item : items) {
      listModel.addElement(item);
    }
    lstDevices.setModel(listModel);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Send")) {
      String message = txtMessage.getText();
      Object[] selectedItems = lstDevices.getSelectedValuesList().toArray();

      listener.sendPressed(selectedItems, message, this);

    }
  }
}
