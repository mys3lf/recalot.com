package com.recalot.model.rec.recommender.helper.matrix;

import java.io.Serializable;

import gnu.trove.map.TIntDoubleMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntDoubleHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntDoubleProcedure;
import gnu.trove.procedure.TIntObjectProcedure;

/**
 * A class that represents a n*m Matrix with double values optimized for space with sparse data.
 * Not set values are always 0
 * 
 * @author LL
 */

public class SparseDoubleMatrix implements Serializable, SparseMatrix<Double>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5094000563236604468L;
	TIntObjectMap<TIntDoubleMap> matrix;
	
	//dimensions, number of non 0 entries
	private int M, N, numberOfEntries;
	
	/**
	 * Creates a new matrix; the matrix has no capacity bounds.
	 */
	public SparseDoubleMatrix(){
		matrix = new TIntObjectHashMap<TIntDoubleMap>();
		numberOfEntries = 0;
	}
	
	/**
	 * Creates a new matrix; the matrix has no capacity bounds.
	 */
	public SparseDoubleMatrix(int m, int n){
		matrix = new TIntObjectHashMap<TIntDoubleMap>();
		M = m;
		N = n;
		numberOfEntries = 0;
	}
	
	/**
	 * Set the element in row i, column j to value b.
	 * @param i row index
	 * @param j column index
	 * @param b value
	 */
	public void set(int i, int j, Double b){
		TIntDoubleMap row = matrix.get(i);
		if (row == null){
			// Every not set entry is implicitly 0
			// don't waste space creating a new row
			if (b == 0) return;
//			System.out.println("new row and not b=0.");
//			System.out.println("not extist.");
			row = new TIntDoubleHashMap();
			matrix.put(i, row);
		}
		// Every not set entry is implicitly 0
		if (b == 0){
			// don't waste space inserting a 0
			if (row.get(j) == 0) return;
			// if an entry was already there
			// remove it instead of writing a 0
			row.remove(j);
			numberOfEntries--;
		} else {
			row.put(j, b);
			numberOfEntries++;
		}
		
	}
	
	/**
	 * Sets the value of the matrix boolean-style.
	 * @param i row index
	 * @param j column index
	 * @param b boolean value
	 */
	public void setBool(int i, int j, boolean b){
		if (b) set(i,j,1.0);
		else set(i,j,0.0);
	}
	
	/**
	 * Gets the value at row i, column j; not yet set values are 0
	 * @param i
	 * @param j
	 * @return
	 */
	public Double get(int i, int j){
		TIntDoubleMap row = matrix.get(i);
		if (row != null){
			double value = row.get(j);
			return value;
		}
//		System.out.println("get not ex");
		return 0.0;
	}
	
	/**
	 * Gets the value at row i, column j as boolean
	 * @param i
	 * @param j
	 * @return
	 */
	public boolean getBool(int i, int j){
		TIntDoubleMap row = matrix.get(i);
		if (row != null){
			double value = row.get(j);
			return value!=0;
		}
//		System.out.println("get not ex");
		return false;
	}
	
	/**
	 * Gets the i'th column
	 * @param i
	 * @return
	 */
	public TIntDoubleMap getRow(int i){
		return matrix.get(i);
	}
	
	/**
	 * Returns an independent copy of the SparseMatrix
	 * @return a copy of the object
	 */
	public SparseDoubleMatrix copy(){
		SparseDoubleMatrix copy = new SparseDoubleMatrix();
		OuterRunner outerRunner = new OuterRunner(copy);
		matrix.forEachEntry(outerRunner);
		return copy;
	}
	
	/**
	 * Needed for copying.
	 */
	private class OuterRunner implements TIntObjectProcedure<TIntDoubleMap>{

		SparseDoubleMatrix copy;
		
		
		OuterRunner(SparseDoubleMatrix copy){
			this.copy = copy;
		}
		
		@Override
		public boolean execute(int i, TIntDoubleMap row) {
			
			InnerRunner innerRunner = new InnerRunner(copy, i);
			
			return row.forEachEntry(innerRunner);
		}
		
	}
	
	/**
	 * Needed for copying.
	 */
	private class InnerRunner implements TIntDoubleProcedure{

		SparseDoubleMatrix copy;
		int i;
		
		InnerRunner(SparseDoubleMatrix copy, int i){
			this.copy = copy;
			this.i=i;
		}
		
		@Override
		public boolean execute(int j, double b) {
			copy.set(i, j, b);
			return true;
		}
		
	}

	@Override
	public int getM() {
		return M;
	}

	@Override
	public int getN() {
		return N;
	}
	
	@Override
	public String getType() {
		return "double";
	}
	
	@Override
	public int getNumberOfEntries() {
		return numberOfEntries;
	}
}
