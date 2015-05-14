package Game;
/**
 *Project 5
 *@author Zhongshan Lu
 *@version 1.0
 */
import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.awt.*;

public class FileHandler {

    private JFileChooser chooser;
    private Component frame;

    /**
     * Sets up a new FileHandler Object
     * @param c the Component on which this object is focused
     */
    public FileHandler(Component c) {
        frame = c;
        chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);  //users can only open/save one file      
    }

    /**
     * Obtains a File from the filesystem based on the open dialog
     * @return the File object that will be used
     */
    public File openFile() {
        chooser.setDialogTitle("Open");
        if(chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            return file;
        }
        return null;
    }


    /**
     * Obtains a File from the filesystem based on the save dialog
     * @return the File object that will be used
     */
    public File saveFile() {
        chooser.setDialogTitle("Save");
        if(chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            return file;
        }
        return null;
    }


    /**
     * Writes a string to a File
     * @param f the File object to write to
     * @param output the String of output to write to the File
     */
    public void writeFile(File f, String output) {
        try {
            FileWriter fileWriter = new FileWriter(f);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(output);
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}