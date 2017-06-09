/**
 * ExpertClerkMedianScoring.java jCOLIBRI2 framework.
 *
 * @author Juan A. Recio-Garc�a. GAIA - Group for Artificial Intelligence
 *         Applications http://gaia.fdi.ucm.es 04/11/2007
 */
package jcolibri.method.retrieve.DiverseByMedianRetrieval;

import java.util.*;

import jcolibri.cbrcore.*;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.util.AttributeUtils;
import jcolibri.util.CopyUtils;

/**
 * ExpertClerk Median algorithm. This algorithm chooses the first case that is
 * closed to the median of cases. Then the remaining are selected taking into
 * account negative and possitive characteristics. A characteristic is an
 * attribute that exceeds a predefined threshold. It is positive if is greater
 * thatn the value of the median. And negative otherwise. The number of positive
 * plus the negative characteristics is used to rank the cases and obtain the
 * following k-1 cases.
 * <p>
 * See:
 * <p>
 * H. Shimazu. ExpertClerk: A Conversational Case-Based Reasoning Tool for
 * Developing Salesclerk Agents in E-Commerce Webshops. Artif. Intell. Rev.,
 * 18(3-4):223-244, 2002.
 *
 * @author Juan A. Recio-Garcia
 * @author Developed at University College Cork (Ireland) in collaboration with
 *         Derek Bridge.
 * @version 1.0
 *
 */
public class ExpertClerkMedianScoring {
    /******************************************************************************/
    /** STATIC METHODS **/
    /******************************************************************************/

    /**
     * Returns diverse cases using the ExpertClerk median method.
     *
     * @param cases
     *            to retrieve from
     * @param simConfig
     *            is the nn configuration
     * @param thresholds
     *            to obtain the characteristics
     * @return a collection of cases
     */
    public static Collection<RetrievalResult> getDiverseByMedian(Collection<CBRCase> cases, NNConfig simConfig,
            HashMap<Attribute, Double> thresholds) {
        CaseComponent median = calculateMedian(cases);

        CBRQuery query = new CBRQuery();
        query.setDescription(median);
        Collection<RetrievalResult> distancesToMedian = NNScoringMethod.evaluateSimilarity(cases, query, simConfig);
        CBRCase first = distancesToMedian.iterator().next().get_case();

        ArrayList<RetrievalResult> characteristics = new ArrayList<>();
        double maxCharacteristics = AttributeUtils.getAttributes(cases.iterator().next().getDescription()).size();
        for (CBRCase _case : cases) {
            if (_case.equals(first)) continue;
            int chars = computeCharacteristics(_case, median, simConfig, thresholds);
            characteristics.add(new RetrievalResult(_case, (chars) / maxCharacteristics));
        }
        characteristics.add(new RetrievalResult(first, 1.0));
        java.util.Collections.sort(characteristics);

        return characteristics;
    }

    /**
     * Calculates the median
     */
    private static CaseComponent calculateMedian(Collection<CBRCase> cases) {
        HashMap<Attribute, HashMap<Object, Integer>> enumCount = new HashMap<>();
        HashMap<Attribute, Double> numValues = new HashMap<>();

        for (CBRCase _case : cases) {
            for (Attribute at : AttributeUtils.getAttributes(_case.getDescription())) {
                if (at.equals(_case.getDescription().getIdAttribute())) continue;
                Object value = AttributeUtils.findValue(at, _case.getDescription());
                if (value == null) continue;

                if (value instanceof Number) {
                    Double sum = numValues.get(at);
                    if (sum == null) numValues.put(at, ((Number)value).doubleValue());
                    else numValues.put(at, sum + ((Number)value).doubleValue());
                } else {
                    HashMap<Object, Integer> enumValues = enumCount.get(at);
                    if (enumValues == null) {
                        enumValues = new HashMap<>();
                        enumCount.put(at, enumValues);
                    }
                    Integer count = enumValues.get(value);
                    if (count == null) enumValues.put(value, new Integer(0));
                    else enumValues.put(value, new Integer(count + 1));
                }
            }
        }
        CaseComponent res = CopyUtils.copyCaseComponent(cases.iterator().next().getDescription());
        for (Attribute at : AttributeUtils.getAttributes(res)) {
            HashMap<Object, Integer> enumValues = enumCount.get(at);
            if (enumValues != null) {
                Object maxObject = null;
                int max = 0;
                for (Object value : enumValues.keySet()) {
                    Integer appears = enumValues.get(value);
                    if (appears > max) {
                        max = appears;
                        maxObject = value;
                    }
                }
                AttributeUtils.setValue(at, res, maxObject);
                continue;
            }
            Double sum = numValues.get(at);
            if (sum != null) {
                if (at.getType().equals(Integer.class))
                    AttributeUtils.setValue(at, res, ((Number)(sum / cases.size())).intValue());
                else if (at.getType().equals(Double.class)) AttributeUtils.setValue(at, res, (sum / cases.size()));
                else if (at.getType().equals(Float.class))
                    AttributeUtils.setValue(at, res, ((Number)(sum / cases.size())).floatValue());
                else if (at.getType().equals(Long.class))
                    AttributeUtils.setValue(at, res, ((Number)(sum / cases.size())).longValue());
                else if (at.getType().equals(Short.class))
                    AttributeUtils.setValue(at, res, ((Number)(sum / cases.size())).shortValue());
            } else AttributeUtils.setValue(at, res, null);
        }
        return res;
    }

    /**
     * Computes the characteristics
     */
    private static int computeCharacteristics(CBRCase _case, CaseComponent median, NNConfig simConfig,
            HashMap<Attribute, Double> thresholds) {
        int characteristics = 0;
        for (Attribute at : AttributeUtils.getAttributes(_case.getDescription())) {
            if (at.equals(_case.getDescription().getIdAttribute())) continue;

            Object value = AttributeUtils.findValue(at, _case.getDescription());
            Object medValue = AttributeUtils.findValue(at, median);

            if ((value == null) || (medValue == null)) continue;

            if (value instanceof Number) {
                double v = ((Number)value).doubleValue();
                double medV = ((Number)medValue).doubleValue();
                double ad = simConfig.getWeight(at) * (v - medV);

                Double threshold = thresholds.get(at);
                if (threshold == null) threshold = 0.5;
                if (Math.abs(ad) > threshold) characteristics++;
            } else if (value instanceof Enum) {
                double v = ((Enum<?>)value).ordinal();
                double medV = ((Enum<?>)medValue).ordinal();
                double ad = simConfig.getWeight(at) * (v - medV);

                Double threshold = thresholds.get(at);
                if (threshold == null) threshold = 1.0;
                if (Math.abs(ad) > threshold) characteristics++;
            } else if (!value.equals(medValue)) characteristics++;

        }
        return characteristics;
    }

    /*
     *
     * private static CaseComponent calculateMedian(Collection<CBRCase> cases) {
     * HashMap<Attribute,HashMap<Object,Integer>> enumCount = new
     * HashMap<Attribute, HashMap<Object,Integer>>(); HashMap<Attribute,Double>
     * numValues = new HashMap<Attribute,Double>();
     *
     * for(CBRCase _case :cases) { for(Attribute at :
     * AttributeUtils.getAttributes(_case.getDescription())) {
     * if(at.equals(_case.getDescription().getIdAttribute())) continue; Object
     * value = AttributeUtils.findValue(at, _case.getDescription()); if(value
     * instanceof Enum) { HashMap<Object,Integer> enumValues =
     * enumCount.get(at); if(enumValues == null) { enumValues = new
     * HashMap<Object,Integer>(); enumCount.put(at, enumValues); } Integer count
     * = enumValues.get(value); if(count == null) enumValues.put(value, new
     * Integer(0)); else enumValues.put(value, new Integer(count+1)); } else
     * if(value instanceof Number) { Double sum = numValues.get(at); if(sum ==
     * null) numValues.put(at, ((Number)value).doubleValue()); else
     * numValues.put(at, sum+((Number)value).doubleValue()); } } } CaseComponent
     * res =
     * CopyUtils.copyCaseComponent(cases.iterator().next().getDescription());
     * for(Attribute at: AttributeUtils.getAttributes(res)) {
     * HashMap<Object,Integer> enumValues = enumCount.get(at); if(enumValues !=
     * null) { Object maxObject = null; int max = 0; for(Object value :
     * enumValues.keySet()) { Integer appears = enumValues.get(value);
     * if(appears > max) { max = appears; maxObject = value; } }
     * AttributeUtils.setValue(at, res, maxObject); continue; } Double sum =
     * numValues.get(at); if(sum != null) {
     * if(at.getType().equals(Integer.class)) AttributeUtils.setValue(at, res,
     * ((Number)(sum/(double)cases.size())).intValue() ); else
     * if(at.getType().equals(Double.class)) AttributeUtils.setValue(at, res,
     * (sum/(double)cases.size()) ); else if(at.getType().equals(Float.class))
     * AttributeUtils.setValue(at, res,
     * ((Number)(sum/(double)cases.size())).floatValue() ); else
     * if(at.getType().equals(Long.class)) AttributeUtils.setValue(at, res,
     * ((Number)(sum/(double)cases.size())).longValue() ); else
     * if(at.getType().equals(Short.class)) AttributeUtils.setValue(at, res,
     * ((Number)(sum/(double)cases.size())).shortValue() ); } else
     * AttributeUtils.setValue(at, res, null); } return res; }
     *
     *
     */

}
