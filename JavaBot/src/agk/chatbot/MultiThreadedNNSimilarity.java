package agk.chatbot;

import java.util.*;
import java.util.concurrent.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;

public class MultiThreadedNNSimilarity extends NNScoringMethod {
	
	@SuppressWarnings("unchecked")
	public static Collection<RetrievalResult> evaluateSimilarity(Collection<CBRCase> cases, CBRQuery query, NNConfig simConfig) {
		List<RetrievalResult> coll = Collections.synchronizedList(new ArrayList<>());

		ExecutorService executor = Executors.newWorkStealingPool();
		for (CBRCase c : cases) {
			executor.submit(new Runnable() {
				@Override
				public void run() {
					GlobalSimilarityFunction gsf = simConfig.getDescriptionSimFunction();
					coll.add(new RetrievalResult(c, gsf.compute(c.getDescription(), query.getDescription(), c, query, simConfig)));
				}
			});
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Collections.sort(coll);
		
		return coll;
	}

}
