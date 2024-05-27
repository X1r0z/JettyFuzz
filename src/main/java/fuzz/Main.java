package fuzz;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.FuzzerSecurityIssueCritical;
import com.code_intelligence.jazzer.api.Jazzer;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.PathResource;

import java.net.URI;

public class Main {
    public static void fuzzerTestOneInput(FuzzedDataProvider data) {
        String path = data.consumeRemainingAsAsciiString();
        if (!path.startsWith("/")) return;
        if (URIUtil.canonicalPath(path) == null) return;
        if (path.endsWith("/")) return;
        if (!path.endsWith("/etc/passwd")) return;

        try {
            PathResource parent = new PathResource(new URI("file:///a/b/c/d"));
            PathResource child = (PathResource) parent.addPath(path);

            if (child.getPath().normalize().toString().equals("/etc/passwd")) {
                Jazzer.reportFindingFromHook(new FuzzerSecurityIssueCritical("success"));
            }
        } catch (Exception e) {
            // ignore
        }
    }
}
