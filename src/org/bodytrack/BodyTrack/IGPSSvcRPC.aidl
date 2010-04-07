package org.bodytrack.BodyTrack;

interface IGPSSvcRPC {
	void startLogging();
	void stopLogging();
	boolean isLogging();
}