/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Client;

import java.awt.FileDialog;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author MaxKleb
 */
public class Client extends javax.swing.JFrame {
    FileDialog fc;
    Socket socket = null; 
    Thread clientThread;
    String fileName = "";
        
    /**
     * Creates new form Client
     */
    public Client() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        clientText = new javax.swing.JTextArea();
        connectButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        clientText.setColumns(20);
        clientText.setRows(5);
        jScrollPane1.setViewportView(clientText);

        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 204));
        jLabel2.setText("ClientApplication");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(connectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(connectButton)
                    .addComponent(jLabel2))
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
        // TODO add your handling code here:
        if(connectButton.getText().equals("Exit")){
            setVisible(false); //you can't see me!
            dispose(); //Destroy the JFrame object
        }
        else {
            if(fileName.equals("")){
                fc = new FileDialog(this, "Choose a file", FileDialog.LOAD);
                fc.setDirectory("C:\\");
                fc.setVisible(true);
                fileName = fc.getFile();
                clientText.setText(clientText.getText()+fileName+"\n");
                connectButton.setText("Copy");
            }
            else{

                clientThread = new Thread(){

                    public void run(){
                       PrintWriter out = null;
                       BufferedReader in = null;
                       int filesize = 4346250; //filesize temporary hardcoded 
                       int bytesRead;
                       int current = 0;

                        try {
                            socket = new Socket("127.0.0.1", 55555);   //establish the socket connection between the client and the server
                            clientText.setText("Connection has been created.\n");

                            out = new PrintWriter(socket.getOutputStream(), true);
                            out.println("<dist>"+fileName);
                            //out.close();

                            byte [] mybytearray = new byte [filesize];
                            InputStream is = socket.getInputStream();

                            FileOutputStream fos = new FileOutputStream ("copy_"+fileName);

                            BufferedOutputStream bos = new BufferedOutputStream(fos);

                            bytesRead = is.read(mybytearray, 0, mybytearray.length/2);
                            current = bytesRead;

                            //clientText.setText(clientText.getText()+"\nCurrent bytesRead is - "+current);
                            do{
                           //   clientText.setText(clientText.getText()+"\nbytesRead - "+bytesRead+" Current is "+current);
                              bytesRead =  is.read(mybytearray, current, (mybytearray.length/2-current)); 
                              if (bytesRead >=0) current +=bytesRead;
                            }while (bytesRead > 0);

                            System.out.println("Middle!!!");

                            //Continue window
                            bos.write(mybytearray, 0, current);
                            JFrame frame = new JFrame("JOptionPane showMessageDialog example");
                            int n = -1;
                            n = JOptionPane.showConfirmDialog(frame,
                            "50 % has been copied.\nWould you like to continue?",
                            "Question",
                            JOptionPane.DEFAULT_OPTION);
                            //System.out.println(n);
                            //clientText.setText(clientText.getText()+"\nMiddle!!!!!");

                            while(n!=0){}
                            int next = current;
                            bytesRead = is.read(mybytearray, 0, mybytearray.length);
                            current = bytesRead;
                            do{
                             // clientText.setText(clientText.getText()+"\nbytesRead - "+bytesRead+" Current is "+current);
                              bytesRead =  is.read(mybytearray, current, (mybytearray.length-current)); 
                              if (bytesRead >=0) current +=bytesRead;
                            }while (bytesRead > 0);
                            bos.write(mybytearray, 0, current);
                            bos.close();
                            bos.flush();
                            //socket.close();
                            clientText.setText(clientText.getText()+"File was copied.\nThe last byte was "+current+"\n");

                            //Wating for disconnect command
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String msg;
                            while ((msg = in.readLine()) != null){
                                if(msg.contains("DisconnectAll")){
                                    socket.close();
                                    clientText.setText(clientText.getText()+"Server has been disconnect.\n");
                                    break;
                                }

                            }

                     } catch (IOException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }

                    }
                };
                connectButton.setText("Exit");
                clientThread.start();
                // connectButton.setText("Exit");
            }
        }
    }//GEN-LAST:event_connectButtonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea clientText;
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
