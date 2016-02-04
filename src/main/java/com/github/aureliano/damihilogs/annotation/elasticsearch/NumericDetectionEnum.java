package com.github.aureliano.damihilogs.annotation.elasticsearch;

/**
 * Allows to disable automatic numeric type detection
 *
 * @see <a href="http://www.elasticsearch.org/guide/en/elasticsearch/reference/current/mapping-root-object-type.html#_numeric_detection">Numeric Detection</a>
 */
public enum NumericDetectionEnum {
	/**
	 * Use default value in ElasticSearch
	 */
	NA,
	
	/**
	 * Enable Numeric Detection
	 */
	TRUE,
	
	/**
	 * Disable Numeric Detection
	 */
	FALSE
}