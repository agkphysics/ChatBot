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

package agk.chatbot.cbr;

import java.util.ArrayList;
import java.util.Collection;

import jcolibri.cbrcore.*;

/**
 * This class implements to at least a basic level all case base features
 * specified in {@link CBRCaseBase}.
 * 
 * @author Aaron
 */
public class InMemoryCaseBase implements CBRCaseBase {

    protected Collection<CBRCase> cases;
    protected Connector connector;

    /**
     * Creates a new {@link InMemoryCaseBase} object.
     */
    public InMemoryCaseBase() {
        super();
        cases = new ArrayList<>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.casebase.LinealCaseBase#init(jcolibri.cbrcore.Connector)
     */
    @Override
    public void init(Connector connector) {
        this.connector = connector;
        cases = connector.retrieveAllCases();
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.casebase.LinealCaseBase#forgetCases(java.util.Collection)
     */
    @Override
    public void forgetCases(Collection<CBRCase> cases) {
        cases.removeAll(cases);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.casebase.LinealCaseBase#getCases()
     */
    @Override
    public Collection<CBRCase> getCases() {
        return cases;
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.casebase.LinealCaseBase#learnCases(java.util.Collection)
     */
    @Override
    public void learnCases(Collection<CBRCase> cases) {
        connector.storeCases(cases);
        cases.addAll(cases);
    }

    /*
     * (non-Javadoc)
     * 
     * @see jcolibri.casebase.LinealCaseBase#close()
     */
    @Override
    public void close() {
        cases.clear();
        connector.close();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * jcolibri.cbrcore.CBRCaseBase#getCases(jcolibri.cbrcore.CaseBaseFilter)
     */
    @Override
    public Collection<CBRCase> getCases(CaseBaseFilter filter) {
        return cases;
    }
}
