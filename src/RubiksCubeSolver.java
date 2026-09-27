package rubikscube;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class RubiksCubeSolver {
	private final long timeoutMillis;

	public RubiksCubeSolver(long timeoutMillis) {
		this.timeoutMillis = timeoutMillis;
	}

	public RubiksCubeSolver() {
		this(10_000);
	}

	public String solve(RubiksCube start) throws TimeoutException {
		RubiksCube cube = new RubiksCube(start);
		if (cube.isSolved()) {
			return "";
		}
		long deadline = System.currentTimeMillis() + timeoutMillis;
		String bfsResult = tryBFS(cube, deadline, 10);
		if (bfsResult != null) {
			return bfsResult;
		}
		return tryAStar(cube, deadline);
	}

	private String tryBFS(RubiksCube start, long deadline, int maxDepth) throws TimeoutException {
		ArrayDeque<BFSState> queue = new ArrayDeque<>();
		Set<String> visited = new HashSet<>();
		queue.add(new BFSState(new RubiksCube(start), new ArrayList<>()));
		visited.add(start.stateKey());
		int checked = 0;
		while (!queue.isEmpty()) {
			if (System.currentTimeMillis() > deadline) {
				return null;
			}
			if (checked++ > 200000) {
				return null;
			}
			BFSState current = queue.poll();
			if (current.cube.isSolved()) {
				return flatten(current.path);
			}
			if (current.path.size() >= maxDepth) {
				continue;
			}
			char[] faces = {'F', 'B', 'R', 'L', 'U', 'D'};
			for (char face : faces) {
				if (!current.path.isEmpty()) {
					Move lastMove = current.path.get(current.path.size() - 1);
					if (lastMove.face == face) {
						continue;
					}
				}
				RubiksCube next = new RubiksCube(current.cube);
				next.applyMove(face, 1);
				String key = next.stateKey();
				
				if (visited.contains(key)) {
					continue;
				}
				
				visited.add(key);
				List<Move> newPath = new ArrayList<>(current.path);
				newPath.add(new Move(face, 1));
				queue.add(new BFSState(next, newPath));
			}
		}
		return null;
	}

	private String tryAStar(RubiksCube start, long deadline) throws TimeoutException {
		PriorityQueue<AStarState> queue = new PriorityQueue<>((a, b) -> {
			int f1 = a.g + a.h;
			int f2 = b.g + b.h;
			if (f1 != f2) return Integer.compare(f1, f2);
			return Integer.compare(a.g, b.g);
		});
		Set<String> visited = new HashSet<>();
		int h = heuristic(start);
		queue.add(new AStarState(new RubiksCube(start), new ArrayList<>(), 0, h));
		visited.add(start.stateKey());
		int checked = 0;
		while (!queue.isEmpty()) {
			if (System.currentTimeMillis() > deadline) {
				throw new TimeoutException("Solver exceeded time limit");
			}
			if (checked++ > 500000) {
				throw new TimeoutException("Solver exceeded time limit");
			}
			AStarState current = queue.poll();
			if (current.cube.isSolved()) {
				return flatten(current.path);
			}
			for (Move move : buildMoves()) {
				if (!current.path.isEmpty()) {
					Move lastMove = current.path.get(current.path.size() - 1);
					if (lastMove.face == move.face) {
						continue;
					}
				}
				RubiksCube next = new RubiksCube(current.cube);
				next.applyMove(move.face, move.turns);
				String key = next.stateKey();
				
				if (visited.contains(key)) {
					continue;
				}
				visited.add(key);
				int newG = current.g + move.turns;
				int newH = heuristic(next);
				List<Move> newPath = new ArrayList<>(current.path);
				newPath.add(move);
				queue.add(new AStarState(next, newPath, newG, newH));
			}
		}
		throw new TimeoutException("Solver exceeded time limit");
	}

	private int heuristic(RubiksCube cube) {
		int edges = cube.misplacedEdges();
		int corners = cube.misplacedCorners();
		int h1 = (edges + 1) / 2;
		int h2 = (corners + 1) / 2;
		int h3 = (edges + corners + 1) / 2;
		return Math.max(Math.max(h1, h2), h3);
	}
	
	private static Move[] buildMoves() {
		List<Move> list = new ArrayList<>();
		char[] faces = {'F', 'B', 'R', 'L', 'U', 'D'};
		for (char face : faces) {
			list.add(new Move(face, 1));
			list.add(new Move(face, 2));
			list.add(new Move(face, 3));
		}
		return list.toArray(new Move[0]);
	}
	
	private static String flatten(List<Move> sequence) {
		StringBuilder sb = new StringBuilder();
		for (Move move : sequence) {
			for (int i = 0; i < move.turns; i++) {
				sb.append(move.face);
			}
		}
		return sb.toString();
	}
	
	private static final class BFSState {
		final RubiksCube cube;
		final List<Move> path;
		
		BFSState(RubiksCube cube, List<Move> path) {
			this.cube = cube;
			this.path = path;
		}
	}
	
	private static final class AStarState {
		final RubiksCube cube;
		final List<Move> path;
		final int g;
		final int h;
		
		AStarState(RubiksCube cube, List<Move> path, int g, int h) {
			this.cube = cube;
			this.path = path;
			this.g = g;
			this.h = h;
		}
	}
	
	private static final class Move {
		final char face;
		final int turns;
		
		Move(char face, int turns) {
			this.face = face;
			this.turns = turns;
		}
	}
}
