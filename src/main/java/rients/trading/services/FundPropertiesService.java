package rients.trading.services;

import java.util.List;
import java.util.Map;

import rients.trading.download.model.Dagkoers;

public interface FundPropertiesService {

	public Map<String, String> extractFundProperties(String fundName, List<Dagkoers> koersen);
}
