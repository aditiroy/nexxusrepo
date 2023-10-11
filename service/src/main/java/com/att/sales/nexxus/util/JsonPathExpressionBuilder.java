package com.att.sales.nexxus.util;

import java.util.ArrayList;
import java.util.List;
/**
 * <b>JsonPathExpressionBuilder</b> is used to build the json path expression which can be then passed into the
 *  <b>JsonPathUtil</b> to search or set a node in a JSON string/object.
 * 
 *
 */
public class JsonPathExpressionBuilder{

  /** The Constant JSON_PATH_EXPR_SELECT_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_SELECT_PLACEHOLDER = "<SELECT>";

  /** The Constant JSON_PATH_EXPR_DOT. */
  public static final String JSON_PATH_EXPR_DOT =".";

  /** The Constant JSON_PATH_EXPR_SELECT_ALL. */
  public static final String JSON_PATH_EXPR_SELECT_ALL ="*";

  /** The Constant JSON_PATH_EXPR_ARRAYNODE_OPEN. */
  public static final String JSON_PATH_EXPR_ARRAYNODE_OPEN = "[";

  /** The Constant JSON_PATH_EXPR_ARRAYNODE_CLOSE. */
  public static final String JSON_PATH_EXPR_ARRAYNODE_CLOSE = "]";

  /** The Constant JSON_PATH_EXPR_WHERE_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_WHERE_PLACEHOLDER = "<WHERE>";

  /** The Constant JSON_PATH_EXPR_IS_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_IS_PLACEHOLDER = "<IS>";

  /** The Constant JSON_PATH_EXPR_WHERE_IS_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_WHERE_IS_PLACEHOLDER = "(@.<WHERE> == <IS>)";
  
  /** The Constant JSON_PATH_EXPR_LIST_IS_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_LIST_IS_PLACEHOLDER = "(@ == <IS>)";
  
  /** The Constant JSON_PATH_EXPR_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_QUERY_PLACEHOLDER = "?";

  /** The Constant JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER="==";
  
  /** The Constant JSON_PATH_EXPR_LESS_THAN_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_LESS_THAN_QUERY_PLACEHOLDER="<";
  
  /** The Constant JSON_PATH_EXPR_LESS_THAN_EQUAL_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_LESS_THAN_EQUAL_QUERY_PLACEHOLDER="<=";
  
  /** The Constant JSON_PATH_EXPR_GREATER_THAN_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_GREATER_THAN_QUERY_PLACEHOLDER=">";
  
  /** The Constant JSON_PATH_EXPR_GREATER_THAN_EQUAL_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_GREATER_THAN_EQUAL_QUERY_PLACEHOLDER=">=";
  
  /** The Constant JSON_PATH_EXPR_NOT_EQUAL_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_NOT_EQUAL_QUERY_PLACEHOLDER="!=";
  
  /** The Constant JSON_PATH_EXPR_REGEX_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_REGEX_QUERY_PLACEHOLDER="=~";
  
  /** The Constant JSON_PATH_EXPR_IN_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_IN_QUERY_PLACEHOLDER="in";
  
  /** The Constant JSON_PATH_EXPR_NOT_IN_QUERY_PLACEHOLDER. */
  private static final String JSON_PATH_EXPR_NOT_IN_QUERY_PLACEHOLDER="nin";
  
  /** The Constant JSON_PATH_EXPR_QUERY_PLACEHOLDER_MULTICONDTION_OPEN. */
  private static final String JSON_PATH_EXPR_QUERY_PLACEHOLDER_MULTICONDTION_OPEN = "?(";

  /** The Constant JSON_PATH_EXPR_QUERY_PLACEHOLDER_MULTICONDTION_CLOSE. */
  private static final String JSON_PATH_EXPR_QUERY_PLACEHOLDER_MULTICONDTION_CLOSE = ")";

  /** The json path expression string. */
  private final StringBuilder jsonPathExpressionString = new StringBuilder("$");
  
  /** The has multiple conditions. */
  private boolean hasMultipleConditions;
  
  /** The is array node of data. */
  private boolean isArrayNodeOfData;

  /** The array node conditions. */
  private List<String> arrayNodeConditions = new ArrayList<String>(5) {
  	private static final long serialVersionUID = 1L;
  	
	  /* (non-Javadoc)
	   * @see java.util.AbstractCollection#toString()
	   */
	  @Override
  	public String toString(){
  		StringBuilder arrayNodeConditionBuilder = new StringBuilder(JSON_PATH_EXPR_ARRAYNODE_OPEN);
  		if(hasMultipleConditions) {
  			arrayNodeConditionBuilder.append(JSON_PATH_EXPR_QUERY_PLACEHOLDER_MULTICONDTION_OPEN);
  		}
  		else {
  			arrayNodeConditionBuilder.append(JSON_PATH_EXPR_QUERY_PLACEHOLDER);
  		}
  		for(String condition:arrayNodeConditions) {
  			arrayNodeConditionBuilder.append(condition);
  		}
  		if(hasMultipleConditions) {
  			arrayNodeConditionBuilder.append(JSON_PATH_EXPR_QUERY_PLACEHOLDER_MULTICONDTION_CLOSE);
  		}
  		arrayNodeConditionBuilder.append(JSON_PATH_EXPR_ARRAYNODE_CLOSE);
      return arrayNodeConditionBuilder.toString();
    }
  };

  /** The select where. */
  private final StringBuilder selectWhere = new StringBuilder(JSON_PATH_EXPR_WHERE_IS_PLACEHOLDER);

  /**
   * Objectnode.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder objectnode(Object... parameters) {
	Object key = null;
	if (parameters.length != 0) {
	  	key = parameters[0];
	}
  	if(key!=null){
  		jsonPathExpressionString.append(JSON_PATH_EXPR_DOT+key);
  	}
  	else {
  		jsonPathExpressionString.append(JSON_PATH_EXPR_DOT);
  	}
    return this;
  }

  /**
   * Key.
   *
   * @param key the key
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder key(String key) {
  	objectnode(key);
  	return this;
  }

  /**
   * Arraynode.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder arraynode(Object... parameters) {
	Object key = null;
	isArrayNodeOfData = false;
	if (parameters.length != 0) {
	  	key = parameters[0];
	}
  	if(!arrayNodeConditions.isEmpty()) {
  		buildNode();
  	}
  	arrayNodeConditions.clear();
  	if(key!=null) {
  		jsonPathExpressionString.append(JSON_PATH_EXPR_DOT+key+JSON_PATH_EXPR_SELECT_PLACEHOLDER);
  	}
  	else {
  		jsonPathExpressionString.append(JSON_PATH_EXPR_DOT+JSON_PATH_EXPR_SELECT_PLACEHOLDER);
  	}
  	
  	if(parameters.length>1 && parameters[1]!=null) {
  		boolean arrayNodeOfData = (boolean) parameters[1];
  		if(arrayNodeOfData) {
  			isArrayNodeOfData = true;
  		}  		
  	}
  	
  	if(parameters.length>2 && parameters[2]!=null) {
  		boolean arrayNodeInCondtion = (boolean) parameters[1];
  		if(arrayNodeInCondtion) {
  			jsonPathExpressionString.setLength(0);
  			jsonPathExpressionString.append(key+JSON_PATH_EXPR_SELECT_PLACEHOLDER);	
  		}  		
  	}
  	  	
    return this;
  }

  /**
   * Builds the node.
   */
  private void buildNode() {
  	if(arrayNodeConditions.isEmpty()) {
  		return;
  	}
  	else if(arrayNodeConditions.size()>1) {
  		hasMultipleConditions = true;
  	}
  	int position = jsonPathExpressionString.lastIndexOf(JSON_PATH_EXPR_SELECT_PLACEHOLDER);
  	jsonPathExpressionString.replace(position, position + JSON_PATH_EXPR_SELECT_PLACEHOLDER.length(),
  			arrayNodeConditions.toString());

	}
  
  /**
   * Select.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder select(Object... parameters) {
  	Object key = null;
  	if (parameters.length != 0) {
  		key = parameters[0];
    	if(JSON_PATH_EXPR_SELECT_ALL.equals(key) || key instanceof Integer) {
       	 int position = jsonPathExpressionString.lastIndexOf(JSON_PATH_EXPR_SELECT_PLACEHOLDER);
       	 jsonPathExpressionString.replace(position, position + JSON_PATH_EXPR_SELECT_PLACEHOLDER.length(), 
       			 JSON_PATH_EXPR_ARRAYNODE_OPEN+key+JSON_PATH_EXPR_ARRAYNODE_CLOSE+"");
    	}
  	}
	selectWhere.setLength(0);
	if(isArrayNodeOfData) {
		selectWhere.append(JSON_PATH_EXPR_LIST_IS_PLACEHOLDER);
	}
	else {
		selectWhere.append(JSON_PATH_EXPR_WHERE_IS_PLACEHOLDER);
	}
  	return this;
  }

  /**
   * Where.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder where(Object... parameters) {
	String key = (String) parameters[0];
	boolean reverseCondition = false;
	if(parameters.length>1 && parameters[1]!=null) {
		reverseCondition =  (boolean) parameters[1];
	}
	if(reverseCondition) {
		selectWhere.setLength(0);
		selectWhere.append("(<WHERE> == @.<IS>)");
	}
	int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_WHERE_PLACEHOLDER);
  	selectWhere.replace(position, position + JSON_PATH_EXPR_WHERE_PLACEHOLDER.length(), key);
  	return this;
  }

  /**
   * Checks if is.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder is(Object... parameters) {
	Object key = parameters[0];
	boolean forceNonString = false;
	if(parameters.length>1 && parameters[1]!=null) {
		forceNonString =  (boolean) parameters[1];
	}
	int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_IS_PLACEHOLDER);
	if(forceNonString) {
		selectWhere.replace(position, position + JSON_PATH_EXPR_IS_PLACEHOLDER.length(),key+"");
	}
	else {
	  	if(key instanceof String) {
	  		selectWhere.replace(position, position + JSON_PATH_EXPR_IS_PLACEHOLDER.length(), "\""+key.toString()+"\"");
	  	}
	  	else {
	  		selectWhere.replace(position, position + JSON_PATH_EXPR_IS_PLACEHOLDER.length(),key+"");
	  	}
	}  
  	arrayNodeConditions.add(selectWhere.toString());
  	return this;
  }
    
  /**
   * Lt.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder lt(Object...parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_LESS_THAN_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * Lte.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder lte(Object...parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_LESS_THAN_EQUAL_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * Gt.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder gt(Object... parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_GREATER_THAN_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * Gte.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder gte(Object... parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_GREATER_THAN_EQUAL_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * Ne.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder ne(Object... parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_NOT_EQUAL_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * Regex.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder regex(Object... parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_REGEX_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * In.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder in(Object... parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_IN_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * Nin.
   *
   * @param parameters the parameters
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder nin(Object... parameters) {
	  Object key = parameters[0];
	  replaceOperationPlaceHolder(JSON_PATH_EXPR_NOT_IN_QUERY_PLACEHOLDER);
	  if(parameters.length>1 && parameters[1]!=null) {
		  return is(key,parameters[1]);
	  }
	  return is(key);
  }
  
  /**
   * Replace operation place holder.
   *
   * @param queryPlaceHolder the query place holder
   */
  private void replaceOperationPlaceHolder(String queryPlaceHolder) {
	  if(JSON_PATH_EXPR_LESS_THAN_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_LESS_THAN_QUERY_PLACEHOLDER);
	  }
	  else if(JSON_PATH_EXPR_LESS_THAN_EQUAL_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_LESS_THAN_EQUAL_QUERY_PLACEHOLDER);
	  }
	  else if(JSON_PATH_EXPR_GREATER_THAN_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_GREATER_THAN_QUERY_PLACEHOLDER);
	  }
	  else if(JSON_PATH_EXPR_GREATER_THAN_EQUAL_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_GREATER_THAN_EQUAL_QUERY_PLACEHOLDER);
	  }
	  else if(JSON_PATH_EXPR_NOT_EQUAL_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_NOT_EQUAL_QUERY_PLACEHOLDER);
	  }
	  else if(JSON_PATH_EXPR_REGEX_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_REGEX_QUERY_PLACEHOLDER);
	  }
	  else if(JSON_PATH_EXPR_IN_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_IN_QUERY_PLACEHOLDER);
	  }
	  else if(JSON_PATH_EXPR_NOT_IN_QUERY_PLACEHOLDER.equals(queryPlaceHolder)) {
		  int position = selectWhere.lastIndexOf(JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER);
		  selectWhere.replace(position, position + JSON_PATH_EXPR_EQUAL_QUERY_PLACEHOLDER.length(),
				  JSON_PATH_EXPR_NOT_IN_QUERY_PLACEHOLDER);
	  }	
  }
   
  /**
   * And.
   *
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder and() {
  	arrayNodeConditions.add(" && ");
	selectWhere.setLength(0);
	if(isArrayNodeOfData) {
		selectWhere.append(JSON_PATH_EXPR_LIST_IS_PLACEHOLDER);
	}
	else {
		selectWhere.append(JSON_PATH_EXPR_WHERE_IS_PLACEHOLDER);
	}
  	return this;
  }

  /**
   * Or.
   *
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder or() {
  	arrayNodeConditions.add(" || ");
	selectWhere.setLength(0);
	if(isArrayNodeOfData) {
		selectWhere.append(JSON_PATH_EXPR_LIST_IS_PLACEHOLDER);
	}
	else {
		selectWhere.append(JSON_PATH_EXPR_WHERE_IS_PLACEHOLDER);
	}
  	return this;
  }

  /**
   * Not.
   *
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder not() {
  	arrayNodeConditions.add("!");
	selectWhere.setLength(0);
	if(isArrayNodeOfData) {
		selectWhere.append(JSON_PATH_EXPR_LIST_IS_PLACEHOLDER);
	}
	else {
		selectWhere.append(JSON_PATH_EXPR_WHERE_IS_PLACEHOLDER);
	}
  	return this;
  }

  /**
   * Builds the.
   *
   * @return the string
   */
  public String build() {
  	buildNode();
  	String evaluatedExpression = this.jsonPathExpressionString.toString();
  	clear();
  	return evaluatedExpression;
  }

  /**
   * Clear.
   *
   * @return the json path expression builder
   */
  public JsonPathExpressionBuilder clear() {
  	this.jsonPathExpressionString.setLength(0);
  	this.arrayNodeConditions.clear();
  	this.selectWhere.setLength(0);
  	this.jsonPathExpressionString.append("$");
	return this;
  }

}