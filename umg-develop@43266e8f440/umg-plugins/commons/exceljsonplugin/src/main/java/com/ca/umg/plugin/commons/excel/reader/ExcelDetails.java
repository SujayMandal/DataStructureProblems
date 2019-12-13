package com.ca.umg.plugin.commons.excel.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelDetails {
	
	private boolean hasCorrelationId;
	
	private boolean hasDuplicates;
	
	private boolean hasMissingCorrelationIds;
	
	private short correlationColumnIndex;
	
	private Map<String, List<Integer>> duplicateMap = new HashMap<String, List<Integer>>();
	
	private List<Integer> missingList = new ArrayList<Integer>();

	public boolean hasCorrelationId() {
		return hasCorrelationId;
	}

	public void setHasCorrelationId(boolean hasCorrelationId) {
		this.hasCorrelationId = hasCorrelationId;
	}

	public boolean hasDuplicates() {
		return hasDuplicates;
	}

	public void setHasDuplicates(boolean hasDuplicates) {
		this.hasDuplicates = hasDuplicates;
	}

	public boolean hasMissingCorrelationIds() {
		return hasMissingCorrelationIds;
	}

	public void setHasMissingCorrelationIds(boolean hasMissingCorrelationIds) {
		this.hasMissingCorrelationIds = hasMissingCorrelationIds;
	}

	/**
	 * This method returns a map of cellValue (key), List<'rownum'> (value). If the the size of the list is more than one, then it means there are 
	 * duplicate rows for the given cell value.
	 * @return
	 */
	public Map<String, List<Integer>> getDuplicateMap() {
		return duplicateMap;
	}

	public List<Integer> getMissingList() {
		return missingList;
	}

	public short getCorrelationColumnIndex() {
		return correlationColumnIndex;
	}

	public void setCorrelationColumnIndex(short correlationColumnIndex) {
		this.correlationColumnIndex = correlationColumnIndex;
	}
	
}