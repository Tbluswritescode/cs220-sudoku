package sudoku;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import javax.swing.border.LineBorder;

/**
 * 
 * This is the GUI (Graphical User Interface) for Sudoku.
 * 
 * It extends JFrame, which means that it is a subclass of JFrame.
 * The JFrame is the main class, and owns the JMenuBar, which is
 * the menu bar at the top of the screen with the File and Help
 * and other menus.
 * 
 * 
 * One of the most important instance variables is a JCanvas, which is
 * kind of like the canvas that we will paint all of the grid squared onto.
 * 
 * @author jaimespacco
 *
 */
public class SudokuGUI extends JFrame {

	private Sudoku sudoku;

	private static final long serialVersionUID = 1L;

	// Sudoku boards have 9 rows and 9 columns
	private int numRows = 9;
	private int numCols = 9;

	// the current row and column we are potentially putting values into
	private int currentRow = -1;
	private int currentCol = -1;

	// hint row and hint column
	private int hintRow = -1;
	private int hintCol = -1;

	// show legal values toggle
	private boolean showLegals = false;
	private boolean showInvalids = false;

	// figuring out how big to make each button
	// honestly not sure how much detail is needed here with margins
	protected final int MARGIN_SIZE = 5;
	protected final int DOUBLE_MARGIN_SIZE = MARGIN_SIZE * 2;
	protected int squareSize = 90;
	private int width = DOUBLE_MARGIN_SIZE + squareSize * numCols;
	private int height = DOUBLE_MARGIN_SIZE + squareSize * numRows;

	// for lots of fun, too much fun really, try "Wingdings"
	private static Font FONT = new Font("Verdana", Font.BOLD, 40);
	private static Color FONT_COLOR = Color.BLACK;
	private static Color BACKGROUND_COLOR = Color.GRAY;
	private static Color Highlight = Color.cyan;
	private static Color Underline = Color.red;
	private static Color Hint = Color.pink;

	// the canvas is a panel that gets drawn on
	private JPanel panel;

	// this is the menu bar at the top that owns all of the buttons
	private JMenuBar menuBar;

	// 2D array of buttons; each sudoku square is a button
	private JButton[][] buttons = new JButton[numRows][numCols];

	private class MyKeyListener extends KeyAdapter {
		public final int row;
		public final int col;
		public final Sudoku sudoku;

		MyKeyListener(int row, int col, Sudoku sudoku) {
			this.sudoku = sudoku;
			this.row = row;
			this.col = col;
		}

		public void keyTyped(KeyEvent e) {
			char key = e.getKeyChar();
			// System.out.println(key);
			if (Character.isDigit(key)) {
				// use ascii values to convert chars to ints
				int digit = key - '0';
				System.out.println(key);
				if (currentRow == row && currentCol == col) {
					if (!sudoku.isLegal(row, col, digit)) {
						JOptionPane.showMessageDialog(null,
								String.format("%d cannot go in row %d and col %d", digit, row + 1, col + 1));
					}
					sudoku.set(row, col, digit);
				}
				update();
			}
		}
	}

	private class ButtonListener implements ActionListener {
		public final int row;
		public final int col;
		public final Sudoku sudoku;

		ButtonListener(int row, int col, Sudoku sudoku) {
			this.sudoku = sudoku;
			this.row = row;
			this.col = col;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			hintCol = -1;
			hintRow = -1;
			if (row == currentRow && col == currentCol) {
				currentRow = -1;
				currentCol = -1;
			} else if (sudoku.isBlank(row, col)) {
				// we can try to enter a value in a
				currentRow = row;
				currentCol = col;

				// TODO: figure out some way that users can enter values
				// A simple way to do this is to take keyboard input
				// or you can cycle through possible legal values with each click
				// or pop up a selector with only the legal valuess

			} else {
				// TODO: error dialog letting the user know that they cannot enter values
				// where a value has already been placed
				JOptionPane.showMessageDialog(null, "Can't enter a value here");
			}

			update();
		}
	}

	/**
	 * Put text into the given JButton
	 * 
	 * @param row
	 * @param col
	 * @param text
	 */
	private void setText(int row, int col, String text) {
		buttons[row][col].setText(text);
	}

	/**
	 * This is a private helper method that updates the GUI/view
	 * to match any changes to the model
	 */
	private void update() {
		for (int row = 0; row < numRows; row++) {
			/*
			 * ::::::NEW FEATURE ::::::
			 * This allows the theme to be updated
			 */
			for (int col = 0; col < numCols; col++) {
				int top = 1;
				int left = 1;
				int right = 1;
				int bottom = 1;
				if (row % 3 == 2) {
					bottom = 5;
				}
				if (col % 3 == 2) {
					right = 5;
				}
				if (row == 0) {
					top = 5;
				}
				if (col == 9) {
					bottom = 5;
				}
				if (hintRow == row && hintCol == col) {
					buttons[row][col].setBackground(Hint);
					buttons[row][col].setBorder(new LineBorder(FONT_COLOR));
					setText(row, col, "");
				} else if (row == currentRow && col == currentCol && sudoku.isBlank(row, col)) {
					// draw this grid square special!
					// this is the grid square we are trying to enter value into
					buttons[row][col].setForeground(Underline);
					// I can't figure out how to change the background color of a grid square, ugh
					// Maybe I should have used JLabel instead of JButton?
					buttons[row][col].setBackground(Highlight);
					buttons[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, FONT_COLOR));
					setText(row, col, "_");
					Collection<Integer> legals = sudoku.getLegalValues(row, col);

					if (showLegals) {
						JOptionPane.showMessageDialog(null, legals.toString());
					}
				} else {
					buttons[row][col].setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, FONT_COLOR));
					buttons[row][col].setForeground(FONT_COLOR);
					buttons[row][col].setBackground(BACKGROUND_COLOR);
					int val = sudoku.get(row, col);
					if (val == 0) {
						setText(row, col, "");
					} else {
						setText(row, col, val + "");
					}
					if (showInvalids && !sudoku.isBlank(row, col)) {
						if (!sudoku.isValid(row, col)) {
							buttons[row][col].setBackground(Color.RED);
						}
					}
				}
			}
		}
		repaint();
	}

	private void createMenuBar() {
		menuBar = new JMenuBar();

		//
		// File menu
		//
		JMenu file = new JMenu("File");
		menuBar.add(file);

		// anonymous inner class
		// basically, immediately create a class that implements actionlistener
		// and then give it the given actionPerformed method.
		addToMenu(file, "New Game", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sudoku.load("easy1.txt");
				update();
			}
		});
		addToMenu(file, "Theme Select", new ActionListener() {
			/** This menu item allows the user to select from three different themes */
			@Override
			public void actionPerformed(ActionEvent e) {
				JPopupMenu color = new JPopupMenu("Theme Select");
				JMenuItem dark = new JMenuItem("Dark Mode");
				JMenuItem sea = new JMenuItem("Sea Mode");
				JMenuItem light = new JMenuItem("Light Mode");

				dark.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FONT_COLOR = Color.lightGray;
						BACKGROUND_COLOR = Color.black;
						Highlight = new Color(121, 145, 138);
						Underline = Color.magenta;
						Hint = Color.green;
						update();
					}
				});

				sea.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FONT_COLOR = new Color(100, 155, 189);
						BACKGROUND_COLOR = new Color(15, 31, 56);
						Highlight = new Color(33, 71, 130);
						Underline = new Color(162, 235, 213);
						Hint = new Color(129, 199, 150);
						update();
					}
				});
				light.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						FONT_COLOR = Color.BLACK;
						BACKGROUND_COLOR = Color.GRAY;
						Highlight = Color.cyan;
						Underline = Color.red;
						Hint = Color.pink;

						update();
					}
				});
				color.add(dark);
				color.add(sea);
				color.add(light);

				color.show(panel, width / 2, height / 2);
				update();
			}
		});

		addToMenu(file, "Save", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String board = sudoku.toSaveString();
				JFileChooser jfc = new JFileChooser(new File("."));

				int returnVal = jfc.showSaveDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					Util.writeToFile(selectedFile, board);
					System.out.println(selectedFile.getAbsolutePath());
					JOptionPane.showMessageDialog(null,
							"Saved game to file " + selectedFile.getAbsolutePath());
				}
				// TODO: save the current game to a file!
				// HINT: Check the Util.java class for helpful methods
				// HINT: check out JFileChooser
				// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html

				update();
			}
		});

		addToMenu(file, "Load", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(new File("."));

				int returnVal = jfc.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File selectedFile = jfc.getSelectedFile();
					System.out.println(selectedFile.getAbsolutePath());
					sudoku.load(selectedFile);
					JOptionPane.showMessageDialog(null,
							"Loaded game from file " + selectedFile.getAbsolutePath());

				}
				update();
			}
		});

		//
		// Help menu
		//
		JMenu help = new JMenu("Help");
		menuBar.add(help);

		addToMenu(help, "Hint", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentRow = -1;
				currentCol = -1;
				for (int r = 0; r < 9; r++) {
					for (int c = 0; c < 9; c++) {
						if (sudoku.isBlank(r, c) && sudoku.getLegalValues(r, c).size() == 1) {
							hintRow = r;
							hintCol = c;
							update();
							return;
						}
					}
				}
				JOptionPane.showMessageDialog(null, "Give the user a hint! Highlight the most constrained square\n" +
						"which is the square where the fewest posssible values can go");
			}
		});
		JMenuItem legals = new JCheckBoxMenuItem("Show Legal Values");
		help.add(legals);
		legals.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				showLegals = !showLegals;
				if (showLegals) {
					JOptionPane.showMessageDialog(null, "Show Legal Values Enabled");
				} else {
					JOptionPane.showMessageDialog(null, "Show Legal Values Disabled");
				}
			}
		});

		/*
		 * ::::::NEW FEATURE ::::::
		 * This menu option asks a user to load a solution text file and highlights any
		 * answers which do not match the solution
		 * Next expansion would be allowing you to clear buttons after discovering a
		 * mistake.
		 */
		JMenuItem invalids = new JCheckBoxMenuItem("Highlight Incorrect Answers");
		help.add(invalids);
		invalids.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				showInvalids = !showInvalids;
				if (showInvalids) {
					JFileChooser jfc = new JFileChooser(new File("."));

					int returnVal = jfc.showOpenDialog(null);
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						File selectedFile = jfc.getSelectedFile();
						System.out.println(selectedFile.getAbsolutePath());
						sudoku.loadSolution(selectedFile);
						JOptionPane.showMessageDialog(null,
								"Loaded solution from file " + selectedFile.getAbsolutePath());

					}
				} else {
					JOptionPane.showMessageDialog(null, "Highlight incorrect values Disabled");
				}
				update();
			}
		});
		this.setJMenuBar(menuBar);
	}

	/**
	 * Private helper method to put
	 * 
	 * @param menu
	 * @param title
	 * @param listener
	 */
	private void addToMenu(JMenu menu, String title, ActionListener listener) {
		JMenuItem menuItem = new JMenuItem(title);
		menu.add(menuItem);
		menuItem.addActionListener(listener);
	}

	private void createMouseHandler() {
		MouseAdapter a = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.printf("%s\n", e.getButton());
			}

		};
		this.addMouseMotionListener(a);
		this.addMouseListener(a);
	}

	private void createKeyboardHandlers() {
		for (int r = 0; r < buttons.length; r++) {
			for (int c = 0; c < buttons[r].length; c++) {
				buttons[r][c].addKeyListener(new MyKeyListener(r, c, sudoku));
			}
		}
	}

	public SudokuGUI() {
		sudoku = new Sudoku();
		// load a puzzle from a text file
		// right now we only have 1 puzzle, but we could always add more!
		sudoku.load("sudoku\\cs220-sudoku\\src\\sudoku\\easy1.txt");

		setTitle("Sudoku!");

		this.setSize(width, height);

		// the JPanel where everything gets painted
		panel = new JPanel();
		// set up a 9x9 grid layout, since sudoku boards are 9x9
		panel.setLayout(new GridLayout(9, 9));
		// set the preferred size
		// If we don't do this, often the window will be minimized
		// This is a weird quirk of Java GUIs
		panel.setPreferredSize(new Dimension(width, height));

		// This sets up 81 JButtons (9 rows * 9 columns)
		for (int r = 0; r < numRows; r++) {
			for (int c = 0; c < numCols; c++) {
				JButton b = new JButton();
				b.setPreferredSize(new Dimension(squareSize, squareSize));

				b.setFont(FONT);
				b.setForeground(FONT_COLOR);
				b.setBackground(BACKGROUND_COLOR);
				b.setOpaque(true);
				buttons[r][c] = b;
				// add the button to the canvas
				// the layout manager (the 9x9 GridLayout from a few lines earlier)
				// will make sure we get a 9x9 grid of these buttons
				panel.add(b);

				// thicker borders in some places
				// sudoku boards use 3x3 sub-grids
				int top = 1;
				int left = 1;
				int right = 1;
				int bottom = 1;
				if (r % 3 == 2) {
					bottom = 5;
				}
				if (c % 3 == 2) {
					right = 5;
				}
				if (r == 0) {
					top = 5;
				}
				if (c == 9) {
					bottom = 5;
				}
				b.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, FONT_COLOR));

				//
				// button handlers!
				//
				// check the ButtonListener class to see what this does
				//
				b.addActionListener(new ButtonListener(r, c, sudoku));
			}
		}

		this.getContentPane().add(panel, BorderLayout.CENTER);
		this.setPreferredSize(new Dimension(width, height));
		this.setResizable(false);
		this.pack();
		this.setLocation(100, 100);
		this.setFocusable(true);

		createMenuBar();
		createKeyboardHandlers();
		createMouseHandler();

		// close the GUI application when people click the X to close the window
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		update();
		repaint();
	}

	public static void main(String[] args) {
		SudokuGUI g = new SudokuGUI();
		g.setVisible(true);
	}

}
