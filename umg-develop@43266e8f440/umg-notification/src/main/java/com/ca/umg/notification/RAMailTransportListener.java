package com.ca.umg.notification;

import javax.mail.event.TransportEvent;
import javax.mail.event.TransportListener;

public class RAMailTransportListener implements TransportListener {

	@Override
	public void messageDelivered(TransportEvent e) {
		System.out.println("messageDelivered");
	}

	@Override
	public void messageNotDelivered(TransportEvent e) {
		System.out.println("messageNotDelivered");
	}

	@Override
	public void messagePartiallyDelivered(TransportEvent e) {
		System.out.println("messagePartiallyDelivered");
	}		
}
