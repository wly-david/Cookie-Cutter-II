package cc2.g11;

import cc2.sim.Point;
import cc2.sim.Shape;
import cc2.sim.Dough;
import cc2.sim.Move;

import java.util.*;


public class Player implements cc2.sim.Player {

	private boolean[] row_2 = new boolean [0];
	private int[] row_2_pos;

	private Random gen = new Random();
	
	
	private int[][][] count0;
	private int[][][] opponent_count0;
	private ArrayList<HashSet<Integer>> set0;
	private ArrayList<HashSet<Integer>> opponent_set0;
	public Shape cutter(int length, Shape[] shapes, Shape[] opponent_shapes)
	{
		// check if first try of given cutter length
		Point[] cutter = new Point [length];
		if (row_2.length != cutter.length - 1) {
			// save cutter length to check for retries
			row_2 = new boolean [cutter.length - 1];
			row_2_pos = new int [cutter.length - 1];
			for(int i = 0 ;i < row_2.length/2; i ++) {
//				row_2_pos[2*i] = i;
//				row_2_pos[2*i+1] = row_2.length-1-i;
				row_2_pos[2*i] = row_2.length-1-i;
				row_2_pos[2*i+1] = i;
			}
			if (row_2.length%2 == 1)
				row_2_pos[row_2.length-1] = row_2.length/2;
			for (int i = 0 ; i != cutter.length ; ++i)
				cutter[i] = new Point(i, 0);
		} else {
			// pick a random cell from 2nd row but not same
			int i;
			int j=0;
			do {
				i = row_2_pos[j];
				j++;
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
		int side = dough.side();
		count0 =new int [side][side][shapes.length];
		opponent_count0 =new int [side][side][shapes.length];
		for (int si = 0 ; si != shapes.length ; ++si) {
			for (int i = 0 ; i != side ; ++i){
				for (int j = 0 ; j != side ; ++j) {
					Point q = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, q)){
							for (Point p : s)
								count0[p.i+q.i][p.j+q.j][si]++;
						}
					}
					rotations = opponent_shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, q)){
							for (Point p : s)
								opponent_count0[p.i+q.i][p.j+q.j][si]++;
						}
					}
				}
			}
		}
		
//		System.out.println("Printing my count for 11 shape ");
//		for(int i=0;i<side;i++) {
//			for(int j=0;j<side;j++) {
//				System.out.print(count0[i][j][0]+" ");
//			}
//			System.out.println();
//		}
//		
//		System.out.println("Printing opponents count for 11 shape ");
//		
//		System.out.println();
//		for(int i=0;i<side;i++) {
//			for(int j=0;j<side;j++) {
//				System.out.print(opponent_count0[i][j][0]+" ");
//			}
//			System.out.println();
//		}
//		System.out.println();
//		for(int i=0;i<side;i++) {
//		for(int j=0;j<side;j++) {
//			System.out.print(set0.get ( i * side +j).size()+" ");
//		}
//			System.out.println();
//		}
//		System.out.println();
		// prune larger shapes if initial move
		int minidx = -1;
		if (dough.uncut()) {
			int min = Integer.MAX_VALUE;
			for (Shape s : shapes)
				if (min > s.size())
					min = s.size();
			for (int s = 0 ; s != shapes.length ; ++s)
				if (shapes[s].size() == min)
					minidx = s;
		}
		// find all valid cuts
		ArrayList <Move> moves = new ArrayList <Move> ();
		

		double difference = Integer.MIN_VALUE;
		for (int si = 0 ; si != shapes.length ; ++si) {
			if (si != minidx && dough.uncut()) continue;
			for (int i = 0 ; i != side ; ++i){
				for (int j = 0 ; j != side ; ++j) {
					Point p = new Point(i, j);
					Shape[] rotations = shapes[si].rotations();
					for (int ri = 0 ; ri != rotations.length; ++ri) {
						Shape s = rotations[ri];
						if (dough.cuts(s, p)) {
							double sum = s.size();
//							double sum = 0;							
//							HashSet<Integer> s0 = new HashSet<Integer>();
//							HashSet<Integer> s1 = new HashSet<Integer>();
//							HashSet<Integer> s2 = new HashSet<Integer>();
//							HashSet<Integer> o0 = new HashSet<Integer>();
//							HashSet<Integer> o1 = new HashSet<Integer>();
//							HashSet<Integer> o2 = new HashSet<Integer>();
							for (Point q : s){
								sum -= count0[p.i+q.i][p.j+q.j][0]*11.0*11.0/s.size()/s.size();
								sum -= count0[p.i+q.i][p.j+q.j][1]*8.0*8.0/s.size()/s.size();
								sum -= count0[p.i+q.i][p.j+q.j][2]*5.0*5.0/s.size()/s.size();
								sum += opponent_count0[p.i+q.i][p.j+q.j][0];
								sum += opponent_count0[p.i+q.i][p.j+q.j][1];
								sum += opponent_count0[p.i+q.i][p.j+q.j][2];
								
//								int idx;
//								idx = 0 * side * side + (p.i+q.i) * side + p.j+q.j;
//								s0.addAll(set0.get(idx));
//								o0.addAll(opponent_set0.get(idx));
//								idx = 1 * side * side + (p.i+q.i) * side + p.j+q.j;
//								s1.addAll(set0.get(idx));
//								o1.addAll(opponent_set0.get(idx));
//								idx = 2 * side * side + (p.i+q.i) * side + p.j+q.j;
//								s1.addAll(set0.get(idx));
//								o2.addAll(opponent_set0.get(idx));
							}
//							sum = s.size() + o0.size()*11.0+o1.size()*8.0+o2.size()*5.0
//									- (s0.size()*11.0*11.0/s.size()/s.size()+s1.size()*8.0*8.0/s.size()/s.size()+s2.size()*5.0*5.0/s.size()/s.size());
							
							if (sum > difference){
								difference = sum;
								moves.clear();
								moves.add(new Move(si, ri, p));
								
							}
							else if (sum == difference){
								moves.add(new Move(si, ri, p));
							}
						}
					}
				}
			}
//			if (moves.size() > 0)
//				break;
		}
		// return a cut randomly
		
		Move rand_move = moves.get(gen.nextInt(moves.size()));
		return rand_move;
	}

}
