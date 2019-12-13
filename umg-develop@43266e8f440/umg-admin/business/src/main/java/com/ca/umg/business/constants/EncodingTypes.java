/**
 * 
 */
package com.ca.umg.business.constants;

/**
 * @author elumalas
 *
 */
public enum EncodingTypes {
	MD5 ("MD5"),SHA1 ("SHA1"),SHA256 ("SHA256"),SHA384("SHA284"),SHA512("SHA512");
    private String name;   
    
    private EncodingTypes(String s) {
        name = s;
    }

    public static EncodingTypes getType(String type) {
    	EncodingTypes encodingTypes = null;
		switch (type){
		case "MD5":
			encodingTypes = EncodingTypes.MD5;
			break;
		case "SHA1":
			encodingTypes = EncodingTypes.SHA1;
			break;
		case "SHA384":
			encodingTypes = EncodingTypes.SHA384;
			break;
		case "SHA512":
			encodingTypes = EncodingTypes.SHA512;
			break;
		default:
			encodingTypes = EncodingTypes.SHA256;
			break;
		}
		return encodingTypes;
	}

	public String getName() {
		return name;
	}
    
}
