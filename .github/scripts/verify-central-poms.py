#!/usr/bin/env python3
"""Verify the release-candidate Maven POMs without publishing anything."""
from pathlib import Path
import xml.etree.ElementTree as ET

EXPECTED = {"common": "0.1.1", "block-updater": "1.26.60-R1", "item-updater": "1.26.50-R1"}
GROUP = "io.github.ryzemcbe"
for module, version in EXPECTED.items():
    pom = Path(module) / "build" / "publications" / "maven" / "pom-default.xml"
    assert pom.is_file(), f"Missing generated publication POM: {pom}"
    root = ET.parse(pom).getroot()
    def val(name):
        node = root.find("{*}" + name)
        return node.text.strip() if node is not None and node.text else None
    assert val("groupId") == GROUP, (pom, "groupId", val("groupId"))
    assert val("artifactId") == module, (pom, "artifactId", val("artifactId"))
    assert val("version") == version, (pom, "version", val("version"))
    assert val("name"), f"Missing POM name for {module}"
    assert root.find("{*}licenses/{*}license") is not None, f"Missing license in {module}"
    assert root.find("{*}developers/{*}developer") is not None, f"Missing developers in {module}"
    assert root.find("{*}scm/{*}url") is not None, f"Missing SCM in {module}"
    print(f"OK: {GROUP}:{module}:{version}")

for module in ("block-updater", "item-updater"):
    root = ET.parse(Path(module) / "build" / "publications" / "maven" / "pom-default.xml").getroot()
    deps = root.findall(".//{*}dependency")
    assert any(
        dep.findtext("{*}groupId") == GROUP
        and dep.findtext("{*}artifactId") == "common"
        and dep.findtext("{*}version") == EXPECTED["common"]
        for dep in deps
    ), f"{module} must reference the matching common publication"
    print(f"OK: {module} -> common")

