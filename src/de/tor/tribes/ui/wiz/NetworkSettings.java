/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FirstPage.java
 *
 * Created on Aug 27, 2011, 2:50:38 PM
 */
package de.tor.tribes.ui.wiz;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardController;
import org.netbeans.spi.wizard.WizardPanel;
import org.netbeans.spi.wizard.WizardPanelNavResult;

/**
 *
 * @author Torridity
 */
public class NetworkSettings extends javax.swing.JPanel implements WizardPanel {

    private WizardController wizCtrl;
    private Map currentSettings = null;

    /** Creates new form FirstPage */
    public NetworkSettings(final WizardController pWizCtrl, final Map map) {
        initComponents();
        wizCtrl = pWizCtrl;
        currentSettings = map;
        wizCtrl.setProblem("Bitte teste die Einstellungen um fortzufahren");
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel8 = new javax.swing.JPanel();
        jDirectConnectOption = new javax.swing.JRadioButton();
        jProxyConnectOption = new javax.swing.JRadioButton();
        jProxyAdressLabel = new javax.swing.JLabel();
        jProxyHost = new javax.swing.JTextField();
        jProxyPortLabel = new javax.swing.JLabel();
        jProxyPort = new javax.swing.JTextField();
        jRefeshNetworkButton = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jProxyTypeChooser = new javax.swing.JComboBox();
        jLabel11 = new javax.swing.JLabel();
        jProxyUser = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jProxyPassword = new javax.swing.JPasswordField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();

        setMaximumSize(new java.awt.Dimension(400, 400));
        setMinimumSize(new java.awt.Dimension(400, 400));
        setPreferredSize(new java.awt.Dimension(400, 400));
        setLayout(new java.awt.GridBagLayout());

        jPanel8.setMaximumSize(new java.awt.Dimension(400, 231));
        jPanel8.setOpaque(false);
        jPanel8.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jDirectConnectOption);
        jDirectConnectOption.setSelected(true);
        jDirectConnectOption.setText("Ich bin direkt mit dem Internet verbunden");
        jDirectConnectOption.setMaximumSize(new java.awt.Dimension(259, 23));
        jDirectConnectOption.setMinimumSize(new java.awt.Dimension(259, 23));
        jDirectConnectOption.setOpaque(false);
        jDirectConnectOption.setPreferredSize(new java.awt.Dimension(259, 23));
        jDirectConnectOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jDirectConnectOptionfireChangeConnectTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jDirectConnectOption, gridBagConstraints);

        buttonGroup1.add(jProxyConnectOption);
        jProxyConnectOption.setText("Ich benutze einen Proxy für den Internetzugang");
        jProxyConnectOption.setOpaque(false);
        jProxyConnectOption.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jProxyConnectOptionfireChangeConnectTypeEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyConnectOption, gridBagConstraints);

        jProxyAdressLabel.setText("Proxy Adresse");
        jProxyAdressLabel.setMaximumSize(new java.awt.Dimension(100, 23));
        jProxyAdressLabel.setMinimumSize(new java.awt.Dimension(100, 23));
        jProxyAdressLabel.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyAdressLabel, gridBagConstraints);

        jProxyHost.setToolTipText("Adresse des Proxy Servers");
        jProxyHost.setEnabled(false);
        jProxyHost.setMinimumSize(new java.awt.Dimension(6, 23));
        jProxyHost.setPreferredSize(new java.awt.Dimension(6, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyHost, gridBagConstraints);

        jProxyPortLabel.setText("Proxy Port");
        jProxyPortLabel.setMaximumSize(new java.awt.Dimension(70, 23));
        jProxyPortLabel.setMinimumSize(new java.awt.Dimension(70, 23));
        jProxyPortLabel.setPreferredSize(new java.awt.Dimension(70, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyPortLabel, gridBagConstraints);

        jProxyPort.setToolTipText("Port des Proxy Servers");
        jProxyPort.setEnabled(false);
        jProxyPort.setMaximumSize(new java.awt.Dimension(40, 23));
        jProxyPort.setMinimumSize(new java.awt.Dimension(40, 23));
        jProxyPort.setPreferredSize(new java.awt.Dimension(40, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyPort, gridBagConstraints);

        jRefeshNetworkButton.setText("Testen");
        jRefeshNetworkButton.setToolTipText("Netzwerkeinstellungen aktualisieren und prüfen");
        jRefeshNetworkButton.setMaximumSize(new java.awt.Dimension(120, 23));
        jRefeshNetworkButton.setMinimumSize(new java.awt.Dimension(120, 23));
        jRefeshNetworkButton.setPreferredSize(new java.awt.Dimension(120, 23));
        jRefeshNetworkButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRefeshNetworkButtonfireUpdateProxySettingsEvent(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(15, 5, 0, 5);
        jPanel8.add(jRefeshNetworkButton, gridBagConstraints);

        jLabel10.setText("Proxy Typ");
        jLabel10.setMaximumSize(new java.awt.Dimension(100, 23));
        jLabel10.setMinimumSize(new java.awt.Dimension(100, 23));
        jLabel10.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jLabel10, gridBagConstraints);

        jProxyTypeChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "HTTP", "SOCKS" }));
        jProxyTypeChooser.setToolTipText("Art des Proxy Servers");
        jProxyTypeChooser.setEnabled(false);
        jProxyTypeChooser.setMinimumSize(new java.awt.Dimension(100, 23));
        jProxyTypeChooser.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyTypeChooser, gridBagConstraints);

        jLabel11.setText("Benutzername");
        jLabel11.setMaximumSize(new java.awt.Dimension(100, 23));
        jLabel11.setMinimumSize(new java.awt.Dimension(100, 23));
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jLabel11, gridBagConstraints);

        jProxyUser.setToolTipText("Benutzername zur Authentifizierung beim Proxy Server");
        jProxyUser.setEnabled(false);
        jProxyUser.setMaximumSize(new java.awt.Dimension(150, 23));
        jProxyUser.setMinimumSize(new java.awt.Dimension(150, 23));
        jProxyUser.setPreferredSize(new java.awt.Dimension(150, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyUser, gridBagConstraints);

        jLabel12.setText("Passwort");
        jLabel12.setMaximumSize(new java.awt.Dimension(100, 23));
        jLabel12.setMinimumSize(new java.awt.Dimension(100, 23));
        jLabel12.setPreferredSize(new java.awt.Dimension(100, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jLabel12, gridBagConstraints);

        jProxyPassword.setToolTipText("Passwort zur Authentifizierung beim Proxy Server");
        jProxyPassword.setEnabled(false);
        jProxyPassword.setMaximumSize(new java.awt.Dimension(150, 23));
        jProxyPassword.setMinimumSize(new java.awt.Dimension(150, 23));
        jProxyPassword.setPreferredSize(new java.awt.Dimension(150, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jPanel8.add(jProxyPassword, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jPanel8, gridBagConstraints);

        jScrollPane1.setMaximumSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setMinimumSize(new java.awt.Dimension(400, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(400, 100));

        jTextPane1.setContentType("text/html");
        jTextPane1.setEditable(false);
        jTextPane1.setText("<html>Die Netzwerkeinstellungen sind notwendig, falls du nicht direkt mit dem Internet verbunden bist. Dies ist oftmals in Firmennetzwerken oder Wohnheimen der Fall. Als Privatperson in einem Heimnetzwerk sollte man in der Regel direkt mit dem Internet verbunden sein. Klicke auf den Button <b>Testen</b>, um deine Einstellungen zu überprüfen und fortfahren zu können.</html>");
        jScrollPane1.setViewportView(jTextPane1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(jScrollPane1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

private void jDirectConnectOptionfireChangeConnectTypeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jDirectConnectOptionfireChangeConnectTypeEvent
    jProxyHost.setEnabled(jProxyConnectOption.isSelected());
    jProxyPort.setEnabled(jProxyConnectOption.isSelected());
    jProxyUser.setEnabled(jProxyConnectOption.isSelected());
    jProxyPassword.setEnabled(jProxyConnectOption.isSelected());
    jProxyTypeChooser.setEnabled(jProxyConnectOption.isSelected());
    wizCtrl.setProblem("Bitte teste die Einstellungen um fortzufahren");
}//GEN-LAST:event_jDirectConnectOptionfireChangeConnectTypeEvent

private void jProxyConnectOptionfireChangeConnectTypeEvent(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jProxyConnectOptionfireChangeConnectTypeEvent
    jProxyHost.setEnabled(jProxyConnectOption.isSelected());
    jProxyPort.setEnabled(jProxyConnectOption.isSelected());
    jProxyUser.setEnabled(jProxyConnectOption.isSelected());
    jProxyPassword.setEnabled(jProxyConnectOption.isSelected());
    jProxyTypeChooser.setEnabled(jProxyConnectOption.isSelected());
    wizCtrl.setProblem("Bitte teste die Einstellungen um fortzufahren");
}//GEN-LAST:event_jProxyConnectOptionfireChangeConnectTypeEvent

private void jRefeshNetworkButtonfireUpdateProxySettingsEvent(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRefeshNetworkButtonfireUpdateProxySettingsEvent


    if (jProxyConnectOption.isSelected()) {
        try {
            Integer.parseInt(jProxyPort.getText());
        } catch (NumberFormatException nfe) {
            wizCtrl.setProblem("Der Wert für den Port ist ungültig");
            return;
        }
    }

    Map tempSettings = new HashMap();
    tempSettings.put("proxySet", Boolean.toString(jProxyConnectOption.isSelected()));
    tempSettings.put("proxyHost", (jProxyConnectOption.isSelected()) ? jProxyHost.getText() : "");
    tempSettings.put("proxyPort", (jProxyConnectOption.isSelected()) ? jProxyPort.getText() : "");
    tempSettings.put("proxyType", (jProxyConnectOption.isSelected()) ? Integer.toBinaryString(jProxyTypeChooser.getSelectedIndex()) : "");
    tempSettings.put("proxyUser", (jProxyConnectOption.isSelected()) ? jProxyUser.getText() : "");
    tempSettings.put("proxyPassword", (jProxyConnectOption.isSelected()) ? new String(jProxyPassword.getPassword()) : "");



    /* if (jProxyConnectOption.isSelected()) {
    //store properties
    SocketAddress addr = null;
    try {
    addr = new InetSocketAddress(jProxyHost.getText(), Integer.parseInt(jProxyPort.getText()));
    } catch (NumberFormatException nfe) {
    wizCtrl.setProblem("Der Wert für den Port ist ungültig");
    return;
    }
    currentSettings.put("proxySet", Boolean.toString(true));
    currentSettings.put("proxyHost", jProxyHost.getText());
    currentSettings.put("proxyPort", jProxyPort.getText());
    currentSettings.put("proxyType", Integer.toString(jProxyTypeChooser.getSelectedIndex()));
    currentSettings.put("proxyUser", jProxyUser.getText());
    currentSettings.put("proxyPassword", new String(jProxyPassword.getPassword()));
    
    //create proxy object
    switch (jProxyTypeChooser.getSelectedIndex()) {
    case 1: {
    webProxy = new Proxy(Proxy.Type.SOCKS, addr);
    break;
    }
    default: {
    webProxy = new Proxy(Proxy.Type.HTTP, addr);
    break;
    }
    }
    if ((jProxyUser.getText().length() >= 1) && (jProxyPassword.getPassword().length > 1)) {
    Authenticator.setDefault(new Authenticator() {
    
    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
    return new PasswordAuthentication(jProxyUser.getText(), jProxyPassword.getPassword());
    }
    });
    }
    } else {
    currentSettings.put("proxySet", Boolean.toString(jProxyConnectOption.isSelected()));
    currentSettings.put("proxyHost", "");
    currentSettings.put("proxyPort", "");
    currentSettings.put("proxyType", "");
    currentSettings.put("proxyUser", "");
    currentSettings.put("proxyPassword", "");
    Authenticator.setDefault(null);
    
    webProxy = Proxy.NO_PROXY;
    }
    
     */
    if (checkConnectivity(ProxyHelper.getProxyFromProperties(tempSettings))) {
        currentSettings.put("proxySet", Boolean.toString(jProxyConnectOption.isSelected()));
        currentSettings.put("proxyHost", (jProxyConnectOption.isSelected()) ? jProxyHost.getText() : "");
        currentSettings.put("proxyPort", (jProxyConnectOption.isSelected()) ? jProxyPort.getText() : "");
        currentSettings.put("proxyType", (jProxyConnectOption.isSelected()) ? Integer.toBinaryString(jProxyTypeChooser.getSelectedIndex()) : "");
        currentSettings.put("proxyUser", (jProxyConnectOption.isSelected()) ? jProxyUser.getText() : "");
        currentSettings.put("proxyPassword", (jProxyConnectOption.isSelected()) ? new String(jProxyPassword.getPassword()) : "");
    }
}//GEN-LAST:event_jRefeshNetworkButtonfireUpdateProxySettingsEvent

    private boolean checkConnectivity(Proxy webProxy) {
        boolean result = false;
        try {
            URLConnection c = new URL("http://www.dsworkbench.de").openConnection(webProxy);
            c.setConnectTimeout(10000);
            String header = c.getHeaderField(0);
            if (header != null) {
                wizCtrl.setProblem(null);
                result = true;
            } else {
                wizCtrl.setProblem("Verbindung fehlgeschlagen. Bitte überprüfe deine Einstellungen");
            }
        } catch (Exception in) {
            wizCtrl.setProblem("Verbindung fehlgeschlagen. Bitte überprüfe deine Einstellungen");
        }
        return result;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JRadioButton jDirectConnectOption;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JLabel jProxyAdressLabel;
    private javax.swing.JRadioButton jProxyConnectOption;
    private javax.swing.JTextField jProxyHost;
    private javax.swing.JPasswordField jProxyPassword;
    private javax.swing.JTextField jProxyPort;
    private javax.swing.JLabel jProxyPortLabel;
    private javax.swing.JComboBox jProxyTypeChooser;
    private javax.swing.JTextField jProxyUser;
    private javax.swing.JButton jRefeshNetworkButton;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public WizardPanelNavResult allowNext(String stepName, Map settings, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;
    }

    @Override
    public WizardPanelNavResult allowBack(String stepName, Map settings, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;
    }

    @Override
    public WizardPanelNavResult allowFinish(String stepName, Map settings, Wizard wizard) {
        return WizardPanelNavResult.PROCEED;
    }
}
