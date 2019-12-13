/*
 * RArray.java
 * Author Name: Manasi Seshadri (manasi.seshadri@altisource.com)
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
import com.ca.umg.modelet.converter.impl.MatlabConverter;
import com.ca.umg.modelet.exception.ModeletExceptionCodes;
import com.ca.umg.modelet.lang.type.DataType;
import com.ca.umg.modelet.lang.type.LangTypeConstants;
import org.rosuda.REngine.REXP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ca.framework.core.ioreduce.DataTypeValueEnum.getDataTypeValueEnum;
import static com.ca.framework.core.ioreduce.NativeDataTypeValueEnum.getNativeDataTypeValueEnum;

/**
 * N-d array - NOT SUPPORTED YET - marshalling only compiled not tested, unmarshalling not coded
 * **/
@SuppressWarnings("PMD")
public class RArray
extends AbstractRType
{
	private static final Logger LOGGER = LoggerFactory.getLogger(MatlabConverter.class);
	private List<Object>        ndarray;
	private List<Integer>       dimensions;
	private List<Object>        dimNames;
	private Object[]			objectArray;
	private RDataTypes elementDataType;
	private boolean stringsAsFactors;


	/**
	 * Creates a new RArray object.
	 *
	 * @param objArray DOCUMENT ME!
	 * @param dims DOCUMENT ME!
	 *
	 * @throws BusinessException DOCUMENT ME!
	 **/
	public RArray(final List<Object> objArray,
			final List<Object> dims, final boolean stringsAsFactors)
					throws BusinessException
					{
		super();
		this.stringsAsFactors = stringsAsFactors;
		arrayConstructor(objArray, dims);
					}

	/**
	 * DOCUMENT ME!
	 *
	 * @param objArray DOCUMENT ME!
	 * @param dims DOCUMENT ME!
	 *
	 * @throws BusinessException DOCUMENT ME!
	 **/
	private void arrayConstructor(final List<Object> objArray,
			final List<Object> dims)
					throws BusinessException
					{
		if (objArray != null)
		{
			//check nd array for completeness, for example, we should have a 3x4 array defined not list of 3 items with differing lengths each
			// if you have no data, insert 0 or blank strings based on the primitive type for those elements
			checkArray(objArray);

			// gets a integer array containing number of items in each dimension
			dimensions = new ArrayList<Integer>();

			//fillDimensions(objArray, dimensions);

			objectArray = buildObjectArrayFromList(objArray);

			// 1D array cannot be a matrix - throw it out
			if (dimensions != null && dimensions.size() < 2)
			{
			    LOGGER.debug("In RArray 1D array cannot be a matrix");
				throw new BusinessException(ModeletExceptionCodes.MOBE000002,
						new String[] { getClass().getName(), "1D array" });
			}

			// convert it to a nD array of DataTypes
			ndarray = new ArrayList<Object>();
			buildDataTypeArray(objArray, ndarray);

			// if dimension names are not null and size is not 0 then consider this else just ignore
			if (dims != null && !dims.isEmpty())
			{
				// check size of names array (i.e. number of dimensions - you can have null dimension names in some cases but need to have placeholder nulls there
				if (dims.size() != dimensions.size())
				{
				    LOGGER.debug("In RArray dims-size not equal dimension size");
					throw new BusinessException(ModeletExceptionCodes.MOBE000002,
							new String[] { getClass().getName(), "dimNames" });
				}

				dimNames = new ArrayList<Object>();

				for (int i = 0; i < dimensions.size(); i++)
				{
					// if there is no dimension names array for that dimension, put a dummy null strings array there,
					// else check that the provided length should match the number of items in that dimension
					// if you want to name a few rows and not others, add null strings
					if (dims.get(i) != null)
					{
						if (dims.get(i) instanceof List<?> && ((List<?>)dims.get(i)).size() == dimensions.get(i))
						{
							for (Object o : (List<Object>)dims.get(i))
							{
								// check that the list is a list of nulls or strings
								if (o != null && !(o instanceof String))
								{
								    LOGGER.debug("In RArray list not list of strings");
									throw new BusinessException(ModeletExceptionCodes.MOBE000002,
											new String[] { getClass().getName(), "dimNames" });
								}
							}

							dimNames.add(dims.get(i));
						}
						else
						{
						    LOGGER.debug("In RArray dims not isntace of list");
							throw new BusinessException(ModeletExceptionCodes.MOBE000002,
									new String[] { getClass().getName(), "dimNames" });
						}
					}
					else
					{
						List<String> subNames = new ArrayList<String>();

						for (int j = 0; j < dimensions.get(i); j++)
						{
							subNames.add(null);
						}

						dimNames.add(subNames);
					}
				}
			}
		}
		else
		{
		    LOGGER.debug("In RArray objarray null");
			throw new BusinessException(ModeletExceptionCodes.MOBE000002,
					new String[] { getClass().getName(), "List<Object>" });
		}
					}

	/**
	 * DOCUMENT ME!
	 *
	 * @param objmatrix DOCUMENT ME!
	 * @param dim DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws BusinessException DOCUMENT ME!
	 **/
	//	private void fillDimensions(List<Object>  objmatrix,
	//	                                     List<Integer> dim)
	//	  throws BusinessException
	//	{
	//		if ((objmatrix == null) || (objmatrix.size() == 0))
	//		{
	//			throw new BusinessException(ModeletExceptionCodes.MOBE000002,
	//			                            new String[] { "RArray", "List<Object>" });
	//		}
	//		else
	//		{
	//			//dim should not be null here. If it is, dont initialize or we lose the reference we passed.
	////			if (dim == null)
	////			{
	////				dim = new ArrayList<Integer>();
	////			}
	//
	//			dim.add(objmatrix.size());
	//
	//			if (objmatrix.get(0) instanceof List<?>)
	//			{
	//				fillDimensions((List<Object>)(objmatrix.get(0)), dim);
	//			}
	//
	//
	//		}
	//	}

	/**
	 * DOCUMENT ME!
	 *
	 * @param objmatrix DOCUMENT ME!
	 *
	 * @throws BusinessException DOCUMENT ME!
	 **/
	private void checkArray(final List<Object> objmatrix)
			throws BusinessException
			{
		if (objmatrix == null || objmatrix.isEmpty())
		{
		    LOGGER.debug("In RArray objmatrix null");
			throw new BusinessException(ModeletExceptionCodes.MOBE000002,
					new String[] { getClass().getName(), "List<Object>" });
		}
		else
		{
			int dim = -1;

			for (Object o : objmatrix)
			{
				if (o instanceof List<?>)
				{
					List<Object> subObj = (List<Object>)o;

					if (subObj == null || subObj.isEmpty())
					{
					    LOGGER.debug("In RArray subObj null");
						throw new BusinessException(ModeletExceptionCodes.MOBE000002,
								new String[] { getClass().getName(), "List<Object>" });
					}
					else
					{
						if (dim == -1)
						{
							dim = subObj.size();
						}
						else
						{
							if (dim != subObj.size())
							{
							    LOGGER.debug("In RArray subObj size not matching");
								throw new BusinessException(ModeletExceptionCodes.MOBE000002,
										new String[] { getClass().getName(), "dimensions" });
							}
						}
					}

					checkArray((List<Object>)o);
				}
			}
		}
			}

	/**
	 * DOCUMENT ME!
	 *
	 * @param objmatrix DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 *
	 * @throws BusinessException DOCUMENT ME!
	 **/
	private void buildDataTypeArray(final List<Object> objmatrix, final List<Object> dtList)
			throws BusinessException
			{


		for (Object o : objmatrix)
		{
			if (o instanceof List<?>)
			{
				dtList.add(new ArrayList<Object>());
				buildDataTypeArray((List<Object>)o, (List<Object>)dtList.get(dtList.size()-1));
			}
			else
			{
				if (o instanceof DataType)
				{
					dtList.add(o);
				}
				else
				{
					dtList.add(AbstractRType.createRDataTypeFromObject(new FieldInfo(o), stringsAsFactors));
				}
			}
		}


			}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	//	private List<String> buildFlatListOfNDArray(List<Object> objMatrix)
	//	{
	//		List<String> strList = new ArrayList<String>();
	//
	//		for (Object o : objMatrix)
	//		{
	//			if (o instanceof List<?>)
	//			{
	//				strList.addAll(buildFlatListOfNDArray((List<Object>)o));
	//			}
	//			else
	//			{
	//				// o HAS to be instanceof DataType as we are calling this from a good checked RArray representation
	//				strList.add(((DataType)o).toNative());
	//			}
	//		}
	//
	//		return strList;
	//	}

	private List<AbstractRType> buildFlatListOfNDArray()
	{

		List<AbstractRType> flatArray = new ArrayList<AbstractRType>();
		//MS: PMD nonsense
		int two = 2;
		int zero = 0;
		if (dimensions.size() % two != zero)		{
			for (int i=0; i<dimensions.get(0); i++)	{
				flatArray.addAll(splitBy2D((Object[])objectArray[i], 1));
			}
		}
		else	{
			flatArray.addAll(splitBy2D(objectArray,  0));
		}

		return flatArray;
	}

	private List<AbstractRType> splitBy2D(final Object[] objArray, final int startIndex) 	{
		List<AbstractRType> objs = new ArrayList<AbstractRType>();
		for (int j=0; j<dimensions.get(startIndex+1); j++)	{
			for (int i=0; i<dimensions.get(startIndex); i++)	{

				Object o = ((Object[])objArray[i])[j];

				if (o instanceof Object[])	{
					Object[] subArray = (Object[])o;
					objs.addAll(splitBy2D(subArray, startIndex+2));
				}
				else	{
					// the inner object is an AbstractRType
					objs.add((AbstractRType)o);
				}
			}
		}

		return objs;
	}

	private Object[] buildObjectArrayFromList(final List<Object> lst)	throws BusinessException	{
		Object[] retArray  = new Object[lst.size()];

		if (lst != null)	{
			for (int i=0; i<lst.size(); i++)	{
				if (lst.get(i) instanceof List<?>)	{
					retArray[i] = buildObjectArrayFromList((List<Object>)lst.get(i));

					//MS: PMD nonsense
					int zero = 0;
					if (i==zero)	{
						dimensions.add(Integer.valueOf(lst.size()));
					}
				}
				else	{
					retArray[i] = AbstractRType.createRDataTypeFromObject(new FieldInfo(lst.get(i)), stringsAsFactors);
				}
			}

		}

		//MS: returning 0 length array instead of null here because of PMD checks - need to test if this has greater impact
		return retArray;

	}

	/**
	 * Creates a new RArray object.
	 *
	 * @param output DOCUMENT ME!
	 * @param elementType DOCUMENT ME!
	 *
	 * @throws BusinessException DOCUMENT ME!
	 **/
	public RArray(final REXP       output,
			final RDataTypes elementType)
					throws BusinessException
					{
		super();
		
		LOGGER.debug("Entered RArray elementType {} for output is {}  : ",elementType,output);

		REXP dim = output.getAttribute("dim");

		//MS: PMD nonsense - needs to be removed when this function is implemented
		if (elementType == RDataTypes.R_INTEGER && dim == null)	{
			LOGGER.debug("Do nothing");
		}

		//		if (dim == null || dim.getType() != REXP.XT_ARRAY_INT || dim.asIntArray() == null || dim.asIntArray().length < 3)	{
		//			throw new BusinessException(ModeletExceptionCodes.MOBE000002,new String[] {"RArray", "REXP"});
		//		}
		//
		//		switch (elementType)	{
		//		case R_INTEGER:
		//			int[] intArray = output.asIntArray();
		//
		//			List<Object> lstObj = new ArrayList<Object>();
		//
		//			// by level
		//			for (int i=0; i<dim.asIntArray()[0]; i++)	{
		//				List<Object> lstObj1 = new ArrayList<Object>();
		//				Object[] fillMatrixFromArray(objArray, nrow, ncol, byRow)
		//			}
		//
		//		case R_NUMERIC:
		//			double[][] twoDArray = output.asMatrix();
		//			Double[][] twoDWrapper = null;
		//
		//			for (int i=0; i<twoDArray.length; i++)	{
		//				if (twoDWrapper == null)	{
		//					twoDWrapper = new Double[twoDArray.length][twoDArray[i].length];
		//				}
		//				twoDWrapper[i] = ArrayUtils.toObject(twoDArray[i]);
		//			}
		//			//TODO:MS dimensions names
		//			createMatrixFrom2DArray(twoDWrapper, null);
		//			break;
		//		case R_CHARACTER:
		//			REXP dim = output.getAttribute("dim");
		//    		if (dim == null || dim.getType() != REXP.XT_ARRAY_INT || dim.asIntArray() == null || dim.asIntArray().length != 2)	{
		//    			throw new BusinessException(ModeletExceptionCodes.MOBE000002,new String[] {"RMatrix", "REXP"});
		//    		}
		//
		//    		int[] ds = dim.asIntArray();
		//			int nrow = ds[0];
		//			int ncol = ds[1];
		//			String[][] strMatrix = new String[nrow][ncol];
		//			String[] strArray = output.asStringArray();
		//
		//			//TODO:MS get attribute here - byrow. Currently assuming default: byrow=false, i.e. fill it over rows first then column
		//			//TODO:MS - get attribute here - dimnames. Currently assuming null.
		//			createMatrixFrom2DArray(fillMatrixFromArray(strArray, nrow, ncol, false), null);
		//			break;
		//
		//		case R_LOGICAL:
		//			REXP dim_b = output.getAttribute("dim");
		//    		if (dim_b == null || dim_b.getType() != REXP.XT_ARRAY_INT || dim_b.asIntArray() == null || dim_b.asIntArray().length != 2)	{
		//    			throw new BusinessException(ModeletExceptionCodes.MOBE000002,new String[] {"RMatrix", "REXP"});
		//    		}
		//
		//    		int[] ds_b = dim_b.asIntArray();
		//			int nrow_b = ds_b[0];
		//			int ncol_b = ds_b[1];
		//			boolean[][] blnMatrix = new boolean[nrow_b][ncol_b];
		//			int[] intBArray = output.asIntArray();
		//			Boolean[] blnArray = new Boolean[intBArray.length];
		//
		//			for (int i=0; i<intBArray.length; i++)	{
		//				blnArray[i] = (intBArray[i] == 1)?true:false;
		//			}
		//			//TODO:MS get attribute here - byrow. Currently assuming default: byrow=false, i.e. fill it over rows first then column
		//			//TODO:MS - get attribute here - dimnames. Currently assuming null.
		//			createMatrixFrom2DArray(fillMatrixFromArray(blnArray, nrow_b, ncol_b, false), null);
		//			break;
		//
		//		default:
		//			throw new BusinessException(ModeletExceptionCodes.MOBE000003,new String[] {"RMatrix", elementType.getName()});
		//
		//		}
					}
	
	public RArray(final org.rosuda.JRI.REXP       output,
			final RDataTypes elementType)
					throws BusinessException
					{
		super();
		
		LOGGER.debug("Entered RArray elementType {} for output is {}  : ",elementType,output);

		org.rosuda.JRI.REXP dim = output.getAttribute("dim");

		//MS: PMD nonsense - needs to be removed when this function is implemented
		if (elementType == RDataTypes.R_INTEGER && dim == null)	{
			LOGGER.debug("Do nothing");
		}
					}

	/**
	 * DOCUMENT ME!
	 *
	 * @param objArray DOCUMENT ME!
	 * @param nrow DOCUMENT ME!
	 * @param ncol DOCUMENT ME!
	 * @param byRow DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	//	private Object[][] fillMatrixFromArray(Object[] objArray,
	//	                                       int      nrow,
	//	                                       int      ncol,
	//	                                       boolean  byRow)
	//	{
	//		Object[][] objMatrix = new Object[nrow][ncol];
	//		int        index     = 0;
	//
	//		if (byRow == false)
	//		{
	//			//default
	//			for (int j = 0; j < ncol; j++)
	//			{
	//				for (int i = 0; i < nrow; i++)
	//				{
	//					objMatrix[i][j] = objArray[index];
	//					index++;
	//				}
	//			}
	//		}
	//		else
	//		{
	//			for (int i = 0; i < nrow; i++)
	//			{
	//				for (int j = 0; j < ncol; j++)
	//				{
	//					objMatrix[i][j] = objArray[index];
	//					index++;
	//				}
	//			}
	//		}
	//
	//		return objMatrix;
	//	}

	/**
	 * Creates a new RArray object.
	 *
	 * @param hmValue DOCUMENT ME!
	 *
	 * @throws BusinessException DOCUMENT ME!
	 **/
	public RArray(final Map<String, Object> hmValue, final boolean stringsAsFactors)
			throws BusinessException
			{
		super();
		this.stringsAsFactors = stringsAsFactors;

		if (hmValue != null)
		{
			Object oType  = hmValue.get(LangTypeConstants.R_DATA_TYPE);
			Object oData  = hmValue.get(LangTypeConstants.R_ARRAY_DATA);
			Object oNames = hmValue.get(LangTypeConstants.R_ARRAY_NAMES);

			if (oData instanceof List<?> &&
					oType instanceof String && oType.equals(getRDataType().getName()))
			{
				List<Object> objs  = (List<Object>)oData;
				List<Object> names = null;

				if (oNames instanceof List<?>)
				{
					names = (List<Object>)oNames;
				}

				arrayConstructor(objs, names);
			}
			else
			{
			    LOGGER.debug("In RArray odata not list");
				throw new BusinessException(ModeletExceptionCodes.MOBE000002,
						new String[]
								{
						getClass().getName(),
						LangTypeConstants.R_ARRAY_DATA + "/" +
								LangTypeConstants.R_DATA_TYPE
								});
			}
		}
		else
		{
		    LOGGER.debug("In RArray hmvalue null");
			throw new BusinessException(ModeletExceptionCodes.MOBE000002,
					new String[] { getClass().getName(), "HashMap" });
		}
			}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public String toNative()
	{
		String strRet = null;

		//as array() in R creates a 1D array with element NA, we dont allow a RArray with 0 elements, as this does not translate to array(), so we will not check here
		StringBuilder sb = new StringBuilder(getRDataType().getName());

		String tmp1 = "(c(";
		sb.append(tmp1);

		List<AbstractRType> objList = buildFlatListOfNDArray();

		int          index   = 0;

		for (AbstractRType s : objList)
		{
			sb.append(s.toNative());

			if (index < objList.size() - 1)
			{
				String comma = ",";
				sb.append(comma);
			}

			index++;
		}

		String t = "),c(";
		sb.append(t);

		index = 0;

		for (Integer i : dimensions)
		{
			sb.append(i.toString());

			if (index < dimensions.size() - 1)
			{
				String comma = ",";
				sb.append(comma);
			}
			index++;
		}

		String bracket = ")";
		sb.append(bracket);

		if (dimNames != null && !dimNames.isEmpty())
		{
			try
			{
				String dimnames = ",dimnames=";
				sb.append(dimnames);

				RVector       vector  = null;
				List<RVector> vecList = new ArrayList<RVector>();

				for (Object o : dimNames)
				{
					// this is called from a good checked RArray so no need for type checking
					List<Object> strs = (List<Object>)o;

					vector = new RVector(strs, RDataTypes.R_CHARACTER);
					vecList.add(vector);
				}

				RList list = new RList(vecList, null, stringsAsFactors);
				sb.append(list.toNative());
			}
			catch (BusinessException be)
			{
				LOGGER.debug("This is never going to happen because we have checked anc created the dimNames");
			}
		}

		String clbracket = ")";
		sb.append(clbracket);

		strRet = sb.toString();

		return strRet;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public boolean isPrimitive()
	{
		return false;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	@Override
	public RDataTypes getRDataType()
	{
		return RDataTypes.R_ARRAY;
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 **/
	//	@Override
	//	public Object toJava()
	//	{
	//		HashMap<String, Object> hmValues = new HashMap<String, Object>();
	//		hmValues.put(LangTypeConstants.R_DATA_TYPE, getRDataType().getName());
	//		hmValues.put(LangTypeConstants.R_ARRAY_DATA, buildObjectArray(ndarray));
	//
	//		if (dimNames != null && !dimNames.isEmpty())
	//		{
	//			hmValues.put(LangTypeConstants.R_ARRAY_NAMES, dimNames);
	//		}
	//
	//		return hmValues;
	//	}

	@Override
	public FieldInfo toUmgType(final String name, final String sequence) {
		final FieldInfo fi = new FieldInfo();
		fi.setCollection(false);
		fi.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
		fi.setModelParameterName(name);
		fi.setSequence(sequence);
		fi.setNativeDataType(getRDataType().getName());

		final List<Object> objs = new ArrayList<Object>();

		final Integer rows = dimensions.get(0);
		final Integer cols = dimensions.get(1);
		final Integer depth = dimensions.get(2);

		for (int d = 0; d < depth; d++) {
			final List<Object> subObjs = new ArrayList<>();
			for (int i = 0; i < rows; i++) {
				final List<Object> subSubObjs = new ArrayList<>();
				for (int j = 0; j < cols; j++) {
					subSubObjs.add("");
				}
				subObjs.add(subSubObjs);
			}
			objs.add(subObjs);
		}

		final ArrayList<Object> alValues = new ArrayList<Object>();

		FieldInfo f = new FieldInfo();
		f.setCollection(false);
		f.setDataType(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType());
		f.setModelParameterName(LangTypeConstants.R_ARRAY_DATA);
		f.setSequence("1");
		f.setValue(objs.toArray());

		alValues.add(f);

		if (dimNames != null && dimNames.size() > 0) {
			final List<Object> rowNames = (List<Object>) dimNames.get(0);

			f = new FieldInfo();
			f.setCollection(false);
			f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
			f.setModelParameterName(LangTypeConstants.R_MATRIX_ROW_NAMES);
			f.setSequence("2");
			f.setValue(rowNames.toArray());

			alValues.add(f);
		}

		if (dimNames != null && dimNames.size() > 1) {
			final List<Object> colNames = (List<Object>) dimNames.get(1);
			f = new FieldInfo();
			f.setCollection(false);
			f.setDataType(com.ca.umg.modelet.common.DataType.STRING.getUmgType());
			f.setModelParameterName(LangTypeConstants.R_MATRIX_COL_NAMES);
			f.setSequence("3");
			f.setValue(colNames.toArray());

			alValues.add(f);
		}

		fi.setValue(alValues.toArray());

		return fi;
	}

	@Override
	public Object getPrimitive() {
		// TODO Auto-generated method stub
		return null;
	}

	public RArray(final List<Object> hmValue, final boolean stringsAsFactors) throws BusinessException {
		super();
		this.stringsAsFactors = stringsAsFactors;
		if (hmValue != null) {
			HashMap<String, Object> attribute;
			List<Object> oNames = null;
			List<Object> rowNames;
			List<Object> colNames;

			for (final Object objAttribute : hmValue) {
				attribute = (HashMap<String, Object>) objAttribute;
				if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_ARRAY_DATA)) {
					elementDataType = RDataTypes.getTypeEnumFromName(attribute.get("dataType").toString());
					ndarray = marshallData((List<Object>) attribute.get("value"));
				} else if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_ARRAY_ROW_NAMES)) {
					rowNames = marshallRowNames((List<Object>) attribute.get("value"));
					if (rowNames != null) {
						if (oNames == null) {
							oNames = new ArrayList<>(2);
							oNames.add(0, null);
							oNames.add(1, null);
						}

						oNames.set(0, rowNames);
					}
				} else if (attribute.get("modelParameterName").toString().equalsIgnoreCase(LangTypeConstants.R_ARRAY_COL_NAMES)) {
					colNames = marshallColNames((List<Object>) attribute.get("value"));
					if (colNames != null) {
						if (oNames == null) {
							oNames = new ArrayList<>(2);
							oNames.add(0, null);
							oNames.add(1, null);
						}

						oNames.set(1, colNames);
					}
				}
			}

			dimNames = oNames;
		} else {
		    LOGGER.debug("In RArray hmvalue null List<Object> stringsAsFactors");
			throw new BusinessException(ModeletExceptionCodes.MOBE000002, new String[] { this.getClass().getName(), "FieldInfo value" });
		}
	}

	private List<Object> marshallData(final List<Object> hmValue) throws BusinessException {
		final int row = hmValue.size();
		final int col = ((List<?>) hmValue.get(0)).size();
		final int depth = ((List<?>) ((List<?>) hmValue.get(0)).get(0)).size();

		dimensions = new ArrayList<Integer>();
		dimensions.add(depth);
		dimensions.add(row);
		dimensions.add(col);

		final List<Object> values = new ArrayList<Object>();

		for (Object first : hmValue) {
			final List<Object> firstList = new ArrayList<Object>();
			values.add(firstList);
			final List secondValue = (List) first;
			for (Object value : secondValue) {
				final List thirdValue = (List) value;
				final List<Object> secondList = new ArrayList<Object>();
				firstList.add(secondList);
				for (Object value1 : thirdValue) {
					secondList.add(createRDataTypeFromObject(new FieldInfo(value1), stringsAsFactors));
				}
			}
		}

		return values;
	}

	private List<Object> marshallRowNames(final List<Object> hmValue) throws BusinessException {
		return convertValueToList(hmValue);
	}

	private List<Object> marshallColNames(final List<Object> hmValue) throws BusinessException {
		return convertValueToList(hmValue);
	}

	private List<Object> convertValueToList(final List<Object> hmValue) throws BusinessException {
		if (hmValue == null) {
			return null;
		} else {
			final List<Object> names = new ArrayList<Object>();

			for (Object value : hmValue) {
				names.add(value);
			}

			return names;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Map<String, Object> toUmgType1(final String name, final String sequence) {
		final List<Object> objs = new ArrayList<Object>();

		final Integer rows = dimensions.get(0);
		final Integer cols = dimensions.get(1);
		final Integer depth = dimensions.get(2);

		for (int d = 0; d < depth; d++) {
			final List<Object> subObjs = new ArrayList<>();
			for (int i = 0; i < rows; i++) {
				final List<Object> subSubObjs = new ArrayList<>();
				for (int j = 0; j < cols; j++) {
					subSubObjs.add("");
				}
				subObjs.add(subSubObjs);
			}
			objs.add(subObjs);
		}
		
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);

		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), name);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), sequence);
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), getNativeDataTypeValueEnum(getRDataType().getName()).getIntValue());
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);

		final ArrayList<Object> alValues = new ArrayList<Object>();
		alValues.add(createData(objs));
		
		if (dimNames != null && dimNames.size() > 0) {
			alValues.add(createDimRowNames());
		}
		
		if (dimNames != null && dimNames.size() > 1) {
			alValues.add(createDimColNames());
		}

		parameterList.set(FieldInfoEnum.VALUE.getIndex(), alValues.toArray());	

		return newElement;
	}
	
	private Map<String, Object> createData(final List<Object> objs) {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		

		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_ARRAY_DATA);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "1");
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.OBJECT.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), objs.toArray());	

		return newElement;
	}
	
	private Map<String, Object> createDimRowNames() {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		

		final List<Object> rowNames = (List<Object>) dimNames.get(0);

		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_MATRIX_ROW_NAMES);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "2");
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), rowNames.toArray());	

		return newElement;
	}
	
	private Map<String, Object> createDimColNames() {
		final Map<String, Object> newElement = new HashMap<String, Object>();
		
    	final List<Object> parameterList = new ArrayList<Object>(FieldInfoEnum.values().length);
		
		final Object dummy = new Object();
		for (int i = 0; i < FieldInfoEnum.values().length; ++i) {
			parameterList.add(dummy);
		}

		newElement.put("p", parameterList);
		
		final List<Object> colNames = (List<Object>) dimNames.get(1);
		
		parameterList.set(FieldInfoEnum.FIELD_NAME.getIndex(), LangTypeConstants.R_MATRIX_COL_NAMES);
		parameterList.set(FieldInfoEnum.SEQUENCE.getIndex(), "3");
		parameterList.set(FieldInfoEnum.NATIVE_DATA_TYPE.getIndex(), null);
		parameterList.set(FieldInfoEnum.DATA_TYPE.getIndex(), getDataTypeValueEnum(com.ca.umg.modelet.common.DataType.STRING.getUmgType()).getIntValue());		
		parameterList.set(FieldInfoEnum.COLLECTION.getIndex(), BooleanValueEnum.FALSE.getIntValue());
		parameterList.set(FieldInfoEnum.P.getIndex(),  null);
		parameterList.set(FieldInfoEnum.VALUE.getIndex(), colNames.toArray());	

		return newElement;
	}

}
