package engine.expressions.parser.parboiled;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/24/13
 * Time: 10:20 PM
 */
class HumanReadable {
    public static final String hr(Collection<String> coll) {
        List<String> list = new ArrayList<String>(coll);
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                builder.append(" or ");
            } else if (i >= 1) {
                builder.append(", ");
            }

            builder.append(hr(list.get(i)));
        }
        return builder.toString();
    }

    public static String hr(String s) {
        if ("MathFunc".equals(s)) {
            return "mathematical function";
        }
        return s.toLowerCase();
    }
}
