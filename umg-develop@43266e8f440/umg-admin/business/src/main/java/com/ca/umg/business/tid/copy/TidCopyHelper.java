package com.ca.umg.business.tid.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;

@Named
@SuppressWarnings({ "PMD" })
public class TidCopyHelper {

	public TidCopyHelper() {
		// TODO Auto-generated constructor stub
	}
	
	public ArrayList<String> getNewMidParamNameList (Map<String, MidParamInfo> newMidParamMap) {
    	ArrayList<String> newMidParamNameList = new ArrayList<>();

			for (String midKey : newMidParamMap.keySet()) {
				MidParamInfo midParamParent = newMidParamMap.get(midKey);
				if (midParamParent.getChildren() != null) {
					newMidParamNameList.addAll(getMidChildList(midParamParent));
				} else {
					newMidParamNameList.add(midParamParent.getFlatenedName());
				}
			}
		return newMidParamNameList;
    }
	
    public ArrayList<String> getMidChildList(MidParamInfo midParamParent) {
    	ArrayList<String> childFlatOutputList = new ArrayList<>();
    	childFlatOutputList.add(midParamParent.getFlatenedName());
		List<MidParamInfo> midOutputParentChildList = midParamParent
				.getChildren();

		if (midOutputParentChildList != null) {
			for (MidParamInfo midChild : midOutputParentChildList) {
				if (midChild.getChildren() == null) {
					childFlatOutputList.add(midChild.getFlatenedName());
				} else {
					childFlatOutputList.addAll(getMidChildList(midChild));
				}
			}
		}

		return childFlatOutputList;
	}
    
    public ArrayList<String> getTidParamNameList (Map<String, TidParamInfo> tidParamMap) {
    	ArrayList<String> tidParamNameList = new ArrayList<>();

			for (String midKey : tidParamMap.keySet()) {
				TidParamInfo midParamParent = tidParamMap.get(midKey);
				if (midParamParent.getChildren() != null) {
					tidParamNameList.addAll(getTidChildList(midParamParent));
				} else {
					tidParamNameList.add(midParamParent.getFlatenedName());
				}
			}
		return tidParamNameList;
    }
    
    public ArrayList<String> getTidChildList(TidParamInfo tidParamParent) {
    	ArrayList<String> childFlatOutputList = new ArrayList<>();
    	
    	childFlatOutputList.add(tidParamParent.getFlatenedName());
		List<TidParamInfo> tidParentChildList = tidParamParent.getChildren();
		if (tidParentChildList != null) {
			for (TidParamInfo tidChild : tidParentChildList) {
				if (tidChild.getChildren() == null) {
					childFlatOutputList.add(tidChild.getFlatenedName());
				} else {
					childFlatOutputList.addAll(getTidChildList(tidChild));
				}
			}
		}
		return childFlatOutputList;
	}
    
    public Map<String, String> getTidParamFlatNameMap (Map<String, TidParamInfo> tidParamMap) {
    	Map<String,String> newTidParamNameMap = new HashMap<>();
			for (String midKey : tidParamMap.keySet()) {
				TidParamInfo tidParamParent = tidParamMap.get(midKey);
				if (tidParamParent.getChildren() != null) {
					newTidParamNameMap.put(tidParamParent.getFlatenedName(), tidParamParent.getApiName());
					newTidParamNameMap.putAll(getTidChildMap(tidParamParent));
				} else {
					newTidParamNameMap.put(tidParamParent.getFlatenedName(), tidParamParent.getApiName());
				}
			}
		return newTidParamNameMap;
    }
    
    private Map<String, String> getTidChildMap(TidParamInfo tidParamParent) {
    	Map<String, String> childFlatOutputMap = new HashMap<>();
		List<TidParamInfo> tidParentChildList = tidParamParent
				.getChildren();

		for (TidParamInfo midChild : tidParentChildList) {
			if (midChild.getChildren() == null) {
				childFlatOutputMap.put(midChild.getFlatenedName(), midChild.getApiName());
			} else {
				childFlatOutputMap.put(midChild.getFlatenedName(), midChild.getApiName());
				childFlatOutputMap.putAll(getTidChildMap(midChild));
			}
		}
		return childFlatOutputMap;
	}

    public void modifyNewTidTree(TidParamInfo tidParamInfoObj, TidParamInfo tidParamInfoForModification, 
    		String parentOfTidParamForModification, Boolean addParameter ) {
    	if (StringUtils.equals(tidParamInfoObj.getFlatenedName(), parentOfTidParamForModification)) {
    		if (addParameter) {
	    		if (tidParamInfoObj.getChildren() != null) {
	    			tidParamInfoObj.getChildren().add(tidParamInfoForModification);
	    		} else {
	    			List<TidParamInfo> newChildList = new ArrayList<>();
	    			newChildList.add(tidParamInfoForModification);
	    			tidParamInfoObj.setChildren(newChildList);
	    		}
    		} else {
    		    if (tidParamInfoObj.getChildren() == null && StringUtils.equals(tidParamInfoObj.getApiName(), tidParamInfoForModification.getApiName())) {
    		        tidParamInfoObj = tidParamInfoForModification;
                } else {
                    for (TidParamInfo newTidParamChild : tidParamInfoObj.getChildren()) {
                        if (StringUtils.equals(tidParamInfoForModification.getFlatenedName(), newTidParamChild.getFlatenedName())) {
                            int index = tidParamInfoObj.getChildren().indexOf(newTidParamChild);
                            tidParamInfoObj.getChildren().set(index, tidParamInfoForModification);
                            break;
                        }
                    }
                }
    		}
    	} else {
    		for (TidParamInfo child : tidParamInfoObj.getChildren()) {
    			String childsFaltName = child.getFlatenedName();
    			if (StringUtils.contains(parentOfTidParamForModification, childsFaltName)) {
    				modifyNewTidTree (child,tidParamInfoForModification,parentOfTidParamForModification,addParameter);
    				break;
    			}
    		}
    	}
    }
}
