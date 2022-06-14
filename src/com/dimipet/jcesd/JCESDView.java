package com.dimipet.jcesd;

import java.awt.Color;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class JCESDView extends FrameView {

    /**
     * 2 ειδών βαθμολογίες για ερωτήσεις
     */
    private int a[] = {0, 1, 2, 3};
    private int b[] = {3, 2, 1, 0};
    
    private Question[] questions = new Question[20];
    //
    private ArrayList<JTextField> collectionOfJTextFields = new ArrayList<JTextField>();
    private ArrayList<JCheckBox> collectionOfCheckBoxes = new ArrayList<JCheckBox>();
    private ArrayList collectionOfButtonGroups = new ArrayList();
    ExcelController xlc = new ExcelController();
    //
    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;
    private JDialog aboutBox;
    //constructor

    public JCESDView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {

            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String) (evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer) (evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        this.jTextFieldTestCode.setVisible(false);
        this.jComboBoxAge.setVisible(false);
        this.jComboBoxEdu.setVisible(false);
        this.jComboBoxSex.setVisible(false);
        this.jButtonCancel.setVisible(false);
        this.jButtonSubmit.setVisible(false);

        this.questions[0] = new Question(1, "Με ενοχλούσαν πράγματα που συνήθως δε με ενοχλούν.", a, -1, false);
        this.questions[1] = new Question(2, "Δεν είχα διάθεση να φάω. Η όρεξή μου ήταν κακή.", a, -1, false);
        this.questions[2] = new Question(3, "Αισθανόμουν ότι δε θα μπορούσα να ξεφύγω από τις μαύρες μου, ακόμα ούτε και με τη βοήθεια της οικογένειάς μου ή των φίλων μου.", a, -1, false);
        this.questions[3] = new Question(4, "Αισθανόμουν ότι είμαι το ίδιο καλά όπως οι άλλοι άνθρωποι.", b, -1, false);
        this.questions[4] = new Question(5, "Είχα πρόβλημα στο να κρατήσω το μυαλό μου συγκεντρωμένο σ’ αυτό που έκανα.", a, -1, false);
        this.questions[5] = new Question(6, "Αισθανόμουν κατάθλιψη.", a, -1, false);
        this.questions[6] = new Question(7, "Αισθανόμουν ότι οτιδήποτε έκανα απαιτούσε μεγάλη προσπάθεια.", a, -1, false);
        this.questions[7] = new Question(8, "Αισθανόμουν γεμάτος/η ελπίδα για το μέλλον.", b, -1, false);
        this.questions[8] = new Question(9, "Πίστευα ότι η ζωή μου ολόκληρη ήταν μια αποτυχία.", a, -1, false);
        this.questions[9] = new Question(10, "Αισθανόμουν γεμάτος /η φόβο.", a, -1, false);
        this.questions[10] = new Question(11, "Ο ύπνος μου ήταν ανήσυχος.", a, -1, false);
        this.questions[11] = new Question(12, "Ήμουν χαρούμενος /η.", b, -1, false);
        this.questions[12] = new Question(13, "Μιλούσα λιγότερο από το συνηθισμένο.", a, -1, false);
        this.questions[13] = new Question(14, "Αισθανόμουν μοναξιά.", a, -1, false);
        this.questions[14] = new Question(15, "Οι άνθρωποι δεν ήταν φιλικοί μαζί μου.", a, -1, false);
        this.questions[15] = new Question(16, "Απολάμβανα τη ζωή.", b, -1, false);
        this.questions[16] = new Question(17, "Ξεσπούσα σε κλάμα.", a, -1, false);
        this.questions[17] = new Question(18, "Αισθανόμουν λυπημένος /η.", a, -1, false);
        this.questions[18] = new Question(19, "Ένιωθα ότι οι άλλοι με αντιπαθούσαν.", a, -1, false);
        this.questions[19] = new Question(20, "Δε μπορούσα να τα καταφέρω να ξεκινήσω να κάνω πράγματα.", a, -1, false);

        this.collectionOfJTextFields.add(this.jTextField0);
        this.collectionOfJTextFields.add(this.jTextField1);
        this.collectionOfJTextFields.add(this.jTextField2);
        this.collectionOfJTextFields.add(this.jTextField3);
        this.collectionOfJTextFields.add(this.jTextField4);
        this.collectionOfJTextFields.add(this.jTextField5);
        this.collectionOfJTextFields.add(this.jTextField6);
        this.collectionOfJTextFields.add(this.jTextField7);
        this.collectionOfJTextFields.add(this.jTextField8);
        this.collectionOfJTextFields.add(this.jTextField9);
        this.collectionOfJTextFields.add(this.jTextField10);
        this.collectionOfJTextFields.add(this.jTextField11);
        this.collectionOfJTextFields.add(this.jTextField12);
        this.collectionOfJTextFields.add(this.jTextField13);
        this.collectionOfJTextFields.add(this.jTextField14);
        this.collectionOfJTextFields.add(this.jTextField15);
        this.collectionOfJTextFields.add(this.jTextField16);
        this.collectionOfJTextFields.add(this.jTextField17);
        this.collectionOfJTextFields.add(this.jTextField18);
        this.collectionOfJTextFields.add(this.jTextField19);

        this.collectionOfButtonGroups.add(this.buttonGroup0);
        this.collectionOfButtonGroups.add(this.buttonGroup1);
        this.collectionOfButtonGroups.add(this.buttonGroup2);
        this.collectionOfButtonGroups.add(this.buttonGroup3);
        this.collectionOfButtonGroups.add(this.buttonGroup4);
        this.collectionOfButtonGroups.add(this.buttonGroup5);
        this.collectionOfButtonGroups.add(this.buttonGroup6);
        this.collectionOfButtonGroups.add(this.buttonGroup7);
        this.collectionOfButtonGroups.add(this.buttonGroup8);
        this.collectionOfButtonGroups.add(this.buttonGroup9);
        this.collectionOfButtonGroups.add(this.buttonGroup10);
        this.collectionOfButtonGroups.add(this.buttonGroup11);
        this.collectionOfButtonGroups.add(this.buttonGroup12);
        this.collectionOfButtonGroups.add(this.buttonGroup13);
        this.collectionOfButtonGroups.add(this.buttonGroup14);
        this.collectionOfButtonGroups.add(this.buttonGroup15);
        this.collectionOfButtonGroups.add(this.buttonGroup16);
        this.collectionOfButtonGroups.add(this.buttonGroup17);
        this.collectionOfButtonGroups.add(this.buttonGroup18);
        this.collectionOfButtonGroups.add(this.buttonGroup19);

        this.collectionOfCheckBoxes.add(this.jCheckBox0_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox0_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox0_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox0_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox1_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox1_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox1_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox1_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox2_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox2_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox2_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox2_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox3_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox3_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox3_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox3_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox4_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox4_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox4_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox4_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox5_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox5_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox5_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox5_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox6_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox6_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox6_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox6_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox7_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox7_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox7_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox7_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox8_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox8_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox8_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox8_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox9_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox9_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox9_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox9_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox10_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox10_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox10_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox10_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox11_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox11_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox11_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox11_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox12_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox12_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox12_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox12_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox13_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox13_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox13_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox13_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox14_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox14_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox14_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox14_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox15_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox15_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox15_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox15_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox16_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox16_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox16_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox16_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox17_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox17_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox17_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox17_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox18_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox18_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox18_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox18_3);
        this.collectionOfCheckBoxes.add(this.jCheckBox19_0);
        this.collectionOfCheckBoxes.add(this.jCheckBox19_1);
        this.collectionOfCheckBoxes.add(this.jCheckBox19_2);
        this.collectionOfCheckBoxes.add(this.jCheckBox19_3);
    }

    //Dimitris hardcode start ===========================================================================
    /**
     * checks the integrity of the checkboxes. If every checkbox is checked then
     * it returns true else it returns false
     * @return boolean
     */
    private boolean isAnswerIntegrityOK() {
        boolean ret = false;
        for (Question iter : this.questions) {
            //System.out.println("έξέταση ερώτησης "+ iter.getQuestionNumber() +" --> "+ iter.isAnswered());
            if (!iter.isAnswered()) {
                ret = false;
                break;
            } else {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Assigns a value to a question (buttongroup) based on the checked
     * checkbox
     *
     * @param btnGrp ButtonGroup if checkboxes
     * @param chkBx0 checkBox 0
     * @param chkBx1 checkBox 1
     * @param chkBx2 checkBox 2
     * @param chkBx3 checkBox 3
     * @return
     */
    private int calcValue(javax.swing.ButtonGroup btnGrp, javax.swing.JCheckBox chkBx0, javax.swing.JCheckBox chkBx1, javax.swing.JCheckBox chkBx2, javax.swing.JCheckBox chkBx3) {
        int ret = -1;

        if (chkBx0.isSelected()) {
            ret = 0;
        } else if (chkBx1.isSelected()) {
            ret = 1;
        } else if (chkBx2.isSelected()) {
            ret = 2;
        } else if (chkBx3.isSelected()) {
            ret = 3;
        }

        return ret;
    }

    /**
     * calculates final value based on the calculated value of each question
     *
     * @return integer
     */
    private int calcFinalValue() {
        int ret = 0;
        for (Question iter : this.questions) {
            //System.out.println("έξέταση ερώτησης "+ iter.getQuestionNumber() +" --> "+ iter.isAnswered());
            ret = ret + iter.getValueSelected();
        }
        return ret;
    }

    /**
     * calculates final value based on the calculated value of each question
     * then writes everything to the file
     */
    private void calcFinalValueAndWriteToFile() {
        int ret = 0;
        xlc.createNewRow();
        for (Question iter : this.questions) {
            //System.out.println("έξέταση ερώτησης "+ iter.getQuestionNumber() +" --> "+ iter.isAnswered());
            xlc.createAndWriteToCell(iter.getValueSelected());
            ret = ret + iter.getValueSelected();
        }
        xlc.createAndWriteToCell(ret);
        xlc.createAndWriteToCell(this.jTextFieldTestCode.getText());
        xlc.createAndWriteToCell(this.jComboBoxAge.getSelectedItem().toString());
        xlc.createAndWriteToCell(this.jComboBoxSex.getSelectedItem().toString());
        xlc.createAndWriteToCell(this.jComboBoxEdu.getSelectedItem().toString());
        xlc.writeToFile();
    }

    private void resetAll() {
        Iterator<javax.swing.JTextField> iterJTxtFld = collectionOfJTextFields.iterator();
        Iterator<javax.swing.ButtonGroup> iterBtnBx = collectionOfButtonGroups.iterator();

        javax.swing.JTextField curTxtFld;
        javax.swing.ButtonGroup curChkBx;

        while (iterJTxtFld.hasNext()) {
            curTxtFld = iterJTxtFld.next();
            curTxtFld.setText("");
        }

        while (iterBtnBx.hasNext()) {
            curChkBx = iterBtnBx.next();
            curChkBx.clearSelection();
        }

        for (Question iter : this.questions) {
            //System.out.println("έξέταση ερώτησης "+ iter.getQuestionNumber() +" --> "+ iter.isAnswered());
            iter.setAnswered(false);
            iter.setValueSelected(-1);
        }

        this.jTextAreaStatus.setText("");

        this.jTextFieldTestCode.setText("Test Code");
        this.jComboBoxAge.setSelectedIndex(0);
        this.jComboBoxEdu.setSelectedIndex(0);
        this.jComboBoxSex.setSelectedIndex(0);

        this.jTextFieldTestCode.setVisible(false);
        this.jComboBoxAge.setVisible(false);
        this.jComboBoxEdu.setVisible(false);
        this.jComboBoxSex.setVisible(false);
        this.jButtonCancel.setVisible(false);
        this.jButtonSubmit.setVisible(false);
    }
    //Dimitris hardcode end =============================================================================

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = JCESDApp.getApplication().getMainFrame();
            aboutBox = new JCESDAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        //JCESDApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jLabel0 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jCheckBox0_0 = new javax.swing.JCheckBox();
        jCheckBox0_1 = new javax.swing.JCheckBox();
        jCheckBox0_2 = new javax.swing.JCheckBox();
        jCheckBox0_3 = new javax.swing.JCheckBox();
        jCheckBox1_0 = new javax.swing.JCheckBox();
        jCheckBox1_1 = new javax.swing.JCheckBox();
        jCheckBox1_2 = new javax.swing.JCheckBox();
        jCheckBox1_3 = new javax.swing.JCheckBox();
        jCheckBox2_0 = new javax.swing.JCheckBox();
        jCheckBox2_1 = new javax.swing.JCheckBox();
        jCheckBox2_2 = new javax.swing.JCheckBox();
        jCheckBox2_3 = new javax.swing.JCheckBox();
        jCheckBox3_0 = new javax.swing.JCheckBox();
        jCheckBox3_1 = new javax.swing.JCheckBox();
        jCheckBox3_2 = new javax.swing.JCheckBox();
        jCheckBox3_3 = new javax.swing.JCheckBox();
        jCheckBox4_0 = new javax.swing.JCheckBox();
        jCheckBox4_1 = new javax.swing.JCheckBox();
        jCheckBox4_2 = new javax.swing.JCheckBox();
        jCheckBox4_3 = new javax.swing.JCheckBox();
        jCheckBox5_0 = new javax.swing.JCheckBox();
        jCheckBox5_1 = new javax.swing.JCheckBox();
        jCheckBox5_2 = new javax.swing.JCheckBox();
        jCheckBox5_3 = new javax.swing.JCheckBox();
        jCheckBox6_0 = new javax.swing.JCheckBox();
        jCheckBox6_1 = new javax.swing.JCheckBox();
        jCheckBox6_2 = new javax.swing.JCheckBox();
        jCheckBox6_3 = new javax.swing.JCheckBox();
        jCheckBox7_0 = new javax.swing.JCheckBox();
        jCheckBox7_1 = new javax.swing.JCheckBox();
        jCheckBox7_2 = new javax.swing.JCheckBox();
        jCheckBox7_3 = new javax.swing.JCheckBox();
        jCheckBox8_0 = new javax.swing.JCheckBox();
        jCheckBox8_1 = new javax.swing.JCheckBox();
        jCheckBox8_2 = new javax.swing.JCheckBox();
        jCheckBox8_3 = new javax.swing.JCheckBox();
        jCheckBox9_0 = new javax.swing.JCheckBox();
        jCheckBox9_1 = new javax.swing.JCheckBox();
        jCheckBox9_2 = new javax.swing.JCheckBox();
        jCheckBox9_3 = new javax.swing.JCheckBox();
        jCheckBox10_0 = new javax.swing.JCheckBox();
        jCheckBox10_1 = new javax.swing.JCheckBox();
        jCheckBox10_2 = new javax.swing.JCheckBox();
        jCheckBox10_3 = new javax.swing.JCheckBox();
        jCheckBox11_0 = new javax.swing.JCheckBox();
        jCheckBox11_1 = new javax.swing.JCheckBox();
        jCheckBox11_2 = new javax.swing.JCheckBox();
        jCheckBox11_3 = new javax.swing.JCheckBox();
        jCheckBox12_0 = new javax.swing.JCheckBox();
        jCheckBox12_1 = new javax.swing.JCheckBox();
        jCheckBox12_2 = new javax.swing.JCheckBox();
        jCheckBox12_3 = new javax.swing.JCheckBox();
        jCheckBox13_0 = new javax.swing.JCheckBox();
        jCheckBox13_1 = new javax.swing.JCheckBox();
        jCheckBox13_2 = new javax.swing.JCheckBox();
        jCheckBox13_3 = new javax.swing.JCheckBox();
        jCheckBox14_0 = new javax.swing.JCheckBox();
        jCheckBox14_1 = new javax.swing.JCheckBox();
        jCheckBox14_2 = new javax.swing.JCheckBox();
        jCheckBox14_3 = new javax.swing.JCheckBox();
        jCheckBox15_0 = new javax.swing.JCheckBox();
        jCheckBox15_1 = new javax.swing.JCheckBox();
        jCheckBox15_2 = new javax.swing.JCheckBox();
        jCheckBox15_3 = new javax.swing.JCheckBox();
        jCheckBox16_0 = new javax.swing.JCheckBox();
        jCheckBox16_1 = new javax.swing.JCheckBox();
        jCheckBox16_2 = new javax.swing.JCheckBox();
        jCheckBox16_3 = new javax.swing.JCheckBox();
        jCheckBox17_0 = new javax.swing.JCheckBox();
        jCheckBox17_1 = new javax.swing.JCheckBox();
        jCheckBox17_2 = new javax.swing.JCheckBox();
        jCheckBox17_3 = new javax.swing.JCheckBox();
        jCheckBox18_0 = new javax.swing.JCheckBox();
        jCheckBox18_1 = new javax.swing.JCheckBox();
        jCheckBox18_2 = new javax.swing.JCheckBox();
        jCheckBox18_3 = new javax.swing.JCheckBox();
        jCheckBox19_0 = new javax.swing.JCheckBox();
        jCheckBox19_1 = new javax.swing.JCheckBox();
        jCheckBox19_2 = new javax.swing.JCheckBox();
        jCheckBox19_3 = new javax.swing.JCheckBox();
        jTextField0 = new javax.swing.JTextField();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jTextField13 = new javax.swing.JTextField();
        jTextField14 = new javax.swing.JTextField();
        jTextField15 = new javax.swing.JTextField();
        jTextField16 = new javax.swing.JTextField();
        jTextField17 = new javax.swing.JTextField();
        jTextField18 = new javax.swing.JTextField();
        jTextField19 = new javax.swing.JTextField();
        jLabelA = new javax.swing.JLabel();
        jLabelB = new javax.swing.JLabel();
        jLabelC = new javax.swing.JLabel();
        jLabelD = new javax.swing.JLabel();
        jTextFieldTestCode = new javax.swing.JTextField();
        jComboBoxAge = new javax.swing.JComboBox();
        jComboBoxSex = new javax.swing.JComboBox();
        jComboBoxEdu = new javax.swing.JComboBox();
        jButtonSubmit = new javax.swing.JButton();
        jButtonCancel = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        CalcMenuItem = new javax.swing.JMenuItem();
        ResetMenuItem = new javax.swing.JMenuItem();
        RandomMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaStatus = new javax.swing.JTextArea();
        buttonGroup0 = new javax.swing.ButtonGroup();
        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        buttonGroup3 = new javax.swing.ButtonGroup();
        buttonGroup4 = new javax.swing.ButtonGroup();
        buttonGroup5 = new javax.swing.ButtonGroup();
        buttonGroup6 = new javax.swing.ButtonGroup();
        buttonGroup7 = new javax.swing.ButtonGroup();
        buttonGroup8 = new javax.swing.ButtonGroup();
        buttonGroup9 = new javax.swing.ButtonGroup();
        buttonGroup10 = new javax.swing.ButtonGroup();
        buttonGroup11 = new javax.swing.ButtonGroup();
        buttonGroup12 = new javax.swing.ButtonGroup();
        buttonGroup13 = new javax.swing.ButtonGroup();
        buttonGroup14 = new javax.swing.ButtonGroup();
        buttonGroup15 = new javax.swing.ButtonGroup();
        buttonGroup16 = new javax.swing.ButtonGroup();
        buttonGroup17 = new javax.swing.ButtonGroup();
        buttonGroup18 = new javax.swing.ButtonGroup();
        buttonGroup19 = new javax.swing.ButtonGroup();
        jDialog1 = new javax.swing.JDialog();

        mainPanel.setName("mainPanel"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.dimipet.jcesd.JCESDApp.class).getContext().getResourceMap(JCESDView.class);
        jLabel0.setText(resourceMap.getString("jLabel0.text")); // NOI18N
        jLabel0.setName("jLabel0"); // NOI18N

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        buttonGroup0.add(jCheckBox0_0);
        jCheckBox0_0.setToolTipText(resourceMap.getString("jCheckBox0_0.toolTipText")); // NOI18N
        jCheckBox0_0.setName("jCheckBox0_0"); // NOI18N

        buttonGroup0.add(jCheckBox0_1);
        jCheckBox0_1.setToolTipText(resourceMap.getString("jCheckBox0_1.toolTipText")); // NOI18N
        jCheckBox0_1.setName("jCheckBox0_1"); // NOI18N

        buttonGroup0.add(jCheckBox0_2);
        jCheckBox0_2.setToolTipText(resourceMap.getString("jCheckBox0_2.toolTipText")); // NOI18N
        jCheckBox0_2.setName("jCheckBox0_2"); // NOI18N

        buttonGroup0.add(jCheckBox0_3);
        jCheckBox0_3.setToolTipText(resourceMap.getString("jCheckBox0_3.toolTipText")); // NOI18N
        jCheckBox0_3.setName("jCheckBox0_3"); // NOI18N

        buttonGroup1.add(jCheckBox1_0);
        jCheckBox1_0.setToolTipText(resourceMap.getString("jCheckBox1_0.toolTipText")); // NOI18N
        jCheckBox1_0.setName("jCheckBox1_0"); // NOI18N

        buttonGroup1.add(jCheckBox1_1);
        jCheckBox1_1.setToolTipText(resourceMap.getString("jCheckBox1_1.toolTipText")); // NOI18N
        jCheckBox1_1.setName("jCheckBox1_1"); // NOI18N

        buttonGroup1.add(jCheckBox1_2);
        jCheckBox1_2.setToolTipText(resourceMap.getString("jCheckBox1_2.toolTipText")); // NOI18N
        jCheckBox1_2.setName("jCheckBox1_2"); // NOI18N

        buttonGroup1.add(jCheckBox1_3);
        jCheckBox1_3.setToolTipText(resourceMap.getString("jCheckBox1_3.toolTipText")); // NOI18N
        jCheckBox1_3.setName("jCheckBox1_3"); // NOI18N

        buttonGroup2.add(jCheckBox2_0);
        jCheckBox2_0.setToolTipText(resourceMap.getString("jCheckBox2_0.toolTipText")); // NOI18N
        jCheckBox2_0.setName("jCheckBox2_0"); // NOI18N

        buttonGroup2.add(jCheckBox2_1);
        jCheckBox2_1.setToolTipText(resourceMap.getString("jCheckBox2_1.toolTipText")); // NOI18N
        jCheckBox2_1.setName("jCheckBox2_1"); // NOI18N

        buttonGroup2.add(jCheckBox2_2);
        jCheckBox2_2.setToolTipText(resourceMap.getString("jCheckBox2_2.toolTipText")); // NOI18N
        jCheckBox2_2.setName("jCheckBox2_2"); // NOI18N

        buttonGroup2.add(jCheckBox2_3);
        jCheckBox2_3.setToolTipText(resourceMap.getString("jCheckBox2_3.toolTipText")); // NOI18N
        jCheckBox2_3.setName("jCheckBox2_3"); // NOI18N

        buttonGroup3.add(jCheckBox3_0);
        jCheckBox3_0.setToolTipText(resourceMap.getString("jCheckBox3_0.toolTipText")); // NOI18N
        jCheckBox3_0.setName("jCheckBox3_0"); // NOI18N

        buttonGroup3.add(jCheckBox3_1);
        jCheckBox3_1.setToolTipText(resourceMap.getString("jCheckBox3_1.toolTipText")); // NOI18N
        jCheckBox3_1.setName("jCheckBox3_1"); // NOI18N

        buttonGroup3.add(jCheckBox3_2);
        jCheckBox3_2.setToolTipText(resourceMap.getString("jCheckBox3_2.toolTipText")); // NOI18N
        jCheckBox3_2.setName("jCheckBox3_2"); // NOI18N

        buttonGroup3.add(jCheckBox3_3);
        jCheckBox3_3.setToolTipText(resourceMap.getString("jCheckBox3_3.toolTipText")); // NOI18N
        jCheckBox3_3.setName("jCheckBox3_3"); // NOI18N

        buttonGroup4.add(jCheckBox4_0);
        jCheckBox4_0.setToolTipText(resourceMap.getString("jCheckBox4_0.toolTipText")); // NOI18N
        jCheckBox4_0.setName("jCheckBox4_0"); // NOI18N

        buttonGroup4.add(jCheckBox4_1);
        jCheckBox4_1.setToolTipText(resourceMap.getString("jCheckBox4_1.toolTipText")); // NOI18N
        jCheckBox4_1.setName("jCheckBox4_1"); // NOI18N

        buttonGroup4.add(jCheckBox4_2);
        jCheckBox4_2.setToolTipText(resourceMap.getString("jCheckBox4_2.toolTipText")); // NOI18N
        jCheckBox4_2.setName("jCheckBox4_2"); // NOI18N

        buttonGroup4.add(jCheckBox4_3);
        jCheckBox4_3.setToolTipText(resourceMap.getString("jCheckBox4_3.toolTipText")); // NOI18N
        jCheckBox4_3.setName("jCheckBox4_3"); // NOI18N

        buttonGroup5.add(jCheckBox5_0);
        jCheckBox5_0.setToolTipText(resourceMap.getString("jCheckBox5_0.toolTipText")); // NOI18N
        jCheckBox5_0.setName("jCheckBox5_0"); // NOI18N

        buttonGroup5.add(jCheckBox5_1);
        jCheckBox5_1.setToolTipText(resourceMap.getString("jCheckBox5_1.toolTipText")); // NOI18N
        jCheckBox5_1.setName("jCheckBox5_1"); // NOI18N

        buttonGroup5.add(jCheckBox5_2);
        jCheckBox5_2.setToolTipText(resourceMap.getString("jCheckBox5_2.toolTipText")); // NOI18N
        jCheckBox5_2.setName("jCheckBox5_2"); // NOI18N

        buttonGroup5.add(jCheckBox5_3);
        jCheckBox5_3.setToolTipText(resourceMap.getString("jCheckBox5_3.toolTipText")); // NOI18N
        jCheckBox5_3.setName("jCheckBox5_3"); // NOI18N

        buttonGroup6.add(jCheckBox6_0);
        jCheckBox6_0.setToolTipText(resourceMap.getString("jCheckBox6_0.toolTipText")); // NOI18N
        jCheckBox6_0.setName("jCheckBox6_0"); // NOI18N

        buttonGroup6.add(jCheckBox6_1);
        jCheckBox6_1.setToolTipText(resourceMap.getString("jCheckBox6_1.toolTipText")); // NOI18N
        jCheckBox6_1.setName("jCheckBox6_1"); // NOI18N

        buttonGroup6.add(jCheckBox6_2);
        jCheckBox6_2.setToolTipText(resourceMap.getString("jCheckBox6_2.toolTipText")); // NOI18N
        jCheckBox6_2.setName("jCheckBox6_2"); // NOI18N

        buttonGroup6.add(jCheckBox6_3);
        jCheckBox6_3.setToolTipText(resourceMap.getString("jCheckBox6_3.toolTipText")); // NOI18N
        jCheckBox6_3.setName("jCheckBox6_3"); // NOI18N

        buttonGroup7.add(jCheckBox7_0);
        jCheckBox7_0.setToolTipText(resourceMap.getString("jCheckBox7_0.toolTipText")); // NOI18N
        jCheckBox7_0.setName("jCheckBox7_0"); // NOI18N

        buttonGroup7.add(jCheckBox7_1);
        jCheckBox7_1.setToolTipText(resourceMap.getString("jCheckBox7_1.toolTipText")); // NOI18N
        jCheckBox7_1.setName("jCheckBox7_1"); // NOI18N

        buttonGroup7.add(jCheckBox7_2);
        jCheckBox7_2.setToolTipText(resourceMap.getString("jCheckBox7_2.toolTipText")); // NOI18N
        jCheckBox7_2.setName("jCheckBox7_2"); // NOI18N

        buttonGroup7.add(jCheckBox7_3);
        jCheckBox7_3.setToolTipText(resourceMap.getString("jCheckBox7_3.toolTipText")); // NOI18N
        jCheckBox7_3.setName("jCheckBox7_3"); // NOI18N

        buttonGroup8.add(jCheckBox8_0);
        jCheckBox8_0.setToolTipText(resourceMap.getString("jCheckBox8_0.toolTipText")); // NOI18N
        jCheckBox8_0.setName("jCheckBox8_0"); // NOI18N

        buttonGroup8.add(jCheckBox8_1);
        jCheckBox8_1.setToolTipText(resourceMap.getString("jCheckBox8_1.toolTipText")); // NOI18N
        jCheckBox8_1.setName("jCheckBox8_1"); // NOI18N

        buttonGroup8.add(jCheckBox8_2);
        jCheckBox8_2.setToolTipText(resourceMap.getString("jCheckBox8_2.toolTipText")); // NOI18N
        jCheckBox8_2.setName("jCheckBox8_2"); // NOI18N

        buttonGroup8.add(jCheckBox8_3);
        jCheckBox8_3.setToolTipText(resourceMap.getString("jCheckBox8_3.toolTipText")); // NOI18N
        jCheckBox8_3.setName("jCheckBox8_3"); // NOI18N

        buttonGroup9.add(jCheckBox9_0);
        jCheckBox9_0.setToolTipText(resourceMap.getString("jCheckBox9_0.toolTipText")); // NOI18N
        jCheckBox9_0.setName("jCheckBox9_0"); // NOI18N

        buttonGroup9.add(jCheckBox9_1);
        jCheckBox9_1.setToolTipText(resourceMap.getString("jCheckBox9_1.toolTipText")); // NOI18N
        jCheckBox9_1.setName("jCheckBox9_1"); // NOI18N

        buttonGroup9.add(jCheckBox9_2);
        jCheckBox9_2.setToolTipText(resourceMap.getString("jCheckBox9_2.toolTipText")); // NOI18N
        jCheckBox9_2.setName("jCheckBox9_2"); // NOI18N

        buttonGroup9.add(jCheckBox9_3);
        jCheckBox9_3.setToolTipText(resourceMap.getString("jCheckBox9_3.toolTipText")); // NOI18N
        jCheckBox9_3.setName("jCheckBox9_3"); // NOI18N

        buttonGroup10.add(jCheckBox10_0);
        jCheckBox10_0.setToolTipText(resourceMap.getString("jCheckBox10_0.toolTipText")); // NOI18N
        jCheckBox10_0.setName("jCheckBox10_0"); // NOI18N

        buttonGroup10.add(jCheckBox10_1);
        jCheckBox10_1.setToolTipText(resourceMap.getString("jCheckBox10_1.toolTipText")); // NOI18N
        jCheckBox10_1.setName("jCheckBox10_1"); // NOI18N

        buttonGroup10.add(jCheckBox10_2);
        jCheckBox10_2.setToolTipText(resourceMap.getString("jCheckBox10_2.toolTipText")); // NOI18N
        jCheckBox10_2.setName("jCheckBox10_2"); // NOI18N

        buttonGroup10.add(jCheckBox10_3);
        jCheckBox10_3.setToolTipText(resourceMap.getString("jCheckBox10_3.toolTipText")); // NOI18N
        jCheckBox10_3.setName("jCheckBox10_3"); // NOI18N

        buttonGroup11.add(jCheckBox11_0);
        jCheckBox11_0.setToolTipText(resourceMap.getString("jCheckBox11_0.toolTipText")); // NOI18N
        jCheckBox11_0.setName("jCheckBox11_0"); // NOI18N

        buttonGroup11.add(jCheckBox11_1);
        jCheckBox11_1.setToolTipText(resourceMap.getString("jCheckBox11_1.toolTipText")); // NOI18N
        jCheckBox11_1.setName("jCheckBox11_1"); // NOI18N

        buttonGroup11.add(jCheckBox11_2);
        jCheckBox11_2.setToolTipText(resourceMap.getString("jCheckBox11_2.toolTipText")); // NOI18N
        jCheckBox11_2.setName("jCheckBox11_2"); // NOI18N

        buttonGroup11.add(jCheckBox11_3);
        jCheckBox11_3.setToolTipText(resourceMap.getString("jCheckBox11_3.toolTipText")); // NOI18N
        jCheckBox11_3.setName("jCheckBox11_3"); // NOI18N

        buttonGroup12.add(jCheckBox12_0);
        jCheckBox12_0.setToolTipText(resourceMap.getString("jCheckBox12_0.toolTipText")); // NOI18N
        jCheckBox12_0.setName("jCheckBox12_0"); // NOI18N

        buttonGroup12.add(jCheckBox12_1);
        jCheckBox12_1.setToolTipText(resourceMap.getString("jCheckBox12_1.toolTipText")); // NOI18N
        jCheckBox12_1.setName("jCheckBox12_1"); // NOI18N

        buttonGroup12.add(jCheckBox12_2);
        jCheckBox12_2.setToolTipText(resourceMap.getString("jCheckBox12_2.toolTipText")); // NOI18N
        jCheckBox12_2.setName("jCheckBox12_2"); // NOI18N

        buttonGroup12.add(jCheckBox12_3);
        jCheckBox12_3.setToolTipText(resourceMap.getString("jCheckBox12_3.toolTipText")); // NOI18N
        jCheckBox12_3.setName("jCheckBox12_3"); // NOI18N

        buttonGroup13.add(jCheckBox13_0);
        jCheckBox13_0.setToolTipText(resourceMap.getString("jCheckBox13_0.toolTipText")); // NOI18N
        jCheckBox13_0.setName("jCheckBox13_0"); // NOI18N

        buttonGroup13.add(jCheckBox13_1);
        jCheckBox13_1.setToolTipText(resourceMap.getString("jCheckBox13_1.toolTipText")); // NOI18N
        jCheckBox13_1.setName("jCheckBox13_1"); // NOI18N

        buttonGroup13.add(jCheckBox13_2);
        jCheckBox13_2.setToolTipText(resourceMap.getString("jCheckBox13_2.toolTipText")); // NOI18N
        jCheckBox13_2.setName("jCheckBox13_2"); // NOI18N

        buttonGroup13.add(jCheckBox13_3);
        jCheckBox13_3.setToolTipText(resourceMap.getString("jCheckBox13_3.toolTipText")); // NOI18N
        jCheckBox13_3.setName("jCheckBox13_3"); // NOI18N

        buttonGroup14.add(jCheckBox14_0);
        jCheckBox14_0.setToolTipText(resourceMap.getString("jCheckBox14_0.toolTipText")); // NOI18N
        jCheckBox14_0.setName("jCheckBox14_0"); // NOI18N

        buttonGroup14.add(jCheckBox14_1);
        jCheckBox14_1.setToolTipText(resourceMap.getString("jCheckBox14_1.toolTipText")); // NOI18N
        jCheckBox14_1.setName("jCheckBox14_1"); // NOI18N

        buttonGroup14.add(jCheckBox14_2);
        jCheckBox14_2.setToolTipText(resourceMap.getString("jCheckBox14_2.toolTipText")); // NOI18N
        jCheckBox14_2.setName("jCheckBox14_2"); // NOI18N

        buttonGroup14.add(jCheckBox14_3);
        jCheckBox14_3.setToolTipText(resourceMap.getString("jCheckBox14_3.toolTipText")); // NOI18N
        jCheckBox14_3.setName("jCheckBox14_3"); // NOI18N

        buttonGroup15.add(jCheckBox15_0);
        jCheckBox15_0.setToolTipText(resourceMap.getString("jCheckBox15_0.toolTipText")); // NOI18N
        jCheckBox15_0.setName("jCheckBox15_0"); // NOI18N

        buttonGroup15.add(jCheckBox15_1);
        jCheckBox15_1.setToolTipText(resourceMap.getString("jCheckBox15_1.toolTipText")); // NOI18N
        jCheckBox15_1.setName("jCheckBox15_1"); // NOI18N

        buttonGroup15.add(jCheckBox15_2);
        jCheckBox15_2.setToolTipText(resourceMap.getString("jCheckBox15_2.toolTipText")); // NOI18N
        jCheckBox15_2.setName("jCheckBox15_2"); // NOI18N

        buttonGroup15.add(jCheckBox15_3);
        jCheckBox15_3.setToolTipText(resourceMap.getString("jCheckBox15_3.toolTipText")); // NOI18N
        jCheckBox15_3.setName("jCheckBox15_3"); // NOI18N

        buttonGroup16.add(jCheckBox16_0);
        jCheckBox16_0.setToolTipText(resourceMap.getString("jCheckBox16_0.toolTipText")); // NOI18N
        jCheckBox16_0.setName("jCheckBox16_0"); // NOI18N

        buttonGroup16.add(jCheckBox16_1);
        jCheckBox16_1.setToolTipText(resourceMap.getString("jCheckBox16_1.toolTipText")); // NOI18N
        jCheckBox16_1.setName("jCheckBox16_1"); // NOI18N

        buttonGroup16.add(jCheckBox16_2);
        jCheckBox16_2.setToolTipText(resourceMap.getString("jCheckBox16_2.toolTipText")); // NOI18N
        jCheckBox16_2.setName("jCheckBox16_2"); // NOI18N

        buttonGroup16.add(jCheckBox16_3);
        jCheckBox16_3.setToolTipText(resourceMap.getString("jCheckBox16_3.toolTipText")); // NOI18N
        jCheckBox16_3.setName("jCheckBox16_3"); // NOI18N

        buttonGroup17.add(jCheckBox17_0);
        jCheckBox17_0.setToolTipText(resourceMap.getString("jCheckBox17_0.toolTipText")); // NOI18N
        jCheckBox17_0.setName("jCheckBox17_0"); // NOI18N

        buttonGroup17.add(jCheckBox17_1);
        jCheckBox17_1.setToolTipText(resourceMap.getString("jCheckBox17_1.toolTipText")); // NOI18N
        jCheckBox17_1.setName("jCheckBox17_1"); // NOI18N

        buttonGroup17.add(jCheckBox17_2);
        jCheckBox17_2.setToolTipText(resourceMap.getString("jCheckBox17_2.toolTipText")); // NOI18N
        jCheckBox17_2.setName("jCheckBox17_2"); // NOI18N

        buttonGroup17.add(jCheckBox17_3);
        jCheckBox17_3.setToolTipText(resourceMap.getString("jCheckBox17_3.toolTipText")); // NOI18N
        jCheckBox17_3.setName("jCheckBox17_3"); // NOI18N

        buttonGroup18.add(jCheckBox18_0);
        jCheckBox18_0.setToolTipText(resourceMap.getString("jCheckBox18_0.toolTipText")); // NOI18N
        jCheckBox18_0.setName("jCheckBox18_0"); // NOI18N

        buttonGroup18.add(jCheckBox18_1);
        jCheckBox18_1.setToolTipText(resourceMap.getString("jCheckBox18_1.toolTipText")); // NOI18N
        jCheckBox18_1.setName("jCheckBox18_1"); // NOI18N

        buttonGroup18.add(jCheckBox18_2);
        jCheckBox18_2.setToolTipText(resourceMap.getString("jCheckBox18_2.toolTipText")); // NOI18N
        jCheckBox18_2.setName("jCheckBox18_2"); // NOI18N

        buttonGroup18.add(jCheckBox18_3);
        jCheckBox18_3.setToolTipText(resourceMap.getString("jCheckBox18_3.toolTipText")); // NOI18N
        jCheckBox18_3.setName("jCheckBox18_3"); // NOI18N

        buttonGroup19.add(jCheckBox19_0);
        jCheckBox19_0.setToolTipText(resourceMap.getString("jCheckBox19_0.toolTipText")); // NOI18N
        jCheckBox19_0.setName("jCheckBox19_0"); // NOI18N

        buttonGroup19.add(jCheckBox19_1);
        jCheckBox19_1.setToolTipText(resourceMap.getString("jCheckBox19_1.toolTipText")); // NOI18N
        jCheckBox19_1.setName("jCheckBox19_1"); // NOI18N

        buttonGroup19.add(jCheckBox19_2);
        jCheckBox19_2.setToolTipText(resourceMap.getString("jCheckBox19_2.toolTipText")); // NOI18N
        jCheckBox19_2.setName("jCheckBox19_2"); // NOI18N

        buttonGroup19.add(jCheckBox19_3);
        jCheckBox19_3.setToolTipText(resourceMap.getString("jCheckBox19_3.toolTipText")); // NOI18N
        jCheckBox19_3.setName("jCheckBox19_3"); // NOI18N

        jTextField0.setColumns(3);
        jTextField0.setEditable(false);
        jTextField0.setText(resourceMap.getString("jTextField0.text")); // NOI18N
        jTextField0.setBorder(null);
        jTextField0.setName("jTextField0"); // NOI18N

        jTextField1.setColumns(3);
        jTextField1.setEditable(false);
        jTextField1.setText(resourceMap.getString("jTextField1.text")); // NOI18N
        jTextField1.setBorder(null);
        jTextField1.setName("jTextField1"); // NOI18N

        jTextField2.setColumns(3);
        jTextField2.setEditable(false);
        jTextField2.setText(resourceMap.getString("jTextField2.text")); // NOI18N
        jTextField2.setBorder(null);
        jTextField2.setName("jTextField2"); // NOI18N

        jTextField3.setColumns(3);
        jTextField3.setEditable(false);
        jTextField3.setText(resourceMap.getString("jTextField3.text")); // NOI18N
        jTextField3.setBorder(null);
        jTextField3.setName("jTextField3"); // NOI18N

        jTextField4.setColumns(3);
        jTextField4.setEditable(false);
        jTextField4.setText(resourceMap.getString("jTextField4.text")); // NOI18N
        jTextField4.setBorder(null);
        jTextField4.setName("jTextField4"); // NOI18N

        jTextField5.setColumns(3);
        jTextField5.setEditable(false);
        jTextField5.setText(resourceMap.getString("jTextField5.text")); // NOI18N
        jTextField5.setBorder(null);
        jTextField5.setName("jTextField5"); // NOI18N

        jTextField6.setColumns(3);
        jTextField6.setEditable(false);
        jTextField6.setText(resourceMap.getString("jTextField6.text")); // NOI18N
        jTextField6.setBorder(null);
        jTextField6.setName("jTextField6"); // NOI18N

        jTextField7.setColumns(3);
        jTextField7.setEditable(false);
        jTextField7.setText(resourceMap.getString("jTextField7.text")); // NOI18N
        jTextField7.setBorder(null);
        jTextField7.setName("jTextField7"); // NOI18N

        jTextField8.setColumns(3);
        jTextField8.setEditable(false);
        jTextField8.setText(resourceMap.getString("jTextField8.text")); // NOI18N
        jTextField8.setBorder(null);
        jTextField8.setName("jTextField8"); // NOI18N

        jTextField9.setColumns(3);
        jTextField9.setEditable(false);
        jTextField9.setText(resourceMap.getString("jTextField9.text")); // NOI18N
        jTextField9.setBorder(null);
        jTextField9.setName("jTextField9"); // NOI18N

        jTextField10.setColumns(3);
        jTextField10.setEditable(false);
        jTextField10.setText(resourceMap.getString("jTextField10.text")); // NOI18N
        jTextField10.setBorder(null);
        jTextField10.setName("jTextField10"); // NOI18N

        jTextField11.setColumns(3);
        jTextField11.setEditable(false);
        jTextField11.setText(resourceMap.getString("jTextField11.text")); // NOI18N
        jTextField11.setBorder(null);
        jTextField11.setName("jTextField11"); // NOI18N

        jTextField12.setColumns(3);
        jTextField12.setEditable(false);
        jTextField12.setText(resourceMap.getString("jTextField12.text")); // NOI18N
        jTextField12.setBorder(null);
        jTextField12.setName("jTextField12"); // NOI18N

        jTextField13.setColumns(3);
        jTextField13.setEditable(false);
        jTextField13.setText(resourceMap.getString("jTextField13.text")); // NOI18N
        jTextField13.setBorder(null);
        jTextField13.setName("jTextField13"); // NOI18N

        jTextField14.setColumns(3);
        jTextField14.setEditable(false);
        jTextField14.setText(resourceMap.getString("jTextField14.text")); // NOI18N
        jTextField14.setBorder(null);
        jTextField14.setName("jTextField14"); // NOI18N

        jTextField15.setColumns(3);
        jTextField15.setEditable(false);
        jTextField15.setText(resourceMap.getString("jTextField15.text")); // NOI18N
        jTextField15.setBorder(null);
        jTextField15.setName("jTextField15"); // NOI18N

        jTextField16.setColumns(3);
        jTextField16.setEditable(false);
        jTextField16.setText(resourceMap.getString("jTextField16.text")); // NOI18N
        jTextField16.setBorder(null);
        jTextField16.setName("jTextField16"); // NOI18N

        jTextField17.setColumns(3);
        jTextField17.setEditable(false);
        jTextField17.setText(resourceMap.getString("jTextField17.text")); // NOI18N
        jTextField17.setBorder(null);
        jTextField17.setName("jTextField17"); // NOI18N

        jTextField18.setColumns(3);
        jTextField18.setEditable(false);
        jTextField18.setText(resourceMap.getString("jTextField18.text")); // NOI18N
        jTextField18.setBorder(null);
        jTextField18.setName("jTextField18"); // NOI18N

        jTextField19.setColumns(3);
        jTextField19.setEditable(false);
        jTextField19.setText(resourceMap.getString("jTextField19.text")); // NOI18N
        jTextField19.setBorder(null);
        jTextField19.setName("jTextField19"); // NOI18N

        jLabelA.setIcon(resourceMap.getIcon("jLabelA.icon")); // NOI18N
        jLabelA.setText(resourceMap.getString("jLabelA.text")); // NOI18N
        jLabelA.setName("jLabelA"); // NOI18N

        jLabelB.setIcon(resourceMap.getIcon("jLabelB.icon")); // NOI18N
        jLabelB.setText(resourceMap.getString("jLabelB.text")); // NOI18N
        jLabelB.setName("jLabelB"); // NOI18N

        jLabelC.setIcon(resourceMap.getIcon("jLabelC.icon")); // NOI18N
        jLabelC.setText(resourceMap.getString("jLabelC.text")); // NOI18N
        jLabelC.setName("jLabelC"); // NOI18N

        jLabelD.setIcon(resourceMap.getIcon("jLabelD.icon")); // NOI18N
        jLabelD.setText(resourceMap.getString("jLabelD.text")); // NOI18N
        jLabelD.setName("jLabelD"); // NOI18N

        jTextFieldTestCode.setText(resourceMap.getString("jTextFieldTestCode.text")); // NOI18N
        jTextFieldTestCode.setName("jTextFieldTestCode"); // NOI18N

        jComboBoxAge.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ηλικία", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40", "41", "42", "43", "44", "45", "46", "47", "48", "49", "50", "51", "52", "53", "54", "55", "56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68", "69", "70", "71", "72", "73", "74", "75", "76", "77", "78", "79", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89", "90", "91", "92", "93", "94", "95", "96", "97", "98" }));
        jComboBoxAge.setName("jComboBoxAge"); // NOI18N

        jComboBoxSex.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "φύλο", "αρσενικό", "θυληκό" }));
        jComboBoxSex.setName("jComboBoxSex"); // NOI18N

        jComboBoxEdu.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "εκπαίδευση", "ΑΕΙ", "ΤΕΙ", "Μεταπτυχιακό", "Διδακτορικό", "Λύκειο", "Γυμνάσιο", "Δημοτικό", "---" }));
        jComboBoxEdu.setName("jComboBoxEdu"); // NOI18N

        jButtonSubmit.setText(resourceMap.getString("jButtonSubmit.text")); // NOI18N
        jButtonSubmit.setName("jButtonSubmit"); // NOI18N
        jButtonSubmit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSubmitActionPerformed(evt);
            }
        });

        jButtonCancel.setText(resourceMap.getString("jButtonCancel.text")); // NOI18N
        jButtonCancel.setName("jButtonCancel"); // NOI18N
        jButtonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 326, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 323, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 322, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel0, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(jCheckBox7_0)
                                    .addComponent(jCheckBox19_0)
                                    .addComponent(jCheckBox1_0)
                                    .addComponent(jCheckBox8_0)
                                    .addComponent(jCheckBox2_0)
                                    .addComponent(jCheckBox9_0)
                                    .addComponent(jCheckBox10_0)
                                    .addComponent(jCheckBox11_0)
                                    .addComponent(jCheckBox3_0)
                                    .addComponent(jCheckBox15_0)
                                    .addComponent(jCheckBox16_0)
                                    .addComponent(jCheckBox14_0)
                                    .addComponent(jCheckBox13_0)
                                    .addComponent(jCheckBox6_0)
                                    .addComponent(jCheckBox0_0)
                                    .addComponent(jCheckBox5_0)
                                    .addComponent(jCheckBox12_0)
                                    .addComponent(jCheckBox17_0)
                                    .addComponent(jCheckBox18_0)
                                    .addComponent(jCheckBox4_0)))
                            .addGroup(mainPanelLayout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jButtonSubmit)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jButtonCancel))
                                    .addGroup(mainPanelLayout.createSequentialGroup()
                                        .addComponent(jTextFieldTestCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jComboBoxSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jComboBoxEdu, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 133, Short.MAX_VALUE)
                                .addComponent(jLabelA)))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jCheckBox7_1)
                            .addComponent(jCheckBox1_1)
                            .addComponent(jCheckBox8_1)
                            .addComponent(jCheckBox11_1)
                            .addComponent(jCheckBox15_1)
                            .addComponent(jCheckBox3_1)
                            .addComponent(jCheckBox5_1)
                            .addComponent(jCheckBox2_1)
                            .addComponent(jCheckBox14_1)
                            .addComponent(jCheckBox13_1)
                            .addComponent(jCheckBox10_1)
                            .addComponent(jCheckBox9_1)
                            .addComponent(jCheckBox0_1)
                            .addComponent(jCheckBox6_1)
                            .addComponent(jCheckBox19_1)
                            .addComponent(jCheckBox16_1)
                            .addComponent(jCheckBox18_1)
                            .addComponent(jCheckBox17_1)
                            .addComponent(jCheckBox4_1)
                            .addComponent(jCheckBox12_1))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jCheckBox6_2)
                            .addComponent(jCheckBox18_2)
                            .addComponent(jCheckBox19_2)
                            .addComponent(jCheckBox1_2)
                            .addComponent(jCheckBox8_2)
                            .addComponent(jCheckBox3_2)
                            .addComponent(jCheckBox15_2)
                            .addComponent(jCheckBox2_2)
                            .addComponent(jCheckBox14_2)
                            .addComponent(jCheckBox13_2)
                            .addComponent(jCheckBox9_2)
                            .addComponent(jCheckBox10_2)
                            .addComponent(jCheckBox11_2)
                            .addComponent(jCheckBox5_2)
                            .addComponent(jCheckBox4_2)
                            .addComponent(jCheckBox16_2)
                            .addComponent(jCheckBox7_2)
                            .addComponent(jCheckBox0_2)
                            .addComponent(jCheckBox17_2)
                            .addComponent(jCheckBox12_2))
                        .addGap(18, 18, 18)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jCheckBox6_3)
                            .addComponent(jCheckBox1_3)
                            .addComponent(jCheckBox9_3)
                            .addComponent(jCheckBox8_3)
                            .addComponent(jCheckBox11_3)
                            .addComponent(jCheckBox18_3)
                            .addComponent(jCheckBox2_3)
                            .addComponent(jCheckBox14_3)
                            .addComponent(jCheckBox10_3)
                            .addComponent(jCheckBox12_3)
                            .addComponent(jCheckBox13_3)
                            .addComponent(jCheckBox15_3)
                            .addComponent(jCheckBox16_3)
                            .addComponent(jCheckBox4_3)
                            .addComponent(jCheckBox19_3)
                            .addComponent(jCheckBox0_3)
                            .addComponent(jCheckBox17_3)
                            .addComponent(jCheckBox7_3)
                            .addComponent(jCheckBox3_3)
                            .addComponent(jCheckBox5_3)
                            .addComponent(jLabelD))
                        .addGap(4, 4, 4)
                        .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(407, 407, 407)
                        .addComponent(jLabelB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelC)))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabelC)
                    .addComponent(jLabelB)
                    .addComponent(jLabelD)
                    .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(mainPanelLayout.createSequentialGroup()
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jTextFieldTestCode, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBoxAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jComboBoxSex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addComponent(jComboBoxEdu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonSubmit)
                                .addComponent(jButtonCancel)))
                        .addComponent(jLabelA)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox0_2)
                    .addComponent(jCheckBox0_1)
                    .addComponent(jCheckBox0_3)
                    .addComponent(jCheckBox0_0)
                    .addComponent(jTextField0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1_1)
                    .addComponent(jCheckBox1_0)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox1_3)
                    .addComponent(jCheckBox1_2))
                .addGap(2, 2, 2)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox2_3)
                    .addComponent(jCheckBox2_1)
                    .addComponent(jCheckBox2_2)
                    .addComponent(jCheckBox2_0))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox3_0)
                    .addComponent(jCheckBox3_3)
                    .addComponent(jCheckBox3_1)
                    .addComponent(jCheckBox3_2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox4_3)
                    .addComponent(jCheckBox4_2)
                    .addComponent(jCheckBox4_1)
                    .addComponent(jCheckBox4_0)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox5_3)
                    .addComponent(jCheckBox5_2)
                    .addComponent(jCheckBox5_1)
                    .addComponent(jCheckBox5_0)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox6_1)
                    .addComponent(jCheckBox6_0)
                    .addComponent(jCheckBox6_3)
                    .addComponent(jCheckBox6_2)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox7_0)
                    .addComponent(jCheckBox7_2)
                    .addComponent(jCheckBox7_1)
                    .addComponent(jCheckBox7_3)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox8_3)
                    .addComponent(jCheckBox8_0)
                    .addComponent(jCheckBox8_2)
                    .addComponent(jCheckBox8_1)
                    .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jCheckBox9_1)
                    .addComponent(jCheckBox9_0)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox9_3)
                    .addComponent(jCheckBox9_2)
                    .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox10_1)
                    .addComponent(jCheckBox10_0)
                    .addComponent(jCheckBox10_3)
                    .addComponent(jCheckBox10_2)
                    .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox11_1)
                    .addComponent(jCheckBox11_0)
                    .addComponent(jCheckBox11_2)
                    .addComponent(jCheckBox11_3)
                    .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox12_0)
                    .addComponent(jCheckBox12_2)
                    .addComponent(jCheckBox12_1)
                    .addComponent(jCheckBox12_3)
                    .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox13_0)
                    .addComponent(jCheckBox13_2)
                    .addComponent(jCheckBox13_1)
                    .addComponent(jCheckBox13_3)
                    .addComponent(jTextField13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox14_2)
                    .addComponent(jCheckBox14_3)
                    .addComponent(jCheckBox14_0)
                    .addComponent(jCheckBox14_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox15_2)
                    .addComponent(jCheckBox15_3)
                    .addComponent(jCheckBox15_0)
                    .addComponent(jCheckBox15_1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox16_0)
                    .addComponent(jCheckBox16_1)
                    .addComponent(jCheckBox16_2)
                    .addComponent(jCheckBox16_3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox17_3)
                    .addComponent(jCheckBox17_0)
                    .addComponent(jCheckBox17_1)
                    .addComponent(jCheckBox17_2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox18_3)
                    .addComponent(jCheckBox18_0)
                    .addComponent(jCheckBox18_1)
                    .addComponent(jCheckBox18_2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jCheckBox19_1)
                    .addComponent(jCheckBox19_0)
                    .addComponent(jCheckBox19_2)
                    .addComponent(jCheckBox19_3))
                .addGap(20, 20, 20))
        );

        menuBar.setName("menuBar"); // NOI18N
        menuBar.setPreferredSize(new java.awt.Dimension(400, 21));

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        CalcMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        CalcMenuItem.setText(resourceMap.getString("CalcMenuItem.text")); // NOI18N
        CalcMenuItem.setName("CalcMenuItem"); // NOI18N
        CalcMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CalcMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(CalcMenuItem);

        ResetMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        ResetMenuItem.setText(resourceMap.getString("ResetMenuItem.text")); // NOI18N
        ResetMenuItem.setName("ResetMenuItem"); // NOI18N
        ResetMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ResetMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(ResetMenuItem);

        RandomMenuItem.setText(resourceMap.getString("RandomMenuItem.text")); // NOI18N
        RandomMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RandomMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(RandomMenuItem);

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.dimipet.jcesd.JCESDApp.class).getContext().getActionMap(JCESDView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        jScrollPane1.setBorder(null);
        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextAreaStatus.setBackground(resourceMap.getColor("jTextAreaStatus.background")); // NOI18N
        jTextAreaStatus.setColumns(10);
        jTextAreaStatus.setEditable(false);
        jTextAreaStatus.setFont(resourceMap.getFont("jTextAreaStatus.font")); // NOI18N
        jTextAreaStatus.setRows(3);
        jTextAreaStatus.setWrapStyleWord(true);
        jTextAreaStatus.setBorder(null);
        jTextAreaStatus.setName("jTextAreaStatus"); // NOI18N
        jScrollPane1.setViewportView(jTextAreaStatus);

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(statusMessageLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 614, Short.MAX_VALUE)
                        .addComponent(statusAnimationLabel))
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 569, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statusPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 4, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statusPanelLayout.createSequentialGroup()
                        .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(30, 30, 30)
                        .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(statusMessageLabel)
                            .addComponent(statusAnimationLabel)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jDialog1.setName("jDialog1"); // NOI18N

        javax.swing.GroupLayout jDialog1Layout = new javax.swing.GroupLayout(jDialog1.getContentPane());
        jDialog1.getContentPane().setLayout(jDialog1Layout);
        jDialog1Layout.setHorizontalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        jDialog1Layout.setVerticalGroup(
            jDialog1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * when this menu item (calculate) is clicked then it initiates the processes
     * to check users data input, assigns a state of answer on each question,
     * informs the user which questions havent been anwered
     *
     * @param evt
     */
    private void CalcMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CalcMenuItemActionPerformed
        int calcValue;

        calcValue = this.calcValue(this.buttonGroup0, this.jCheckBox0_0, this.jCheckBox0_1, this.jCheckBox0_2, this.jCheckBox0_3);
        if (calcValue > -1) {
            this.questions[0].setValueSelected(calcValue);
            this.questions[0].setAnswered(true);
            this.jTextField0.setForeground(Color.black);
            this.jTextField0.setText("");
            //this.jTextField0.setText(String.valueOf(this.questions[0].getValueSelected()));
        } else {
            this.jTextField0.setForeground(Color.red);
            this.jTextField0.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup1, this.jCheckBox1_0, this.jCheckBox1_1, this.jCheckBox1_2, this.jCheckBox1_3);
        if (calcValue > -1) {
            this.questions[1].setValueSelected(calcValue);
            this.questions[1].setAnswered(true);
            this.jTextField1.setForeground(Color.black);
            this.jTextField1.setText("");
            //this.jTextField1.setText(String.valueOf(this.questions[1].getValueSelected()));
        } else {
            this.jTextField1.setForeground(Color.red);
            this.jTextField1.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup2, this.jCheckBox2_0, this.jCheckBox2_1, this.jCheckBox2_2, this.jCheckBox2_3);
        if (calcValue > -1) {
            this.questions[2].setValueSelected(calcValue);
            this.questions[2].setAnswered(true);
            this.jTextField2.setForeground(Color.black);
            this.jTextField2.setText("");
            //this.jTextField2.setText(String.valueOf(this.questions[2].getValueSelected()));
        } else {
            this.jTextField2.setForeground(Color.red);
            this.jTextField2.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup3, this.jCheckBox3_3, this.jCheckBox3_2, this.jCheckBox3_1, this.jCheckBox3_0);
        if (calcValue > -1) {
            this.questions[3].setValueSelected(calcValue);
            this.questions[3].setAnswered(true);
            this.jTextField3.setForeground(Color.black);
            this.jTextField3.setText("");
            //this.jTextField3.setText(String.valueOf(this.questions[3].getValueSelected()));
        } else {
            this.jTextField3.setForeground(Color.red);
            this.jTextField3.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup4, this.jCheckBox4_0, this.jCheckBox4_1, this.jCheckBox4_2, this.jCheckBox4_3);
        if (calcValue > -1) {
            this.questions[4].setValueSelected(calcValue);
            this.questions[4].setAnswered(true);
            this.jTextField4.setForeground(Color.black);
            this.jTextField4.setText("");
            //this.jTextField4.setText(String.valueOf(this.questions[4].getValueSelected()));
        } else {
            this.jTextField4.setForeground(Color.red);
            this.jTextField4.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup5, this.jCheckBox5_0, this.jCheckBox5_1, this.jCheckBox5_2, this.jCheckBox5_3);
        if (calcValue > -1) {
            this.questions[5].setValueSelected(calcValue);
            this.questions[5].setAnswered(true);
            this.jTextField5.setForeground(Color.black);
            this.jTextField5.setText("");
            //this.jTextField5.setText(String.valueOf(this.questions[5].getValueSelected()));
        } else {
            this.jTextField5.setForeground(Color.red);
            this.jTextField5.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup6, this.jCheckBox6_0, this.jCheckBox6_1, this.jCheckBox6_2, this.jCheckBox6_3);
        if (calcValue > -1) {
            this.questions[6].setValueSelected(calcValue);
            this.questions[6].setAnswered(true);
            this.jTextField6.setForeground(Color.black);
            this.jTextField6.setText("");
            //this.jTextField6.setText(String.valueOf(this.questions[6].getValueSelected()));
        } else {
            this.jTextField6.setForeground(Color.red);
            this.jTextField6.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup7, this.jCheckBox7_3, this.jCheckBox7_2, this.jCheckBox7_1, this.jCheckBox7_0);
        if (calcValue > -1) {
            this.questions[7].setValueSelected(calcValue);
            this.questions[7].setAnswered(true);
            this.jTextField7.setForeground(Color.black);
            this.jTextField7.setText("");
            //this.jTextField7.setText(String.valueOf(this.questions[7].getValueSelected()));
        } else {
            this.jTextField7.setForeground(Color.red);
            this.jTextField7.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup8, this.jCheckBox8_0, this.jCheckBox8_1, this.jCheckBox8_2, this.jCheckBox8_3);
        if (calcValue > -1) {
            this.questions[8].setValueSelected(calcValue);
            this.questions[8].setAnswered(true);
            this.jTextField8.setForeground(Color.black);
            this.jTextField8.setText("");
            //this.jTextField8.setText(String.valueOf(this.questions[8].getValueSelected()));
        } else {
            this.jTextField8.setForeground(Color.red);
            this.jTextField8.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup9, this.jCheckBox9_0, this.jCheckBox9_1, this.jCheckBox9_2, this.jCheckBox9_3);
        if (calcValue > -1) {
            this.questions[9].setValueSelected(calcValue);
            this.questions[9].setAnswered(true);
            this.jTextField9.setForeground(Color.black);
            this.jTextField9.setText("");
            //this.jTextField9.setText(String.valueOf(this.questions[9].getValueSelected()));
        } else {
            this.jTextField9.setForeground(Color.red);
            this.jTextField9.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup10, this.jCheckBox10_0, this.jCheckBox10_1, this.jCheckBox10_2, this.jCheckBox10_3);
        if (calcValue > -1) {
            this.questions[10].setValueSelected(calcValue);
            this.questions[10].setAnswered(true);
            this.jTextField10.setForeground(Color.black);
            this.jTextField10.setText("");
            //this.jTextField10.setText(String.valueOf(this.questions[10].getValueSelected()));
        } else {
            this.jTextField10.setForeground(Color.red);
            this.jTextField10.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup11, this.jCheckBox11_3, this.jCheckBox11_2, this.jCheckBox11_1, this.jCheckBox11_0);
        if (calcValue > -1) {
            this.questions[11].setValueSelected(calcValue);
            this.questions[11].setAnswered(true);
            this.jTextField11.setForeground(Color.black);
            this.jTextField11.setText("");
            //this.jTextField11.setText(String.valueOf(this.questions[11].getValueSelected()));
        } else {
            this.jTextField11.setForeground(Color.red);
            this.jTextField11.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup12, this.jCheckBox12_0, this.jCheckBox12_1, this.jCheckBox12_2, this.jCheckBox12_3);
        if (calcValue > -1) {
            this.questions[12].setValueSelected(calcValue);
            this.questions[12].setAnswered(true);
            this.jTextField12.setForeground(Color.black);
            this.jTextField12.setText("");
            //this.jTextField12.setText(String.valueOf(this.questions[12].getValueSelected()));
        } else {
            this.jTextField12.setForeground(Color.red);
            this.jTextField12.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup13, this.jCheckBox13_0, this.jCheckBox13_1, this.jCheckBox13_2, this.jCheckBox13_3);
        if (calcValue > -1) {
            this.questions[13].setValueSelected(calcValue);
            this.questions[13].setAnswered(true);
            this.jTextField13.setForeground(Color.black);
            this.jTextField13.setText("");
            //this.jTextField13.setText(String.valueOf(this.questions[13].getValueSelected()));
        } else {
            this.jTextField13.setForeground(Color.red);
            this.jTextField13.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup14, this.jCheckBox14_0, this.jCheckBox14_1, this.jCheckBox14_2, this.jCheckBox14_3);
        if (calcValue > -1) {
            this.questions[14].setValueSelected(calcValue);
            this.questions[14].setAnswered(true);
            this.jTextField14.setForeground(Color.black);
            this.jTextField14.setText("");
            //this.jTextField14.setText(String.valueOf(this.questions[14].getValueSelected()));
        } else {
            this.jTextField14.setForeground(Color.red);
            this.jTextField14.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup15, this.jCheckBox15_3, this.jCheckBox15_2, this.jCheckBox15_1, this.jCheckBox15_0);
        if (calcValue > -1) {
            this.questions[15].setValueSelected(calcValue);
            this.questions[15].setAnswered(true);
            this.jTextField15.setForeground(Color.black);
            this.jTextField15.setText("");
            //this.jTextField15.setText(String.valueOf(this.questions[15].getValueSelected()));
        } else {
            this.jTextField15.setForeground(Color.red);
            this.jTextField15.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup16, this.jCheckBox16_0, this.jCheckBox16_1, this.jCheckBox16_2, this.jCheckBox16_3);
        if (calcValue > -1) {
            this.questions[16].setValueSelected(calcValue);
            this.questions[16].setAnswered(true);
            this.jTextField16.setForeground(Color.black);
            this.jTextField16.setText("");
            //this.jTextField16.setText(String.valueOf(this.questions[16].getValueSelected()));
        } else {
            this.jTextField16.setForeground(Color.red);
            this.jTextField16.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup17, this.jCheckBox17_0, this.jCheckBox17_1, this.jCheckBox17_2, this.jCheckBox17_3);
        if (calcValue > -1) {
            this.questions[17].setValueSelected(calcValue);
            this.questions[17].setAnswered(true);
            this.jTextField17.setForeground(Color.black);
            this.jTextField17.setText("");
            //this.jTextField17.setText(String.valueOf(this.questions[17].getValueSelected()));
        } else {
            this.jTextField17.setForeground(Color.red);
            this.jTextField17.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup18, this.jCheckBox18_0, this.jCheckBox18_1, this.jCheckBox18_2, this.jCheckBox18_3);
        if (calcValue > -1) {
            this.questions[18].setValueSelected(calcValue);
            this.questions[18].setAnswered(true);
            this.jTextField18.setForeground(Color.black);
            this.jTextField18.setText("");
            //this.jTextField18.setText(String.valueOf(this.questions[18].getValueSelected()));
        } else {
            this.jTextField18.setForeground(Color.red);
            this.jTextField18.setText("*");
        }

        calcValue = this.calcValue(this.buttonGroup19, this.jCheckBox19_0, this.jCheckBox19_1, this.jCheckBox19_2, this.jCheckBox19_3);
        if (calcValue > -1) {
            this.questions[19].setValueSelected(calcValue);
            this.questions[19].setAnswered(true);
            this.jTextField19.setForeground(Color.black);
            this.jTextField19.setText("");
            //this.jTextField19.setText(String.valueOf(this.questions[19].getValueSelected()));
        } else {
            this.jTextField19.setForeground(Color.red);
            this.jTextField19.setText("*");
        }

        if (this.isAnswerIntegrityOK()) {
            int loopOfArrayQuestions = -1;
            Iterator<javax.swing.JTextField> iterJTxtFld = collectionOfJTextFields.iterator();

            javax.swing.JTextField curTxtFld;

            while (iterJTxtFld.hasNext()) {
                curTxtFld = iterJTxtFld.next();
                loopOfArrayQuestions++;
                //curTxtFld.setText("");
                curTxtFld.setForeground(Color.black);
                curTxtFld.setText(String.valueOf(this.questions[loopOfArrayQuestions].getValueSelected()));
            }


            this.jTextAreaStatus.setForeground(Color.black);
            this.jTextAreaStatus.setText("Συνολική Βαθμολογία: " + this.calcFinalValue());

            this.jButtonCancel.setText("Cancel");
            this.jButtonSubmit.setText("Submit");
            this.jTextFieldTestCode.setText("Test Code");
            this.jTextFieldTestCode.setVisible(true);
            this.jComboBoxAge.setVisible(true);
            this.jComboBoxEdu.setVisible(true);
            this.jComboBoxSex.setVisible(true);
            this.jButtonCancel.setVisible(true);
            this.jButtonSubmit.setVisible(true);


        } else {
            this.jTextAreaStatus.setForeground(Color.red);
            this.jTextAreaStatus.setText("Δεν έχει απαντηθεί κάποια ερώτηση.\nΠαρακαλούμε απαντήστε όλες τις ερωτήσεις");
        }

    }//GEN-LAST:event_CalcMenuItemActionPerformed
    /**
     * resets anwer values, checkboxes, final value, state of checkbox question
     *
     * @param evt
     */
    private void ResetMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ResetMenuItemActionPerformed
        this.resetAll();
    }//GEN-LAST:event_ResetMenuItemActionPerformed
    /**
     * fills checkbox questions with random values
     *
     * @param evt
     */
    private void RandomMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RandomMenuItemActionPerformed
        //
        int chosenCheckBox = 0;
        Random rnd = new Random();

        Iterator<javax.swing.JTextField> iterJTxtFld = collectionOfJTextFields.iterator();
        Iterator<javax.swing.JCheckBox> iterChkBx = collectionOfCheckBoxes.iterator();

        javax.swing.JTextField curTxtFld;
        javax.swing.JCheckBox curChkBx;

        while (iterJTxtFld.hasNext()) {
            curTxtFld = iterJTxtFld.next();
            System.out.println(curTxtFld.getName() + "-------");
            chosenCheckBox = rnd.nextInt(4);
            System.out.println(" chosen rnd number" + chosenCheckBox);

            if (chosenCheckBox == 0) {
                System.out.println(" checking checkbox 0");
                curChkBx = iterChkBx.next();
                System.out.println(" selecting to change" + curChkBx.getName());
                curChkBx.setSelected(true);
                curChkBx.doClick();
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
            } else if (chosenCheckBox == 1) {
                System.out.println(" checking checkbox 1");
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
                System.out.println(" selecting to change" + curChkBx.getName());
                curChkBx.setSelected(true);
                curChkBx.doClick();
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
            } else if (chosenCheckBox == 2) {
                System.out.println(" checking checkbox 2");
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
                System.out.println(" selecting to change" + curChkBx.getName());
                curChkBx.setSelected(true);
                curChkBx.doClick();
                curChkBx = iterChkBx.next();
            } else if (chosenCheckBox == 3) {
                System.out.println(" checking checkbox 3");
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
                curChkBx = iterChkBx.next();
                System.out.println(" selecting to change" + curChkBx.getName());
                curChkBx.setSelected(true);
                curChkBx.doClick();
            }
            System.out.println(" current checkbox");
        }
    }//GEN-LAST:event_RandomMenuItemActionPerformed

    private void jButtonSubmitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSubmitActionPerformed
        if (this.jTextFieldTestCode.getText().equals("Test Code")
                || this.jComboBoxAge.getSelectedItem().toString().equals("ηλικία")
                || this.jComboBoxSex.getSelectedItem().toString().equals("φύλο")
                || this.jComboBoxEdu.getSelectedItem().toString().equals("εκπαίδευση")) {
            this.jTextAreaStatus.setForeground(Color.red);
            this.jTextAreaStatus.setText("Συμπληρώστε τα στοιχεία που λείπουν");
        } else {
            this.calcFinalValueAndWriteToFile();
            this.resetAll();
            this.jTextAreaStatus.setForeground(Color.black);
            this.jTextAreaStatus.setText("Τα στοιχεία αποθηκεύτηκαν. Αν θέλετε Ξεκινήστε νέο Test");
        }

    }//GEN-LAST:event_jButtonSubmitActionPerformed

    private void jButtonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelActionPerformed
        this.resetAll();
    }//GEN-LAST:event_jButtonCancelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem CalcMenuItem;
    private javax.swing.JMenuItem RandomMenuItem;
    private javax.swing.JMenuItem ResetMenuItem;
    private javax.swing.ButtonGroup buttonGroup0;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup10;
    private javax.swing.ButtonGroup buttonGroup11;
    private javax.swing.ButtonGroup buttonGroup12;
    private javax.swing.ButtonGroup buttonGroup13;
    private javax.swing.ButtonGroup buttonGroup14;
    private javax.swing.ButtonGroup buttonGroup15;
    private javax.swing.ButtonGroup buttonGroup16;
    private javax.swing.ButtonGroup buttonGroup17;
    private javax.swing.ButtonGroup buttonGroup18;
    private javax.swing.ButtonGroup buttonGroup19;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.ButtonGroup buttonGroup3;
    private javax.swing.ButtonGroup buttonGroup4;
    private javax.swing.ButtonGroup buttonGroup5;
    private javax.swing.ButtonGroup buttonGroup6;
    private javax.swing.ButtonGroup buttonGroup7;
    private javax.swing.ButtonGroup buttonGroup8;
    private javax.swing.ButtonGroup buttonGroup9;
    private javax.swing.JButton jButtonCancel;
    private javax.swing.JButton jButtonSubmit;
    private javax.swing.JCheckBox jCheckBox0_0;
    private javax.swing.JCheckBox jCheckBox0_1;
    private javax.swing.JCheckBox jCheckBox0_2;
    private javax.swing.JCheckBox jCheckBox0_3;
    private javax.swing.JCheckBox jCheckBox10_0;
    private javax.swing.JCheckBox jCheckBox10_1;
    private javax.swing.JCheckBox jCheckBox10_2;
    private javax.swing.JCheckBox jCheckBox10_3;
    private javax.swing.JCheckBox jCheckBox11_0;
    private javax.swing.JCheckBox jCheckBox11_1;
    private javax.swing.JCheckBox jCheckBox11_2;
    private javax.swing.JCheckBox jCheckBox11_3;
    private javax.swing.JCheckBox jCheckBox12_0;
    private javax.swing.JCheckBox jCheckBox12_1;
    private javax.swing.JCheckBox jCheckBox12_2;
    private javax.swing.JCheckBox jCheckBox12_3;
    private javax.swing.JCheckBox jCheckBox13_0;
    private javax.swing.JCheckBox jCheckBox13_1;
    private javax.swing.JCheckBox jCheckBox13_2;
    private javax.swing.JCheckBox jCheckBox13_3;
    private javax.swing.JCheckBox jCheckBox14_0;
    private javax.swing.JCheckBox jCheckBox14_1;
    private javax.swing.JCheckBox jCheckBox14_2;
    private javax.swing.JCheckBox jCheckBox14_3;
    private javax.swing.JCheckBox jCheckBox15_0;
    private javax.swing.JCheckBox jCheckBox15_1;
    private javax.swing.JCheckBox jCheckBox15_2;
    private javax.swing.JCheckBox jCheckBox15_3;
    private javax.swing.JCheckBox jCheckBox16_0;
    private javax.swing.JCheckBox jCheckBox16_1;
    private javax.swing.JCheckBox jCheckBox16_2;
    private javax.swing.JCheckBox jCheckBox16_3;
    private javax.swing.JCheckBox jCheckBox17_0;
    private javax.swing.JCheckBox jCheckBox17_1;
    private javax.swing.JCheckBox jCheckBox17_2;
    private javax.swing.JCheckBox jCheckBox17_3;
    private javax.swing.JCheckBox jCheckBox18_0;
    private javax.swing.JCheckBox jCheckBox18_1;
    private javax.swing.JCheckBox jCheckBox18_2;
    private javax.swing.JCheckBox jCheckBox18_3;
    private javax.swing.JCheckBox jCheckBox19_0;
    private javax.swing.JCheckBox jCheckBox19_1;
    private javax.swing.JCheckBox jCheckBox19_2;
    private javax.swing.JCheckBox jCheckBox19_3;
    private javax.swing.JCheckBox jCheckBox1_0;
    private javax.swing.JCheckBox jCheckBox1_1;
    private javax.swing.JCheckBox jCheckBox1_2;
    private javax.swing.JCheckBox jCheckBox1_3;
    private javax.swing.JCheckBox jCheckBox2_0;
    private javax.swing.JCheckBox jCheckBox2_1;
    private javax.swing.JCheckBox jCheckBox2_2;
    private javax.swing.JCheckBox jCheckBox2_3;
    private javax.swing.JCheckBox jCheckBox3_0;
    private javax.swing.JCheckBox jCheckBox3_1;
    private javax.swing.JCheckBox jCheckBox3_2;
    private javax.swing.JCheckBox jCheckBox3_3;
    private javax.swing.JCheckBox jCheckBox4_0;
    private javax.swing.JCheckBox jCheckBox4_1;
    private javax.swing.JCheckBox jCheckBox4_2;
    private javax.swing.JCheckBox jCheckBox4_3;
    private javax.swing.JCheckBox jCheckBox5_0;
    private javax.swing.JCheckBox jCheckBox5_1;
    private javax.swing.JCheckBox jCheckBox5_2;
    private javax.swing.JCheckBox jCheckBox5_3;
    private javax.swing.JCheckBox jCheckBox6_0;
    private javax.swing.JCheckBox jCheckBox6_1;
    private javax.swing.JCheckBox jCheckBox6_2;
    private javax.swing.JCheckBox jCheckBox6_3;
    private javax.swing.JCheckBox jCheckBox7_0;
    private javax.swing.JCheckBox jCheckBox7_1;
    private javax.swing.JCheckBox jCheckBox7_2;
    private javax.swing.JCheckBox jCheckBox7_3;
    private javax.swing.JCheckBox jCheckBox8_0;
    private javax.swing.JCheckBox jCheckBox8_1;
    private javax.swing.JCheckBox jCheckBox8_2;
    private javax.swing.JCheckBox jCheckBox8_3;
    private javax.swing.JCheckBox jCheckBox9_0;
    private javax.swing.JCheckBox jCheckBox9_1;
    private javax.swing.JCheckBox jCheckBox9_2;
    private javax.swing.JCheckBox jCheckBox9_3;
    private javax.swing.JComboBox jComboBoxAge;
    private javax.swing.JComboBox jComboBoxEdu;
    private javax.swing.JComboBox jComboBoxSex;
    private javax.swing.JDialog jDialog1;
    private javax.swing.JLabel jLabel0;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelA;
    private javax.swing.JLabel jLabelB;
    private javax.swing.JLabel jLabelC;
    private javax.swing.JLabel jLabelD;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextAreaStatus;
    private javax.swing.JTextField jTextField0;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField14;
    private javax.swing.JTextField jTextField15;
    private javax.swing.JTextField jTextField16;
    private javax.swing.JTextField jTextField17;
    private javax.swing.JTextField jTextField18;
    private javax.swing.JTextField jTextField19;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JTextField jTextFieldTestCode;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables
}
