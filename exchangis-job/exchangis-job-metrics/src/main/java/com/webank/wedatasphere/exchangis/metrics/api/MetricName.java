package com.webank.wedatasphere.exchangis.metrics.api;

/**
 * A metric name with the ability to include semantic tags.
 *
 * This replaces the previous style where metric names where strictly
 * dot-separated strings.
 *
 */
public class MetricName {
    public static final String SEPARATOR = ".";
    public static final MetricName EMPTY = new MetricName();

    private String norm;

    private String title;

    public MetricName() {
        this(null, null);
    }

    public MetricName(String norm) {
        this(norm, null);
    }

    public MetricName(String norm, String title) {
        this.norm = norm;
        this.title = title;
    }

    /**
     * Build a new metric name using the specific path components.
     *
     * @param parts Path of the new metric name.
     * @return A newly created metric name with the specified path.
     **/
    public static MetricName build(String... parts) {
        if (parts == null || parts.length == 0)
            return MetricName.EMPTY;

        if (parts.length == 1)
            return new MetricName(parts[0]);

        return new MetricName(buildName(parts));
    }

    public MetricName withTitle(String title) {
        this.title = title;
        return this;
    }

    private static String buildName(String... names) {
        final StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (String name : names) {
            if (name == null || name.isEmpty())
                continue;

            if (first) {
                first = false;
            } else {
                builder.append(SEPARATOR);
            }

            builder.append(name);
        }

        return builder.toString();
    }

    public String getNorm() {
        return norm;
    }

    public void setNorm(String norm) {
        this.norm = norm;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
