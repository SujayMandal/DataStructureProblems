/**
 * 
 */
package com.ca.framework.core.util;
import java.io.Serializable;

/**
 * @author kamathan
 *
 */
@SuppressWarnings("PMD")
public class TransactionDocumentPayload implements Serializable {

	/**
	 * Generated Serial Version ID
	 */
	private static final long serialVersionUID = 83304813862269159L;

	private TransactionIOPayload txnTIPayload;

	private TransactionIOPayload txnTOPayload;

	private TransactionIOPayload txnMIPayload;

	private TransactionIOPayload txnMOPayload;

	public TransactionIOPayload getTxnTIPayload() {
		return txnTIPayload;
	}

	public void setTxnTIPayload(TransactionIOPayload txnTIPayload) {
		this.txnTIPayload = txnTIPayload;
	}

	public TransactionIOPayload getTxnTOPayload() {
		return txnTOPayload;
	}

	public void setTxnTOPayload(TransactionIOPayload txnTOPayload) {
		this.txnTOPayload = txnTOPayload;
	}

	public TransactionIOPayload getTxnMIPayload() {
		return txnMIPayload;
	}

	public void setTxnMIPayload(TransactionIOPayload txnMIPayload) {
		this.txnMIPayload = txnMIPayload;
	}

	public TransactionIOPayload getTxnMOPayload() {
		return txnMOPayload;
	}

	public void setTxnMOPayload(TransactionIOPayload txnMOPayload) {
		this.txnMOPayload = txnMOPayload;
	}

}