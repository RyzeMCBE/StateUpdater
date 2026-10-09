<div align="center">

# 🧱 StateUpdater · RyzeMCBE

### Actualización y migración de estados de bloques e ítems de Minecraft Bedrock

[![Licencia LGPL-3.0](https://img.shields.io/badge/Licencia-LGPL--3.0-blue)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-orange)](https://github.com/RyzeMCBE/StateUpdater)
[![Desarrollo](https://img.shields.io/badge/Estado-En%20desarrollo-yellow)](https://github.com/RyzeMCBE/StateUpdater)

[Repositorio](https://github.com/RyzeMCBE/StateUpdater) · [Allay](https://github.com/RyzeMCBE/Allay) · [Protocol](https://github.com/RyzeMCBE/Protocol)

</div>

## 📖 Descripción

**StateUpdater** es una biblioteca que transforma representaciones NBT de bloques e ítems de Minecraft Bedrock entre formatos y versiones utilizando esquemas de migración generados. Esta copia de trabajo pertenece al ecosistema **RyzeMCBE Development Studio** y conserva el reconocimiento a sus autores originales.

Su función es ayudar al servidor a interpretar y actualizar estados de bloques e ítems cuando sus representaciones cambian entre versiones. **No sustituye los codecs de red** de [Protocol](https://github.com/RyzeMCBE/Protocol).

## 📦 Módulos

| Módulo | Propósito |
| --- | --- |
| `common` | Componentes compartidos entre los actualizadores. |
| `block-updater` | Migración de estados NBT de bloques. |
| `item-updater` | Migración de estados NBT de ítems. |
| `tools` | Utilidades y recursos adicionales del código fuente. |

Las versiones declaradas en `gradle.properties` del código importado son **common 0.1.1**, **block-updater 1.26.50-G1** e **item-updater 1.26.20-R1**. Esto no significa que el servidor completo haya superado pruebas para todas esas versiones.

## 🔗 Integración con Allay

Allay utiliza bibliotecas de actualización de estados. En el fork RyzeMCBE, este proyecto se incorpora como fuente local mediante un **submódulo Git** y un **build compuesto de Gradle**.

```text
Allay/
├── protocol-local/      → RyzeMCBE/Protocol
├── stateupdater-local/  → RyzeMCBE/StateUpdater
├── api/
├── server/
└── settings.gradle.kts
```

Para clonar todas las dependencias enlazadas:

```bash
git clone --recurse-submodules https://github.com/RyzeMCBE/Allay.git
cd Allay
git submodule sync --recursive
git submodule update --init --recursive
./gradlew build --no-daemon
```

> [!NOTE]
> Un submódulo queda fijado a un commit concreto. Los cambios futuros en `main` de StateUpdater **no actualizan automáticamente** la revisión utilizada por Allay. La compatibilidad y las compilaciones deben verificarse antes de desplegar.

## 🛠️ Compilación independiente

Requiere **JDK 21** y las dependencias Gradle del proyecto.

```bash
git clone https://github.com/RyzeMCBE/StateUpdater.git
cd StateUpdater
chmod +x gradlew
./gradlew build --no-daemon
```

En Windows, utiliza `gradlew.bat`.

## 🧩 Uso en otros proyectos

Si utilizas artefactos publicados de la línea original:

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("org.allaymc.stateupdater:block-updater:<version>")
    implementation("org.allaymc.stateupdater:item-updater:<version>")
}
```

Los artefactos de Maven Central pueden diferir de este fork. Para compilar exactamente esta revisión, utiliza el código fuente del repositorio.

## 🧪 Verificación y diagnóstico

Prueba las transformaciones de NBT con datos representativos, los cambios de versión, los runtime IDs y los casos de bloques e ítems antiguos. Una compilación exitosa no certifica por sí sola que todos los datos de mundo sean compatibles.

## ⚖️ Licencia y atribuciones

Se conserva la licencia **[LGPL-3.0](LICENSE)**. Este fork reconoce el trabajo de AllayMC y los proyectos relacionados con los esquemas de migración:

- [BedrockItemUpgradeSchema](https://github.com/pmmp/BedrockItemUpgradeSchema)
- [BedrockBlockUpgradeSchema](https://github.com/pmmp/BedrockBlockUpgradeSchema)
- [Cloudburst BlockStateUpdater](https://github.com/CloudburstMC/BlockStateUpdater)

---

<div align="center">

**RyzeMCBE Development Studio** · Software e infraestructura para Minecraft Bedrock

[Allay](https://github.com/RyzeMCBE/Allay) · [Protocol](https://github.com/RyzeMCBE/Protocol) · [StateUpdater](https://github.com/RyzeMCBE/StateUpdater)

<sub>Proyecto independiente, sin afiliación con Mojang Studios ni Microsoft.</sub>

</div>
