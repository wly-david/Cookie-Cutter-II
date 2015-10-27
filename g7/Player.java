package cc2.g7;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;

public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];

	private Random gen = new Random();
	
//	private Shape last_shape = null;
//	private Point last_pos = null;
	private Move last_move = null;
//	private int[] transform = {2,0,0};

	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		if (row_2.length != cutter.length - 1) {
			// save cutter length to check for retries
			row_2 = new boolean [cutter.length - 1];
			for (int i = 0 ; i != cutter.length ; ++i)
				cutter[i] = new Point(i, 0);
		} else {
			// pick a random cell from 2nd row but not same
			int i;
			do {
				i = gen.nextInt(cutter.length - 1);
			} while (row_2[i]);
			row_2[i] = true;
			cutter[cutter.length - 1] = new Point(i, 1);
			for (i = 0 ; i != cutter.length - 1 ; ++i)
				cutter[i] = new Point(i, 0);
		}
		return new Shape(cutter);
	}

	public Move cut(Dough dough, Shape[] shapes, Shape[] opponent_shapes)
	{
		if (!dough.uncut()) {
			if (last_move != null) {
				Shape[] rotations = shapes[last_move.shape].rotations();
				Shape s = rotations[last_move.rotation];
				int min_i = Integer.MAX_VALUE;
				int min_j = Integer.MAX_VALUE;
				int max_i = Integer.MIN_VALUE;
				int max_j = Integer.MIN_VALUE;
				for (Point p : s) {
					if (min_i > p.i) min_i = p.i;
					if (max_i < p.i) max_i = p.i;
					if (min_j > p.j) min_j = p.j;
					if (max_j < p.j) max_j = p.j;
				}
				int[] transform = new int[2];
				if (max_i > max_j) {
					transform[0]= 0;
					transform[1]= max_j+1;
				}
				else {
					transform[0]= max_i+1;
					transform[1]= 0;
				}

				Point pos = new Point(last_move.point.i+transform[0],last_move.point.j+transform[1]);
				if (dough.cuts(s, pos)) {
					last_move = new Move(last_move.shape,last_move.rotation,pos);
					return (last_move);
				}
			}
		}
		// prune larger shapes if initial move
		if (dough.uncut()) {
			int min = Integer.MAX_VALUE;
			for (Shape s : shapes)
				if (min > s.size())
					min = s.size();
			for (int s = 0 ; s != shapes.length ; ++s)
				if (shapes[s].size() != min)
					shapes[s] = null;
		}
		// find all valid cuts
		ArrayList <Move> moves = new ArrayList <Move> ();
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (shapes[si] == null) continue;
			for (int i = 0 ; i != dough.side() ; ++i){
				for (int j = 0 ; j != dough.side() ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p))
							moves.add(new Move(si, ri, p));
					}
				}
			}
			if (moves.size() > 0)
				break;
		}
		// return a cut randomly
		Move rand_move = moves.get(gen.nextInt(moves.size()));
		if (!dough.uncut()) {
			last_move = rand_move;
		}
		return rand_move;
	}
}
