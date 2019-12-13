package com.ca.pool;

import com.ca.framework.core.constants.PoolConstants;

public enum Channel {

	HTTP("HTTP"),
	FILE("File"),
	ANY(PoolConstants.ANY);
	
	private final String channel;
	
	private Channel(final String channel) {
		this.channel = channel;
	}

	public String getChannel() {
		return channel;
	}
	
	public static Channel getChannelByType(final String channel) {
		Channel transactionChannel;
		if (channel != null && channel.equalsIgnoreCase(HTTP.getChannel())) {
			transactionChannel = HTTP;
		} else if (channel != null && channel.equalsIgnoreCase(FILE.getChannel())){
			transactionChannel = FILE;
		} else {
			transactionChannel = ANY;
		}
		
		return transactionChannel;
	}
}
