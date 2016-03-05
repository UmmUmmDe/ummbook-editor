package uud.ummbook.editor;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

public class UmmbookEditor extends JFrame implements ActionListener, TreeSelectionListener {
	
	public static final String URL_WIKI = "https://github.com/UmmUmmDe/ummbook-editor/wiki";

	private static final long serialVersionUID = -6250419800099118349L;
	
	public static Game game;
	
	public JTree scenes;
	public DefaultMutableTreeNode rootNode;
	public DefaultTreeModel treeModel;
	public JTextArea text;
	public String sceneName;
	public int selectedChoice = -1;
	
	private JFileChooser fileChooser;
	
	public UmmbookEditor() {
		super("Ummbook Editor");
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		JMenuBar bar = new JMenuBar();
		JMenu menu = createMenu(bar, "File", new MenuItem[] {
				new MenuItem("New", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Open...", KeyEvent.VK_O, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Close", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("{separator}"),
				new MenuItem("Save", KeyEvent.VK_S, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Save as...", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)),
				new MenuItem("{separator}"),
				new MenuItem("Exit", KeyEvent.VK_X, KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK))
		});
		menu.setMnemonic(KeyEvent.VK_F);
		menu = createMenu(bar, "Edit", new MenuItem[] {
				new MenuItem("Undo", KeyEvent.VK_U, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Redo", KeyEvent.VK_R, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)),
				new MenuItem("{separator}"),
				new MenuItem("Cut", KeyEvent.VK_T, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Copy", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Paste", KeyEvent.VK_P, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Delete", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)),
				new MenuItem("{separator}"),
				new MenuItem("Find...", KeyEvent.VK_F, KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Find again", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_G, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("Select all", KeyEvent.VK_A, KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK))
		});
		menu.setMnemonic(KeyEvent.VK_E);
		menu = createMenu(bar, "Help", new MenuItem[] {
				new MenuItem("Wiki", KeyEvent.VK_W, KeyStroke.getKeyStroke(KeyEvent.VK_F1, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("{separator}"),
				new MenuItem("About Ummbook", KeyEvent.VK_A)
		});
		menu.setMnemonic(KeyEvent.VK_H);
		menu = createMenu(bar, "Scene", new MenuItem[] {
				new MenuItem("New scene", KeyEvent.VK_N, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)),
				new MenuItem("Delete open scene", KeyEvent.VK_D, KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_DOWN_MASK)),
				new MenuItem("{separator}"),
				new MenuItem("New choice", KeyEvent.VK_C, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK)),
				new MenuItem("Remove choice", KeyEvent.VK_R),
				new MenuItem("Edit choice", KeyEvent.VK_E, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK))
		});
		menu.setMnemonic(KeyEvent.VK_S);
		setJMenuBar(bar);
		
		fileChooser = new JFileChooser();
		
		scenes = new JTree();
		scenes.setVisible(false);
		scenes.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		scenes.setShowsRootHandles(true);
		scenes.addTreeSelectionListener(this);
		text = new JTextArea(5, 30);
		text.setEditable(false);
		
		sceneName = "";
		
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(scenes), new JScrollPane(text));
		split.setOneTouchExpandable(true);
		split.setDividerLocation(150);
		add(split);
		
		pack();
		setSize(800, 600);
		setVisible(true);
		setExtendedState(getExtendedState() | MAXIMIZED_BOTH);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		reset();
	}

	public static void main(String[] args) {
		new UmmbookEditor();
	}
	
	public JMenu createMenu(JMenuBar menubar, String name, MenuItem[] items) {
		JMenu menu = new JMenu(name);
		for (MenuItem mitem : items) {
			JMenuItem item;
			if (mitem.name.equals("{separator}")) {
				menu.addSeparator();
				continue;
			} else {
				item = new JMenuItem(mitem.name, mitem.key);
				item.setAccelerator(mitem.stroke);
				item.addActionListener(this);
			}
			menu.add(item);
		}
		menubar.add(menu);
		return menu;
	}
	
	public void reset() {
		rootNode = new DefaultMutableTreeNode("Scenes");
		treeModel = new DefaultTreeModel(rootNode);
		if (game != null) {
			for (Scene s : game.scenes) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(s.name);
				treeModel.insertNodeInto(node, rootNode, 0);
				for (int i = 0; i < s.choices.size(); i++) {
					DefaultMutableTreeNode choice = new DefaultMutableTreeNode("Choice " + (i + 1));
					treeModel.insertNodeInto(choice, node, 0);
				}
			}
		}
		scenes.setModel(treeModel);
		text.setText("");
		text.setEditable(false);
		sceneName = "";
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		int result;
		String json = null;
		JTextField choiceScene, choiceText;
		Scene scene;
		
		if (game != null) {
			json = game.toJSON();
		}
		switch (cmd) {
		case "New":
			if (game != null && !game.original.equals(json)) {
				result = JOptionPane.showConfirmDialog(this, "Create a new file without saving?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.CANCEL_OPTION) {
					break;
				} else if (result == JOptionPane.NO_OPTION) {
					if (!save()) {
						break;
					}
				}
			}
			game = new Game();
			reset();
			scenes.setVisible(true);
			break;
		case "Open...":
			if (game != null && !game.original.equals(json)) {
				result = JOptionPane.showConfirmDialog(this, "Open another file without saving?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.CANCEL_OPTION) {
					break;
				} else if (result == JOptionPane.NO_OPTION) {
					if (!save()) {
						break;
					}
				}
			}
			result = fileChooser.showOpenDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				game = new Game(file.getAbsolutePath());
				reset();
				scenes.setVisible(true);
			}
			break;
		case "Close":
			if (game != null && !game.original.equals(json)) {
				result = JOptionPane.showConfirmDialog(this, "Close without saving?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
				if (result == JOptionPane.CANCEL_OPTION) {
					break;
				} else if (result == JOptionPane.NO_OPTION) {
					if (!save()) {
						break;
					}
				}
				game = null;
			}
			game = null;
			reset();
			scenes.setVisible(false);
			break;
		case "Save":
			save();
			break;
		case "Save as...":
			result = fileChooser.showSaveDialog(this);
			if (result == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				game.file = file.getAbsolutePath();
				save();
			}
			break;
		case "Exit":
			result = JOptionPane.showConfirmDialog(this, "Exit without saving?", "Save?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (result == JOptionPane.CANCEL_OPTION) {
				break;
			} else if (result == JOptionPane.NO_OPTION) {
				if (!save()) {
					break;
				}
			}
			System.exit(0);
			break;
		case "Wiki":
			try {
				Desktop.getDesktop().browse(new URI(URL_WIKI));
			} catch (IOException | URISyntaxException e1) {
				e1.printStackTrace();
			}
			break;
		case "About Ummbook":
			JOptionPane.showMessageDialog(this, "Ummbook Editor v1.0\n\nUmmbook is software created by UmmUmmDe.\nIt is licensed under MIT.", "About Ummbook", JOptionPane.PLAIN_MESSAGE);
			break;
		case "New scene":
			if (game != null) {
				String name = JOptionPane.showInputDialog(this, "What is the name of the scene?");
				if (name != null && !name.isEmpty()) {
					game.scenes.add(new Scene(name, "Text goes here"));
					reset();
				}
			}
			break;
		case "Delete open scene":
			if (game != null) {
				scene = null;
				for (Scene s : game.scenes) {
					if (s.name.equals(sceneName)) {
						scene = s;
						break;
					}
				}
				if (scene != null) {
					result = JOptionPane.showConfirmDialog(this, "Delete open scene?", "Delete scene?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						game.scenes.remove(scene);
						reset();
					}
				}
			}
			break;
		case "New choice":
			if (game != null) {
				scene = null;
				for (Scene s : game.scenes) {
					if (s.name.equals(sceneName)) {
						scene = s;
					}
				}
				if (scene != null) {
					scene.text = text.getText();
					if (scene.choices.size() < 10) {
						choiceScene = new JTextField("Scene");
						choiceText = new JTextField("Text");
						showEditChoiceDialog(choiceScene, choiceText);
						Choice choice = new Choice(choiceText.getText(), choiceScene.getText());
						scene.choices.add(choice);
						reset();
					}
				}
			}
			break;
		case "Remove choice":
			if (game != null && selectedChoice != -1) {
				scene = null;
				for (Scene s : game.scenes){
					if (s.name.equals(sceneName)) {
						scene = s;
						break;
					}
				}
				if (scene != null) {
					scene.choices.remove(selectedChoice);
					selectedChoice = -1;
					reset();
				}
			}
			break;
		case "Edit choice":
			if (game != null && selectedChoice != -1) {
				scene = null;
				for (Scene s : game.scenes) {
					if (s.name.equals(sceneName)) {
						scene = s;
						break;
					}
				}
				if (scene != null) {
					Choice choice = scene.choices.get(selectedChoice);
					choiceScene = new JTextField(choice.scene);
					choiceText = new JTextField(choice.text);
					showEditChoiceDialog(choiceScene, choiceText);
					choice.scene = choiceScene.getText();
					choice.text = choiceText.getText();
				}
			}
			break;
		}
	}
	
	public int showEditChoiceDialog(JTextField choiceScene, JTextField choiceText) {
		return JOptionPane.showOptionDialog(this, new Object[] {
				choiceScene,
				choiceText
		}, "Edit choice", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
	}

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node = ((DefaultMutableTreeNode) e.getPath().getLastPathComponent());
		if (node == rootNode) {
			return;
		}
		if (game != null) {
			String name = node.toString();
			Scene scene = null;
			for (Scene s : game.scenes) {
				if (s.name.equals(sceneName)) {
					scene = s;
				}
			}
			if (scene != null) {
				scene.text = text.getText();
			}
			if (name.length() > 6 && name.substring(0, 6).equals("Choice")) {
				selectedChoice = Integer.parseInt(name.substring(7)) - 1;
				String parent = node.getParent().toString();
				for (Scene s : game.scenes) {
					if (s.name.equals(parent)) {
						text.setText(s.text);
						text.setEditable(true);
						sceneName = parent;
					}
				}
			} else {
				for (Scene s : game.scenes) {
					if (s.name.equals(name)) {
						text.setText(s.text);
						text.setEditable(true);
						sceneName = name;
					}
				}
				selectedChoice = -1;
			}
		}
	}
	
	public boolean save() {
		if (game != null) {
			int result;
			Scene scene = null;
			for (Scene s : game.scenes) {
				if (s.name.equals(sceneName)) {
					scene = s;
				}
			}
			if (scene != null) {
				scene.text = text.getText();
			}
			if (game.file == null) {
				result = fileChooser.showSaveDialog(this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					game.file = file.getAbsolutePath();
				} else {
					return false;
				}
			}
			try (PrintWriter out = new PrintWriter(game.file)) {
				String json = game.toJSON();
				out.println(json);
				game.original = json;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}

}

class MenuItem {
	
	public String name;
	public int key;
	public KeyStroke stroke;
	
	public MenuItem(String name) {
		this(name, -1);
	}
	
	public MenuItem(String name, int key) {
		this(name, key, null);
	}
	
	public MenuItem(String name, int key, KeyStroke stroke) {
		this.name = name;
		this.key = key;
		this.stroke = stroke;
	}
	
}
