package engine.expressions.parser.auto_complete;

/**
 * User: Oleksiy Pylypenko
 * Date: 3/27/13
 * Time: 12:25 PM
 */
public class Completion {
    private final CompletionType type;
    private final String prefix;

    public Completion(CompletionType type, String prefix) {
        if (type == null) {
            throw new IllegalArgumentException("type");
        }
        if (prefix == null) {
            throw new IllegalArgumentException("prefix");
        }
        this.type = type;
        this.prefix = prefix;
    }

    public Completion(CompletionType type) {
        this(type, "");
    }

    public CompletionType getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Completion that = (Completion) o;

        if (!prefix.equals(that.prefix)) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + prefix.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return type + (prefix.isEmpty() ? "" : "(" + prefix + ")");
    }
}
