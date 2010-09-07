package org.bodytrack.BodyTrack;

/*This AIDL file defines the RPc interface for the GPS service.
*/

interface IGPSSvcRPC {
	void bringToForeground();
	void startLogging();
	void stopLogging();
	boolean isLogging();
}