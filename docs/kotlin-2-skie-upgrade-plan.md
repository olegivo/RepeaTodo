# План: Kotlin 2.4 + AGP (поддерживаемый) + SKIE вместо kswift

Документ для следующего агента и ревью. Слои **не смешивать**.

| Слой | Что внутри | Чего нет |
| --- | --- | --- |
| **Действия** | Что сделано в этой сессии | Оценки |
| **Факты** | Версии, файлы, официальные матрицы, логи эксперимента | «надо делать X» |
| **Исследования** | Что читали, какие пути смотрели | Решение |
| **Умозаключения** | Рекомендуемый план и фазы | Утверждения без пометки, что это вывод |

База: `origin/master` = `b99f755` (`build: AGP 8.13, compileSdk/targetSdk 36 (#33)`). Handoff PR #33 уже в master: `docs/pr-33-handoff.md`.

Дата сверки версий: **2026-09-05**.

---

## С чего продолжать

1. PR #33 закрыт и смёржен. На master: AGP **8.13.0**, Gradle **8.13**, `compileSdk`/`targetSdk` **36**, Kotlin **1.9.10**, kswift **оставлен**.
2. Этот документ — **план**, не реализация. Код toolchain не менять, пока пользователь не скажет внедрять.
3. Kotlin 2.x + SKIE **нельзя** делать «поверх» 1.9.10 без снятия kswift. Это подтверждено экспериментом (см. «Действия»).
4. «Последний стабильный AGP» и «последний стабильный Kotlin» **не пересекаются** в официальной матрице JetBrains. Выбирать пару, а не оба максимума.

---

## Действия

Что сделано в сессии плана (не интерпретация).

- Прочитан `docs/pr-33-handoff.md` с актуального `origin/master` (после merge #33).
- Сняты текущие версии из `gradle/libs.versions.toml`, `build.gradle.kts`, `androidApp/build.gradle.kts`, `shared/build.gradle.kts`, wrapper.
- Сняты latest/release с Maven Central и Google Maven (kotlin-gradle-plugin, AGP, SKIE, SQLDelight, coroutines, datetime, turbine, kotest, koin, moko-mvvm, atomicfu, Compose BOM, AndroidX).
- Прочитаны официальные матрицы: [KGP/AGP/Gradle](https://kotlinlang.org/docs/gradle-configure-project.html), [KMP compatibility](https://kotlinlang.org/docs/multiplatform/multiplatform-compatibility-guide.html), [About AGP](https://developer.android.com/build/releases/about-agp), [SKIE intro](https://skie.touchlab.co/intro), [SKIE sealed](https://skie.touchlab.co/features/sealed), [SKIE installation 0.10.14](https://skie.touchlab.co/Installation), [SKIE migration](https://skie.touchlab.co/migration), [SKIE configuration](https://skie.touchlab.co/configuration), [SQLDelight upgrading 2.0](https://sqldelight.github.io/sqldelight/latest/upgrading-2.0/), [AGP 9 KMP migration](https://kotlinlang.org/docs/multiplatform/multiplatform-project-agp-9-migration.html), [JetBrains AGP 9 blog](https://blog.jetbrains.com/kotlin/2026/01/update-your-projects-for-agp9/).
- Разобраны iOS call site’ы kswift: `NavigationDestinationKs`, `ToDoListKs`.
- Точечный эксперимент на Linux Cloud Agent (ветка плана, затем **откат** рабочих файлов):

  1. Только `kotlin = "2.4.10"` → конфигурация `:androidApp` падает: `freeCompilerArgs` в `android.kotlinOptions` — **error** (KGP 2.4).
  2. `compilerOptions` внутри `android {}` — unresolved. Нужен блок `kotlin { compilerOptions { … } }`.
  3. После переноса opt-in: «Starting in Kotlin 2.0, the Compose Compiler Gradle plugin is required».
  4. Подключён `org.jetbrains.kotlin.plugin.compose` 2.4.10, убран `composeOptions.kotlinCompilerExtensionVersion`.
  5. С **kswift 0.6.1**: configure `:shared` падает с `'java.io.File org.jetbrains.kotlin.konan.util.DependencyDirectories.getLocalKonanDir(java.lang.String)'` + «No Kotlin Targets Declared». Предупреждение: `kotlin-compiler-embeddable` на classpath вместе с KGP.
  6. kswift снят с `:shared` и из корневого `plugins {}`. Предупреждение про `kotlin-compiler-embeddable` **исчезло**.
  7. `:shared:compileKotlinJvm` — **BUILD SUCCESSFUL** на Kotlin 2.4.10 + AGP 8.13 + SQLDelight **1.5.5** + moko-mvvm **0.16.1** + kotest plugin 5.8 + datetime 0.4.0.
  8. `:shared:jvmTest` — **FAIL**: ICE IR lowering в `TaskUiKtTest.kt` (Kotest 5.8 / compiler plugin × Kotlin 2.4).
  9. `:androidApp:assembleDebug` — **FAIL**: `AbstractTimeSource` (тот же класс classloader’ов, что в handoff #33 при смешении classpath).

Логи эксперимента (не в git):

- `/opt/cursor/artifacts/exp-kotlin-2410-sqldelight15.log`
- `/opt/cursor/artifacts/exp-kotlin-2410-round4.log`
- `/opt/cursor/artifacts/exp-kotlin-2410-no-kswift.log`
- `/opt/cursor/artifacts/exp-kotlin-2410-jvmtest-only.log`
- `/opt/cursor/artifacts/exp-kotlin-2410-android-assemble.log`

Рабочие gradle-файлы после эксперимента возвращены к `master`.

---

## Факты

### Текущий стек на `master` (`b99f755`)

| Компонент | Сейчас |
| --- | --- |
| Kotlin / KGP | 1.9.10 |
| AGP | 8.13.0 (`plugins {}`, не `buildscript`) |
| Gradle | 8.13 |
| JDK | 17 (toolchain в модулях) |
| compileSdk / targetSdk | 36 / 36 (`androidApp`); `shared` compileSdk 36, без library `targetSdk` |
| Compose | BOM `2023.03.00`, compiler extension **1.5.3** |
| kotlinx-coroutines | 1.6.4 |
| kotlinx-datetime | 0.4.0 |
| SQLDelight | 1.5.5, plugin `com.squareup.sqldelight` |
| moko-kswift | 0.6.1, только `SealedToSwiftEnumFeature` |
| moko-mvvm | 0.16.1 (`mvvm-core`, `mvvm-flow`, export в iOS framework) |
| Koin | 3.4.0 / compose 3.4.3 |
| Kotest | 5.8.0, plugin `io.kotest.multiplatform` |
| Turbine | 0.12.3 |
| AndroidX | activity-compose 1.7.0, appcompat 1.6.1, navigation-compose 2.5.3, constraintlayout 2.1.4 / compose 1.0.1, material 1.8.0 |
| atomicfu | force **0.17.3** (обход Native klib 0.23.x на Kotlin 1.9.10) |

Свойства, которые #33 добавил под KGP 1.9.10:

- `kotlin.mpp.androidGradlePluginCompatibility.nowarn=true`
- `kotlin.mpp.androidSourceSetLayoutVersion=2`
- `kotlin.apple.xcodeCompatibility.nowarn=true`

На KGP 2.4.10 первые два уже **unsupported** (предупреждение эксперимента).

### Последние стабильные версии на 2026-09-05 (Maven / Google)

| Компонент | latest/release | Замечание |
| --- | --- | --- |
| Kotlin | **2.4.10** (2.4.20 = RC3) | Official «current Stable» в KMP guide |
| AGP | **9.4.0** (9.5.0 = alpha) | Требует Gradle **9.6.0** |
| Gradle (для AGP 9.4) | 9.6.0 | Вне max Gradle у KGP 2.4.10 (**9.5.0**) |
| SKIE | **0.10.14** | Официально Kotlin **2.0.0–2.4.10** |
| SQLDelight | **2.3.2** (2.4.0 = rc1) | Plugin `app.cash.sqldelight` |
| kotlinx-coroutines | **1.11.0** | Собрано на Kotlin 2.2.20 |
| kotlinx-datetime | **0.8.0** (`0.8.0-0.6.x-compat` — compat-артефакт) | С 0.7 `Instant`/`Clock` уехали в `kotlin.time` |
| Turbine | **1.2.1** | |
| Kotest | **6.2.4** | Plugin **`io.kotest`**, не `io.kotest.multiplatform`; нужен KSP |
| Koin | **4.2.2** (и core, и androidx-compose) | Compiler plugin Koin к 2.4 чинили в 1.0.1; **этот репозиторий compiler plugin не использует** |
| moko-mvvm | **0.16.1** | Последний релиз 2023-04-21, Kotlin 1.8.10 / coroutines 1.6.4 |
| moko-kswift | 0.6.1 (archived) | На 2.4.10 configure **ломается** |
| atomicfu | 0.33.0 | Пин 0.17.3 после Kotlin ≥ 1.9.21 / 2.x снимать |
| Compose BOM | **2026.08.00** | |
| activity-compose | **1.13.0** | |
| appcompat | **1.8.0** | |
| navigation-compose | **2.10.0** | |
| constraintlayout | **2.2.2** / compose **1.1.2** | |
| material | **1.14.0** | |
| lifecycle-viewmodel | **2.11.0** | Кандидат на замену moko, не обязан в первом PR |
| KSP | **2.3.11** | Нумерация больше не `2.4.10-x`; JetBrains quickstart: Kotlin 2.4.10 + KSP 2.3.10 |

### Официальная матрица KGP 2.4.0–2.4.10

Источник: Kotlin docs, 2026-09-05.

| | min | max fully supported |
| --- | --- | --- |
| Gradle | 7.6.3 | **9.5.0** |
| AGP | 8.5.2 | **9.1.0** |
| Xcode (KMP plugin) | | **26.4** |

JetBrains явно пишет: более новые Gradle/AGP *можно* пробовать, но это уже не «fully supported» — предупреждения и дырки в фичах.

AGP 9.1.x требует Gradle **9.3.1**. Это внутри max Gradle KGP 2.4.10.

AGP **9.4.0** требует Gradle **9.6.0** — **вне** официального max KGP 2.4.10.

KGP 1.9.0–1.9.10 официально: AGP до **7.4.0**, Gradle до **7.6.0**. То есть связка master (KGP 1.9.10 + AGP 8.13) уже была «за матрицей» и держалась на `nowarn` из #33.

### kswift в iOS (факт использования)

Плагин: только `SealedToSwiftEnumFeature`. Call site’ы:

- `NavigationDestinationKs` — `iosApp/RepeaTodo/ViewFactory.swift`
- `ToDoListKs` — `iosApp/RepeaTodo/Main/DrawerToDoListsView.swift` (`Identifiable`, mapper из `CFlow`)

Kotlin-источники:

- `NavigationDestination` (sealed class) — `shared/.../MainNavigator.kt`
- `ToDoList` (sealed interface) — `shared/.../ToDoList.kt`

`WorkState` в Swift — как Kotlin-классы (`WorkStateCompleted`), не `*Ks`.

Xcode ссылается на сгенерированный kswift-файл:

- `iosApp/RepeaTodo.xcodeproj/project.pbxproj` → `shared/build/bin/iosSimulatorArm64/debugFramework/sharedSwift/RepeaTodo_shared.swift`

Транзитивный `kswift-runtime` из `moko-mvvm-flow` — это **не** Gradle-плагин.

### SKIE (официально)

- Совместим с Kotlin **2.0.0–2.4.10**, Swift 5.8+.
- Рекомендация Touchlab: latest SKIE + latest *поддерживаемый* Kotlin → **0.10.14 + 2.4.10**.
- Sealed/sealed interface → Swift enum + `onEnum(of:)`. Для sealed interface Hashable на сгенерированном enum, если прямые дети — классы (`ToDoList.Predefined` / `ToDoList.Custom` — data class, условие выполняется).
- Flow/suspend по умолчанию **включены**. Для существующего проекта их можно выключить глобально (`FlowInterop.Enabled(false)`, `SuspendInterop.Enabled(false)` / `coroutinesInterop.set(false)`), чтобы не ломать Combine/`CFlow`.
- SKIE обрабатывает и **exported** зависимости (moko-mvvm-flow). Конфиг по FQCN-префиксу обязателен, если Flow interop оставляем выключенным и для moko.
- Несовместим со Swift Export (экспериментальный путь JetBrains). Swift Export по-прежнему не даёт exhaustive sealed enum.

### SQLDelight 2 (официально)

- Group/plugin: `com.squareup.sqldelight` → `app.cash.sqldelight`.
- DSL: `database("RepeaTodoDb") { }` → `databases { register("RepeaTodoDb") { packageName.set(...) } }`.
- `INTEGER AS Int` / `INTEGER AS Boolean` требуют `primitive-adapters` (`IntColumnAdapter`, `BooleanColumnAdapter`). В репо: `Task.daysPeriodicity INTEGER AS Int`, `ToDoList.isPredefined INTEGER AS Boolean`.
- `INTEGER AS Instant` / `INTEGER AS Priority` уже с кастомными адаптерами; после datetime 0.7+ тип Instant — `kotlin.time.Instant`.
- `Schema.version` становится `Long`.
- iOS `DriverFactoryImpl` / `wrapConnection` / `migrate` — правки по гайду 2.0.
- 2.3.2 заявляет совместимость с AGP 9 DSL.

### kotlinx-datetime 0.7/0.8 (официально)

- `kotlinx.datetime.Instant` и `Clock` удалены; использовать `kotlin.time.Instant` / `kotlin.time.Clock`.
- В stdlib 2.3 Instant помечен `@WasExperimental(ExperimentalTime)` / `@SinceKotlin("2.3")` — на 2.4.10 **стабилен**.
- В репо десятки импортов `kotlinx.datetime.Instant` + `.sq` `import kotlinx.datetime.Instant`.
- Compat-артефакт `0.8.0-0.6.x-compat` нужен только если чужие библиотеки ещё требуют старый тип. moko-mvvm Instant не экспортирует.

### Kotest 6 (официально)

- Plugin id: `io.kotest` (не `io.kotest.multiplatform`).
- Compiler plugin заменён на **KSP**. Для KMP: `com.google.devtools.ksp` **перед** `io.kotest`.
- JVM-тесты могут идти через `kotest-runner-junit5` без KSP; commonTest/Native discovery — через KSP.

### AGP 9 (официально)

Ломающие изменения относительно 8.13:

- Built-in Kotlin в `com.android.application`: `kotlin("android")` больше не нужен (opt-out `android.builtInKotlin=false` до AGP 10).
- KMP-модуль **нельзя** сочетать с `com.android.library`. Нужен `com.android.kotlin.multiplatform.library`, блок `kotlin { android { … } }` вместо `androidTarget()` + верхнего `android {}`.
- У этого репо структура уже разделена (`androidApp` + `shared`) — сплит модулей не нужен, только смена плагина в `shared`.
- Gradle ≥ 9.1.0 (для 9.1.x — 9.3.1).
- `androidTarget` с KGP 2.3+ — deprecation (на 2.3.10 warning откатили). На 2.4.10 при AGP 8.13 `androidTarget()` ещё живой путь.

### Эксперимент: что именно доказано на этом репо

| Гипотеза | Результат |
| --- | --- |
| KGP 2.4.10 + AGP 8.13 конфигурируются вместе | Да, после Compose plugin + `kotlin.compilerOptions` |
| kswift 0.6.1 на KGP 2.4.10 | Нет: configure crash `getLocalKonanDir` |
| SQLDelight 1.5.5 генерит и компилирует JVM main на 2.4.10 | Да (`compileKotlinJvm`) |
| moko-mvvm 0.16.1 как JVM-зависимость на 2.4.10 | Да (компиляция common/jvm main) |
| Kotest 5.8 + Kotlin 2.4 | Нет: ICE в `compileTestKotlinJvm` |
| Android assemble на 2.4.10 без смены SQLDelight/Kotest | Нет: `AbstractTimeSource` |
| iOS/XCFramework / SKIE runtime | **Не проверялось** (хост linux_x64) |

---

## Исследования

Не является выбранным планом, пока не попало в «Умозаключения».

### Почему нельзя взять «оба latest»

- Latest Kotlin **2.4.10** ↔ AGP fully supported **≤ 9.1.0**, Gradle **≤ 9.5.0**.
- Latest AGP **9.4.0** ↔ Gradle **9.6.0** ↔ вне матрицы 2.4.10.
- Отзывы/практика (handoff #33): прыжок Kotlin 2.0.21 при живом kswift/SQLDelight 1.5.5 уже краснил Bitrise iOS. Смешивать AGP 9 DSL rewrite в тот же PR, что и SKIE, повторяет ту же ошибку изоляции.

Варианты, которые смотрели:

1. **Kotlin 2.4.10 + AGP 8.13** (уже на master) — минимальный разрыв матрицы, AGP 9 не трогаем.
2. **Kotlin 2.4.10 + AGP 9.1.1** — latest AGP *внутри* официальной матрицы KGP 2.4.10; обязателен новый Android-KMP plugin и Gradle 9.3.1.
3. **Kotlin 2.4.10 + AGP 9.4.0** — формально «latest AGP», но вне fully supported; Gradle 9.6.0. Не рассматривать как цель первого прохода.
4. Ждать Kotlin, который официально закроет AGP 9.4 — 2.4.20 пока RC.

### kswift → SKIE (не ручные enum)

Пользователь зафиксировал SKIE, не ручные Swift enum из handoff #33.

Минимальный Swift-дифф (два файла + pbxproj):

```swift
// ViewFactory.swift
switch onEnum(of: destination) {
case .addTask: fatalError("Not implemented")
case .editTask(let value): EditTaskView.factory(uuid: value.uuid)
}

// DrawerToDoListsView.swift
ForEach(...) { list in
    switch onEnum(of: list) {
    case .custom(let custom): customitemViewFactory(custom)
    case .predefined: Label(list.title, systemImage: "list.bullet")
    }
}
```

`ToDoListKs: Identifiable` заменить на Identifiable по `uuid` у самого `ToDoList` / у SKIE-enum (Touchlab: Hashable на enum, если дети — классы).

Первая итерация SKIE (рекомендация Touchlab для существующих проектов): **только sealed**, Flow/suspend **выключить**, чтобы не ломать `CFlow` + Combine в `iosApp/RepeaTodo/Interop/MokoMvvm/*`.

Убрать из Xcode ссылку на `RepeaTodo_shared.swift`. SKIE вшивает Swift в framework, отдельный generated file в pbxproj не нужен.

Не включать Swift Export параллельно.

### moko-mvvm

Новее 0.16.1 нет. JVM main на 2.4.10 собрался. Native klib **не проверен**. Риски:

- старый atomicfu/coroutines транзитивно (лечится поднятием coroutines 1.11 + снятие пина 0.17.3);
- SKIE прогонит exported moko — держать FlowInterop off и для `dev.icerock.moko`;
- если Bitrise Native link снова упадёт на klib IR — запасной путь: `androidx.lifecycle:lifecycle-viewmodel` 2.11.0 + тонкая обёртка `viewModelScope` / отказ от `CFlow` в пользу SKIE Flow (это уже отдельный PR, не мешать с первым).

Заменять moko «заодно» в Kotlin 2 PR — лишний продуктовый объём (весь iOS Interop).

### SQLDelight: обязательно ли 2.x в первом PR

Эксперимент: 1.5.5 ещё генерит JVM main на 2.4.10. Но:

- handoff #33: Gradle 8 implicit dependency generate/verify;
- Android assemble на 2.4.10 уже падает с `AbstractTimeSource` при живых старых плагинах;
- 2.3.2 нужен для AGP 9 и снимает overlap schema/verify;
- пакетный rename механический (~10 Kotlin-файлов + 3 `.sq` + DSL).

Оставлять 1.5.5 на Kotlin 2.4 — технически возможно для JVM compile, но это известный долг, который снова всплывёт на CI/Android.

### Compose

На Kotlin 2.0+ обязателен `org.jetbrains.kotlin.plugin.compose` той же версии, что KGP. `composeOptions.kotlinCompilerExtensionVersion = "1.5.3"` невалиден (1.5.3 = Kotlin 1.9.10).

BOM 2026.08.00 — последний стабильный; Material3 API в androidApp уже на Experimental opt-in, проверить window-size-class координаты после BOM.

### Source sets / hierarchy

`shared/build.gradle.kts` вручную делает `iosMain`/`iosTest` и `jvmMain.dependsOn(commonMain)`, `jvmTest.dependsOn(jvmMain)`. На KGP 2.4 это конфликтует с default hierarchy template и даёт warning «jvmTest can't depend on jvmMain». В первом PR: либо `kotlin.mpp.applyDefaultHierarchyTemplate=false` (как сейчас по сути), либо выкинуть ручные `dependsOn` и опереться на template. Второй вариант чище, но трогает структуру source sets.

`expect`/`actual` class — Beta, warning; флаг `-Xexpect-actual-classes` или оставить warning.

### Что сознательно не проверяли

- Bitrise `primary` / Xcode 26 + SKIE 0.10.14 (нужен macOS).
- Реальный Swift compile после `onEnum`.
- SQLDelight 2 generate/verify на этой схеме.
- Kotest 6 + KSP 2.3.11 на этом дереве.
- AGP 9.1.1 configure.
- Swift Export.

---

## Умозаключения

### Целевая пара Kotlin/AGP

**Не брать AGP 9.4.0** вместе с Kotlin 2.4.10: официально не fully supported, Gradle 9.6.0 вне max KGP.

Рекомендуемая цель «latest *поддерживаемые*»:

| | Версия | Почему |
| --- | --- | --- |
| Kotlin | **2.4.10** | Последний стабильный; SKIE 0.10.14 его поддерживает |
| AGP (фаза 1) | **8.13.0** (оставить) | Уже на master, внутри матрицы 2.4.10 (8.5.2–9.1.0), не смешивать с SKIE |
| AGP (фаза 2) | **9.1.1** | Последний AGP в fully supported для KGP 2.4.10 |
| Gradle фаза 1 | **8.14.4** (или оставить 8.13) | KGP 2.4 предупреждает, что 8.13 deprecated к Kotlin 2.5; 8.14.4 — совет KGP. Не блокер |
| Gradle фаза 2 | **9.3.1** | Минимум AGP 9.1 |
| SKIE | **0.10.14** | Latest, Kotlin 2.4.10 |
| kswift | **удалить** | Configure-blocker на 2.4.10 |

### Две фазы, не один PR

Опыт #33: Kotlin 2 + iOS-стек в одном коммите дал неизолируемые красные Bitrise. SKIE официально просит итеративную миграцию фич.

**Фаза 1 — Kotlin 2.4.10 + SKIE + библиотеки, AGP остаётся 8.13.**  
**Фаза 2 — AGP 9.1.1 + `com.android.kotlin.multiplatform.library` + Gradle 9.3.1.**

### Целевые библиотеки фазы 1

| Компонент | С → На |
| --- | --- |
| SQLDelight | 1.5.5 → **2.3.2** (`app.cash.sqldelight`) |
| kotlinx-coroutines | 1.6.4 → **1.11.0** |
| kotlinx-datetime | 0.4.0 → **0.8.0** + импорты `kotlin.time.Instant`/`Clock` |
| Kotest | 5.8.0 → **6.2.4**, plugin `io.kotest`, KSP **2.3.11** |
| Turbine | 0.12.3 → **1.2.1** |
| Koin | 3.4.x → **4.2.2** (без Koin compiler plugin) |
| Compose | BOM 2023.03.00 → **2026.08.00**, plugin compose 2.4.10 |
| activity-compose | 1.7.0 → **1.13.0** |
| appcompat | 1.6.1 → **1.8.0** |
| navigation-compose | 2.5.3 → **2.10.0** |
| constraintlayout | 2.1.4 / 1.0.1 → **2.2.2** / **1.1.2** |
| material | 1.8.0 → **1.14.0** |
| moko-mvvm | 0.16.1 → **0.16.1** (новых нет; Native проверить на Bitrise) |
| atomicfu pin | снять после coroutines 1.11 |
| kswift | удалить plugin, `kswift {}`, catalog, pbxproj generated file |

### Порядок работ фазы 1 (внутри одного PR, коммиты логические)

1. **Сборка toolchain без iOS-плагинов:** Kotlin 2.4.10, Compose compiler plugin, `kotlin { compilerOptions }`, убрать deprecated `gradle.properties` (`androidGradlePluginCompatibility.nowarn`, `androidSourceSetLayoutVersion`). Локально: `:shared:compileKotlinJvm`.
2. **Снять kswift** (иначе configure не живёт). Пока SKIE не подключён, iOS sealed switch сломается — поэтому сразу шаг 3.
3. **SKIE 0.10.14** на `:shared`, sealed ON, Flow/suspend OFF (включая group `dev.icerock.moko`). Переписать два Swift-файла на `onEnum(of:)`. Выкинуть `RepeaTodo_shared.swift` из pbxproj.
4. **SQLDelight 2.3.2:** plugin/group, DSL, primitive adapters, rename пакетов, iOS/Android/JVM drivers, `Schema.version` Long. Прогнать generate+verify в одном графе (проверка, что `mustRunAfter` больше не нужен).
5. **datetime 0.8.0** и coroutines 1.11.0; снять atomicfu pin. Массовая замена `kotlinx.datetime.Instant` → `kotlin.time.Instant`.
6. **Kotest 6.2.4 + KSP 2.3.11.** Это обязательный шаг: 5.8 даёт ICE на 2.4. `:shared:jvmTest`.
7. **Koin 4.2.2 + AndroidX/Compose BOM.** `:androidApp:assembleDebug`.
8. **Иерархия source sets:** убрать незаконные `jvmTest.dependsOn(jvmMain)` и ручной ios-граф *или* явно `applyDefaultHierarchyTemplate=false`.
9. Bitrise cache keys: не `konan-k1910-` / `gradle-deps-agp813-` от 1.9.10 — новые ключи (`konan-k2410-`, …), иначе подтянется старый Konan.
10. Bitrise `primary` — единственный источник истины по iOS/SKIE/moko Native.

### Фаза 2 (отдельный PR, после зелёного Bitrise фазы 1)

1. Gradle 9.3.1, AGP 9.1.1.
2. `shared`: `com.android.library` + верхний `android {}` + `androidTarget()` → `com.android.kotlin.multiplatform.library` + `kotlin { android { namespace, compileSdk, minSdk, compilerOptions, androidResources } }`.
3. `androidApp`: убрать `kotlin("android")`, built-in Kotlin AGP 9; Compose plugin оставить.
4. Не включать `android.newDsl` opt-out без нужды; если какой-то плагин не готов — точечный `android.newDsl=false`, не глобальный откат.
5. Не прыгать на AGP 9.4, пока KGP не объявит его fully supported.

### Подводные камни (краткий чеклист)

- kswift на 2.4.10 = мёртвый configure; «оставить на потом» нельзя.
- SKIE Flow по умолчанию ON сломает `CFlow` Combine. Выключить до отдельной миграции Interop.
- Kotest 5.8 на 2.4 = ICE, не «просто warning».
- Compose 1.5.3 compiler не существует для 2.4.
- `android.kotlinOptions.freeCompilerArgs` — compile error скрипта.
- datetime 0.8 без замены импортов Instant не соберётся; `.sq` тоже.
- SQLDelight 2: `AS Int`/`AS Boolean` без primitive-adapters не соберётся.
- atomicfu 0.17.3 после coroutines 1.11 — уже вредный пин.
- moko 0.16.1 на Native — единственная оставшаяся крупная неизвестность; не сносить в фазе 1 без красного Bitrise.
- `kotlin-compiler-embeddable` на classpath (kswift/старые плагины) → непредсказуемый KGP и `AbstractTimeSource`.
- Linux-агент не собирает iOS; не считать JVM/Android достаточными для merge фазы 1.

### Критерии готовности фазы 1

- `:shared:compileKotlinJvm`, `:shared:jvmTest` (как сейчас ~270 тестов), `:androidApp:assembleDebug` локально.
- `:shared:generateCommonMainRepeaTodoDbSchema` + `:shared:verifySqlDelightMigration` в одном графе.
- Bitrise `primary` зелёный (iOS framework + SKIE + Xcode).
- В Swift нет `*Ks`, в Gradle нет `dev.icerock.moko.kswift`.
- `docs/pr-33-handoff.md` не переписывать; при необходимости короткий addendum в конце этого файла.

### Не делать по своей инициативе

- AGP 9.4.0 / Gradle 9.6.0 «потому что latest».
- Swift Export вместо SKIE.
- Замена moko-mvvm в том же PR, что SKIE, без красного Native.
- Оставлять kswift «на всякий случай».
- Kotlin 2.4.20-RC.
- SQLDelight 2.4.0-rc1.
- Коммит бинарного шума `*.db` после generate schema.

---

## Ограничения среды

- Linux Cloud Agent: iOS Native disabled. Источник истины по SKIE/Xcode — Bitrise (`osx-xcode-latest-stable`, в #33 был Xcode 26.x; KGP 2.4.10 тестировался до Xcode 26.4).
- `BITRISE_TOKEN` — runtime secret с **старта** сессии; см. `AGENTS.md` и `.cursor/skills/bitrise-ci/SKILL.md`.
- Отвечать по-русски; `git add` только с явными путями; не коммитить `.idea` / `.vscode`.

---

## Addendum: фаза 1 внедрена (2026-09-05)

Что сделано в коде (не переписывать разделы выше).

- Kotlin **2.4.10**, Gradle **8.14.4**, AGP **8.13.0**, SKIE **0.10.14**, kswift удалён.
- SQLDelight **2.3.2**, coroutines **1.11.0**, datetime **0.8.0** (`kotlin.time.Instant`/`Clock`), Kotest **6.2.4**, KSP **2.3.11**, Koin **4.2.2**, Turbine **1.2.1**.
- AndroidX для AGP 8.13 / compileSdk 36: Compose BOM **2026.06.01**, navigation-compose **2.9.8**. Latest BOM 2026.08.00 и navigation 2.10.0 требуют compileSdk 37 и AGP 9.1 — это фаза 2.
- Swift: `onEnum(of:)` вместо `*Ks`; `RepeaTodo_shared.swift` убран из pbxproj. SKIE Flow/Suspend OFF.
- Bitrise cache keys: `gradle-deps-k2410-`, `konan-k2410-`.
- SQLDelight 2: `mustRunAfter` generateSchema → generateInterface/verify всё ещё нужен (тот же implicit dependency).
- Turbine 1.2: `Flow.testIn` требует `TurbineContext`; в `FreeSpec` свой `testIn` через публичный `Turbine()`.
- Kotest 6: `io.kotest.data` нет; свои `Row2`…`Row6`. Listener через `extension()`, `TestResult` из `io.kotest.engine.test`.

Локально на Linux Cloud Agent (2026-09-05):

- `:shared:compileKotlinJvm` — SUCCESS
- `:shared:jvmTest` — SUCCESS, **271** тест
- `:androidApp:assembleDebug` — SUCCESS
- `:shared:generateCommonMainRepeaTodoDbSchema` + `:shared:verifySqlDelightMigration` в одном графе — SUCCESS

iOS / SKIE / Xcode / moko Native — только Bitrise `primary`. `3.db` после generate не коммитить.

Bitrise `primary` на `b6d728a` (`89c9e8aa`, 2026-09-05):

- `:shared:assembleSharedDebugXCFramework` + SKIE — SUCCESS
- Xcode simulator — FAIL: `sealed interface ToDoList` в SKIE это Swift **protocol**, `extension ToDoList: Identifiable` нельзя. Чинить `ForEach(..., id: \.uuid)`.
- Debug-скрипт `ls .../sharedSwift/` — leftover kswift, падает и **скипает** `jvmTest` (`is_always_run: true`).

Последующие красные `primary` (те же классы ошибок, плюс Xcode 26):

- `a49ad06` / `4e8e11f4` — `ToDoList` protocol + leftover `sharedSwift/`.
- `165841b` / `bd7f12b0` — `CFlowExt.swift`: Xcode 26 не видит `DispatchQueue` через один `import Combine`; нужен `import Foundation`.

### iOS зелёный: `07ad3ca` / `9f4e4020` (2026-09-05)

Bitrise `primary` [9f4e4020](https://app.bitrise.io/build/9f4e4020-f9f8-4394-980e-efed47413845): **success**, 15:04:54–15:10:41 UTC, Xcode **26.6**, `osx-tahoe-26`. Коммит `07ad3cae70d8e0891782f76d320e5bb70ba0e72c`.

Все шаги `primary` зелёные, включая ранее красные:

- Gradle Android + `:shared:assembleSharedDebugXCFramework` + SKIE — SUCCESS (`shared.xcframework`)
- **Xcode Build for Simulator — SUCCESS, Archive Succeeded**, 19.17s (шаг больше не skippable)
- Script без `sharedSwift/` — SUCCESS
- `:shared:jvmTest` — SUCCESS
- Check DB Migrations — SUCCESS

Неблокеры Xcode (как на master, не чинить в фазе 1): `TaskUi: Identifiable`, `ViewModel: ObservableObject`, `stateNullable<T>` explicit specialize, `scanHexInt32` deprecated.

Критерий готовности фазы 1 по iOS выполнен. Фаза 2 (AGP 9.1 / KMP Android library) — отдельный PR.
