package rubikscube;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeoutException;

public class Solver {
	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("File names are not specified");
			System.out.println("usage: java " + MethodHandles.lookup().lookupClass().getName() + " input_file output_file");
			return;
		}
		String inputPath = args[0];
		String outputPath = args[1];
		try {
			RubiksCube cube = new RubiksCube(inputPath);
			RubiksCubeSolver solver = new RubiksCubeSolver();
			String solution = solver.solve(cube);
			writeSolution(outputPath, solution);
		} catch (IOException | IncorrectFormatException e) {
			System.err.println("Unable to read cube: " + e.getMessage());
			e.printStackTrace();
		} catch (TimeoutException e) {
			System.err.println("Solver timed out before finishing: " + e.getMessage());
			try {
				writeSolution(outputPath, "");
			} catch (IOException ioException) {
				System.err.println("Failed to write empty solution file: " + ioException.getMessage());
			}
		} catch (Exception e) {
			System.err.println("Unexpected failure: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private static void writeSolution(String outputPath, String solution) throws IOException {
		try (FileWriter writer = new FileWriter(new File(outputPath))) {
			writer.write(solution == null ? "" : solution);
			writer.write(System.lineSeparator());
		}
	}
}
