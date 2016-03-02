package uud.ummbook.editor;

import javax.swing.JFrame;

public class UmmbookEditor extends JFrame {

	private static final long serialVersionUID = -6250419800099118349L;
	
	public UmmbookEditor() {
		super("Ummbook Editor");
		setSize(800, 600);
		setVisible(true);
	}

	public static void main(String[] args) {
		new UmmbookEditor();
	}

}
