package com.aws.s3client.ui;

import com.aws.s3client.biz.S3Operations;
import com.aws.s3client.biz.S3OperationsImpl;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class S3Client extends JFrame {
    private static S3Client frame;
    private JPanel mainPanel;
    private JLabel uploadLabel;
    private JLabel selectBucket;
    private JComboBox bucketCombo;
    private JButton uploadFileButton;
    private JLabel fileUploadLabel;
    private JLabel keyNameLabel;
    private JTextField s3KeytextField;
    private JButton uploadToS3Button;
    private JLabel uploadResultLabel;
    private JLabel downloadLabel;
    private JLabel selectBucketLabel2;
    private JComboBox bucketCombo2;
    private JLabel enterKeyLabel2;
    private JTextField keyTextField2;
    private JButton downloadButton;
    private JButton deleteButton;
    private JLabel downloadResultLabel;
    private JTextField enterNewBucketNameTextBox;
    private JLabel bucketCreationLabel;
    private JButton addBucketButton;
    private final JFileChooser openFileChooser;
    private static S3Operations s3Operations;
    private final String addNewBucketOption = "<Add New Bucket>";
    //selected Fields on the UI.
    private String selectedBucketToUpload;
    private String selectedBucketToDownload;
    private String newBucketNameEntered;
    private String enteredKeyNameToUpload;
    private String enteredKeyNameToDownload;
    private File file;

    public S3Client(String appName) {
        super(appName);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setContentPane(mainPanel);
        this.pack();

        openFileChooser = new JFileChooser();
        openFileChooser.setCurrentDirectory(new File("c:\\temp"));
        //openFileChooser.setFileFilter(new FileNameExtensionFilter("PNG files","png"));

        setDataOnLoad();
        setInitialUIControlsEnablement();
        attachListeners();
    }

    public static void main(String[] args) {
        s3Operations = new S3OperationsImpl();
        frame = new S3Client("AWS S3 Client");
        frame.setVisible(true);
    }

    /**
     *
     */
    private void setInitialUIControlsEnablement() {
        enterNewBucketNameTextBox.setEnabled(String.valueOf(bucketCombo.getSelectedItem()).equals(addNewBucketOption));
        uploadFileButton.setEnabled(!String.valueOf(bucketCombo.getSelectedItem()).equals(addNewBucketOption));
        s3KeytextField.setEnabled(false);
        uploadToS3Button.setEnabled(false);
        keyTextField2.setEnabled(bucketCombo2.getSelectedItem() != null);
        downloadButton.setEnabled(false);
        deleteButton.setEnabled(false);
        selectedBucketToUpload = String.valueOf(bucketCombo.getSelectedItem());
        selectedBucketToDownload = String.valueOf(bucketCombo2.getSelectedItem());
    }

    /**
     * set data on the view on load.
     */
    private void setDataOnLoad() {
        for (String item : s3Operations.getBucketLists()) {
            bucketCombo.addItem(item);
            bucketCombo2.addItem(item);
        }
        bucketCombo.addItem(addNewBucketOption);
        selectedBucketToUpload = String.valueOf(bucketCombo.getSelectedItem());
        selectedBucketToDownload = String.valueOf(bucketCombo2.getSelectedItem());
    }

    /**
     * list of listeners.
     */
    private void attachListeners() {
        uploadBucketListComboListener();
        uploadEnterBucketNameListener();
        clickUploadFileButtonListener();
        uploadEnterKeyNameListener();
        clickUploadToS3ButtonListener();
        downloadBucketListComboListener();
        downloadEnterKeyNameListener();
        clickDownloadFromS3Listener();
        clickDeleteFromS3Listener();
    }

    /**
     *
     */
    private void uploadBucketListComboListener() {
        bucketCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedBucketToUpload = String.valueOf(bucketCombo.getSelectedItem());
                if (selectedBucketToUpload.equals(addNewBucketOption)) {
                    enterNewBucketNameTextBox.setEnabled(true);
                    enterNewBucketNameTextBox.setText("");
                    enterNewBucketNameTextBox.setFocusable(true);
                    uploadFileButton.setEnabled(false);
                } else {
                    enterNewBucketNameTextBox.setEnabled(false);
                    uploadFileButton.setEnabled(true);
                }
            }
        });
    }

    /**
     *
     */
    private void uploadEnterBucketNameListener() {
        enterNewBucketNameTextBox.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                newBucketNameEntered = ((JTextField) e.getSource()).getText() + String.valueOf(e.getKeyChar());
                if (newBucketNameEntered.length() > 0) {
                    uploadFileButton.setEnabled(true);
                } else {
                    uploadFileButton.setEnabled(false);
                    s3KeytextField.setEnabled(false);
                    uploadToS3Button.setEnabled(false);
                }
            }
        });
    }

    /**
     *
     */
    private void clickUploadFileButtonListener() {
        uploadFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int returnValue = openFileChooser.showOpenDialog(frame);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    file = openFileChooser.getSelectedFile();
                    s3KeytextField.setEnabled(true);
                    s3KeytextField.setFocusable(true);
                } else {
                    s3KeytextField.setEnabled(false);
                    uploadToS3Button.setEnabled(false);
                    fileUploadLabel.setText("please upload a File to continue");
                }
            }
        });
    }

    /**
     *
     */
    private void uploadEnterKeyNameListener() {
        s3KeytextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                enteredKeyNameToUpload = ((JTextField) e.getSource()).getText() + String.valueOf(e.getKeyChar());
                uploadToS3Button.setEnabled(enteredKeyNameToUpload.length() > 0);
            }
        });
    }

    /**
     *
     */
    private void clickUploadToS3ButtonListener() {
        uploadToS3Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedBucketToUpload.equals(addNewBucketOption)) {
                    selectedBucketToUpload = newBucketNameEntered;
                    String createBucketResult = s3Operations.createBucket(newBucketNameEntered);
                    bucketCreationLabel.setText(createBucketResult);
                }
                String putResult = s3Operations.putObject(selectedBucketToUpload, enteredKeyNameToUpload, file);
                uploadResultLabel.setText(putResult);

                reloadBucketList();
                enterNewBucketNameTextBox.setText("");
                s3KeytextField.setText("");
            }
        });
    }

    /**
     *
     */
    private void downloadBucketListComboListener() {
        bucketCombo2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedBucketToDownload = String.valueOf(bucketCombo2.getSelectedItem());
                if (selectedBucketToDownload != null || !selectedBucketToDownload.isEmpty()) {
                    keyTextField2.setEnabled(true);
                } else {
                    keyTextField2.setEnabled(false);
                }
            }
        });
    }

    /**
     *
     */
    private void downloadEnterKeyNameListener() {
        keyTextField2.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                enteredKeyNameToDownload = ((JTextField) e.getSource()).getText() + String.valueOf(e.getKeyChar());
                downloadButton.setEnabled(enteredKeyNameToDownload.length() > 0);
                deleteButton.setEnabled(enteredKeyNameToDownload.length() > 0);
            }
        });
    }

    /**
     *
     */
    private void clickDownloadFromS3Listener() {
        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String downloadResult = s3Operations.getObject(selectedBucketToDownload, enteredKeyNameToDownload);
                downloadResultLabel.setText(downloadResult);
            }
        });
    }

    /**
     *
     */
    private void clickDeleteFromS3Listener() {
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deleteResult = s3Operations.deleteObject(selectedBucketToDownload, enteredKeyNameToDownload);
                downloadResultLabel.setText(deleteResult);
            }
        });
    }

    /**
     *
     */
    private void reloadBucketList() {
        bucketCombo.removeAllItems();
        for (String item : s3Operations.getBucketLists()) {
            bucketCombo.addItem(item);
            bucketCombo2.addItem(item);
        }
        bucketCombo.addItem(addNewBucketOption);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(11, 4, new Insets(70, 70, 70, 70), 20, 20, true, true));
        mainPanel.setBackground(new Color(-1969425));
        mainPanel.setEnabled(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(-16777216)), "AWS S3 Client", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, this.$$$getFont$$$("Fira Code Medium", Font.BOLD, 16, mainPanel.getFont()), null));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        mainPanel.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 2, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        uploadLabel = new JLabel();
        uploadLabel.setText("Upload a File to AWS S3:");
        mainPanel.add(uploadLabel, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keyNameLabel = new JLabel();
        keyNameLabel.setText("Enter a Key Name for file:");
        mainPanel.add(keyNameLabel, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        s3KeytextField = new JTextField();
        s3KeytextField.setText("");
        mainPanel.add(s3KeytextField, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        uploadToS3Button = new JButton();
        uploadToS3Button.setText("Upload To S3");
        mainPanel.add(uploadToS3Button, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downloadLabel = new JLabel();
        downloadLabel.setText("Download or Delete a File from S3:");
        mainPanel.add(downloadLabel, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectBucketLabel2 = new JLabel();
        selectBucketLabel2.setText("Select a Bucket:");
        mainPanel.add(selectBucketLabel2, new com.intellij.uiDesigner.core.GridConstraints(7, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bucketCombo2 = new JComboBox();
        mainPanel.add(bucketCombo2, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enterKeyLabel2 = new JLabel();
        enterKeyLabel2.setText("Enther the Key value of the file:");
        mainPanel.add(enterKeyLabel2, new com.intellij.uiDesigner.core.GridConstraints(8, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        keyTextField2 = new JTextField();
        keyTextField2.setText("");
        mainPanel.add(keyTextField2, new com.intellij.uiDesigner.core.GridConstraints(8, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        downloadButton = new JButton();
        downloadButton.setText("Download  From S3");
        mainPanel.add(downloadButton, new com.intellij.uiDesigner.core.GridConstraints(9, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setText("Delete From S3");
        mainPanel.add(deleteButton, new com.intellij.uiDesigner.core.GridConstraints(9, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selectBucket = new JLabel();
        selectBucket.setText("Select a Bucket:");
        mainPanel.add(selectBucket, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        uploadFileButton = new JButton();
        uploadFileButton.setText("Upload a File...");
        mainPanel.add(uploadFileButton, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bucketCombo = new JComboBox();
        mainPanel.add(bucketCombo, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        enterNewBucketNameTextBox = new JTextField();
        enterNewBucketNameTextBox.setText("Enter Bucket Name");
        mainPanel.add(enterNewBucketNameTextBox, new com.intellij.uiDesigner.core.GridConstraints(2, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        uploadResultLabel = new JLabel();
        uploadResultLabel.setText("");
        mainPanel.add(uploadResultLabel, new com.intellij.uiDesigner.core.GridConstraints(5, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileUploadLabel = new JLabel();
        fileUploadLabel.setText("");
        mainPanel.add(fileUploadLabel, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bucketCreationLabel = new JLabel();
        bucketCreationLabel.setText("");
        mainPanel.add(bucketCreationLabel, new com.intellij.uiDesigner.core.GridConstraints(5, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        downloadResultLabel = new JLabel();
        downloadResultLabel.setText("");
        mainPanel.add(downloadResultLabel, new com.intellij.uiDesigner.core.GridConstraints(10, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont) {
        if (currentFont == null) return null;
        String resultName;
        if (fontName == null) {
            resultName = currentFont.getName();
        } else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if (testFont.canDisplay('a') && testFont.canDisplay('1')) {
                resultName = fontName;
            } else {
                resultName = currentFont.getName();
            }
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
