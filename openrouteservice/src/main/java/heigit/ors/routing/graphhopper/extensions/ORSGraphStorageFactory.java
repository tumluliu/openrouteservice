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

// Authors: M. Rylov

package heigit.ors.routing.graphhopper.extensions;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import heigit.ors.routing.graphhopper.extensions.storages.builders.GraphStorageBuilder;

import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.GHDirectory;
import com.graphhopper.storage.GraphExtension;
import com.graphhopper.storage.GraphExtension.ExtendedStorageSequence;
import com.graphhopper.storage.GraphHopperStorage;
import com.graphhopper.storage.GraphStorageFactory;
import com.graphhopper.storage.TurnCostExtension;

public class ORSGraphStorageFactory implements GraphStorageFactory {

	private static Logger LOGGER = Logger.getLogger(ORSGraphStorageFactory.class.getName());
	
	private List<GraphStorageBuilder> _graphStorageBuilders;
	
	public ORSGraphStorageFactory(List<GraphStorageBuilder> graphStorageBuilders) {
		_graphStorageBuilders = graphStorageBuilders;
	}

	@Override
	public GraphHopperStorage createStorage(GHDirectory dir, GraphHopper gh) {
		EncodingManager encodingManager = gh.getEncodingManager();
		GraphExtension geTurnCosts = null;
		ArrayList<GraphExtension> graphExtensions = new ArrayList<GraphExtension>();
		
		if (encodingManager.needsTurnCostsSupport())
		{
			Path path = Paths.get(dir.getLocation(), "turn_costs");
			File fileEdges  = Paths.get(dir.getLocation(), "edges").toFile();
			File fileTurnCosts = path.toFile();
			
			// First we need to check if turncosts are available. This check is required when we introduce a new feature, but an existing graph does not have it yet.
			if ((!hasGraph(gh) && !fileEdges.exists()) || (fileEdges.exists() && fileTurnCosts.exists()))
				geTurnCosts =  new TurnCostExtension();
		}

		if (_graphStorageBuilders != null && _graphStorageBuilders.size() > 0)
		{
			for(GraphStorageBuilder builder : _graphStorageBuilders)
			{
				try
				{
					GraphExtension ext = builder.init(gh);
					if (ext != null)
						graphExtensions.add(ext);
				}
				catch(Exception ex)
				{
					LOGGER.error(ex);
				}
			}
		}

		GraphExtension graphExtension = null;
		
		if (geTurnCosts == null && graphExtensions.size() == 0)
			graphExtension =  new GraphExtension.NoOpExtension();
		else if (geTurnCosts != null && graphExtensions.size() > 0)
		{
			ArrayList<GraphExtension> seq = new ArrayList<GraphExtension>();
			seq.add(geTurnCosts);
			seq.addAll(graphExtensions);
			
			graphExtension = getExtension(seq);
		} 
		else if (geTurnCosts != null)
		{
			graphExtension = geTurnCosts;
		}
		else if (graphExtensions.size() > 0)
		{
			graphExtension = getExtension(graphExtensions);
		}
		
	    if (gh.getLMFactoryDecorator().isEnabled())
          	gh.initLMAlgoFactoryDecorator();

	    if (gh.getCHFactoryDecorator().isEnabled()) 
            gh.initCHAlgoFactoryDecorator();
	     
		if (gh.isCHEnabled())
            return new GraphHopperStorage(gh.getCHFactoryDecorator().getWeightings(), dir, encodingManager, gh.hasElevation(), graphExtension);
		else
			return new GraphHopperStorage(dir, encodingManager, gh.hasElevation(), graphExtension);
	}
	
	private GraphExtension getExtension(ArrayList<GraphExtension> graphExtensions)
	{
		if (graphExtensions.size() > 1)
	    {
	    	ArrayList<GraphExtension> seq = new ArrayList<GraphExtension>();
	    	seq.addAll(graphExtensions);
	    	return new ExtendedStorageSequence(seq); 
	    }
	    else
	    	return graphExtensions.size() == 0 ? new GraphExtension.NoOpExtension() : graphExtensions.get(0);
	}
	
	private boolean hasGraph(GraphHopper gh)
	{
		try
		{
			gh.getGraphHopperStorage();
			return true;
		}
		catch(Exception ex)
		{}
		
		return false;
	}
}
