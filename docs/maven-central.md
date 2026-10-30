# Publicación de Engine StateUpdater en Maven Central

Se usa el namespace de RyzeMCBE `io.github.ryzemcbe`. Los builds de
desarrollo continúan con las coordenadas originales.

Con `-PcentralRelease=true`, las coordenadas propuestas serán:

- `io.github.ryzemcbe:common:0.1.1`
- `io.github.ryzemcbe:block-updater:1.26.60-R1`
- `io.github.ryzemcbe:item-updater:1.26.50-R1`

Las dos bibliotecas updater dependen de `common:0.1.1`. Esa relación
debe figurar en sus POM bajo el namespace nuevo, y se verifica por CI.

## Validar sin publicar

```bash
./gradlew build --no-daemon
./gradlew :common:generatePomFileForMavenPublication \
  :block-updater:generatePomFileForMavenPublication \
  :item-updater:generatePomFileForMavenPublication \
  -PcentralRelease=true --no-daemon
python3 .github/scripts/verify-central-poms.py
```

Comprobar las bibliotecas transitivas y las licencias de terceros.
Verificar que las coordenadas no existan ya en Central. Los JARs
publicados deben incluir fuentes, documentación y firmas.

## Publicación manual posterior

El proyecto ya usa `com.vanniktech.maven.publish` con
`publishToMavenCentral()` y `signAllPublications()`. Solo con la
auditoría de dependencias y CI aprobadas, configurar token Sonatype
y clave GPG fuera del repositorio, y ejecutar:

```bash
./gradlew publishToMavenCentral -PcentralRelease=true
```

Completar la aprobación manual en
https://central.sonatype.com/publishing cuando el deployment sea
**VALIDATED**. No ejecutar antes de cerrar el control de dependencias.
