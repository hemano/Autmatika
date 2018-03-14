package com.autmatika.testing.api.beans;

import com.autmatika.testing.api.AbstractActor;

public class ActorBean extends AbstractActor {

/**
 * @param stepId business module step id
 * @param actorOnFail action to execute on failure
 * @param uiAction web UI action taking place during test
 * @param elementLocation logical name / element location of element
 * @param parameters distinguishes a UI action that requires more user input
 */
	public ActorBean(String stepId, String actorOnFail, String uiAction, String elementLocation, String parameters) {
		this.stepId = stepId;
		this.actorOnFail = actorOnFail;
		this.uiAction = uiAction;
		this.elementLocation = elementLocation;
		this.parameters = parameters;
		
	}


	

}
