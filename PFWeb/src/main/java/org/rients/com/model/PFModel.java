package org.rients.com.model;

import java.util.ArrayList;

public class PFModel {
	private ArrayList<Modelregel> pfModel = new ArrayList<Modelregel>();
	
	public boolean isPlusOnDate(int date) {
		Modelregel last = null;
		for (Modelregel mr : pfModel) {
			if (mr.getDatumInt() > date) {
				if (last == null) {
					last = mr;
				}
				break;
			}
			last = mr;
		}
		if (last == null) {
			System.out.println("last is null");
		}
		return last.isStijger();
	}
	
	public Modelregel findNextPlus(int date) {
		Modelregel returnValue = null;
		for (Modelregel mr : pfModel) {
			if (mr.getDatumInt() >= date) {
				if (mr.isStijger()) {
					returnValue = mr;
					break;
				}
			}
		}
		return returnValue;
		
	}

	public ArrayList<Modelregel> getPfModel() {
		return pfModel;
	}

	public void setPfModel(ArrayList<Modelregel> pfModel) {
		this.pfModel = pfModel;
	}
}
