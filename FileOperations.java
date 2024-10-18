
import java.io.File;
import javax.swing.*;

public class FileOperations {

    public void renameFile(File file) {
        String newName = JOptionPane.showInputDialog("Enter new name for the file:");
        if (newName != null && !newName.trim().isEmpty()) {
            File newFile = new File(file.getParent() + File.separator + newName);
            file.renameTo(newFile);
            JOptionPane.showMessageDialog(null, "File renamed successfully.");
        }
    }

    public void deleteFile_Folder(File file) {
        int response = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this file/folder?", "Delete", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            if (deleteRecursive(file)) {
                JOptionPane.showMessageDialog(null, "File/folder deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete file/folder.");
            }
        }
    }

    private boolean deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] contents = file.listFiles();
            if (contents != null) {
                for (File f : contents) {
                    deleteRecursive(f);
                }
            }
        }
        return file.delete();
    }

    public void showProperties(File file) {
        long size = file.length();
        if (file.isDirectory()) {
            String message = "Name: " + file.getName() + "\nPath: " + file.getPath()
                    + "\nSize: " + size + " bytes"
                    +"\nTotal files: "+ file.listFiles().length
                    + "\nLast Modified: " + new java.util.Date(file.lastModified());
            JOptionPane.showMessageDialog(null, message, "Properties", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String message = "Name: " + file.getName() + "\nPath: " + file.getPath()
                + "\nSize: " + size + " bytes"
                + "\nLast Modified: " + new java.util.Date(file.lastModified());
        JOptionPane.showMessageDialog(null, message, "Properties", JOptionPane.INFORMATION_MESSAGE);
    }

    public void moveFile(File file) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File targetFile = fileChooser.getSelectedFile();
            if (!targetFile.exists()) {
                if (file.renameTo(targetFile)) {
                    JOptionPane.showMessageDialog(null, "File moved successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to move file.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "A file with the same name already exists.");
            }
        }
    }
}
