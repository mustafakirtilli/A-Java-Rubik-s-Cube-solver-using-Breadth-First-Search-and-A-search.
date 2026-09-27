# Rubik's Cube Solver

A Java implementation of a 3x3 Rubik's Cube solver using Breadth-First Search (BFS) and A* search.

## Features

- Represents a 3x3 Rubik's Cube in Java
- Supports all six face rotations: F, B, R, L, U, and D
- Uses Breadth-First Search for shallow solutions
- Uses A* search for deeper searches
- Tracks visited cube states to avoid repeated work
- Uses a heuristic based on misplaced edges and corners
- Includes search depth, state, and runtime limits
- Reads scrambled cube states from a file
- Writes the solution moves to an output file

## Project Structure

```text
src/
└── rubikscube/
    ├── IncorrectFormatException.java
    ├── RubiksCube.java
    ├── RubiksCubeSolver.java
    └── Solver.java

## Algorithms

Breadth-First Search
The solver first attempts Breadth-First Search with a limited search depth. BFS explores cube states level by level and is effective for cubes that are relatively close to the solved state.
A* Search
If BFS does not find a solution within its limits, the solver switches to A* search.
A* prioritizes cube states using:
f(n) = g(n) + h(n)
where:
- g(n) represents the cost of reaching the current state
- h(n) estimates the remaining distance to the solved state
The heuristic is based on the number of misplaced edges and corners.

## Optimizations

To reduce unnecessary search and control memory and runtime usage, the solver:
- Tracks visited cube states to avoid revisiting the same configuration
- Avoids consecutive moves on the same face
- Uses a maximum BFS depth
- Limits the number of BFS states checked
- Limits the number of A* states expanded
- Uses a runtime limit to prevent excessively long searches
  
##Technologies and Concepts

- Java
- Breadth-First Search
- A* Search
- Priority Queues
- Hash Sets
- Heuristic Search
- State-Space Search
- File Input/Output
  
##Background

This project was originally developed as part of a data structures and algorithms course.
The main challenge was managing the very large search space of a Rubik's Cube while keeping memory usage and execution time manageable. The project uses a combination of BFS, A* search, heuristics, visited-state tracking, and search limits to balance solution quality with performance.
