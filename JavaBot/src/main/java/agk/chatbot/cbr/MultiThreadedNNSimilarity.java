/*
 * Copyright (C) 2016-2018 Aaron Keesing
 *
 * This file is part of CBR Chat Bot.
 *
 * CBR Chat Bot is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * CBR Chat Bot is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * CBR Chat Bot. If not, see <http://www.gnu.org/licenses/>.
 */

package agk.chatbot.cbr;

import java.util.*;
import java.util.concurrent.*;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;

public class MultiThreadedNNSimilarity {

    public static List<RetrievalResult> evaluateSimilarity(final Collection<CBRCase> cases, final CBRQuery query, final NNConfig simConfig) {
        final List<RetrievalResult> coll = Collections.synchronizedList(new ArrayList<RetrievalResult>());

        ExecutorService executor = Executors.newWorkStealingPool();
        for (final CBRCase c : cases) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    GlobalSimilarityFunction gsf = simConfig.getDescriptionSimFunction();
                    coll.add(new RetrievalResult(c,
                            gsf.compute(c.getDescription(), query.getDescription(), c, query, simConfig)));
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
