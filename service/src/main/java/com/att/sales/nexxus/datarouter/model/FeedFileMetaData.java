package com.att.sales.nexxus.datarouter.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class FeedFileMetaData.
 *
 * @author AbdulQadeer File to create X-ATT-DR-META, needed to publish file to
 *         DataRouter
 */
public class FeedFileMetaData {

	/** The log. */
	private static Logger log = LoggerFactory.getLogger(FeedFileMetaData.class);

	/** The file type. */
	private String file_type = "zip";

	/** The feed type. */
	private String feed_type = "Full-data-set";

	/** The compression. */
	private String compression = "N";

	/** The delimiter. */
	private String delimiter = ",";

	/** The record count. */
	private String record_count = "12";

	DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/** The publish date. */
	private String publish_date = formatter.format(new Date(System.currentTimeMillis()));


	/** The file size. */
	private String file_size;

	/** The splits. */
	private String splits = "1";

	/** The feed id. */
	private String feed_id;

	/** The version. */
	private String version = "1.0";

	/**
	 * Instantiates a new feed file meta data.
	 */
	public FeedFileMetaData() {

	}

	/**
	 * Instantiates a new feed file meta data.
	 *
	 * @param recordCount
	 *            the record count
	 * @param fileSize
	 *            the file size
	 * @param feedId
	 *            the feed id
	 */
	public FeedFileMetaData(String fileSize, String feedId) {
		super();
		this.file_size = fileSize;
		this.feed_id = feedId;
	}

	/**
	 * Gets the compression.
	 *
	 * @return the compression
	 */
	public String getCompression() {
		return compression;
	}

	/**
	 * Sets the compression.
	 *
	 * @param compression
	 *            the new compression
	 */
	public void setCompression(String compression) {
		this.compression = compression;
	}

	/**
	 * Gets the delimiter.
	 *
	 * @return the delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * Sets the delimiter.
	 *
	 * @param delimiter
	 *            the new delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * Gets the splits.
	 *
	 * @return the splits
	 */
	public String getSplits() {
		return splits;
	}

	/**
	 * Sets the splits.
	 *
	 * @param splits
	 *            the new splits
	 */
	public void setSplits(String splits) {
		this.splits = splits;
	}

	/**
	 * Gets the version.
	 *
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * Sets the version.
	 *
	 * @param version
	 *            the new version
	 */
	public void setVersion(String version) {
		this.version = version;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String val = "{\"ERROR\":\"0001\",\"Message:\":\"Cound Not Generate JSON HEADER for the FEED file\"}";
		ObjectMapper om = new ObjectMapper();
		try {
			val = om.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			log.error("Exception in toString %s", e);
		}
		return val;
	}

	/**
	 * @return the file_type
	 */
	public String getFile_type() {
		return file_type;
	}

	/**
	 * @param file_type
	 *            the file_type to set
	 */
	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	/**
	 * @return the feed_type
	 */
	public String getFeed_type() {
		return feed_type;
	}

	/**
	 * @param feed_type
	 *            the feed_type to set
	 */
	public void setFeed_type(String feed_type) {
		this.feed_type = feed_type;
	}

	/**
	 * @return the record_count
	 */
	public String getRecord_count() {
		return record_count;
	}

	/**
	 * @param record_count
	 *            the record_count to set
	 */
	public void setRecord_count(String record_count) {
		this.record_count = record_count;
	}

	/**
	 * @return the publish_date
	 */
	public String getPublish_date() {
		return publish_date;
	}

	/**
	 * @param publish_date
	 *            the publish_date to set
	 */
	public void setPublish_date(String publish_date) {
		this.publish_date = publish_date;
	}

	/**
	 * @return the file_size
	 */
	public String getFile_size() {
		return file_size;
	}

	/**
	 * @param file_size
	 *            the file_size to set
	 */
	public void setFile_size(String file_size) {
		this.file_size = file_size;
	}

	/**
	 * @return the feed_id
	 */
	public String getFeed_id() {
		return feed_id;
	}

	/**
	 * @param feed_id
	 *            the feed_id to set
	 */
	public void setFeed_id(String feed_id) {
		this.feed_id = feed_id;
	}

}
