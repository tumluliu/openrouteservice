/*|----------------------------------------------------------------------------------------------
 *|														Heidelberg University
 *|	  _____ _____  _____      _                     	Department of Geography		
 *|	 / ____|_   _|/ ____|    (_)                    	Chair of GIScience
 *|	| |  __  | | | (___   ___ _  ___ _ __   ___ ___ 	(C) 2014-2016
 *|	| | |_ | | |  \___ \ / __| |/ _ \ '_ \ / __/ _ \	
 *|	| |__| |_| |_ ____) | (__| |  __/ | | | (_|  __/	Berliner Strasse 48								
 *|	 \_____|_____|_____/ \___|_|\___|_| |_|\___\___|	D-69120 Heidelberg, Germany	
 *|	        	                                       	http://www.giscience.uni-hd.de
 *|								
 *|----------------------------------------------------------------------------------------------*/
package heigit.ors.routing.parameters;

public class CyclingParameters extends ProfileParameters {
	private int _difficultyLevel = -1;
	private int _maximumGradient = -1;

	public CyclingParameters()
	{

	}

	public int getDifficultyLevel() {
		return _difficultyLevel;
	}

	public void setDifficultyLevel(int level) {
		this._difficultyLevel = level;
	}

	public int getMaximumGradient() {
		return _maximumGradient;
	}

	public void setMaximumGradient(int maximumGradient) {
		_maximumGradient = maximumGradient;
	}
}