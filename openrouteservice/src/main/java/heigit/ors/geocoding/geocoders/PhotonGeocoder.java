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
package heigit.ors.geocoding.geocoders;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONObject;

import com.graphhopper.util.Helper;

import heigit.ors.util.HTTPUtility;

public class PhotonGeocoder extends AbstractGeocoder {

	private static final ArrayList<String> supportedLanguages = new ArrayList<String>();
	
	static
	{
		//{"message":"language kg is not supported, supported languages are: de, en, it, fr"}
		supportedLanguages.add("de");
		supportedLanguages.add("en");
		supportedLanguages.add("it");
		supportedLanguages.add("fr");
	}
	
	public PhotonGeocoder(String geocodingURL, String reverseGeocodingURL, String userAgent) {
		super(geocodingURL, reverseGeocodingURL, userAgent);
	}

	public GeocodingResult[] geocode(Address address, String languages, SearchBoundary boundary, int limit) throws Exception
	{
		throw new Exception("Structured geocoding is not supported.");
	}

    public GeocodingResult[] geocode(String address, String languages, SearchBoundary boundary, int limit) throws IOException
    {
    	String code = (languages == null) ? "en" : languages.toLowerCase();
    	
    	if (!supportedLanguages.contains(code))
    		code = "en";
    	
		String reqParams = "?q=" + URLEncoder.encode(GeocodingUtils.sanitizeAddress(address), "UTF-8") + "&limit=" + limit + "&lang=" + code;
		String respContent = HTTPUtility.getResponse(geocodingURL + reqParams, 5000, userAgent, "UTF-8");
		
		if (!Helper.isEmpty(respContent) && !respContent.equals("[]")) {
	    	
			return getGeocodeResults(respContent, boundary);
		}
		
		return null;
    }
	
	public GeocodingResult[] reverseGeocode(double lon, double lat, int limit) throws IOException
	{
		String reqParams = "?lat=" + lat  + "&lon=" + lon + "&limit=" + limit;
		String respContent = HTTPUtility.getResponse(reverseGeocodingURL + reqParams, 5000, userAgent, "UTF-8");

		if (!Helper.isEmpty(respContent) && !respContent.equals("[]")) {
			return getGeocodeResults(respContent, null);
		}

		return null;
	}
	
	private GeocodingResult[] getGeocodeResults(String respContent, SearchBoundary boundary)
	{
		JSONObject features = new JSONObject(respContent);
		JSONArray arr = (JSONArray)features.get("features");
	
		GeocodingResult[] results = new GeocodingResult[arr.length()];
		
		for (int j = 0; j < arr.length(); j++) {
			JSONObject feature = arr.getJSONObject(j);
			JSONObject geomObj = feature.getJSONObject("geometry");
			JSONArray coordsObj = geomObj.getJSONArray("coordinates");
			
			double lon = Double.parseDouble(coordsObj.get(0).toString());
			double lat = Double.parseDouble(coordsObj.get(1).toString());
			
			if (boundary != null && !boundary.contains(lon, lat))
				continue;		

			JSONObject props = feature.getJSONObject("properties");

			String country = props.optString("country");
			String state = props.optString("state");
			String postal_code = props.optString("postcode");
			String locality = props.optString("city");
			String street = props.optString("street");
			String house_number = props.optString("housenumber");
			String name = props.optString("name");
			String house = name;
			String osm_value = props.optString("osm_value");
			if (!Helper.isEmpty(osm_value))
			{
				String osm_key = props.optString("osm_key");
				if (osm_value.equals("district"))
				{
					//state_district = osm_key;
				}
				else if (osm_value.equals("state") && osm_key.equals("place"))
					state = name;
			}

			if (state != null || locality != null || street != null) {
				GeocodingResult gr = new GeocodingResult();
				gr.locality = locality;
				gr.country = country;
				gr.region = state;
				//gr.stateDistrict = state_district;
				gr.postalCode = postal_code;
				gr.street = street;
				gr.name = house;
				gr.houseNumber = house_number;
				gr.longitude = lon;
				gr.latitude = lat;
				
				results[j] = gr;
			}
		}
		
		Arrays.sort(results, new GeocodingResultComparator());
		
		return results;
	}
}
