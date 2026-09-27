package rubikscube;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;


public class RubiksCube {
	private static final char UP_COLOR = 'O';
	private static final char DOWN_COLOR = 'R';
	private static final char FRONT_COLOR = 'W';
	private static final char BACK_COLOR = 'Y';
	private static final char LEFT_COLOR = 'G';
	private static final char RIGHT_COLOR = 'B';

	private final char[] U = new char[9];
	private final char[] D = new char[9];
	private final char[] F = new char[9];
	private final char[] B = new char[9];
	private final char[] L = new char[9];
	private final char[] R = new char[9];

	public RubiksCube() {
		reset();
	}

	public RubiksCube(RubiksCube other) {
		System.arraycopy(other.U, 0, U, 0, 9);
		System.arraycopy(other.D, 0, D, 0, 9);
		System.arraycopy(other.F, 0, F, 0, 9);
		System.arraycopy(other.B, 0, B, 0, 9);
		System.arraycopy(other.L, 0, L, 0, 9);
		System.arraycopy(other.R, 0, R, 0, 9);
	}

	public RubiksCube(String fileName) throws IOException, IncorrectFormatException {
		this();
		readFromFile(fileName);
	}

	private void reset() {
		Arrays.fill(U, UP_COLOR);
		Arrays.fill(D, DOWN_COLOR);
		Arrays.fill(F, FRONT_COLOR);
		Arrays.fill(B, BACK_COLOR);
		Arrays.fill(L, LEFT_COLOR);
		Arrays.fill(R, RIGHT_COLOR);
	}

	private void readFromFile(String fileName) throws IOException, IncorrectFormatException {
		try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
			String[] lines = new String[9];
			for (int i = 0; i < 9; i++) {
				lines[i] = br.readLine();
				if (lines[i] == null) {
					throw new IncorrectFormatException("File has fewer than 9 lines");
				}
			}
			for (int r = 0; r < 3; r++) {
				String row = lines[r];
				validateRow(row, 6);
				for (int c = 0; c < 3; c++) {
					U[r * 3 + c] = row.charAt(3 + c);
				}
			}
			for (int r = 0; r < 3; r++) {
				String row = lines[3 + r];
				validateRow(row, 12);
				for (int c = 0; c < 3; c++) {
					L[r * 3 + c] = row.charAt(c);
					F[r * 3 + c] = row.charAt(3 + c);
					R[r * 3 + c] = row.charAt(6 + c);
					B[r * 3 + c] = row.charAt(9 + c);
				}
			}
			for (int r = 0; r < 3; r++) {
				String row = lines[6 + r];
				validateRow(row, 6);
				for (int c = 0; c < 3; c++) {
					D[r * 3 + c] = row.charAt(3 + c);
				}
			}
		}
	}

	private static void validateRow(String row, int minLength) throws IncorrectFormatException {
		if (row == null || row.length() < minLength) {
			throw new IncorrectFormatException("Malformed cube row: " + row);
		}
	}

	public boolean isSolved() {
		return faceSolved(U, UP_COLOR) && faceSolved(D, DOWN_COLOR) && faceSolved(F, FRONT_COLOR)
				&& faceSolved(B, BACK_COLOR) && faceSolved(L, LEFT_COLOR) && faceSolved(R, RIGHT_COLOR);
	}

	private static boolean faceSolved(char[] face, char expected) {
		for (char c : face) {
			if (c != expected) {
				return false;
			}
		}
		return true;
	}

	public void applyMoves(String moves) {
		if (moves == null) {
			return;
		}
		for (int i = 0; i < moves.length(); i++) {
			char ch = moves.charAt(i);
			if (isMove(ch)) {
				applyMove(ch, 1);
			}
		}
	}

	public void applyMove(char move, int turns) {
		int normalized = ((turns % 4) + 4) % 4;
		for (int i = 0; i < normalized; i++) {
			switch (move) {
				case 'F':
					rotateF();
					break;
				case 'B':
					rotateB();
					break;
				case 'R':
					rotateR();
					break;
				case 'L':
					rotateL();
					break;
				case 'U':
					rotateU();
					break;
				case 'D':
					rotateD();
					break;
				default:
					break;
			}
		}
	}

	private static boolean isMove(char c) {
		return c == 'F' || c == 'B' || c == 'R' || c == 'L' || c == 'U' || c == 'D';
	}

	private void rotateFaceClockwise(char[] face) {
		char tmp = face[0];
		face[0] = face[6];
		face[6] = face[8];
		face[8] = face[2];
		face[2] = tmp;

		tmp = face[1];
		face[1] = face[3];
		face[3] = face[7];
		face[7] = face[5];
		face[5] = tmp;
	}

	private void rotateF() {
		rotateFaceClockwise(F);
		char u6 = U[6], u7 = U[7], u8 = U[8];
		U[6] = L[8];
		U[7] = L[5];
		U[8] = L[2];

		char d0 = D[0], d1 = D[1], d2 = D[2];
		L[2] = d0;
		L[5] = d1;
		L[8] = d2;

		char r0 = R[0], r3 = R[3], r6 = R[6];
		D[0] = r6;
		D[1] = r3;
		D[2] = r0;

		R[0] = u6;
		R[3] = u7;
		R[6] = u8;
	}

	private void rotateB() {
		rotateFaceClockwise(B);
		char u0 = U[0], u1 = U[1], u2 = U[2];
		U[0] = R[2];
		U[1] = R[5];
		U[2] = R[8];

		char d6 = D[6], d7 = D[7], d8 = D[8];
		R[2] = d8;
		R[5] = d7;
		R[8] = d6;

		char l0 = L[0], l3 = L[3], l6 = L[6];
		D[6] = l0;
		D[7] = l3;
		D[8] = l6;

		L[0] = u2;
		L[3] = u1;
		L[6] = u0;
	}

	private void rotateR() {
		rotateFaceClockwise(R);
		char u2 = U[2], u5 = U[5], u8 = U[8];
		U[2] = F[2];
		U[5] = F[5];
		U[8] = F[8];

		char d2 = D[2], d5 = D[5], d8 = D[8];
		F[2] = d2;
		F[5] = d5;
		F[8] = d8;

		char b0 = B[0], b3 = B[3], b6 = B[6];
		D[2] = b6;
		D[5] = b3;
		D[8] = b0;

		B[0] = u8;
		B[3] = u5;
		B[6] = u2;
	}

	private void rotateL() {
		rotateFaceClockwise(L);
		char u0 = U[0], u3 = U[3], u6 = U[6];
		U[0] = B[8];
		U[3] = B[5];
		U[6] = B[2];

		char d0 = D[0], d3 = D[3], d6 = D[6];
		B[2] = d6;
		B[5] = d3;
		B[8] = d0;

		char f0 = F[0], f3 = F[3], f6 = F[6];
		D[0] = f0;
		D[3] = f3;
		D[6] = f6;

		F[0] = u0;
		F[3] = u3;
		F[6] = u6;
	}

	private void rotateU() {
		rotateFaceClockwise(U);
		char f0 = F[0], f1 = F[1], f2 = F[2];
		F[0] = R[0];
		F[1] = R[1];
		F[2] = R[2];

		char b0 = B[0], b1 = B[1], b2 = B[2];
		R[0] = b0;
		R[1] = b1;
		R[2] = b2;

		char l0 = L[0], l1 = L[1], l2 = L[2];
		B[0] = l0;
		B[1] = l1;
		B[2] = l2;

		L[0] = f0;
		L[1] = f1;
		L[2] = f2;
	}

	private void rotateD() {
		rotateFaceClockwise(D);
		char f6 = F[6], f7 = F[7], f8 = F[8];
		F[6] = L[6];
		F[7] = L[7];
		F[8] = L[8];

		char b6 = B[6], b7 = B[7], b8 = B[8];
		L[6] = b6;
		L[7] = b7;
		L[8] = b8;

		char r6 = R[6], r7 = R[7], r8 = R[8];
		B[6] = r6;
		B[7] = r7;
		B[8] = r8;

		R[6] = f6;
		R[7] = f7;
		R[8] = f8;
	}

	public String stateKey() {
		StringBuilder sb = new StringBuilder(54);
		appendFace(sb, U);
		appendFace(sb, D);
		appendFace(sb, F);
		appendFace(sb, B);
		appendFace(sb, L);
		appendFace(sb, R);
		return sb.toString();
	}

	public String upDownKey() {
		StringBuilder sb = new StringBuilder(18);
		appendFace(sb, U);
		appendFace(sb, D);
		return sb.toString();
	}

	private static void appendFace(StringBuilder sb, char[] face) {
		for (char c : face) {
			sb.append(c);
		}
	}

	public int mismatchCount() {
		return countMismatch(U, UP_COLOR) + countMismatch(D, DOWN_COLOR) + countMismatch(F, FRONT_COLOR)
				+ countMismatch(B, BACK_COLOR) + countMismatch(L, LEFT_COLOR) + countMismatch(R, RIGHT_COLOR);
	}

	public int misplacedEdges() {
		int count = 0;
		if (U[7] != UP_COLOR || F[1] != FRONT_COLOR) count++;
		if (U[5] != UP_COLOR || R[1] != RIGHT_COLOR) count++;
		if (U[1] != UP_COLOR || B[1] != BACK_COLOR) count++;
		if (U[3] != UP_COLOR || L[1] != LEFT_COLOR) count++;

		if (D[1] != DOWN_COLOR || F[7] != FRONT_COLOR) count++;
		if (D[5] != DOWN_COLOR || R[7] != RIGHT_COLOR) count++;
		if (D[7] != DOWN_COLOR || B[7] != BACK_COLOR) count++;
		if (D[3] != DOWN_COLOR || L[7] != LEFT_COLOR) count++;

		if (F[5] != FRONT_COLOR || R[3] != RIGHT_COLOR) count++;
		if (F[3] != FRONT_COLOR || L[5] != LEFT_COLOR) count++;
		if (B[5] != BACK_COLOR || R[5] != RIGHT_COLOR) count++;
		if (B[3] != BACK_COLOR || L[3] != LEFT_COLOR) count++;
		return count;
	}

	public int misplacedCorners() {
		int count = 0;
		if (U[8] != UP_COLOR || F[2] != FRONT_COLOR || R[0] != RIGHT_COLOR) count++;
		if (U[6] != UP_COLOR || F[0] != FRONT_COLOR || L[2] != LEFT_COLOR) count++;
		if (U[0] != UP_COLOR || B[2] != BACK_COLOR || L[0] != LEFT_COLOR) count++;
		if (U[2] != UP_COLOR || B[0] != BACK_COLOR || R[2] != RIGHT_COLOR) count++;

		if (D[2] != DOWN_COLOR || F[8] != FRONT_COLOR || R[6] != RIGHT_COLOR) count++;
		if (D[0] != DOWN_COLOR || F[6] != FRONT_COLOR || L[8] != LEFT_COLOR) count++;
		if (D[6] != DOWN_COLOR || B[8] != BACK_COLOR || L[6] != LEFT_COLOR) count++;
		if (D[8] != DOWN_COLOR || B[6] != BACK_COLOR || R[8] != RIGHT_COLOR) count++;
		return count;
	}

	private static int countMismatch(char[] face, char expected) {
		int mismatches = 0;
		for (char c : face) {
			if (c != expected) {
				mismatches++;
			}
		}
		return mismatches;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int r = 0; r < 3; r++) {
			sb.append("   ");
			for (int c = 0; c < 3; c++) {
				sb.append(U[r * 3 + c]);
			}
			sb.append('\n');
		}
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				sb.append(L[r * 3 + c]);
			}
			for (int c = 0; c < 3; c++) {
				sb.append(F[r * 3 + c]);
			}
			for (int c = 0; c < 3; c++) {
				sb.append(R[r * 3 + c]);
			}
			for (int c = 0; c < 3; c++) {
				sb.append(B[r * 3 + c]);
			}
			sb.append('\n');
		}
		for (int r = 0; r < 3; r++) {
			sb.append("   ");
			for (int c = 0; c < 3; c++) {
				sb.append(D[r * 3 + c]);
			}
			sb.append('\n');
		}
		return sb.toString();
	}

	public static int order(String moves) {
		if (moves == null || moves.isEmpty()) {
			return 1;
		}
		RubiksCube baseline = new RubiksCube();
		RubiksCube cursor = new RubiksCube();
		int count = 1;
		while (true) {
			cursor.applyMoves(moves);
			if (cursor.stateKey().equals(baseline.stateKey())) {
				return count;
			}
			count++;
			if (count > 1000) {

				return count;
			}
		}
	}
}

