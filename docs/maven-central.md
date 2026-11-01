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

## Auditoría de dependencias directas

El CI ejecuta `.github/scripts/audit-central-resolution.py` contra los tres
POM de publicación: las dependencias externas directas están accesibles
en Maven Central, y el módulo `common` se trata como publicación de
primer nivel pendiente, no como dependencia de terceros. Esto **no**
sustituye una prueba de resolución transitiva completa desde un consumidor
que use exclusivamente `mavenCentral()` después de publicar.

## Verificación segura de la firma desde GitHub Actions

El workflow `.github/workflows/verify-maven-central-signing.yml` permite
probar **únicamente** firmas (NO publica ni necesita token Sonatype).

1. En GitHub Settings > Environments, el entorno `maven-central` debe
   permitir exclusivamente la rama `main`.
2. Configurar `GPG_PRIVATE_KEY` y `GPG_PASSPHRASE` como **environment
   secrets**, no variables públicas ni archivos de código.
3. Revisar y fusionar este PR en `main` cuando esté aprobado. GitHub
   muestra workflows manuales desde la rama por defecto.
4. En Actions > «Maven Central - Verify GPG signatures (NO PUBLISH)» >
   «Run workflow» > seleccionar `main` y ejecutar.
5. Aprobar el environment si hay `Required reviewers`.
6. Confirmar en el job que se compilan los módulos, se verifican los POM,
   se auditan las dependencias directas y aparecen firmas `.asc` para los
   tres módulos.

Separa la fase de firma de la publicación para evitar deploys accidentales.
Las claves de Sonatype `MAVEN_CENTRAL_USERNAME` y
`MAVEN_CENTRAL_PASSWORD` quedan disponibles para un workflow de publicación
posterior, que debe protegerse y habilitarse **solo** cuando todas las
dependencias, licencias, Javadoc y pruebas estén verificadas.
No incluir `publishToMavenCentral` ni `publishAndReleaseToMavenCentral`
en el workflow de verificación de firma.

