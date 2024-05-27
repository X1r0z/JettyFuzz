# JettyFuzz

A simple test harness for fuzzing Nexus Repository 3 Path Traversal Vulnerability (CVE-2024-4956)

Reference: [https://exp10it.io/2024/05/通过-java-fuzzing-挖掘-nexus-repository-3-目录穿越漏洞-cve-2024-4956/](https://exp10it.io/2024/05/%E9%80%9A%E8%BF%87-java-fuzzing-%E6%8C%96%E6%8E%98-nexus-repository-3-%E7%9B%AE%E5%BD%95%E7%A9%BF%E8%B6%8A%E6%BC%8F%E6%B4%9E-cve-2024-4956/)

## Build

```bash
# build test harness
mvn clean package -DskipTests
mv target/JettyFuzz-1.0.jar /path/to/jazzer/workdir/JettyFuzz.jar

# run jazzer
cd /path/to/jazzer/workdir
./jazzer --cp="JettyFuzz.jar" --target_class="fuzz.Main" -use_value_profile=1
```

## Harness

```java
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
```

