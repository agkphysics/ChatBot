package jcolibri.connector.plaintextutils;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import jcolibri.connector.TypeAdaptor;

/**
 * Converts data types between its textual representation and Java types. By
 * default it only manages a few data types:
 * <ul>
 * <li>BigDecimal
 * <li>Boolean
 * <li>Byte
 * <li>Date
 * <li>Double
 * <li>Float
 * <li>Int
 * <li>Long
 * <li>Object
 * <li>Short
 * <li>String
 * <li>URL
 * </ul>
 * Even so, developers can store any class in the text file if the class
 * implements the jcolibri.connectors.TypeAdaptor interface.
 *
 * @author Juan Antonio Recio Garc�a
 * @version 2.0
 * @see jcolibri.connector.TypeAdaptor
 */
public class PlainTextTypeConverter {

    /**
     * Coverts a string representation of the data in an object.
     *
     * @param <T>
     *
     * @param <T>
     *
     * @param data
     *            string representation of the data.
     * @param type
     *            type of the data.
     * @return the object trepresented by data and type.
     */
    @SuppressWarnings("unchecked")
    public static Object convert(String data, Class<?> type) {
        try {
            if (data == null) return null;
            else if (data.equals("null")) return null;
            else if (type.equals(BigDecimal.class)) return new BigDecimal(data);
            else if (type.equals(Boolean.class)) return new Boolean(data);
            else if (type.equals(Byte.class)) return new Byte(data);
            else if (type.equals(Date.class)) return new SimpleDateFormat().parse(data);
            else if (type.equals(Double.class)) return new Double(data);
            else if (type.equals(Float.class)) return new Float(data);
            else if (type.equals(Integer.class)) return new Integer(data);
            else if (type.equals(Long.class)) return new Long(data);
            else if (type.equals(Object.class)) return data;
            else if (type.equals(Short.class)) return new Short(data);
            else if (type.equals(String.class)) return data;
            else if (type.equals(URL.class)) return data;
            else if (Enum.class.isAssignableFrom(type)) {
                return Enum.valueOf((Class<? extends Enum>)type, data);
            } else {
                TypeAdaptor adaptor = (TypeAdaptor)type.newInstance();
                adaptor.fromString(data);
                return adaptor;
            }
        } catch (Exception e) {
            org.apache.commons.logging.LogFactory.getLog(PlainTextTypeConverter.class)
                    .error("Error converting types: " + e.getMessage());
        }

        return null;
    }
}
