/**
 * This package contains the Recommenders Extension.
 * <p>
 * The Recommenders extension has been designed having in mind the future design
 * process of CBR systems in jCOLIBRI 2.
 * <p>
 * We proposes a flexible way to design CBR systems in jCOLIBRI 2 using a
 * library of templates obtained from a previously designed set of CBR systems.
 * In case-based fashion, jCOLIBRI will retrieve templates from a library of
 * templates (i.e. a case base of CBR design experience); the designer will
 * choose one, and adapt it.
 * <p>
 * We represent templates graphically as shown in following figures. Each
 * rectangle in the template is a subtask. Simple tasks (shown as blue or pale
 * grey rectangles) can be solved directly by a method included in this
 * extension. Complex tasks (shown as red or dark grey rectangles) are solved by
 * decomposition methods having other associated templates. There may be
 * multiple alternative methods to solve any given task.
 * <p>
 * Before implementing this extension, we developed templates for recommender
 * systems and then generated the methods that solve every task. That allowed us
 * to develop many recommender systems that are included in the
 * <b>{@link jcolibri.test.recommenders}</b> package. By now, templates are only
 * a graphical representation of CBR systems, although in a short future they
 * will be used to generate them.
 * <p>
 * Following text has been extracted from:<br>
 * <i>Recio-Garc�a, J. A., D�az-Agudo, B., Bridge, D. & Gonz�lez-Calero, P. A.
 * 2007 Semantic Templates for Designing Recommender Systems. Procs. of the 12th
 * UK Workshop on Case-Based Reasoning, CMS Press, University of Greenwich,
 * UK.</i>
 * <p>
 * <b>The jCOLIBRI team thanks to Derek Bridge his collaboration and supervision
 * during the development of this extension.</b>
 *
 * <h2>Templates for Recommender systems</h2> We have done an analysis of
 * recommender systems that is based in part on the conceptual framework
 * described in the review paper by Bridge et al. (2006). The framework
 * distinguishes between collaborative and case-based, reactive and proactive,
 * single-shot and conversational, and asking and proposing. Within this
 * framework, the authors review a selection of papers from the case-based
 * recommender systems literature, covering the development of these systems
 * over the last ten years.
 *
 * We take the systems� interaction behaviour as the fundamental distinction
 * from which we construct recommenders:
 * <ul>
 * <li>Single-Shot Systems make a suggestion and finish. Figure 1 shows the
 * template for this kind of system, where One-Off Preference Elicitation and
 * Retrieval are complex tasks that are solved by decomposition methods having
 * other associated templates. <center><img src="fig2.jpg"/><br>
 * Figure 1
 * <p>
 * </center></li>
 * <li>After retrieving items, Conversational Systems (Figure 2) may invite or
 * allow the user to refine his/her current preferences, typically based on the
 * recommended items. Iterated Preference Elicitation might be done by allowing
 * the user to select and critique a recommended item thereby producing a
 * modified query, which requires that one or more retrieved items be displayed
 * (Figure 2 left). Alternatively, it might be done by asking the user a further
 * question or questions thereby refining the query, in which case the retrieved
 * items might be displayed every time (Figure 2 left) or might be displayed
 * only when some criterion is satisfied (e.g. when the size of the set is
 * �small enough�) (Figure 2 right). Note that both templates share the One-Off
 * Preference Elicitation and Retrieval tasks with single-shot systems.
 * <center><img src="fig3.jpg"/><br>
 * Figure 2
 * <p>
 * </center></li>
 * </ul>
 *
 * <h3>One-Off Preference Elicitation</h3> We can identify three templates by
 * which the user�s initial preferences may be elicited:
 * <ul>
 * <li>One possibility is Profile Identification where the user identifies
 * him/herself, e.g. by logging in, enabling retrieval of a user profile from a
 * profile database. This profile might be a content-based profile (e.g.
 * keywords describing the user�s long-term interests, or descriptions of items
 * consumed previously by this user, or descriptions of sessions this user has
 * engaged in previously with this system); or it might be a collaborative
 * filtering style of profile (e.g. the user�s ratings for items). The template
 * allows for the possibility that the user is a new user, in which case there
 * may be some registration process followed by a complex task that elicits an
 * initial profile.</li>
 * <li>An alternative is Initial Query Elicitation. This is itself a complex
 * task with multiple alternative decompositions. The decompositions include:
 * Form-Filling (Figure 3 left) and Navigation-by-Asking (i.e. choosing and
 * asking a question) (Figure 3 center). Various versions of the Entr�e system
 * (Burke 2002) offered interesting further methods: Identify an Item (where the
 * user gives enough information to identify an item that s/he likes, e.g. a
 * restaurant in his/her home town, whose description forms the basis of a
 * query, e.g. for a restaurant in the town being visited); and Select an
 * Exemplar (where a small set of contrasting items is selected and displayed,
 * the user chooses the one most similar to what s/he is seeking, and its
 * description forms the basis of a query).</li>
 * <li>The third possibility is Profile Identification & Query Elicitation, in
 * which the previous two tasks are combined.</li>
 * </ul>
 *
 * <center><img src="fig4.jpg"/><br>
 * Figure 3
 * <p>
 * </center>
 *
 * <h3>Retrieval</h3> Because we are focussing on case-based recommender systems
 * (and related memory-based recommenders including collaborative filters),
 * Retrieval is common to all our recommender systems. Retrieval is a complex
 * task, with many alternative decompositions. The choice of decomposition is,
 * of course, not independent of the choice of decomposition for One-Off
 * Preference Elicitation and Iterated Preference Elicitation. For example, if
 * One-Off Preference Elicitation delivers a ratings profile, then the method
 * chosen for achieving the Retrieval task must be some form of collaborative
 * recommendation.
 * <p>
 * The following is a non-exhaustive list of papers that define methods that can
 * achieve the Retrieval task: Wilke et al. 1998 (similarity-based retrieval
 * using a query of preferred values); Smyth & McClave 2001 (diversity-enhanced
 * similarity-based retrieval); McSherry 2002 (diversity-conscious retrieval);
 * Bridge & Fergsuon 2002 (order-based retrieval); McSherry 2003
 * (compromise-driven retrieval); Bradley & Smyth 2003 (where user profiles are
 * mined and used); Herlocker et al. 1991 (user-based collaborative filtering);
 * Sarwar et al. 2001 (item-based collaborative filtering). In all these ways of
 * achieving Retrieval, a scoring process is followed by a selection process.
 * For example, in similarity-based retrieval (k-NN), items are scored by their
 * similarity to the user�s preferences and then the k highest-scoring items are
 * selected for display; in diversity-enhanced similarity-based retrieval, items
 * are scored in the same way and then a diverse set is selected from the
 * highest-scoring items; and so on. Note also that there are alternative
 * decompositions of the Retrieval task that would not have this two-step
 * character. For example, filter-based retrieval, where the user�s preferences
 * are treated as hard constraints, conventionally does not decompose into two
 * such steps. On the other hand, there are recommender systems in which
 * Retrieval decomposes into more than two steps. For example, in some forms of
 * Navigation-by-Proposing (see below), first a set of items that satisfy the
 * user�s critique is obtained by filter-based retrieval, then these are scored
 * for similarity to the user�s selected item, and finally a subset is chosen
 * for display to the user.
 *
 * <h3>Iterated Preference Elicitation</h3> In Iterated Preference Elicitation
 * the user, who may or may not have just been shown some products (Figure 2),
 * may, either voluntarily or at the system�s behest, provide further
 * information about his/her preferences. Alternative decompositions of this
 * task include:
 * <ul>
 * <li>Form-Filling where the user enters values into a form that usually has
 * the same structure as items in the database (Figure 3 left). We have seen
 * that Form-Filling is also used for One-Off Preference Elicitation. When it is
 * used in Iterated Preference Elicitation, it is most likely that the user
 * edits values s/he previously entered into the form.
 * <li>Navigation-by-Asking is another method that can be used for both One-Off
 * Preference Elicitation and for Iterated Preference Elicitation. The system
 * refines the query with the user�s answer to a question about his/her
 * preferences. The system uses a heuristic to choose the next best question to
 * ask. Bergmann reviews some of the methods that have been used to choose this
 * question (Bergmann 2002).
 * <li>Navigation-by-Proposing (also known as tweaking and as critiquing)
 * requires that the user has been shown a set of candidate items. S/he selects
 * the one that comes closest to satisfying his/her requirements but then offers
 * a critique (e.g. �like this but cheaper�). A complex query is constructed
 * that is intended to retrieve items that are similar to the selected item but
 * which also satisfy the critique. The selection of the candidate item and its
 * critiques must be performed during the Display Item List task. Therefore, the
 * Create Complex Query task will receive that information and modify the query
 * according to the user selection. Burke reviews early work on this topic
 * (Burke 2002). We note that there has been a body of new work since then, some
 * of it cited in (Bridge et al. 2006) and (Smyth 2007). (Although we describe
 * Navigation-by-Proposing only as a decomposition of Iterated Preference
 * Elicitation, this need not be so. We could additionally, for example, define
 * a template for One-Off Preference Elicitation that uses Select an Exemplar
 * followed by Navigation-by-Proposing.)
 * </ul>
 * Note that the templates for Conversational Systems do not preclude the
 * possibility that the system uses a different method for Iterated Preference
 * Elicitation on different iterations. ExpertClerk is an example of such a
 * recommender. It uses Navigation-by-Asking for One-Off Preference Elicitation,
 * and then it chooses between Navigation-by-Asking and Navigation-by-Proposing
 * for Iterated Preference Elicitation, preferring the former while the set of
 * candidate items remains large (Shimazu 2002). In fact, we have confirmed that
 * we can build a version of ExpertClerk based on the template in Figure 2b and
 * using methods included in this extension (see recommender 8). This informally
 * illustrates the promise of our templates approach: complex systems (such as
 * ExpertClerk) can be constructed by adapting existing templates.
 *
 * <h3>References</h3>
 * <ul>
 * <li>Bergmann, R (2002): Experience Management: Foundations, Development
 * Methodology, and Internet-Based Applications. Springer.
 * <li>Bradley, K & Smyth, B. (2003): Personalized information ordering: a case
 * study in online recruitment. Knowledge-Based Systems, vol.16(5-6): 269-275.
 * <li>Bridge, D & Ferguson, A. (2002): An expressive query language for product
 * recommender systems. Artificial Intelligence Review, vol. 18(3�4), 269�307.
 * <li>Bridge, D., G�ker, M., McGinty, L., & Smyth, B. (2006): Case-based
 * recommender systems. The Knowledge Engineering Review, vol. 23(3): 315-320.
 * <li>Burke, R. (2002): Interactive Critiquing for catalog navigation in
 * e-commerce, vol.18(3-4): 245-267.
 * <li>Herlocker, J., Konstan, J.A., Borchers, A. & Riedl, J (1999): An
 * algorithmic framework for performing collaborative filtering. In Procs. of
 * SIGIR, ACM Press, pp.230-237.
 * <li>McSherry Diversity-conscious retrieval. In Craw, S. & Preece, A. eds.
 * Procs. of the 6th European Conference on Case-Based Reasoning Springer pp.
 * 219-233.
 * <li>McSherry, D, (2003): Similarity and compromise. In K.D.Ashley &
 * D.G.Bridge (eds) Procs. of the 5th International Conference on Case-Based
 * Reasoning. Springer, pp.291�305.
 * <li>Sarwar, B., Karypis, G., Konstan, J.A. & Riedl, J. (2001): Item-based
 * collaborative filtering recommendation algorithms. In Procs of the 10th
 * International Conference on the WWW, ACM Press, pp.285-295.
 * <li>Shimazu H. ExpertClerk: a conversational case-based reasoning tool for
 * developing salesclerk agents in e-commerce webshops. Artificial Intelligence
 * Review vol.18(3-4),223-244.
 * <li>Smyth, B. (2007): Case-based recommendation. In P.Brusilovsky, A.Kobsa &
 * W.Nedjdl (eds.), The Adaptive Web. Springer.
 * <li>Smyth, B & McClave, P. (2001): Similarity vs. diversity. In D.W.Aha &
 * I.Watson (eds) Procs of the 4th International Conference on Case-Based
 * Reasoning. Springer, pp. 347�361.
 * <li>Wilke, W., Lenz, M. & Wess.S (1998): Intelligent sales support with CBR.
 * In M.Lenz et al. Case-Based Reasoning Technology: From Foundations to
 * Applications, Springer, pp.91-113.
 * </ul>
 *
 * <h2>The Recommenders Extension organization</h2> This table summarizes the
 * organization of the implemented methods: <table border="1" cellspacing="2"
 * cellpadding="2" layout-flow:vertical-ideographic;>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.askingAndProposing}</td>
 * <td>Methods to implement the ExpertClerk system</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.casesDisplay}</td>
 * <td>Methods to display cases</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.collaborative}</td>
 * <td>Collaborative retrieval methods</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.conditionals}</td>
 * <td>Conditional methods of the templates</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.ContentBasedProfile}</td>
 * <td>Methods to implement content based profile recommenders</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.navigationByAsking}</td>
 * <td>Navigation by Asking methods</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.navigationByProposing}</td>
 * <td>Navigation by Proposing methods</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.extensions.recommendation.tabuList}</td>
 * <td>Methods to use a tabu list in recommender systems</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.method.retrieve.DiverseByMedianRetrieval}</td>
 * <td>Diversity by Median retrieval</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.method.retrieve.FilterBasedRetrieval}</td>
 * <td>Filter based retrieval</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.method.retrieve.selection.compromiseDriven}</td>
 * <td>Compromise Driven selection</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.method.retrieve.selection.diversity}</td>
 * <td>Diversity selection methods</td>
 * </tr>
 * <tr>
 * <td>{@link jcolibri.method.retrieve.NNretrieval.similarity.local.recommenders}</td>
 * <td>Specific local similarity measures for recommendation systems</td>
 * </tr>
 * </table>
 */
package jcolibri.extensions.recommendation;
