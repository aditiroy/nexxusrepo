/*
 * package com.att.sales.nexxus.filter;
 * 
 * import java.io.IOException;
 * 
 * import javax.servlet.Filter; import javax.servlet.FilterChain; import
 * javax.servlet.FilterConfig; import javax.servlet.ServletException; import
 * javax.servlet.ServletRequest; import javax.servlet.ServletResponse; import
 * javax.servlet.http.HttpServletRequest; import
 * javax.servlet.http.HttpServletResponse; import
 * javax.servlet.http.HttpServletResponseWrapper;
 * 
 * import org.apache.commons.lang3.StringUtils; import org.slf4j.Logger; import
 * org.slf4j.LoggerFactory; import org.springframework.stereotype.Component;
 * 
 * @Component //@Order(Ordered.HIGHEST_PRECEDENCE) public class
 * RequestResponseLoggingFilter implements Filter { private static Logger log =
 * LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
 * 
 * @Override public void doFilter(ServletRequest request, ServletResponse
 * response, FilterChain chain) throws IOException, ServletException {
 * 
 * HttpServletRequest req = (HttpServletRequest) request; HttpServletResponse
 * res = (HttpServletResponse) response; //log.info("Logging Request  {} : {}",
 * StringUtils.normalizeSpace(req.getMethod()),
 * StringUtils.normalizeSpace(req.getRequestURI())); chain.doFilter(request, new
 * HttpServletResponseWrapper((HttpServletResponse) response) { public void
 * setHeader(String name, String value) { if (req.getRequestURI().toString()
 * .equalsIgnoreCase("/nexxus/bulkUploadEthTokens")) {
 * log.info("@ line # 37: name="+name+" | value="+value); if
 * (!name.equalsIgnoreCase("Transfer-Encoding")) { log.info("@ line # 39");
 * super.setHeader(name, value); } else { // super.setHeader(name, null);
 * log.info("Skipping Content-Length Header"); } }
 * 
 * if (req.getRequestURI().toString()
 * .equalsIgnoreCase("/restservices/nexxusdesign/v1/nexxus/getInventory")) {
 * log.info("getinventory headers "+name+" : "+value); //Skipping Content-Length
 * Header if (!name.equalsIgnoreCase("Content-Length")) { super.setHeader(name,
 * value); } } } }); // chain.doFilter(request, response);
 * log.info("Logging Response :{}", res.getContentType()); }
 * 
 * @Override public void init(FilterConfig filterConfig) throws ServletException
 * { // TODO Auto-generated method stub
 * 
 * }
 * 
 * @Override public void destroy() { // TODO Auto-generated method stub
 * 
 * } }
 */