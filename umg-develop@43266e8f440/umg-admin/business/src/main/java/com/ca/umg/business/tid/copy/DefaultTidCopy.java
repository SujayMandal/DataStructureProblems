/**
 * 
 */
package com.ca.umg.business.tid.copy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.exception.SystemException;
import com.ca.framework.core.util.KeyValuePair;
import com.ca.umg.business.constants.BusinessConstants;
import com.ca.umg.business.mapping.info.MappingDescriptor;
import com.ca.umg.business.mid.extraction.info.MappingViewInfo;
import com.ca.umg.business.mid.extraction.info.MappingViews;
import com.ca.umg.business.mid.extraction.info.MidIOInfo;
import com.ca.umg.business.mid.extraction.info.MidParamInfo;
import com.ca.umg.business.mid.extraction.info.ParamInfo;
import com.ca.umg.business.mid.extraction.info.TidIOInfo;
import com.ca.umg.business.mid.extraction.info.TidParamInfo;
import com.ca.umg.business.mid.extraction.info.TidSqlInfo;
import com.ca.umg.business.mid.mapping.MidMapper;
import com.google.common.collect.Maps;

/**
 * @author chandrsa
 * 
 */
@Named
@SuppressWarnings({ "PMD" })
public class DefaultTidCopy implements TidCopy {

    @Inject
    private MidMapper midMapper;
    
    @Inject
    private TidCopyHelper tidCopyHelper;

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultTidCopy.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.ca.umg.business.tid.copy.TidCopy#copyTid(com.ca.umg.business.mid.extraction.info.MidIOInfo,
     * com.ca.umg.business.mapping.info.MappingDescriptor)
     */
    @Override
    public MappingDescriptor copyTid(MidIOInfo midIOInfo, MappingDescriptor mappingDescriptor, List<TidSqlInfo> sqlInfos)
            throws BusinessException, SystemException {
        LOGGER.debug("Star copy TID");
        MappingDescriptor descriptor = null;
        TidIOInfo newTidIOInfo = null;
        MappingViews newMappingViews = null;
        Map<String, TidParamInfo> newTidParamMap = null;
        Map<String, List<String>> newMappingMap = null;
        Map<String, MidParamInfo> newMidParamMap = null;
        Map<String, MidParamInfo> exstngMidParamMap = null;
        Map<String, TidParamInfo> exstngTidParamMap = null;
        Map<String, List<String>> exstngMappingMap = null;
        KeyValuePair<Map<String, Boolean>, Map<String, Boolean>> matchResult = null;

        if (isInputValid(midIOInfo, mappingDescriptor)) {
            newTidIOInfo = new TidIOInfo();
            // creates the tidinput for destination with same as destination mid
            newTidIOInfo.copy(midIOInfo);
            //create default mapping of destination parent to parent
            newMappingViews = midMapper.createMappingViews(midIOInfo);
            //newMappingViews = new MappingViews();
            newMidParamMap = convertToMap(midIOInfo.getMidInput());
            newTidParamMap = convertToMap(newTidIOInfo.getTidInput());
            newMappingMap = createMapForMapViews(newMappingViews);
            //newMappingMap = new HashMap<String, List<String>>();

            exstngMidParamMap = convertToMap(mappingDescriptor.getMidTree().getMidInput());
            exstngTidParamMap = convertToMap(mappingDescriptor.getTidTree().getTidInput());
            exstngMappingMap = createMapForMapViews(mappingDescriptor.getTidMidMapping());

            matchResult = matchMids(newMidParamMap, exstngMidParamMap);

            if (matchResult != null && MapUtils.isNotEmpty(exstngMappingMap) && MapUtils.isNotEmpty(exstngTidParamMap)) {
            	// created new mid param name list for adding extra child mapping if present extra child is present in destination 
            	ArrayList<String> newMidParamNameList = tidCopyHelper.getNewMidParamNameList(newMidParamMap);
                checkMatchAndUpdate(newTidParamMap, newMappingMap, exstngTidParamMap, exstngMappingMap, matchResult, 
                		sqlInfos,newMidParamNameList, newMidParamMap);
                mergeMappings(newMappingMap, newMappingViews);
                mergeTidParams(newTidParamMap, newTidIOInfo);
            }
            descriptor = new MappingDescriptor();
            descriptor.setTidTree(newTidIOInfo);
            descriptor.setTidMidMapping(newMappingViews);
            descriptor.setMidTree(midIOInfo);
        }
        LOGGER.debug("End copy TID");
        return descriptor;
    }

    private void checkMatchAndUpdate(Map<String, TidParamInfo> newTidParamMap, Map<String, List<String>> newMappingMap,
            Map<String, TidParamInfo> exstngTidParamMap, Map<String, List<String>> exstngMappingMap,
            KeyValuePair<Map<String, Boolean>, Map<String, Boolean>> matchResult, List<TidSqlInfo> sqlInfos, 
            ArrayList<String> newMidParamNameList, Map<String, MidParamInfo> newMidParamMap) {
        Map<String, TidSqlInfo> toCopySql = new HashMap<>();
        Map<String, TidSqlInfo> existingSqlMap = prepareExstngSqlMap(sqlInfos);
        //sample for matchResult = new KeyValuePair<>(changedMap, unChangedMap)
        Set<Entry<String, List<String>>> existingMppingsEntryset = exstngMappingMap.entrySet();
        Iterator<Entry<String, List<String>>> exstngMappingIterator = existingMppingsEntryset.iterator();
        Map<String, Boolean> changedMap = matchResult.getKey();
        List<String> changedParent;
        while (exstngMappingIterator.hasNext()) {
        	changedParent = new ArrayList<>();
        	Entry<String, List<String>> exstngMappingEntry = exstngMappingIterator.next();
        	String exstngMappingMappedToParam = exstngMappingEntry.getKey();
        	if  (!StringUtils.contains(exstngMappingMappedToParam,BusinessConstants.SLASH) && changedMap.containsKey(exstngMappingMappedToParam)) {
        		//do nothing as default mapping is there for parent
        	} else if (!StringUtils.contains(exstngMappingMappedToParam,BusinessConstants.SLASH) && !changedMap.containsKey(exstngMappingMappedToParam)) { 
        		//added this below if check for extra params in source but not in destination mid 
        		if (newMidParamNameList.contains(exstngMappingMappedToParam)) {
        			newMappingMap.put(exstngMappingMappedToParam, exstngMappingEntry.getValue());
        		}
        	} else if (mappedToParentHasChanged(StringUtils.substringBeforeLast(exstngMappingMappedToParam, BusinessConstants.SLASH),
        			changedMap, changedParent)) {
        		// create mappings appropriately by checking any parents parent have changed 
        		createMappingsIfParentHasChanged (changedParent, changedMap,
        	    		 newMappingMap, exstngMappingMap); 
        	} else if (mappedToParamHasChanged(exstngMappingMappedToParam, changedMap)) {
        		//added this below if check for extra params in source but not in destination mid 
        		if (newMidParamNameList.contains(exstngMappingMappedToParam)) {
	        		int firstindex = StringUtils.indexOf(exstngMappingMappedToParam, BusinessConstants.SLASH);
	                String parent = StringUtils.substring(exstngMappingMappedToParam, BusinessConstants.NUMBER_ZERO, firstindex);
	                if (!exstngMappingMap.containsKey(parent)) {
	                	//remove the P-P default mapping if copied mapping does not have parent
	                	newMappingMap.remove(parent);
	                	List<String> defaultMappingListForChild = new ArrayList<>();
		        		defaultMappingListForChild.add(exstngMappingMappedToParam);
		        		newMappingMap.put(exstngMappingMappedToParam,defaultMappingListForChild );
	                }
        		}
        	} else {
        		//added this below if check for extra params in source but not in destination mid 
	        		if (newMidParamNameList.contains(exstngMappingMappedToParam)) {
	        		int firstindex = StringUtils.indexOf(exstngMappingMappedToParam, BusinessConstants.SLASH);
	                String parent = StringUtils.substring(exstngMappingMappedToParam, BusinessConstants.NUMBER_ZERO, firstindex);
	                if (!exstngMappingMap.containsKey(parent)) {
	                	newMappingMap.remove(parent);
	                }
	                newMappingMap.put(exstngMappingMappedToParam, exstngMappingEntry.getValue());
        		}
        	}
        	
        }
        
        createDefaultMappingForNewChild(newMappingMap, exstngMappingMap, matchResult);
        updateNewTidParam ( newTidParamMap,  newMappingMap, exstngTidParamMap,  changedMap, 
                newMidParamNameList,  newMidParamMap);
        addMissingParamToNewTidTree(newTidParamMap, newMappingMap, exstngTidParamMap);
        sqlInfos.clear();
        checkAndAccumalateSQL(matchResult.getKey(), existingSqlMap, toCopySql, newMidParamNameList);
        updateMappings(newMappingMap, toCopySql, existingSqlMap,matchResult.getKey());
        replaceParmInTidForSyndMapping(newTidParamMap,newMappingMap,toCopySql,exstngTidParamMap);
        if (MapUtils.isNotEmpty(toCopySql)) {
            sqlInfos.addAll(toCopySql.values());
        }
    }
    
    //updated the tid tree by replacing with existing tree if no changes in destination mid and 
    //nodes in source tid equals nodes in destination mid -- bug fix 3320
    private void updateNewTidParam (Map<String, TidParamInfo> newTidParamMap, Map<String, List<String>> newMappingMap,
            Map<String, TidParamInfo> exstngTidParamMap, Map<String, Boolean> changedMap, 
            ArrayList<String> newMidParamNameList, Map<String, MidParamInfo> newMidParamMap) {
    	List<String> exstngTidParamNames = tidCopyHelper.getTidParamNameList(exstngTidParamMap);
    	for (Entry<String, List<String>> newMapping : newMappingMap.entrySet()) {
    		for (String mappingParam : newMapping.getValue()) {
    			if (exstngTidParamNames.contains(mappingParam) && !isParamInChangedMap(mappingParam, changedMap)) {
    				if (!mappingParam.contains(BusinessConstants.SLASH)) {
    					/*TidParamInfo newTidParamToBeReplaced = newTidParamMap.get(mappingParam);
    					TidParamInfo exstngTidParamToBeCopied = exstngTidParamMap.get(mappingParam);
    					MidParamInfo midParam = newMidParamMap.get(mappingParam);*/
    					replaceInNewTidTree ( mappingParam,  newTidParamMap,
    				    		  exstngTidParamMap, newMidParamMap);
    				} else {
    					int firstindex = StringUtils.indexOf(mappingParam, BusinessConstants.SLASH);
    	                String parent = StringUtils.substring(mappingParam, BusinessConstants.NUMBER_ZERO, firstindex);
    	               /* TidParamInfo newTidParamToBeReplaced = newTidParamMap.get(parent);
    					TidParamInfo exstngTidParamToBeCopied = exstngTidParamMap.get(parent);*/
    					replaceInNewTidTree ( parent,  newTidParamMap,
  				    		  exstngTidParamMap, newMidParamMap);
    				}
    			}
    		}
    	}
    }
    
    private Boolean isParamInChangedMap (String param, Map<String, Boolean> changedMap) {
    	Boolean presentInChngdMap = Boolean.FALSE;
    	if (mappedToParamHasChanged(param, changedMap)) {
    		presentInChngdMap = Boolean.TRUE;
    	} else {
    		if (!param.contains(BusinessConstants.SLASH)) {
    			for (String changedKey : changedMap.keySet()) {
    				if (StringUtils.contains(changedKey, param)) {
    					presentInChngdMap = Boolean.TRUE;
    					break;
    				}
    			}
    		} else {
    			if (isParamsPrntInChangedMap(StringUtils.substringBeforeLast(param, BusinessConstants.SLASH),
    					changedMap)) {
    				presentInChngdMap = Boolean.TRUE;
    			}
    		}
    	}
    	return presentInChngdMap;
    }
    
    private Boolean isParamsPrntInChangedMap (String parentsOfKey, Map<String, Boolean> changedMap) {
    	Boolean prntPrsntInChngdMap = false;
    	if (changedMap.containsKey(parentsOfKey)) {
    		prntPrsntInChngdMap = true;
        } else {
        	if (StringUtils.contains(parentsOfKey, BusinessConstants.SLASH)) {
	        	String parent = StringUtils.substringBeforeLast(parentsOfKey, BusinessConstants.SLASH);
	        	prntPrsntInChngdMap = isParamsPrntInChangedMap (parent, changedMap);
        	}
        }
    	return prntPrsntInChngdMap;
    }
    
    private void replaceInNewTidTree (String paramToreplace, Map<String, TidParamInfo> newTidParamMap,
    		 Map<String, TidParamInfo> exstngTidParamMap, Map<String, MidParamInfo> newMidParamMap) {
    	TidParamInfo newTidParamToBeReplaced = newTidParamMap.get(paramToreplace);
		TidParamInfo exstngTidParamToBeCopied = exstngTidParamMap.get(paramToreplace);
		MidParamInfo midParam = newMidParamMap.get(paramToreplace);
		if (tidCopyHelper.getMidChildList(midParam).size() == 
				tidCopyHelper.getTidChildList(exstngTidParamToBeCopied).size()) {
			//newTidParamToBeReplaced = exstngTidParamToBeCopied;
			newTidParamMap.put(paramToreplace, exstngTidParamToBeCopied);
		}
    }
    
    //add the mappings if parent has changed or parents-parent has changed 
    private void createMappingsIfParentHasChanged (List<String> changedParent, Map<String, Boolean> changedMap,
    		Map<String, List<String>> newMappingMap, Map<String, List<String>> exstngMappingMap) {
    	String changedParen = changedParent.get(changedParent.size()-1);
		String parentChanged = changedParen;
		while (StringUtils.contains(changedParen, BusinessConstants.SLASH)) {
			changedParen = StringUtils.substringBeforeLast(changedParen, BusinessConstants.SLASH);
			if (mappedToParentHasChanged(changedParen, changedMap, changedParent)) {
				parentChanged = changedParent.get(changedParent.size()-1);
				if (StringUtils.contains(parentChanged, BusinessConstants.SLASH)) {
					changedParen = StringUtils.substringBeforeLast(parentChanged, 
    						BusinessConstants.SLASH);
					continue;
				} else {
					if (changedMap.containsKey(changedParen)) {
						// if parent is changed then put P-P defualt mapping 
						List<String> defaultMappingListForchangedParen = new ArrayList<>();
						defaultMappingListForchangedParen.add(changedParen);
		        		newMappingMap.put(changedParen,defaultMappingListForchangedParen);
		        		break;
					} else {
						//add mapping only if there is no P-P mapping in source
						if (!exstngMappingMap.containsKey(changedParen)) {
    						String mappingToAdd = changedParent.get(changedParent.size()-1);
    						List<String> defaultMappingListForchangedParen = new ArrayList<>();
    						defaultMappingListForchangedParen.add(mappingToAdd);
    		        		newMappingMap.put(mappingToAdd,defaultMappingListForchangedParen);
						}
						break;
					}
				}
			} else {
				if (!exstngMappingMap.containsKey(changedParen)) {
					String mappingToAdd = changedParent.get(changedParent.size()-1);
					List<String> defaultMappingListForchangedParen = new ArrayList<>();
					defaultMappingListForchangedParen.add(mappingToAdd);
	        		newMappingMap.put(mappingToAdd,defaultMappingListForchangedParen);
				}
				break;
			}
		}
    }
    
    // checks if the mapped to param has changed
    private Boolean mappedToParamHasChanged (String key, Map<String, Boolean> changedMap ) {
    	Boolean paramChanged = false;
    	if (changedMap.containsKey(key)) {
    		paramChanged = true;
    	}	
    	return paramChanged;
    }
    
    //returns the first parent which has changed   
    private Boolean mappedToParentHasChanged (String parentsOfKey, Map<String, Boolean> changedMap, 
    		List<String> changedParent ) {
    	Boolean parentChanged = false;
    	if (changedMap.containsKey(parentsOfKey)) {
        	parentChanged = true;
        	changedParent.add(parentsOfKey);
        } else {
        	if (StringUtils.contains(parentsOfKey, BusinessConstants.SLASH)) {
	        	String parent = StringUtils.substringBeforeLast(parentsOfKey, BusinessConstants.SLASH);
	        	parentChanged = mappedToParentHasChanged (parent, changedMap, changedParent);
        	}
        }
    	
    	return parentChanged;
    }
    
    //replaces the params in newtidtree from the exisitngtidtree which have the syndicate mappings in new-mapping-map after copy 
    private void replaceParmInTidForSyndMapping (Map<String, TidParamInfo> newTidParamMap, Map<String, List<String>> newMappingMap,
    		Map<String, TidSqlInfo> toCopySql, Map<String, TidParamInfo> exstngTidParamMap) {
    	List<String> paramsToReplaceInTid = new ArrayList<>();
    	TidParamInfo exstngTidParam = null;
    	TidParamInfo newTidParam = null;
    	for (String syndQueryName : toCopySql.keySet()) {
    		for (String mappingKey : newMappingMap.keySet()) {
    			List<String> mapppingList = newMappingMap.get(mappingKey);
    			for (String mapList :mapppingList) {
	    			if (mapList.contains(syndQueryName)) {
	    				paramsToReplaceInTid.add(mappingKey);
	    			}
    			}
    		}
    	}
    	
    	for (String paramToReplace : paramsToReplaceInTid) {
    		String parent = null;
    		if (paramToReplace.contains(BusinessConstants.SLASH)) {
	    		int firstindex = StringUtils.indexOf(paramToReplace, BusinessConstants.SLASH);
	            parent = StringUtils.substring(paramToReplace, BusinessConstants.NUMBER_ZERO, firstindex);
    		} else {
    			parent = paramToReplace;
    		}
            exstngTidParam = exstngTidParamMap.get(parent);
            TidParamInfo tidParamToReplace =  getAndReplaceTidTree(paramToReplace,exstngTidParam,
            		StringUtils.substringBeforeLast(paramToReplace, BusinessConstants.SLASH));
            newTidParam = newTidParamMap.get(parent);
    		tidCopyHelper.modifyNewTidTree ( newTidParam,  tidParamToReplace, 
    	   			 StringUtils.substringBeforeLast(paramToReplace, BusinessConstants.SLASH), Boolean.FALSE) ;
    	}
    }
    
    private TidParamInfo getAndReplaceTidTree (String paramToReplace, TidParamInfo exstngTidParam, String parent) {
    	TidParamInfo exstngTidParamInfo = null;
    	if (StringUtils.equals(exstngTidParam.getFlatenedName(), parent)) {
            if (exstngTidParam.getChildren() == null && StringUtils.equals(paramToReplace, exstngTidParam.getApiName())) {
                exstngTidParamInfo = exstngTidParam;
            } else {
    			for (TidParamInfo newTidParamChild : exstngTidParam.getChildren()) {
            		if (StringUtils.equals(paramToReplace, newTidParamChild.getFlatenedName())) {
            			exstngTidParamInfo = newTidParamChild;
            			break;
            		} 
            	}
            }
    	} else {
    		for (TidParamInfo child : exstngTidParam.getChildren()) {
    			String childsFaltName = child.getFlatenedName();
    			if (StringUtils.contains(parent, childsFaltName)) {
    				exstngTidParamInfo = getAndReplaceTidTree (paramToReplace, child,parent);
    				break;
    			}
    		}
    	}
    	return exstngTidParamInfo;
    }
    
  //added for extra child in destination
  //creating default C-C mapping for new child -- if new child is present in destination and C-C mapping is done in source
    private void createDefaultMappingForNewChild (Map<String,List<String>> newMappingMap, 
    		Map<String, List<String>> exstngMappingMap,  
    		KeyValuePair<Map<String, Boolean>, Map<String, Boolean>> matchResult) {
    	for (Entry<String, Boolean> changedMidEntry : matchResult.getKey().entrySet()) {
    		String changedMidEntryKey = changedMidEntry.getKey();
    		//check for if changed map contains extra params in new mid for which default mapping is required
    		if (changedMidEntryKey.contains(BusinessConstants.SLASH)) {
	    		 int firstindex = StringUtils.indexOf(changedMidEntryKey, BusinessConstants.SLASH);
	             String parent = StringUtils.substring(changedMidEntryKey, BusinessConstants.NUMBER_ZERO, firstindex);
	             //existing map does not contain mapping for changed key and new mapping does not have P-P mapped
	    		if (!exstngMappingMap.containsKey(changedMidEntryKey) && !newMappingMap.containsKey(parent)) {
	    			//mapping does not exist for this in new mapping so adding in new mappingmap
	    			if (!newMappingMap.containsKey(changedMidEntryKey)) {
	    				List<String> mapping = new ArrayList<>();
	    				mapping.add(changedMidEntryKey);
	    				newMappingMap.put(changedMidEntryKey, mapping);
	    			} else {
	    				List<String> mapping = newMappingMap.get(changedMidEntryKey);
	    				if (!mapping.contains(changedMidEntryKey)) {
	    					mapping.add(changedMidEntryKey);
	    				}
	    			}
	    		}
    		}
    	}
    }
    
    //adds the missing params to new tid tree where mapping is present and changes the tidtree for mapped 
    // params if necessary
    private void addMissingParamToNewTidTree (Map<String, TidParamInfo> newTidParamMap, 
    		Map<String, List<String>> newMappingMap, Map<String, TidParamInfo> exstngTidParamMap) {
    	Map<String, String> tidParamFlatNameMap = tidCopyHelper.getTidParamFlatNameMap(newTidParamMap);
    	for (String newMappedToKey : newMappingMap.keySet()) {
			/*//if parent mapped then skip no new mapping required
    		if (newMappedToKey.contains(BusinessConstants.SLASH)) {*/
	    		List<String> newMappedParamList = newMappingMap.get(newMappedToKey);
	    		//{A/A1=[A/A1], A/A2=[A/A2, A/A3]} for e.g A/A2 and A/A3 consist of newmappedparamlist
	    		for (String newMappedParam : newMappedParamList) {
	    			if (!tidParamFlatNameMap.containsKey(newMappedParam)) {
	    				findAndReplaceInNewTidTree (newTidParamMap,exstngTidParamMap,newMappedParam,newMappedToKey,tidParamFlatNameMap);
	    			}
	    		}
    		//}
    	}
    }
    
    private void  findAndReplaceInNewTidTree (Map<String, TidParamInfo> newTidParamMap, 
    		Map<String, TidParamInfo> exstngTidParamMap, String newMappedParam, String exstngParamInTidTree,
    		Map<String, String> tidParamFlatNameMap) {
    	TidParamInfo exstngTidParam = null;
    	TidParamInfo newTidParam = null;
    	if (!newMappedParam.contains(BusinessConstants.SLASH)) {
    		exstngTidParam = exstngTidParamMap.get(newMappedParam);
    		//added this check for not null to not copy syndicate to tid tree
    		if (exstngTidParam != null) {
    			newTidParamMap.put(newMappedParam, exstngTidParam);
    		}
    	}else {
    		int firstindex = StringUtils.indexOf(newMappedParam, BusinessConstants.SLASH);
            String parent = StringUtils.substring(newMappedParam, BusinessConstants.NUMBER_ZERO, firstindex);
            exstngTidParam = exstngTidParamMap.get(parent);
          //added this check for not null to not copy syndicate to tid tree
            if (exstngTidParam != null) {
	            newTidParam = newTidParamMap.get(parent);
	            replaceAndAddParamInNewTidTree (exstngTidParam,  newTidParam,
	            		newMappedParam, exstngParamInTidTree,tidParamFlatNameMap);
            }
    	} 
    }
    
    private void  replaceAndAddParamInNewTidTree (TidParamInfo exstngTidParam, TidParamInfo newTidParam,
    		String newMappedParam, String exstngParamInTidTree, Map<String, String> tidParamFlatNameMap) {
    	TidParamInfo newTidParamInfoToAdd = null;
    	TidParamInfo exstngTidParamInfoToReplace = null;
    	String paramToBeAdded = null;
    	
    	String[] splitNewMappedParam = StringUtils.split(newMappedParam, BusinessConstants.SLASH);
    	
    	for (String param : splitNewMappedParam) {
    		if (!tidParamFlatNameMap.containsValue(param)) {
    			paramToBeAdded = param;
    		}
    	}
    	
    	for (TidParamInfo exstngTidParamChild : exstngTidParam.getChildren()) {
    		if (StringUtils.equals(paramToBeAdded, exstngTidParamChild.getApiName())) {
    			newTidParamInfoToAdd = exstngTidParamChild;
    		} else if (StringUtils.equals(exstngParamInTidTree, exstngTidParamChild.getFlatenedName())) {
    			exstngTidParamInfoToReplace = exstngTidParamChild;
    		}
    	}
    	
    	tidCopyHelper.modifyNewTidTree (newTidParam,newTidParamInfoToAdd,StringUtils.substringBeforeLast(newMappedParam, 
    			BusinessConstants.SLASH), Boolean.TRUE);
    	tidCopyHelper.modifyNewTidTree ( newTidParam,  exstngTidParamInfoToReplace, 
   			 StringUtils.substringBeforeLast(exstngParamInTidTree, BusinessConstants.SLASH), Boolean.FALSE) ;
    }
    
    //updates the mapping for copied queries
    private void updateMappings(Map<String, List<String>> newMappingMap, Map<String, TidSqlInfo> toCopySql,
            Map<String, TidSqlInfo> existingSqlMap, Map<String,Boolean> changedMap) {
    	 Map<String, TidSqlInfo> rejectedSqls = Maps.difference(existingSqlMap, toCopySql).entriesOnlyOnLeft();
         Set<Entry<String, List<String>>> newMppingsEntryset = newMappingMap.entrySet();
         //this loop removes all mapping for not copied syndicate queries
         for (String mappingToRemove : rejectedSqls.keySet()) {
             Iterator<Entry<String, List<String>>> mappingIterator = newMppingsEntryset.iterator();
             while (mappingIterator.hasNext()) {
                 Entry<String, List<String>> entry = mappingIterator.next();
                 Iterator<String> mappedParamItr = entry.getValue().iterator();
                 while (mappedParamItr.hasNext()) {
                     String mapping = mappedParamItr.next();
                     if (StringUtils.contains(mapping, mappingToRemove)) {
                         mappedParamItr.remove();
                     }
                 }

             }
         }
         removeSyndQueriesIfNoMapping (toCopySql, newMappingMap);
    }
    
    //removes the syndicate queries when no mapping are present
    private void removeSyndQueriesIfNoMapping ( Map<String, TidSqlInfo> toCopySql, Map<String, List<String>> newMappingMap) {
    	Set<Entry<String, TidSqlInfo>> toCopySqlEntrySet = toCopySql.entrySet();
        Iterator<Entry<String, TidSqlInfo>> toCopySqlIterator = toCopySqlEntrySet.iterator();
        Set<Entry<String, List<String>>> newMppingsEntryset = newMappingMap.entrySet();
        Boolean usedInMapping ;
       while (toCopySqlIterator.hasNext()) {
       	usedInMapping = Boolean.FALSE;
       	Iterator<Entry<String, List<String>>> newMappingIterator = newMppingsEntryset.iterator();
       	Entry<String, TidSqlInfo> toCopySqlentry = toCopySqlIterator.next();
       	String sqlKeyToSearch = toCopySqlentry.getKey();
       	while (newMappingIterator.hasNext()) {
               Entry<String, List<String>> entry = newMappingIterator.next();
               Iterator<String> mappedParamItr = entry.getValue().iterator();
               while (mappedParamItr.hasNext()) {
                   String mapping = mappedParamItr.next();
                   if (StringUtils.contains(mapping,sqlKeyToSearch )) {
                   	usedInMapping = Boolean.TRUE;
                   	break;
                   }
               }
               if (usedInMapping) {
               	break;
               }
           }
       	if (!usedInMapping) {
       		toCopySqlIterator.remove();
       	}
       }
    }
    

    private boolean isInputValid(MidIOInfo midIOInfo, MappingDescriptor mappingDescriptor) {
        return midIOInfo != null && midIOInfo.getMidInput() != null && mappingDescriptor != null
                && mappingDescriptor.getTidTree() != null && mappingDescriptor.getMidTree() != null
                && mappingDescriptor.getTidMidMapping() != null && mappingDescriptor.getTidTree().getTidInput() != null
                && mappingDescriptor.getMidTree().getMidInput() != null
                && mappingDescriptor.getTidMidMapping().getInputMappingViews() != null;
    }

    private <V> Map<String, V> convertToMap(List<V> values) {
        Map<String, V> map = null;
        ParamInfo paramInfo = null;
        if (CollectionUtils.isNotEmpty(values)) {
            map = new HashMap<>();
            for (V val : values) {
                paramInfo = (ParamInfo) val;
                map.put(paramInfo.getApiName(), val);
            }
        }
        return map;
    }

    //prepares the changed and unchanged map
    private KeyValuePair<Map<String, Boolean>, Map<String, Boolean>> matchMids(Map<String, MidParamInfo> newMidParamMap,
            Map<String, MidParamInfo> exstngMidParamMap) {
        KeyValuePair<Map<String, Boolean>, Map<String, Boolean>> matchResult = null;
        Map<String, Boolean> changedMap = null;
        Map<String, Boolean> unChangedMap = null;
        Map<String, MidParamInfo> differenceMap = null;
        if (MapUtils.isNotEmpty(newMidParamMap) && MapUtils.isNotEmpty(exstngMidParamMap)) {
            changedMap = new HashMap<String, Boolean>();
            unChangedMap = new HashMap<String, Boolean>();
            differenceMap = Maps.difference(exstngMidParamMap, newMidParamMap).entriesOnlyOnLeft();
            if (MapUtils.isNotEmpty(differenceMap)) {
                for (Entry<String, MidParamInfo> existingMidParam : differenceMap.entrySet()) {
                    changedMap.put(existingMidParam.getKey(), BusinessConstants.TRUE);
                }
            }
            for (Entry<String, MidParamInfo> newMidParam : newMidParamMap.entrySet()) {
            	matchParams (newMidParam.getValue(), exstngMidParamMap.get(newMidParam.getKey()), 
            			changedMap, unChangedMap); 
            }
            matchResult = new KeyValuePair<>(changedMap, unChangedMap);
        }
        return matchResult;
    }

    private void matchParams (MidParamInfo midParamNew, MidParamInfo midParamOld, 
    		Map<String, Boolean> changedMap, Map<String, Boolean> unChangedMap) {
        if (midParamNew != null && midParamOld != null && !midParamNew.equals(midParamOld)) {
        	changedMap.put(midParamNew.getFlatenedName(), BusinessConstants.TRUE);
        } else {
        	unChangedMap.put(midParamNew.getFlatenedName(), BusinessConstants.TRUE);
        }
        if (CollectionUtils.isNotEmpty(midParamNew.getChildren())
                && (midParamOld != null && CollectionUtils.isNotEmpty(midParamOld.getChildren()))) {
        	MidParamInfo midParamOldChild = null;
        	for (MidParamInfo midParamNewChild : midParamNew.getChildren()) {
        		midParamOldChild = getChildFromOldParent(midParamNewChild.getApiName(), midParamOld);
        		if (midParamOldChild != null) {
        			matchParams(midParamNewChild,midParamOldChild,changedMap,unChangedMap);
        		} else {
        			changedMap.put(midParamNewChild.getFlatenedName(), BusinessConstants.TRUE);
        		}
        	}
        }
    }
    
    private MidParamInfo getChildFromOldParent (String newChildName, MidParamInfo midParamOld) {
    	MidParamInfo midParamOldChild = null;
    	for (MidParamInfo midParamInfoOldChild : midParamOld.getChildren()) {
    		if (StringUtils.equals(newChildName, midParamInfoOldChild.getApiName())) {
    			midParamOldChild = midParamInfoOldChild;
    			break;
    		}
    	}
    	return midParamOldChild;
    }

    private Map<String, List<String>> createMapForMapViews(MappingViews tidMidMapping) {
        Map<String, List<String>> mappingMap = null;
        List<String> inputs = null;
        if (tidMidMapping != null && CollectionUtils.isNotEmpty(tidMidMapping.getInputMappingViews())) {
            mappingMap = new HashMap<String, List<String>>();
            for (MappingViewInfo viewInfo : tidMidMapping.getInputMappingViews()) {
                if (mappingMap.containsKey(viewInfo.getMappedTo())) {
                    mappingMap.get(viewInfo.getMappedTo()).add(viewInfo.getMappingParam());
                } else {
                    inputs = new ArrayList<>();
                    inputs.add(viewInfo.getMappingParam());
                    mappingMap.put(viewInfo.getMappedTo(), inputs);
                }
            }
        }
        return mappingMap;
    }

    //copies the synd queries 
    private void checkAndAccumalateSQL(Map<String, Boolean> changedMap, 
    		Map<String, TidSqlInfo> existingSqlMap, Map<String, TidSqlInfo> toCopySql, ArrayList<String> newMidParamNameList) {
    	if (MapUtils.isNotEmpty(changedMap)) {
	        Map<String, TidSqlInfo> tmpCopySql = new HashMap<String, TidSqlInfo>();
	        for (Entry<String, TidSqlInfo> existingSql : existingSqlMap.entrySet()) {
	        	canCopyExistingSql(existingSql.getKey(), existingSql.getValue(), changedMap, tmpCopySql, existingSqlMap, 
	        			 newMidParamNameList);
	            toCopySql.putAll(tmpCopySql);
	            tmpCopySql.clear();
	        }
    	} else {
    		toCopySql.putAll(existingSqlMap);
    	}
    }
    
    //copis the syndicate queries by calling to check if no input params have changed 
    private void canCopyExistingSql(String sqlParentName, TidSqlInfo sqlInfo, Map<String, Boolean> changedMap,
            Map<String, TidSqlInfo> tmpCopySql, Map<String, TidSqlInfo> existingSqlMap, ArrayList<String> newMidParamNameList) {
        List<TidParamInfo> sqlInputs = null;
        if (StringUtils.isNotBlank(sqlParentName) && sqlInfo != null) {
            sqlInputs = sqlInfo.getInputParams();
            if (CollectionUtils.isNotEmpty(sqlInputs)) {
            	if(canCopyExisting(getAllSqlInputNames(sqlInputs),changedMap,sqlParentName,existingSqlMap, newMidParamNameList)) {
            		tmpCopySql.put(sqlParentName, sqlInfo);
            	}
            } else {
                tmpCopySql.put(sqlParentName, sqlInfo);
            }
        }
    }
    
    //returns true if any of the none of input params have changed 
    private Boolean canCopyExisting(List<String> sqlInputNames, Map<String, Boolean> changedMap, 
    		String sqlParentName, Map<String, TidSqlInfo> existingSqlMap, ArrayList<String> newMidParamNameList ) {
        boolean copyAllowed = Boolean.TRUE;
        for (String inputName : sqlInputNames) {
        	//check if the changed map has exact entry in sql input params  
        	if (changedMap.containsKey(inputName)) {
        		copyAllowed = Boolean.FALSE;
        		break;
            } else if (existingSqlMap.containsKey(inputName)) {
            	List<TidParamInfo> sqlInputs = existingSqlMap.get(inputName).getInputParams();
            	if (!canCopyExisting(getAllSqlInputNames(sqlInputs),changedMap,inputName,existingSqlMap, newMidParamNameList)) {
            		copyAllowed = Boolean.FALSE;
            		break;
            	}
            } else if (!newMidParamNameList.contains(inputName)) { //added to check if input param is not present in new mid at all -- bug-3312
            	copyAllowed = Boolean.FALSE;
        		break;
            }
        	
        	//check if the parent of any input param is present in changed map
        	if (StringUtils.contains(inputName, BusinessConstants.SLASH)) {
        		String AllParentsOfInputName = StringUtils.substringBeforeLast(inputName, BusinessConstants.SLASH);
        		for (String parent : StringUtils.split(AllParentsOfInputName, BusinessConstants.SLASH)) {
        			if (changedMap.containsKey(parent)) {
        				copyAllowed = Boolean.FALSE;
        				break;
                    }
        		}
        	}
        }
        return copyAllowed;
    }
    
    private List<String> getAllSqlInputNames(List<TidParamInfo> sqlInputs) {
        List<String> sqlInputNames = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sqlInputs)) {
            for (TidParamInfo input : sqlInputs) {
                sqlInputNames.add(input.getFlatenedName());
            }
        }
        return sqlInputNames;
    }

    private MappingViews mergeMappings(Map<String, List<String>> updatedInputMapng, MappingViews newMappingViews) {
        List<MappingViewInfo> viewInfos = null;
        if (MapUtils.isNotEmpty(updatedInputMapng)) {
            viewInfos = new ArrayList<>();
            for (Entry<String, List<String>> updatedMapping : updatedInputMapng.entrySet()) {
                if (CollectionUtils.isNotEmpty(updatedMapping.getValue())) {
                    viewInfos.addAll(prepareMapping(updatedMapping.getKey(), updatedMapping.getValue()));
                }
            }
            newMappingViews.setInputMappingViews(viewInfos);
        }
        return newMappingViews;
    }

    private List<MappingViewInfo> prepareMapping(String mappedTo, List<String> inputMappings) {
        List<MappingViewInfo> viewInfos = new ArrayList<>();
        MappingViewInfo viewInfo = null;
        for (String input : inputMappings) {
            viewInfo = new MappingViewInfo();
            viewInfo.setMappedTo(mappedTo);
            viewInfo.setMappingParam(input);
            viewInfos.add(viewInfo);
        }
        return viewInfos;
    }

    private TidIOInfo mergeTidParams(Map<String, TidParamInfo> updatedTidParamMap, TidIOInfo tidIOInfo) {
        List<TidParamInfo> tidParamInfos = null;
        if (MapUtils.isNotEmpty(updatedTidParamMap)) {
            tidParamInfos = new ArrayList<>();
            for (Entry<String, TidParamInfo> tidEntry : updatedTidParamMap.entrySet()) {
                tidParamInfos.add(tidEntry.getValue());
            }
            tidIOInfo.setTidInput(tidParamInfos);
        }
        return tidIOInfo;
    }

    private Map<String, TidSqlInfo> prepareExstngSqlMap(List<TidSqlInfo> sqlInfos) {
        // Map<String, TidSqlInfo> existingSqlMap = null;
        Map<String, TidSqlInfo> existingSqlMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(sqlInfos)) {
            existingSqlMap = new HashMap<>();
            for (TidSqlInfo tidSqlInfo : sqlInfos) {
                existingSqlMap.put(tidSqlInfo.getSqlName(), tidSqlInfo);
            }
        }
        return existingSqlMap;
    }
}