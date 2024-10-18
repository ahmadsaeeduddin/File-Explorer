
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;

public class App {

    private JFrame mainFrame;
    private JPanel Right_Panel;
    private JPanel Left_Panel;
    private JLabel pathLabel;
    private File currentFolder;
    private int lastslash;
    private final FileOperations fileOperations;
    Icon folderIcon = UIManager.getIcon("FileView.directoryIcon");
    Icon fileIcon = UIManager.getIcon("FileView.fileIcon");

    public App() {
        fileOperations = new FileOperations();
    }

    public void createMainFrame() {
        mainFrame = new JFrame("File Explorer");
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create the left panel with directory buttons and path label
        Left_Panel = new JPanel();
        Left_Panel.setLayout(new BorderLayout());
        Left_Panel.setBackground(Color.black);
        Left_Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Path label at the top
        pathLabel = new JLabel("Path: ", JLabel.CENTER);
        pathLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        pathLabel.setText(System.getProperty("user.home"));
        pathLabel.setFont(new Font("Arial", Font.BOLD, 14));
        pathLabel.setForeground(Color.white);
        Left_Panel.add(pathLabel, BorderLayout.NORTH);

        // Buttons for Left Panel Directories
        JPanel buttonPanel = new JPanel(new GridLayout(4, 2));
        JButton[] button = creatingButtons();

        // Add buttons to the button panel
        for (int i = 0; i < 6; i++) {
            button[i].setFont(new Font("Arial", Font.BOLD, 13));
            buttonPanel.add(button[i]);
        }
        Left_Panel.add(buttonPanel, BorderLayout.CENTER);

        // Create the right panel for displaying files
        Right_Panel = new JPanel();
        Right_Panel.setLayout(new BoxLayout(Right_Panel, BoxLayout.Y_AXIS));
        Right_Panel.setBackground(Color.LIGHT_GRAY);
        Right_Panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane fileScrollPane = new JScrollPane(Right_Panel);

        // Add Back button to the left panel
        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> goBack());
        backButton.setBackground(new Color(135, 206, 250));
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        Left_Panel.add(backButton, BorderLayout.SOUTH);

        // Create a JSplitPane to split the left (directory) and right (file display) panels
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, Left_Panel, fileScrollPane);
        splitPane.setDividerLocation(300);
        splitPane.setOneTouchExpandable(true);

        // Add the split pane to the frame
        mainFrame.add(splitPane);
        mainFrame.setVisible(true);
    }

    private JButton[] creatingButtons() {
        JButton[] button = new JButton[6];
        button[0] = hoverEffect_buttons("Desktop", folderIcon);
        button[1] = hoverEffect_buttons("Documents", folderIcon);
        button[2] = hoverEffect_buttons("Downloads", folderIcon);
        button[3] = hoverEffect_buttons("Pictures", folderIcon);
        button[4] = hoverEffect_buttons("Music", folderIcon);
        button[5] = hoverEffect_buttons("Videos", folderIcon);

        // Add action listeners to load files in the right panel when a button is clicked
        button[0].addActionListener(e -> loadFilesInPanel(System.getProperty("user.home") + "/Desktop"));
        button[1].addActionListener(e -> loadFilesInPanel(System.getProperty("user.home") + "/Documents"));
        button[2].addActionListener(e -> loadFilesInPanel(System.getProperty("user.home") + "/Downloads"));
        button[3].addActionListener(e -> loadFilesInPanel(System.getProperty("user.home") + "/Pictures"));
        button[4].addActionListener(e -> loadFilesInPanel(System.getProperty("user.home") + "/Music"));
        button[5].addActionListener(e -> loadFilesInPanel(System.getProperty("user.home") + "/Videos"));

        return button;
    }

    private JButton hoverEffect_buttons(String text, Icon icon) {
        JButton button = new JButton(text, icon);
        button.setBackground(new Color(255, 250, 205));
        button.setForeground(new Color(54, 69, 79));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(188, 143, 143));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 250, 205));
            }
        });
        return button;
    }

    private void loadFilesInPanel(String folderPath) {
        currentFolder = new File(folderPath);
        pathLabel.setText("Path: " + currentFolder.getPath());
        Right_Panel.removeAll();
        if (currentFolder.exists()) {
            File[] files = currentFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    JButton fileButton;

                    if (file.isDirectory()) {
                        // Folder icon
                        fileButton = hoverEffect_buttons(file.getName(), folderIcon);
                        fileButton.addActionListener(e -> loadFilesInPanel(file.getPath()));
                        fileButton.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseReleased(MouseEvent e) {
                                if (e.isPopupTrigger()) {
                                    showContextMenu(file, e.getComponent(), e.getX(), e.getY());
                                }
                            }
                        });
                    } else {
                        fileButton = hoverEffect_buttons(file.getName(), fileIcon);
                        fileButton.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    openFile(file);
                                }
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                                if (e.isPopupTrigger()) {
                                    showContextMenu(file, e.getComponent(), e.getX(), e.getY());
                                }
                            }
                        });
                    }
                    Right_Panel.add(fileButton);
                }
            }
        }
        Right_Panel.revalidate();
        Right_Panel.repaint();
    }

    private void goBack() {
        String path = pathLabel.getText();
        if (path.startsWith("Path: ")) {
            path = path.substring(6);
        }

        lastslash = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == '\\') {
                lastslash = i;
            }
        }

        String newpath = "";
        if (lastslash != 0) {
            newpath = path.substring(0, lastslash);
        }

        loadFilesInPanel(newpath);

    }

    public void openFile(File file) {
        try {
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                desktop.open(file);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to open file: " + file.getName());
        }
    }

    private void showContextMenu(File file, Component component, int x, int y) {
        JPopupMenu contextMenu = new JPopupMenu();

        JMenuItem propertiesItem = new JMenuItem("Properties");
        propertiesItem.addActionListener(e -> fileOperations.showProperties(file));
        contextMenu.add(propertiesItem);

        JMenuItem renameItem = new JMenuItem("Rename");
        renameItem.addActionListener(e -> rename(file));
        contextMenu.add(renameItem);

        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> delete(file));
        contextMenu.add(deleteItem);

        JMenuItem moveItem = new JMenuItem("Move");
        moveItem.addActionListener(e -> move(file));
        contextMenu.add(moveItem);

        contextMenu.show(component, x, y);
    }

    public void delete(File file) {
        fileOperations.deleteFile_Folder(file);
        loadFilesInPanel(currentFolder.getPath());
    }

    public void rename(File file) {
        fileOperations.renameFile(file);
        loadFilesInPanel(currentFolder.getPath());
    }

    public void move(File file) {
        fileOperations.moveFile(file);
        loadFilesInPanel(currentFolder.getPath());
    }
}
