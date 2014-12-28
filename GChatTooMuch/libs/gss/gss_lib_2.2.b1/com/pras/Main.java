package com.pras;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args){
		SpreadSheetFactory factory = SpreadSheetFactory.getInstance("androidnagssm2011.2@gmail.com", "samsung@123");
		
		ArrayList<SpreadSheet> spreadSheets = factory.getAllSpreadSheets();
		if(spreadSheets == null || spreadSheets.size() == 0){
			System.out.println("No SpreadSheets exit...");
			return;
		}
		
		SpreadSheet sp = spreadSheets.get(0);
		ArrayList<WorkSheet> wks = sp.getAllWorkSheets();
		
		if(wks == null || wks.size() == 0){
			System.out.println("No WorkSheets exit...");
			return;
		}
		
		for(int i=0; i<wks.size(); i++){
			WorkSheet wk = wks.get(i);
			String[] cols = wk.getColumns();
			if(cols == null)
				continue;
			for(int j=0; j<cols.length; j++)
				System.out.print(cols[j]+" ");
			System.out.println();
		}
	}
}
