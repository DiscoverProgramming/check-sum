import javax.swing.*;
import javax.xml.crypto.AlgorithmMethod;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32C;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

public class VerifyGUI {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Checksum Verifier");
        JPanel panel = new JPanel(null);

        JFileChooser fileChooser = new JFileChooser();

        JLabel fileLabel = new JLabel("File: ");
        JTextField fileField = new JTextField();
        JButton browseButton = new JButton("Browse");

        JLabel ofileLabel = new JLabel("Offical CheckSum: ");
        JTextField ofileField = new JTextField();
        JButton checkButton = new JButton("Check");

        JLabel result = new JLabel("Waiting...");

        JButton generateButton = new JButton("Generate");
        
        
        fileLabel.setBounds(15, 15, 50, 20);
        fileField.setBounds(55, 15, 245, 20);
        browseButton.setBounds(320, 15, 90, 20);

        ofileLabel.setBounds(15, 50, 150, 20);
        ofileField.setBounds(150, 50, 150, 20);
        checkButton.setBounds(320, 50, 80, 20);

        result.setBounds(15, 85, 300, 20);
        generateButton.setBounds(300, 85, 105, 20);
        
        browseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int value = fileChooser.showOpenDialog(fileChooser);

                switch(value) {

                    case (JFileChooser.APPROVE_OPTION):
                        File file = fileChooser.getSelectedFile();

                        fileField.setText(file.toString());
                        break;
                }
            }
            
        });

        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();

        JFrame generateFrame = new JFrame("Generate Checksum");
                JPanel generatePanel = new JPanel(null);

                JLabel generateFileLabel = new JLabel("File: ");
                JTextField generateFileField = new JTextField();
                JButton generateBrowseButton = new JButton("Browse");
                JButton generatePanelButton = new JButton("Generate");
                JLabel generateResult = new JLabel();
                generateResult.setFont(new Font("Consolas", Font.BOLD, 10));

                generateFileLabel.setBounds(15, 15, 50, 20);
                generateFileField.setBounds(65, 15, 250, 20);
                generateBrowseButton.setBounds(320, 15, 90, 20);
                generatePanelButton.setBounds(15, 50, 105, 20);
                generateResult.setBounds(130, 30, 300, 50);

                generatePanelButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        try {
                            generateResult.setText("<html>MD5 Checksum(Click to copy):<br>" + MD5Sum(generateFileField.getText()) + "</html>");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                generateResult.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent arg0) {
                        if(arg0.getSource() == generateResult) {

                            Pattern pattern = Pattern.compile("<html>MD5 Checksum\\(Click to copy\\):<br>([A-Za-z0-9]+)</html>");
                            Matcher matcher = pattern.matcher(generateResult.getText());
                            if(matcher.find()) {
                                cb.setContents(new StringSelection(matcher.group(1)), null);
                            }
                        }
                    }
                });

                generateBrowseButton.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        int value = fileChooser.showOpenDialog(fileChooser);

                        if(value == JFileChooser.APPROVE_OPTION) {
                            File file = fileChooser.getSelectedFile();

                            generateFileField.setText(file.toString());
                        }
                    }
                    
                });

                generatePanel.add(generateFileLabel);
                generatePanel.add(generateFileField);
                generatePanel.add(generateBrowseButton);
                generatePanel.add(generatePanelButton);
                generatePanel.add(generateResult);
                generateFrame.add(generatePanel);

                generateFrame.setSize(422, 119);

                generateFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

        checkButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                File f = new File(fileField.getText());

                if(f.isFile()) {
                    boolean valid = false;
                    try {
                        if(MD5Sum(fileField.getText()).equals(ofileField.getText())) {
                            valid = true;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if(valid) {
                        result.setText("Checksums Match!");
                    } else {
                        result.setText("Checksums don't match.");
                    }
                }
            }
            
        });

        generateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                generateFrame.setVisible(true);
            }
            
        });

        panel.add(fileLabel);
        panel.add(fileField);
        panel.add(browseButton);
        panel.add(checkButton);
        panel.add(ofileLabel);
        panel.add(ofileField);
        panel.add(result);
        panel.add(generateButton);
        
        frame.add(panel);
        
        frame.setSize(413, 156);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }

    private static String MD5Sum(String f) throws IOException, NoSuchAlgorithmException {
        byte[] data = Files.readAllBytes(Paths.get(f));
        byte[] hash = MessageDigest.getInstance("MD5").digest(data);
        String checksum = new BigInteger(1, hash).toString(16);

        return checksum;
    }

//     protected static boolean check(String first, String second) throws IOException {

//         byte[] bytesOfMessage = first.getBytes("UTF-8");
// String r = "";
//         try {
//             MessageDigest md = MessageDigest.getInstance("MD5");
//             try (InputStream is = Files.newInputStream(Paths.get(first));
//                 DigestInputStream dis = new DigestInputStream(is, md))
//             {
//                 System.out.println(dis.readAllBytes());

//                 byte[] digest = dis.getMessageDigest().digest(bytesOfMessage);
                
//                 for (int i = 0; i < digest.length; i++) {
//                     r += Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
//             }
//             }
//             // byte[] digest = md.digest(bytesOfMessage);

//             // String r = "";

//             // for (int i = 0; i < digest.length; i++) {
//             //     r += Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
//             // }
//             System.out.println(r);

//             return true;
//         } catch (NoSuchAlgorithmException e) {
//             e.printStackTrace();
//         }
//         return false;
//     }
}
