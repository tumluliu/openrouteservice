/*|----------------------------------------------------------------------------------------------
 *|														Heidelberg University
 *|	  _____ _____  _____      _                     	Department of Geography		
 *|	 / ____|_   _|/ ____|    (_)                    	Chair of GIScience
 *|	| |  __  | | | (___   ___ _  ___ _ __   ___ ___ 	(C) 2014
 *|	| | |_ | | |  \___ \ / __| |/ _ \ '_ \ / __/ _ \	
 *|	| |__| |_| |_ ____) | (__| |  __/ | | | (_|  __/	Berliner Strasse 48								
 *|	 \_____|_____|_____/ \___|_|\___|_| |_|\___\___|	D-69120 Heidelberg, Germany	
 *|	        	                                       	http://www.giscience.uni-hd.de
 *|								
 *|----------------------------------------------------------------------------------------------*/
package heigit.ors.routing.graphhopper.extensions.graphbuilders;

import java.util.List;
import java.util.Map;

import com.carrotsearch.hppc.LongArrayList;
import com.graphhopper.GraphHopper;
import com.graphhopper.reader.ReaderWay;
import com.graphhopper.util.EdgeIteratorState;

import heigit.ors.routing.graphhopper.extensions.DataReaderContext;

public abstract class AbstractGraphBuilder implements GraphBuilder {
	protected Map<String, String> _parameters;
	
	public abstract void init(GraphHopper graphhopper) throws Exception;
	
	public abstract boolean createEdges(DataReaderContext readerCntx, ReaderWay way, LongArrayList osmNodeIds, long wayFlags, List<EdgeIteratorState> createdEdges) throws Exception;
	
	public abstract void finish();
	
	public abstract String getName();
	
	public void setParameters(Map<String, String> parameters)
	{
		_parameters = parameters;
	}
}
