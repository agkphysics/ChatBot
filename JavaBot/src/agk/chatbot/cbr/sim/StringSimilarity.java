/*
 * Copyright (C) 2016, 2017 Aaron Keesing
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

package agk.chatbot.cbr.sim;

import info.debatty.java.stringsimilarity.Cosine;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;
import info.debatty.java.stringsimilarity.SorensenDice;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import jcolibri.datatypes.Text;
import jcolibri.exception.NoApplicableSimilarityFunctionException;
import jcolibri.method.retrieve.NNretrieval.similarity.LocalSimilarityFunction;

public class StringSimilarity implements LocalSimilarityFunction {

    private NormalizedStringSimilarity simFunction;

    public StringSimilarity(String method) {
        switch (method.toLowerCase()) {
        case "sorensen":
            simFunction = new SorensenDice();
            break;
        case "jaccard":
            simFunction = new Jaccard();
            break;
        case "jaro":
            simFunction = new JaroWinkler();
            break;
        case "cosine":
            simFunction = new Cosine();
            break;
        case "hamming":
            simFunction = new HammingDistance();
            break;
        case "levenschtein":
        default:
            simFunction = new NormalizedLevenshtein();
            break;
        }
    }

    @Override
    public double compute(Object caseObject, Object queryObject) throws NoApplicableSimilarityFunctionException {
        if (!isApplicable(caseObject, queryObject)) {
            throw new NoApplicableSimilarityFunctionException("Cannot compare strings with type "
                    + caseObject.getClass().getName() + " and " + queryObject.getClass().getName());
        } else if (caseObject == null || queryObject == null) return 0.0;

        // Convert to strings
        String caseString, queryString;
        if (caseObject instanceof Text) caseString = ((Text)caseObject).toString();
        else caseString = (String)caseObject;
        if (queryObject instanceof Text) queryString = ((Text)queryObject).toString();
        else queryString = (String)queryObject;

        return simFunction.similarity(caseString, queryString);
    }

    @Override
    public boolean isApplicable(Object caseObject, Object queryObject) {
        return (caseObject instanceof String || caseObject instanceof Text)
                || (queryObject instanceof String || queryObject instanceof Text);
    }

}
