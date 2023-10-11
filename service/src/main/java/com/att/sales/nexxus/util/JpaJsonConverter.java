package com.att.sales.nexxus.util;

import java.io.IOException;

import javax.annotation.Nonnull;
import javax.persistence.AttributeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.reflect.TypeToken;

/**
 * A Generic JPA Json Type Convertor.
 *
 * @param <E> the element type
 */


public abstract class JpaJsonConverter<E> implements AttributeConverter<E, String> {
	
	/** The logger. */
	private static Logger logger = LoggerFactory.getLogger(JpaJsonConverter.class);
    
	/** The type. */
	private final TypeToken<E> type = new TypeToken<E>(getClass()) { private static final long serialVersionUID = 1L;};
    
	/** The reader. */
	private ObjectReader reader;

    /* (non-Javadoc)
     * @see javax.persistence.AttributeConverter#convertToDatabaseColumn(java.lang.Object)
     */
    @Override
    public String convertToDatabaseColumn(E attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return serialize(attribute);
        } catch (JsonProcessingException e) {
        	logger.info("cannot serialize attribute {}", attribute, e);
        	return null;
        }
    }

    /**
     * Serialize.
     *
     * @param attribute the attribute
     * @return the string
     * @throws JsonProcessingException the json processing exception
     */
    protected String serialize(@Nonnull E attribute) throws JsonProcessingException {
        return mapper().writeValueAsString(attribute);
    }

    /**
     * Mapper.
     *
     * @return the object mapper
     */
    @Nonnull
    public ObjectMapper mapper() {
        return new ObjectMapper();
    }

    /* (non-Javadoc)
     * @see javax.persistence.AttributeConverter#convertToEntityAttribute(java.lang.Object)
     */
    @Override
    public E convertToEntityAttribute(String dbData) {
        if (dbData == null || StringUtils.isEmpty(dbData)) {
            return valueForEmptyColumn();
        }
        try {
            return deserialize(dbData);
        } catch (IOException e) {
        	logger.info("cannot serialize attribute from  {}", dbData, e);
        	return null;
        }
    }

    /**
     * Value for empty column.
     *
     * @return the e
     */
    public E valueForEmptyColumn() {
        return null;
    }

    /**
     * Deserialize.
     *
     * @param dbData the db data
     * @return the e
     * @throws IOException Signals that an I/O exception has occurred.
     */
    protected E deserialize(@Nonnull String dbData) throws IOException {
        reader = getReader();
        return reader.readValue(dbData);
    }

    /**
     * Gets the reader.
     *
     * @return the reader
     */
    private ObjectReader getReader() {
        ObjectMapper mapper = mapper();
        if (reader == null) {
            reader = mapper.readerFor(mapper.constructType(type.getType()));
        }
        return reader;
    }
}

