/**
 *
 */
package jcolibri.datatypes;

import jcolibri.connector.TypeAdaptor;

/**
 * @author Juanan
 *
 */
public class Text implements TypeAdaptor {

    protected String rawContent;

    public Text() {

    }

    public Text(String content) {
        rawContent = content;
    }

    /*
     * (non-Javadoc)
     *
     * @see jcolibri.connector.TypeAdaptor#fromString(java.lang.String)
     */
    @Override
    public void fromString(String content) throws Exception {
        rawContent = new String(content);
    }

    @Override
    public String toString() {
        return rawContent;
    }

}
