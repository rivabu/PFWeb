package org.rients.com.pfweb.services;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import rients.trading.download.model.Dagkoers;

public interface FundPropertiesService {

	public Map<String, String> extractFundProperties(String fundName, List<Dagkoers> koersen);
	
	public List<Properties> getFileProperties(List<String> files);
}
