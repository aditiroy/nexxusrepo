package com.att.sales.nexxus.output.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * The Class NxOutputBean.
 *
 * @author vt393d
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NxOutputBean implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The nx avpn output. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxAvpnOutputBean> nxAvpnOutput;
	
	/** The nx avpn intl output bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxAvpnIntAccessOutputBean> nxAvpnIntlOutputBean;
	
	/** The nx mis DS 1 access bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxDsAccessBean> nxMisDS1AccessBean;
	
	/** The nx avpn DS 0 DS 1 access bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxDsAccessBean> nxAvpnDS0DS1AccessBean;
	
	/** The nx avpn DS 1 flat rate access bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxDsAccessBean> nxAvpnDS1FlatRateAccessBean;
	
	/** The nx ds 3 access bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxDsAccessBean> nxDs3AccessBean;
	
	/** The nx ethernet acc output bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxEthernetAccessOutputBean> nxEthernetAccOutputBean;
	
	/** The nx adi mis bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxMisBean> nxAdiMisBean;
	
	/** The nx bvoip output bean. */
	@JsonInclude(Include.NON_EMPTY)
	private List<NxBvoipOutputBean> nxBvoipOutputBean;

	/**
	 * Instantiates a new nx output bean.
	 */
	public NxOutputBean() {
		nxAvpnOutput = new ArrayList<>();
		nxAvpnIntlOutputBean = new ArrayList<>();
		nxMisDS1AccessBean = new ArrayList<>();
		nxAvpnDS0DS1AccessBean = new ArrayList<>();
		nxAvpnDS1FlatRateAccessBean = new ArrayList<>();
		nxDs3AccessBean = new ArrayList<>();
		nxEthernetAccOutputBean = new ArrayList<>();
		nxAdiMisBean = new ArrayList<>();
		nxBvoipOutputBean = new ArrayList<>();
	}

	/**
	 * Gets the nx avpn output.
	 *
	 * @return the nx avpn output
	 */
	public List<NxAvpnOutputBean> getNxAvpnOutput() {
		return nxAvpnOutput;
	}

	/**
	 * Sets the nx avpn output.
	 *
	 * @param nxAvpnOutput the new nx avpn output
	 */
	public void setNxAvpnOutput(List<NxAvpnOutputBean> nxAvpnOutput) {
		this.nxAvpnOutput = nxAvpnOutput;
	}

	/**
	 * Gets the nx avpn intl output bean.
	 *
	 * @return the nx avpn intl output bean
	 */
	public List<NxAvpnIntAccessOutputBean> getNxAvpnIntlOutputBean() {
		return nxAvpnIntlOutputBean;
	}

	/**
	 * Sets the nx avpn intl output bean.
	 *
	 * @param nxAvpnIntlOutputBean the new nx avpn intl output bean
	 */
	public void setNxAvpnIntlOutputBean(List<NxAvpnIntAccessOutputBean> nxAvpnIntlOutputBean) {
		this.nxAvpnIntlOutputBean = nxAvpnIntlOutputBean;
	}

	/**
	 * Gets the nx mis DS 1 access bean.
	 *
	 * @return the nx mis DS 1 access bean
	 */
	public List<NxDsAccessBean> getNxMisDS1AccessBean() {
		return nxMisDS1AccessBean;
	}

	/**
	 * Sets the nx mis DS 1 access bean.
	 *
	 * @param nxMisDS1AccessBean the new nx mis DS 1 access bean
	 */
	public void setNxMisDS1AccessBean(List<NxDsAccessBean> nxMisDS1AccessBean) {
		this.nxMisDS1AccessBean = nxMisDS1AccessBean;
	}

	/**
	 * Gets the nx avpn DS 0 DS 1 access bean.
	 *
	 * @return the nx avpn DS 0 DS 1 access bean
	 */
	public List<NxDsAccessBean> getNxAvpnDS0DS1AccessBean() {
		return nxAvpnDS0DS1AccessBean;
	}

	/**
	 * Sets the nx avpn DS 0 DS 1 access bean.
	 *
	 * @param nxAvpnDS0DS1AccessBean the new nx avpn DS 0 DS 1 access bean
	 */
	public void setNxAvpnDS0DS1AccessBean(List<NxDsAccessBean> nxAvpnDS0DS1AccessBean) {
		this.nxAvpnDS0DS1AccessBean = nxAvpnDS0DS1AccessBean;
	}

	/**
	 * Gets the nx avpn DS 1 flat rate access bean.
	 *
	 * @return the nx avpn DS 1 flat rate access bean
	 */
	public List<NxDsAccessBean> getNxAvpnDS1FlatRateAccessBean() {
		return nxAvpnDS1FlatRateAccessBean;
	}

	/**
	 * Sets the nx avpn DS 1 flat rate access bean.
	 *
	 * @param nxAvpnDS1FlatRateAccessBean the new nx avpn DS 1 flat rate access bean
	 */
	public void setNxAvpnDS1FlatRateAccessBean(List<NxDsAccessBean> nxAvpnDS1FlatRateAccessBean) {
		this.nxAvpnDS1FlatRateAccessBean = nxAvpnDS1FlatRateAccessBean;
	}

	/**
	 * Gets the nx ds 3 access bean.
	 *
	 * @return the nx ds 3 access bean
	 */
	public List<NxDsAccessBean> getNxDs3AccessBean() {
		return nxDs3AccessBean;
	}

	/**
	 * Sets the nx ds 3 access bean.
	 *
	 * @param nxDs3AccessBean the new nx ds 3 access bean
	 */
	public void setNxDs3AccessBean(List<NxDsAccessBean> nxDs3AccessBean) {
		this.nxDs3AccessBean = nxDs3AccessBean;
	}

	/**
	 * Gets the nx ethernet acc output bean.
	 *
	 * @return the nx ethernet acc output bean
	 */
	public List<NxEthernetAccessOutputBean> getNxEthernetAccOutputBean() {
		return nxEthernetAccOutputBean;
	}

	/**
	 * Sets the nx ethernet acc output bean.
	 *
	 * @param nxEthernetAccOutputBean the new nx ethernet acc output bean
	 */
	public void setNxEthernetAccOutputBean(List<NxEthernetAccessOutputBean> nxEthernetAccOutputBean) {
		this.nxEthernetAccOutputBean = nxEthernetAccOutputBean;
	}

	/**
	 * Gets the nx adi mis bean.
	 *
	 * @return the nx adi mis bean
	 */
	public List<NxMisBean> getNxAdiMisBean() {
		return nxAdiMisBean;
	}

	/**
	 * Sets the nx adi mis bean.
	 *
	 * @param nxAdiMisBean the new nx adi mis bean
	 */
	public void setNxAdiMisBean(List<NxMisBean> nxAdiMisBean) {
		this.nxAdiMisBean = nxAdiMisBean;
	}

	/**
	 * Gets the nx bvoip output bean.
	 *
	 * @return the nx bvoip output bean
	 */
	public List<NxBvoipOutputBean> getNxBvoipOutputBean() {
		return nxBvoipOutputBean;
	}

	/**
	 * Sets the nx bvoip output bean.
	 *
	 * @param nxBvoipOutputBean the new nx bvoip output bean
	 */
	public void setNxBvoipOutputBean(List<NxBvoipOutputBean> nxBvoipOutputBean) {
		this.nxBvoipOutputBean = nxBvoipOutputBean;
	}

	/**
	 * Checks for value.
	 *
	 * @return true, if successful
	 */
	public boolean hasValue() {
		return !nxAvpnOutput.isEmpty() || !nxAvpnIntlOutputBean.isEmpty() || !nxMisDS1AccessBean.isEmpty()
				|| !nxAvpnDS0DS1AccessBean.isEmpty() || !nxAvpnDS1FlatRateAccessBean.isEmpty()
				|| !nxDs3AccessBean.isEmpty() || !nxEthernetAccOutputBean.isEmpty() || !nxAdiMisBean.isEmpty()
				|| !nxBvoipOutputBean.isEmpty();
	}
}
