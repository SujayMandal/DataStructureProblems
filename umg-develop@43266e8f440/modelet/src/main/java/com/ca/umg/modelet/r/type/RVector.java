/*
 * RVector.java
 * Author: Manasi Seshadri (manasi.seshadri@altisource.com)
 * 
 * -----------------------------------------------------------
 * Copyright 2014 Altisource - Consumer Analytics
 * All rights reserved.
 * Altisource PROPRIETARY/CONFIDENTIAL.
 * -----------------------------------------------------------
 */
package com.ca.umg.modelet.r.type;

import com.ca.framework.core.exception.BusinessException;
import com.ca.framework.core.ioreduce.BooleanValueEnum;
import com.ca.framework.core.ioreduce.FieldInfoEnum;
import com.ca.umg.modelet.common.FieldInfo;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.lang.type.DataType;
import org.rosuda.REngine.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

/**
 * JSON representation HashMap - Required - data, type, rDataType
 * 
 * "payload":[ { "fieldName":"inVector", "sequence":1, "dataType":"vector",
 * "collection":false, "value": { "data": [ "life", "is", null, "beautiful"
 * 
 * ], "type": "character", "rDataType":"vector" } } ]
 * 
 * "payload":[ { "fieldName":"inVector", "sequence":1, "dataType":"character",
 * "collection":true, "value": [ "life", "is", null, "beautiful"]
 * 
 * } ]
 * 
 * R representation c("abc", "def", "ghi", "jkl") for vectors of size > 0 vector
 * ('character') for empty vectors
 * 
 * **/
@SuppressWarnings("PMD")
public class RVector extends AbstractRType {
	private static final Logger LOGGER = LoggerFactory.getLogger(RVector.class);

	private List<DataType> vector;
	private final RDataTypes elementDataType;

	/**
	 * Creates a new RVector object.
	 * 
	 * @param objs
	 *            - List of component objects
	 * @param elementType
	 *            - data type of components
	 * 
	 * @throws BusinessException
	 **/
	public RVector(final List<Object> objs, final RDataTypes elementType) throws BusinessException {
		super();
		elementDataType = elementType;
		createVector(objs);
	}

	/**
	 * Creates vector from list of objects
	 * 
	 * @param objs
	 *            - List of objects
	 * 
	 * @throws BusinessException
	 *             - if elementDataType does not match type of components
	 * 
	 **/
	private void createVector(final List<Object> objs) throws BusinessException {
		vector = new ArrayList<DataType>();
		boolean isRDataTypes = false;
		if (objs != null && objs.size() > 0) {
			for (Object obj : objs) {
				
				if (obj == null || obj.toString().equalsIgnoreCase("-2147483648")) {
					vector.add(null);
				} 
				else{
				AbstractRType element;
				if(obj instanceof List){
					List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) obj;
					for(int i = 0 ; i < list.size() ; i ++ ){
						HashMap<String, Object> hm = (HashMap<String, Object>) list.get(i);
					
							element = AbstractRType.createRDataTypeFromObject(new FieldInfo(hm));
							  isRDataTypes = false;
						
						if (element.getRDataType() == elementDataType) {
							isRDataTypes = true;
							vector.add(element);
						} else {
							// MS: if this is a mix of RNumerics and RIntegers, it comes
							// here because few items are returned as RIntegers which is
							// not equal to "numeric" vector type.
							// For this one case only, convert any RIntegers to
							// RNumerics if the vector type is numeric.
							// No other datatypes are auto converted, for everything
							// else, throw an exception
							if (element instanceof RInteger && elementDataType == RDataTypes.R_NUMERIC) {
								isRDataTypes = true;
	                            if(element.getPrimitive() instanceof Integer) {
	                                element = new RNumeric(new Double((Integer)element.getPrimitive()));
	                            }
	                            else if(element.getPrimitive() instanceof Double) {
	                                element = new RNumeric((Double)element.getPrimitive());
	                            } 
	                            else if(element.getPrimitive() instanceof Long) {
	                                element = new RNumeric((Long)element.getPrimitive());
	                            }
	                            else if(element.getPrimitive() instanceof BigDecimal) {
	                                element = new RNumeric((BigDecimal)element.getPrimitive());
	                            }
	                            else if(element.getPrimitive() instanceof BigInteger) {
	                                element = new RNumeric(new BigDecimal((BigInteger)element.getPrimitive()));
	                            } 
	                            
	                           

								//int intValue = ((Integer) element.getPrimitive()).intValue();
	                            //element = new RNumeric((Double)element.getPrimitive()); // checked cast so I know what I'm doing
								
								vector.add(element);
							} 
							else if(element.getPrimitive() instanceof String && ! isRDataTypes) {
	                             element = new RCharacter(new String((String)element.getPrimitive()));
	                             vector.add(element);
	                         } 
							 
							 else if(element instanceof RList) {
	                             element = new RList(objs, isRDataTypes);
	                             vector.add(element);
	                         } 
							 
							 else if(element.getPrimitive() == null) {
								FieldInfo fieldInfo =  new FieldInfo(objs);
								List<Object> fieldInfoList= (List<Object>) fieldInfo.getValue();
								for (Object fInfo : fieldInfoList) {
									if (fInfo == null) {
										vector.add(null);
									} 
									if(fInfo instanceof List){
										List<HashMap<String, Object>> list1 = (List<HashMap<String, Object>>) fInfo;
										for(int j = 0 ; j < list.size() ; j ++ ){
											 HashMap<String, Object> hm1 = (HashMap<String, Object>) list1.get(j);
											 element =new RList(hm1);
											 vector.add(element);
										 } 
									 }
								}
							 }
							
							else {
							    LOGGER.debug("In RVector element is not integer or numeric");
								throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "Expected Numeric data type, but recevied " + elementDataType + ". Value is " + obj});
							}
						}
					}
				}
				else {
					
					if (obj instanceof AbstractRType) {
						element = (AbstractRType) obj;
					} else {
						element = AbstractRType.createRDataTypeFromObject(new FieldInfo(obj));
						isRDataTypes = false;
					}
	
					if (element.getRDataType() == elementDataType) {
						isRDataTypes = true;
						vector.add(element);
					} else {
						// MS: if this is a mix of RNumerics and RIntegers, it comes
						// here because few items are returned as RIntegers which is
						// not equal to "numeric" vector type.
						// For this one case only, convert any RIntegers to
						// RNumerics if the vector type is numeric.
						// No other datatypes are auto converted, for everything
						// else, throw an exception
						if (element instanceof RInteger && elementDataType == RDataTypes.R_NUMERIC) {
							isRDataTypes = true;
                            if(element.getPrimitive() instanceof Integer) {
                                element = new RNumeric(new Double((Integer)element.getPrimitive()));
                            }
                            else if(element.getPrimitive() instanceof Double) {
                                element = new RNumeric((Double)element.getPrimitive());
                            } 
                            else if(element.getPrimitive() instanceof Long) {
                                element = new RNumeric((Long)element.getPrimitive());
                            }
                            else if(element.getPrimitive() instanceof BigDecimal) {
                                element = new RNumeric((BigDecimal)element.getPrimitive());
                            }
                            else if(element.getPrimitive() instanceof BigInteger) {
                                element = new RNumeric(new BigDecimal((BigInteger)element.getPrimitive()));
                            } 
                            
                           

							//int intValue = ((Integer) element.getPrimitive()).intValue();
                            //element = new RNumeric((Double)element.getPrimitive()); // checked cast so I know what I'm doing
							
							vector.add(element);
						} 
						else if(element.getPrimitive() instanceof String && ! isRDataTypes) {
                             element = new RCharacter(new String((String)element.getPrimitive()));
                             vector.add(element);
                         } 
						 
						 else if(element.getPrimitive() == null) {
							FieldInfo fieldInfo =  new FieldInfo(objs);
							List<Object> fieldInfoList= (List<Object>) fieldInfo.getValue();
							for (Object fInfo : fieldInfoList) {
								if (fInfo == null) {
									vector.add(null);
								} 
								if(fInfo instanceof List){
									List<HashMap<String, Object>> list = (List<HashMap<String, Object>>) fInfo;
									for(int i = 0 ; i < list.size() ; i ++ ){
										 HashMap<String, Object> hm = (HashMap<String, Object>) list.get(i);
										 element =new RList(hm);
										 vector.add(element);
									 } 
								 }
							}
						 }
						
						else {
						    LOGGER.debug("In RVector element is not integer or numeric");
							throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { getClass().getName(), "Expected Numeric data type, but recevied " + elementDataType + ". Value is " + obj});
						}
					}
				}
			}
		} 
		}else {
			vector.add(null);
		}
	}

	/**
	 * Returns size of the RVector
	 * 
	 * @return integer
	 **/
	public int size() {
		int retVal = 0;
		if (vector != null) {
			retVal = vector.size();
		}
		return retVal;
	}

	/**
	 * Internal list of datatypes of this RVector
	 * 
	 * @return List<DataType>
	 **/
	public List<DataType> getVector() {
		return vector;
	}

	/**
	 * Creates a new RVector object.
	 * 
	 * @param output
	 *            - REXP from Rengine.eval
	 * @param elementType
	 *            - RDataType of components
	 * 
	 * @throws BusinessException
	 *             - if component data type is unsupported
	 * 
	 **/
	public RVector(final REXP output, final RDataTypes elementType) throws BusinessException {
		super();
		List<Object> objList = new ArrayList<Object>();
		elementDataType = elementType;
		
		LOGGER.debug("Entered RVector elementType {} for output is {}  : ",elementType,output);

        try {
            switch (elementType) {
                case R_NUMERIC:

                    double[] dblArray = output.asDoubles();

                    for (double element : dblArray) {
                        if (Double.isNaN(element))
                            objList.add(null);
                        else
                            objList.add(BigDecimal.valueOf(element));
                        //objList.add(element);
                    }

                    break;

                case R_INTEGER:

                    int[] intArray = output.asIntegers();

                    for (int element : intArray) {
                        if (element == Integer.MIN_VALUE)
                            objList.add(null);
                        else
                            objList.add(element);
                    }

                    break;

                case R_CHARACTER:

                    String[] strArray = output.asStrings();

                    for (String element : strArray) {
                        if (element != null && !element.equals("NA") && !element.equals("NA_character_"))
                            objList.add(element);
                        else
                            objList.add(null);
                    }

                    break;

                case R_LOGICAL:

                    int[] intBArray = output.asIntegers();

                    for (int element : intBArray) {
                        if (element != 0 && element != 1) {
                            objList.add(null);
                        } else {
                            objList.add(element == 1 ? true : false);
                        }
                    }

                    break;

                default:
                    throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[]{"R", elementType.getName()});
            }
        } catch(Exception e) {
            throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[]{"R", elementType.getName()});
        }

		createVector(objList);
	}
	
	public RVector(final org.rosuda.JRI.REXP output, final RDataTypes elementType) throws BusinessException {
		super();
		List<Object> objList = new ArrayList<Object>();
		elementDataType = elementType;
		
		LOGGER.debug("Entered RVector elementType {} for output is {}  : ",elementType,output);

		switch (elementType) {
			case R_NUMERIC:

				double[] dblArray = output.asDoubleArray();

				for (double element : dblArray) {
					if (Double.isNaN(element))
						objList.add(null);
					else
						objList.add(BigDecimal.valueOf(element));
                    //objList.add(element);
                }

				break;

			case R_INTEGER:

				int[] intArray = output.asIntArray();

				for (int element : intArray) {
					if (element == Integer.MIN_VALUE)
						objList.add(null);
					else
						objList.add(element);
				}

				break;

			case R_CHARACTER:

				String[] strArray = output.asStringArray();

				for (String element : strArray) {
					if (element != null && !element.equals("NA") && !element.equals("NA_character_"))
						objList.add(element);
					else
						objList.add(null);
				}

				break;

			case R_LOGICAL:

				int[] intBArray = output.asIntArray();

				for (int element : intBArray) {
					if (element != 0 && element != 1) {
						objList.add(null);
					} else {
						objList.add(element == 1 ? true : false);
					}
				}

				break;

			default:
				throw new BusinessException(ModeletExceptionCodes.MOBE000001, new String[] { "R", elementType.getName() });
		}

		createVector(objList);
	}

	/**
	 * Creates a new RVector object.
	 * 
	 * @param hmValue
	 *            - HashMap
	 * 
	 * @throws BusinessException
	 *             - if component type is not a string or is null - if data is
	 *             null or not of type List - if rDataType is null or
	 *             unsupported - is map is null - if component type does not
	 *             match with specified type
	 * 
	 **/
	// public RVector(Map<String, Object> hmValue)
	// throws BusinessException
	// {
	// super();
	// vector = new ArrayList<DataType>();
	// elementDataType = RDataTypes.R_LOGICAL; //MS:initializing to logical as
	// that is the R default for vector
	//
	// if (hmValue != null)
	// {
	// Object oObjType = hmValue.get(LangTypeConstants.R_VECTOR_TYPE);
	//
	// if (!(oObjType instanceof String))
	// {
	// throw new BusinessException(ModeletExceptionCodes.MOBE000002,
	// new String[] { getClass().getName(), LangTypeConstants.R_VECTOR_TYPE });
	// }
	// else
	// {
	// elementDataType = RDataTypes.getTypeEnumFromName((String)oObjType);
	// }
	//
	// Object oType = hmValue.get(LangTypeConstants.R_DATA_TYPE);
	// Object oData = hmValue.get(LangTypeConstants.R_VECTOR_DATA);
	//
	// if (oData instanceof List<?> &&
	// oType instanceof String && oType.equals(getRDataType().getName()))
	// {
	// List<Object> objs = (List<Object>)oData;
	//
	// for (Object obj : objs)
	// {
	// if (obj == null)
	// {
	// //MS: preserve nulls without type checking
	// vector.add(null);
	// }
	// else
	// {
	// AbstractRType rType =
	// (AbstractRType)AbstractRType.createRDataTypeFromObject(new
	// FieldInfo(obj));
	//
	// if (rType.getRDataType() == elementDataType)
	// {
	// vector.add(rType);
	// }
	// else
	// {
	// //MS: if this is a mix of RNumerics and RIntegers, it comes here because
	// few items are returned as RIntegers which is not equal to "numeric"
	// vector type.
	// // For this one case only, convert any RIntegers to RNumerics if the
	// vector type is numeric.
	// // No other datatypes are auto converted, for everything else, throw an
	// exception
	// if (rType instanceof RInteger &&
	// ((String)oObjType).equals(RDataTypes.R_NUMERIC.getName()))
	// {
	// rType = new RNumeric(((Integer)(rType.toJava())).doubleValue());
	// //checked cast so I know what I'm doing
	// vector.add(rType);
	// }
	// else
	// {
	// throw new BusinessException(ModeletExceptionCodes.MOBE000002,
	// new String[]
	// {
	// getClass().getName(),
	// LangTypeConstants.R_VECTOR_TYPE
	// });
	// }
	// }
	// }
	// }
	// }
	// else
	// {
	// throw new BusinessException(ModeletExceptionCodes.MOBE000002,
	// new String[]
	// {
	// getClass().getName(),
	// LangTypeConstants.R_DATA_TYPE + "/" +
	// LangTypeConstants.R_VECTOR_DATA
	// });
	// }
	// }
	// else
	// {
	// throw new BusinessException(ModeletExceptionCodes.MOBE000002,
	// new String[] { getClass().getName(), "HashMap" });
	// }
	// }

	/**
	 * Returns String representation of RVector
	 * 
	 * @return String
	 **/
	@Override
	public String toNative() {
		String strRet = null;

		if (vector.isEmpty()) {
			strRet = getRDataType().getName() + "('" + elementDataType.getName() + "')";
		} else {
			StringBuffer sb = new StringBuffer("c(");
			int intSize = vector.size();
			int index = 0;

			for (DataType dt : vector) {
				if (dt == null) {
					String na = "NA";
					sb.append(na);
				} else {
					// vector has only primitive types so toString would have
					// been fine as well
					sb.append(dt.toNative());
				}

				if (index < intSize - 1) {
					String comma = ",";
					sb.append(comma);
				}

				index++;
			}

			String clbracket = ")";
			sb.append(clbracket);
			strRet = sb.toString();
		}

		return strRet;
	}

	/**
	 * Returns false
	 * 
	 * @return false
	 **/
	@Override
	public boolean isPrimitive() {
		return false;
	}

	/**
	 * Returns "vector"
	 * 
	 * @return "vector"
	 **/
	@Override
	public RDataTypes getRDataType() {
		return RDataTypes.R_VECTOR;
	}

	// @Override
	// public Object toJava(){
	//
	// List<Object> objs = new ArrayList<Object>();
	//
	// for (DataType type : vector)
	// {
	// if (type == null)
	// {
	// objs.add(null);
	// }
	// else
	// {
	// objs.add(type.toJava());
	// }
	// }
	//
	// HashMap<String, Object> hmValues = new HashMap<String, Object>();
	// hmValues.put(LangTypeConstants.R_DATA_TYPE, getRDataType().getName());
	// hmValues.put(LangTypeConstants.R_VECTOR_TYPE, elementDataType.getName());
	// hmValues.put(LangTypeConstants.R_VECTOR_DATA, objs);
	//
	// return hmValues;
	//
	// }
	/**
	 * Returns plain Object representation of RVector
	 * 
	 * @return HashMap
	 **/
	@Override
	public FieldInfo toUmgType(final String name, final String sequence) {
		//
		FieldInfo fi = new FieldInfo();
		fi.setCollection(true);
		try {
			fi.setDataType(RDataTypes.getJavaNameFromTypeEnum(elementDataType));
		} catch (BusinessException be) {
			LOGGER.debug("Unknown primitive data type: " + be.getMessage());
		}
		fi.setModelParameterName(name);
		fi.setSequence(sequence);
		List<Object> objs = new ArrayList<Object>();


		for (DataType type : vector) {
			if (type == null) {
				objs.add(null);
			} else {
				objs.add(type.getPrimitive());
			}
		}

		if (vector.size() > 1) {
			fi.setNativeDataType(getRDataType().getName());
		} else {
			fi.setNativeDataType(fi.getDataType());
		}

		fi.setValue(objs.toArray());
		return fi;
	}

	@Override
	public Object getPrimitive() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Map<String, Object> toUmgType1(String name, String sequence) {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		
		newElement.put("p", parameterList);
		
		
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), name);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), sequence);
		
		try {
			parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(RDataTypes.getJavaNameFromTypeEnum(elementDataType)).getIntValue());
			parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getDataTypeValueEnum(RDataTypes.getJavaNameFromTypeEnum(elementDataType)).getIntValue());
		} catch (BusinessException be) {
			LOGGER.debug("Unknown primitive data type: " + be.getMessage());
		}
		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.TRUE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);

		
		List<Object> objs = new ArrayList<Object>();
		for (DataType type : vector) {
			if (type == null) {
				objs.add(null);
			} else {
				objs.add(type.getPrimitive());
			}
		}

		if (vector.size() > 1) {
			parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());
		}

		parameterList.set(FieldInfoEnum.VALUE.getIndex(), objs.toArray());	

		return newElement;
    }
}