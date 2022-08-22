package org.pipeman.pa.permissions;

import java.net.URI;
import java.nio.file.*;

public class WildCardHelper {
    private static final FileSystem FS = FileSystems.getDefault();

    public static boolean matchesDomain(String domain, String matcher) {
        domain = domain.replace('.', '/');
        matcher = matcher.replace('.', '/');

        return FS.getPathMatcher("glob:" + matcher).matches(DebloatedPath.of(domain));
    }

    public static boolean matchesPath(String path, String matcher) {
        return FS.getPathMatcher("glob:" + matcher).matches(DebloatedPath.of(path));
    }

    public static void main(String[] args) {
        System.out.println(matchesPath("/!!a/", "/!(private,!a)/"));
    }


    private record DebloatedPath(String path) implements Path {

        public static DebloatedPath of(String path) {
            return new DebloatedPath(path);
        }

        @Override
        public FileSystem getFileSystem() {
            return null;
        }

        @Override
        public boolean isAbsolute() {
            return false;
        }

        @Override
        public Path getRoot() {
            return null;
        }

        @Override
        public Path getFileName() {
            return null;
        }

        @Override
        public Path getParent() {
            return null;
        }

        @Override
        public int getNameCount() {
            return 0;
        }

        @Override
        public Path getName(int index) {
            return null;
        }

        @Override
        public Path subpath(int beginIndex, int endIndex) {
            return null;
        }

        @Override
        public boolean startsWith(Path other) {
            return false;
        }

        @Override
        public boolean endsWith(Path other) {
            return false;
        }

        @Override
        public Path normalize() {
            return null;
        }

        @Override
        public Path resolve(Path other) {
            return null;
        }

        @Override
        public Path relativize(Path other) {
            return null;
        }

        @Override
        public URI toUri() {
            return null;
        }

        @Override
        public Path toAbsolutePath() {
            return null;
        }

        @Override
        public Path toRealPath(LinkOption... options) {
            return null;
        }

        @Override
        public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) {
            return null;
        }

        @Override
        public int compareTo(Path other) {
            return 0;
        }

        @Override
        public boolean equals(Object other) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public String toString() {
            return path;
        }
    }
}
