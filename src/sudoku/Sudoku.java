package sudoku;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * This is the MODEL class. This class knows all about the
 * underlying state of the Sudoku game. We can VIEW the data
 * stored in this class in a variety of ways, for example,
 * using a simple toString() method, or using a more complex
 * GUI (Graphical User Interface) such as the SudokuGUI
 * class that is included.
 * 
 * @author jaimespacco
 *
 */
public class Sudoku {
	private int[][] board = new int[9][9];
	private int[][] solution = new int[9][9];

	public int get(int row, int col) {
		if (row < 10 && col < 10) {
			return board[row][col];
		}
		return -1;
	}

	public void set(int row, int col, int val) {
		if (isLegal(row, col, val)) {
			board[row][col] = val;
		}
	}

	public boolean isLegal(int row, int col, int val) {
		return getLegalValues(row, col).contains(val);
	}

	public Collection<Integer> getLegalValues(int row, int col) {
		// set up set of all available options by default
		Set<Integer> options = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));

		// remove integers in the same row and column
		for (int i = 0; i < 9; i++) {
			options.remove(board[row][i]);
			options.remove(board[i][col]);
		}
		int rstart = row / 3 * 3;
		int cstart = col / 3 * 3;

		for (int r = rstart; r < rstart + 3; r++) {
			for (int c = cstart; c < cstart + 3; c++) {
				options.remove(board[r][c]);
			}
		}

		return options;
	}

	/**
	 * 
	 * _ _ _ 3 _ 4 _ 8 9
	 * 1 _ 3 2 _ _ _ _ _
	 * etc
	 * 
	 * 
	 * 0 0 0 3 0 4 0 8 9
	 * 
	 */

	public void load(File file, int[][] b) {
		try {
			Scanner scan = new Scanner(new FileInputStream(file));
			// read the file
			for (int r = 0; r < 9; r++) {
				for (int c = 0; c < 9; c++) {
					int val = scan.nextInt();
					b[r][c] = val;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void load(File file) {
		load(file, board);

	}

	public void load(String filename) {
		load(new File(filename), board);

	}

	public void loadSolution(File file) {
		load(file, solution);
	}

	/**
	 * Return which 3x3 grid this row is contained in.
	 * 
	 * @param row
	 * @return
	 */
	public int get3x3row(int row) {
		return row / 3;
	}

	/**
	 * Convert this Sudoku board into a String
	 */
	public String toString() {
		String result = "";
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				int val = get(r, c);
				if (val == 0) {
					result += "_ ";
				} else {
					result += val + " ";
				}
			}
			result += "\n";
		}
		return result;
	}

	// returns a string which can be written to a file and later loaded from a file
	public String toSaveString() {
		String result = "";
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				int val = get(r, c);
				result += val + " ";
			}
			result += "\n";
		}
		return result;
	}

	public String getSolution(File file) {
		load(file, solution);
		return solution.toString();
	}

	public boolean isValid(int row, int col) {
		if (solution[row][col] != board[row][col]) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku();
		sudoku.load("easy1.txt");
		System.out.println(sudoku);

		Scanner scan = new Scanner(System.in);
		while (!sudoku.gameOver()) {
			System.out.println("enter value r, c, v :");
			int r = scan.nextInt();
			int c = scan.nextInt();
			int v = scan.nextInt();
			sudoku.set(r, c, v);

			System.out.println(sudoku);
		}
		scan.close();
	}

	public boolean gameOver() {
		for (int[] row : board) {
			for (int val : row) {
				if (val == 0)
					return false;
			}
		}
		return true;
	}

	public boolean didIWin() {
		if (!gameOver())
			return false;
		for (int r = 0; r < 9; r++) {
			for (int c = 0; c < 9; c++) {
				if (!isLegal(r, c, board[r][c]))
					return false;
			}
		}
		return true;
	}

	public boolean isBlank(int row, int col) {
		return board[row][col] == 0;
	}

}
