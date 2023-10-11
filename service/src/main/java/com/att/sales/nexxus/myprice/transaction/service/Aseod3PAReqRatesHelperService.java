package com.att.sales.nexxus.myprice.transaction.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.att.sales.nexxus.myprice.transaction.model.AllIncPrices3PA;
import com.att.sales.nexxus.myprice.transaction.model.Aseod3PACirReqRatesResponse;
import com.att.sales.nexxus.myprice.transaction.model.Aseod3PAMileageReqRatesResponse;
import com.att.sales.nexxus.myprice.transaction.model.Aseod3PAPortReqRatesResponse;

@Component
public class Aseod3PAReqRatesHelperService {
	
	private static final Logger log = LoggerFactory.getLogger(Aseod3PAReqRatesHelperService.class);
	
	
	@Autowired
	private Aseod3PACirReqRatesServiceImpl cirRateService;
	
	@Autowired
	private Aseod3PAMileageReqRatesServiceImpl mileageRateService;
	
	
	@Autowired
	private Aseod3PAPortReqRatesServiceImpl portRateService;
	
	/** The thread size. */
	@Value("${fmo.threadPool.size}")
	private Integer threadSize;
	
	/**
	 * Gets the thread size.
	 *
	 * @return the thread size
	 */
	public Integer getThreadSize() {
		return threadSize;
	}
	
	/**
	 * Gets the excutor service.
	 *
	 * @return the excutor service
	 */
	protected ExecutorService getExcutorService() {
        return Executors.newFixedThreadPool(getThreadSize());
    }
	
	
	public AllIncPrices3PA getIncPrices3PA(String transactionId) {
	
		AllIncPrices3PA allIncprices=new AllIncPrices3PA();
		try {
			List<Callable<Object>> callable = new ArrayList<Callable<Object>>();
			callable.add(new Executor<Aseod3PACirReqRatesResponse>(cirRateService, transactionId));
			callable.add(new Executor<Aseod3PAPortReqRatesResponse>(portRateService, transactionId));
			callable.add(new Executor<Aseod3PAMileageReqRatesResponse>(mileageRateService, transactionId));
			if(CollectionUtils.isNotEmpty(callable)) {
				ExecutorService executor = getExcutorService();
				try {
					List<Future<Object>> resultLst = executor.invokeAll(callable);
					for (Future<Object> data : resultLst) {
						if (null != data && null != data.get()) {
							Object o=data.get();
							if(o instanceof Aseod3PACirReqRatesResponse) {
								Aseod3PACirReqRatesResponse obj=(Aseod3PACirReqRatesResponse)o;
								if(null!=obj && CollectionUtils.isNotEmpty(obj.getItems())) {
									allIncprices.setAllIncCirPrices(obj.getItems());
								}
								
							}
							if(o instanceof Aseod3PAPortReqRatesResponse) {
								Aseod3PAPortReqRatesResponse obj=(Aseod3PAPortReqRatesResponse)o;
								if(null!=obj && CollectionUtils.isNotEmpty(obj.getItems())) {
									allIncprices.setAllIncPortPrices(obj.getItems());
								}
								
							}
							if(o instanceof Aseod3PAMileageReqRatesResponse) {
								Aseod3PAMileageReqRatesResponse obj=(Aseod3PAMileageReqRatesResponse)o;
								if(null!=obj && CollectionUtils.isNotEmpty(obj.getItems())) {
									allIncprices.setAllIncMileagePrices(obj.getItems());
								}
								
							}
						}
					}
					executor.shutdown();
					executor.awaitTermination(5, TimeUnit.SECONDS);
				}catch (CancellationException ce) {
					log.error("CancellationException while  getting 3PA InrPrices from Myprice: {}",transactionId,ce);
				} catch (ExecutionException ee) {
					log.error("ExecutionException while  getting 3PA InrPrices from Myprice: {}",transactionId,ee.getCause());
				} catch (InterruptedException e) {
					log.error("InterruptedException while  getting 3PA InrPrices from Myprice:{}",transactionId,e);
					Thread.currentThread().interrupt();
				} finally {
					if (!executor.isTerminated()) {
						log.error("cancel non-finished tasks");
					}
					executor.shutdownNow();
				}
			}
		}catch(Exception e) {
			log.error("Exception during getting 3PA InrPrices from Myprice: {}", transactionId,e);
		}
		
		if(CollectionUtils.isNotEmpty(allIncprices.getAllIncCirPrices())||
				CollectionUtils.isNotEmpty(allIncprices.getAllIncMileagePrices())||
				CollectionUtils.isNotEmpty(allIncprices.getAllIncPortPrices())) {
			return allIncprices;
		}
		
		return null;
		
	}
	
	 class Executor<T> implements Callable<Object> {

		private String transactionId;
		private Asenod3PAService<T> obj;
		public Executor(Asenod3PAService<T> obj,String transactionId) {
			this.obj=obj;
			this.transactionId=transactionId;
		}
		@Override
		public Object call() throws Exception {
			return obj.process(transactionId);
		}
		
	}
	
	
}
