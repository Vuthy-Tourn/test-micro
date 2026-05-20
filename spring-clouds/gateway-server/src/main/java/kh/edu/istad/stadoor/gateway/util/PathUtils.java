package kh.edu.istad.stadoor.gateway.util;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

public final class PathUtils {

    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

    private PathUtils() {
    }

    public static boolean matches(String pattern, String path) {
        PathPattern pathPattern = PATH_PATTERN_PARSER.parse(pattern);
        return pathPattern.matches(PathContainer.parsePath(path));
    }
}
