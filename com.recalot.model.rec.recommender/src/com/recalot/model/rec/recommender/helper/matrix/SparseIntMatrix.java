package com.recalot.model.rec.recommender.helper.matrix;

import java.io.Serializable;

import gnu.trove.map.TIntIntMap;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntObjectProcedure;

/**
 * A class that represents a n*m Matrix with integer values optimized for space with sparse data.
 * Not set values are always 0
 * 
 * @author LL
 */

public class SparseIntMatrix implements Serializable, SparseMatrix<Integer>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6478156641138087976L;
	TIntObjectMap<TIntIntMap> matrix;
	
	//dimensions, number of non 0 entries
	private int M, N, numberOfEntries;
	
	/**
	 * Creates a new matrix; the matrix has no capacity bounds.
	 */
	public SparseIntMatrix(){
		matrix = new TIntObjectHashMap<TIntIntMap>();
		numberOfEntries = 0;
	}
	
	/**
	 * Creates a new matrix; the matrix has no capacity bounds.
	 */
	public SparseIntMatrix(int m, int n){
		matrix = new TIntObjectHashMap<TIntIntMap>();
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
	public void set(int i, int j, Integer b){
		TIntIntMap row = matrix.get(i);
		if (row == null){
			// Every not set entry is implicitly 0
			// don't waste space creating a new row
			if (b == 0) return;
//			System.out.println("new row and not b=0.");
//			System.out.println("not extist.");
			row = new TIntIntHashMap();
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
		if (b) set(i,j,1);
		else set(i,j,0);
	}
	
	/**
	 * Gets the value at row i, column j; not yet set values are 0
	 * @param i
	 * @param j
	 * @return
	 */
	public Integer get(int i, int j){
		TIntIntMap row = matrix.get(i);
		if (row != null){
			int value = row.get(j);
			return value;
		}
//		System.out.println("get not ex");
		return 0;
	}

	/**
	 * Gets the value at row i, column j as boolean
	 * @param i
	 * @param j
	 * @return
	 */
	public boolean getBool(int i, int j){
		TIntIntMap row = matrix.get(i);
		if (row != null){
			int value = row.get(j);
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
	public TIntIntMap getRow(int i){
		return matrix.get(i);
	}
	
	/**
	 * Returns an independent copy of the SparseMatrix
	 * @return a copy of the object
	 */
	public SparseIntMatrix copy(){
		SparseIntMatrix copy = new SparseIntMatrix();
		OuterRunner outerRunner = new OuterRunner(copy);
		matrix.forEachEntry(outerRunner);
		return copy;
	}
	
	/**
	 * Needed for copying.
	 */
	private class OuterRunner implements TIntObjectProcedure<TIntIntMap>{
		SparseIntMatrix copy;
		OuterRunner(SparseIntMatrix copy){
			this.copy = copy;
		}
		@Override
		public boolean execute(int i, TIntIntMap row) {
			InnerRunner innerRunner = new InnerRunner(copy, i);
			return row.forEachEntry(innerRunner);
		}
	}
	
	/**
	 * Needed for copying.
	 */
	private class InnerRunner implements TIntIntProcedure{
		SparseIntMatrix copy;
		int i;
		InnerRunner(SparseIntMatrix copy, int i){
			this.copy = copy;
			this.i=i;
		}
		@Override
		public boolean execute(int j, int b) {
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
		return "int";
	}
	
	@Override
	public int getNumberOfEntries() {
		return numberOfEntries;
	}
}
