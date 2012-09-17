package rients.trading.download.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Dagkoers {
	
    public ArrayList<Dagkoers> sort(ArrayList<Dagkoers> data, boolean ascending) {
		Collections.sort((ArrayList<Dagkoers>) data, new DagkoersSort(ascending));
		return data;
	}

	public class DagkoersSort implements Comparator<Dagkoers> {
		public DagkoersSort(boolean ascending) {
			this.ascending = ascending;
		}

		boolean ascending = true;

		public int compare(Dagkoers obj1, Dagkoers obj2) {
			Float key1 = new Float(obj1.getDifference());
			Float key2 = new Float(obj2.getDifference());
			if (ascending)
				return key1.compareTo(key2);
			else
				return key2.compareTo(key1);
		}

	}

	public static String separator = ",";

	public String fondsNaam;
	public String fondsCode;
	public String datum;
	public float openkoers;
	public float highkoers;
	public float lowkoers;
	public float closekoers;
    public float previouskoers;
	public long volume;
	public float avrlang;
	public float avrmiddel;
	public float avrkort;
	public float votalityShort;
	public float prevRate;
	private DagkoersStatus status = DagkoersStatus.DEFAULT; 
	public float difference;
	public float relativeKoers = 0f;

	public float getDifference() {

		float returnValue = 0f;
		if (prevRate != 0f) {
			returnValue = ((closekoers - prevRate) / prevRate) * 100;
		}
		this.difference = returnValue;
		return returnValue;
	}

	public float getAvrCloseRate() {
		return (prevRate + closekoers) / 2;
	}

	public Dagkoers() {
	}
	
    /**
     * @param datum
     * @param koers
     */
    public Dagkoers(String datum, float koers) {
        this.datum = datum;
        this.closekoers = koers;
    }

	public String toString() {
		return this.datum + separator + this.closekoers;
	}

	/**
	 * @return Returns the closekoers.
	 */
	public float getClosekoers() {
		return closekoers;
	}

	/**
	 * @param closekoers
	 *            The closekoers to set.
	 */
	public void setClosekoers(float closekoers) {
		this.closekoers = closekoers;
	}

	/**
	 * @return Returns the separator.
	 */
	public static String getSeparator() {
		return separator;
	}

	/**
	 * @param separator
	 *            The separator to set.
	 */
	public static void setSeparator(String separator) {
		Dagkoers.separator = separator;
	}

	/**
	 * @return Returns the avrkort.
	 */
	public float getAvrkort() {
		return avrkort;
	}

	/**
	 * @param avrkort
	 *            The avrkort to set.
	 */
	public void setAvrkort(float avrkort) {
		this.avrkort = avrkort;
	}

	/**
	 * @return Returns the avrlang.
	 */
	public float getAvrlang() {
		return avrlang;
	}

	/**
	 * @param avrlang
	 *            The avrlang to set.
	 */
	public void setAvrlang(float avrlang) {
		this.avrlang = avrlang;
	}

	/**
	 * @return Returns the avrmiddel.
	 */
	public float getAvrmiddel() {
		return avrmiddel;
	}

	/**
	 * @param avrmiddel
	 *            The avrmiddel to set.
	 */
	public void setAvrmiddel(float avrmiddel) {
		this.avrmiddel = avrmiddel;
	}

	/**
	 * @return Returns the datum.
	 */
	public String getDatum() {
		return datum;
	}
	
	   /**
     * @return Returns the datum.
     */
    public int getDatumInt() {
        return Integer.parseInt(datum);
    }

	/**
	 * @param datum
	 *            The datum to set.
	 */
	public void setDatum(String datum) {
		this.datum = datum;
	}

	/**
	 * @return Returns the fondsCode.
	 */
	public String getFondsCode() {
		return fondsCode;
	}

	/**
	 * @param fondsCode
	 *            The fondsCode to set.
	 */
	public void setFondsCode(String fondsCode) {
		this.fondsCode = fondsCode;
	}

	/**
	 * @return Returns the fondsNaam.
	 */
	public String getFondsNaam() {
		return fondsNaam;
	}

	/**
	 * @param fondsNaam
	 *            The fondsNaam to set.
	 */
	public void setFondsNaam(String fondsNaam) {
		this.fondsNaam = fondsNaam;
	}

	/**
	 * @return Returns the highkoers.
	 */
	public float getHighkoers() {
		return highkoers;
	}

	/**
	 * @param highkoers
	 *            The highkoers to set.
	 */
	public void setHighkoers(float highkoers) {
		this.highkoers = highkoers;
	}

	/**
	 * @return Returns the lowkoers.
	 */
	public float getLowkoers() {
		return lowkoers;
	}

	/**
	 * @param lowkoers
	 *            The lowkoers to set.
	 */
	public void setLowkoers(float lowkoers) {
		this.lowkoers = lowkoers;
	}

	/**
	 * @return Returns the openkoers.
	 */
	public float getOpenkoers() {
		return openkoers;
	}

	/**
	 * @param openkoers
	 *            The openkoers to set.
	 */
	public void setOpenkoers(float openkoers) {
		this.openkoers = openkoers;
	}

	/**
	 * @return Returns the prevRate.
	 */
	public float getPrevRate() {
		return prevRate;
	}

	/**
	 * @param prevRate
	 *            The prevRate to set.
	 */
	public void setPrevRate(float prevRate) {
		this.prevRate = prevRate;
	}

	/**
	 * @return Returns the volume.
	 */
	public long getVolume() {
		return volume;
	}

	/**
	 * @param volume
	 *            The volume to set.
	 */
	public void setVolume(long volume) {
		this.volume = volume;
	}

	/**
	 * @return Returns the votalityShort.
	 */
	public float getVotalityShort() {
		return votalityShort;
	}

	/**
	 * @param votalityShort
	 *            The votalityShort to set.
	 */
	public void setVotalityShort(float votalityShort) {
		this.votalityShort = votalityShort;
	}

	public DagkoersStatus getStatus() {
		return status;
	}

	public void setStatus(DagkoersStatus status) {
		this.status = status;
	}

    /**
     * @return the previouskoers
     */
    public float getPreviouskoers() {
        return previouskoers;
    }

    /**
     * @param previouskoers the previouskoers to set
     */
    public void setPreviouskoers(float previouskoers) {
        this.previouskoers = previouskoers;
    }

    /**
     * @return the relativeKoers
     */
    public float getRelativeKoers() {
        return relativeKoers;
    }

    /**
     * @param relativeKoers the relativeKoers to set
     */
    public void setRelativeKoers(float relativeKoers) {
        this.relativeKoers = relativeKoers;
    }
}
