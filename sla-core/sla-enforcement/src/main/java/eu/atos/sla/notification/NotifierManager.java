package eu.atos.sla.notification;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import eu.atos.sla.datamodel.IAgreement;
import eu.atos.sla.datamodel.IGuaranteeTerm;
import eu.atos.sla.evaluation.guarantee.GuaranteeTermEvaluator.GuaranteeTermEvaluationResult;



public class NotifierManager  implements INotifierManager ,InitializingBean, Runnable {
	private static Logger logger = LoggerFactory.getLogger(NotifierManager.class);

	volatile LinkedBlockingQueue<ResultInformation> resultInformationQueue;
	IAgreementEnforcementNotifier agreementEnforcementNotifier;

	public void setAgreementEnforcementNotifier(
			IAgreementEnforcementNotifier agreementEnforcementNotifier) {
		this.agreementEnforcementNotifier = agreementEnforcementNotifier;
	}


	
	public NotifierManager() {
		resultInformationQueue = new LinkedBlockingQueue<ResultInformation>();
		(new Thread(this)).start();
		logger.debug("NotifierManager has started");
	}


	public void addToBeNotified(IAgreement agreement,
		Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> evaluationResult) {
		try {
			resultInformationQueue.put(new ResultInformation(agreement, evaluationResult));
		} catch (InterruptedException e) {
			logger.error("Fatal error inserting. Notification system is probalby to slow. The queue is full", e);
		}
		logger.debug("ResultInfo for agreement {} has been added", agreement.getId());
	}
	
	@Override
	public void run() {
		// will do the notification if something is in the queue, the queue blocks until something is available
		boolean exit = false;
		try {
			while (!exit){
				ResultInformation resultInformation = resultInformationQueue.take();
				if (agreementEnforcementNotifier!= null)
					try {
						agreementEnforcementNotifier.onFinishEvaluation(resultInformation.agreement, resultInformation.evaluationResult);
					} catch (NotificationException e) {
						logger.error(e.getMessage(), e.getCause());
						
					}
			}
		} catch (InterruptedException e) {
			exit = true;
			logger.debug("Interrupted, probably program shutting down and nothing was in queue.", e);
		}
	}

		

	protected class ResultInformation{
		IAgreement agreement;
		Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> evaluationResult;
		
		protected ResultInformation(IAgreement agreement, Map<IGuaranteeTerm, GuaranteeTermEvaluationResult> evaluationResult){
			this.agreement = agreement;;
			this.evaluationResult = evaluationResult;
		
		}
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}



}