package com.autmatika.testing.api;

/**
 * Anything that is a IActor implements this interface.
 *
 */
public interface IActor {
	
	/**
	 * <p>Every doable does some work. Define that work here, which may be
	 * mundane/complex heavy-lifting, or it may simply be calls into 
	 * other Doables this IActor is composed with. </p>
	 */
	public void doIt();
	
	/**
	 * Do this work before doIt() runs.
	 */
	public void preDo();
	
	/**
	 * Do this work after doIt() runs.
	 */
	public void postDo();
}
