package ex.corba.alu;

import nmsSession.NmsSession_IPOA;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import session.Session_I;

public class NmsSessionImpl extends NmsSession_IPOA {

	public static final Logger LOG = LoggerFactory
			.getLogger(NmsSessionImpl.class);

	private Session_I myAssociatedSession = null;

	public void setAssociatedSession(Session_I emsSession) {
		this.myAssociatedSession = emsSession;
	}

	public Session_I getAssociatedSession() {
		return this.myAssociatedSession;
	}

	@Override
	public void eventLossCleared(String endTime) {
		if (LOG.isInfoEnabled()) {
			LOG.info(endTime + " - eventLossCleared");
		}
	}

	@Override
	public void eventLossOccurred(String startTime, String notificationId) {
		if (LOG.isInfoEnabled()) {
			LOG.info(startTime + " - eventLossOccurred: " + notificationId);
		}
	}

	@Override
	public Session_I associatedSession() {
		return this.myAssociatedSession;
	}

	@Override
	public void endSession() {
		if (LOG.isInfoEnabled()) {
			LOG.info("End session.");
		}
	}

	@Override
	public void ping() {
		myAssociatedSession.ping();

		if (LOG.isInfoEnabled()) {
			LOG.info("Ping.");
		}
	}

	@Override
	public void alarmLossOccurred(String arg0, String arg1) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Alarm Loss Occurred.");
		}
	}

}
