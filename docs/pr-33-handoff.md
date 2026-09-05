# Handoff: PR #33 (`update` → `master`)

Документ для следующего агента. Четыре слоя **не смешивать**:

| Слой | Что внутри | Чего нет |
| --- | --- | --- |
| **Действия** | Что уже сделано в репозитории и CI | Оценки «почему так» |
| **Факты** | Проверяемые состояния файлов, версий, коммитов, прогонов | Гипотезы |
| **Исследования** | Что пробовали, что читали, какие альтернативы смотрели | Решение «делаем X» |
| **Умозаключения** | Интерпретации и рекомендации | Утверждения без пометки, что это вывод |

Язык репозитория и идентификаторы (задачи Gradle, классы, URL) оставлены как в коде.

Связанные файлы на `master`, которые после ребейза уже есть в `update`:

- `AGENTS.md` — среда Cloud Agent, секрет `BITRISE_TOKEN`
- `.cursor/skills/bitrise-ci/SKILL.md` — как читать логи Bitrise через API

---

## С чего продолжать

Состояние на **2026-09-05** (ветка `update`; `origin/master` всё ещё `0782e8f`):

1. Цель PR #33 **достигнута в коде и подтверждена CI**: AGP 8.13 + Gradle 8.13 + `compileSdk` 36 при Kotlin 1.9.10. Bitrise `primary` **SUCCESS** на `c8bd991` (build 97) и на актуализации handoff `54422e2` (build 98).
2. **Не удалять moko-kswift** и не править iOS sealed-enum обёртки, пока пользователь явно не выберет вариант из раздела «Исследования → kswift».
3. **Не поднимать Kotlin 2.x** в этом PR: локально и на Bitrise это уже ломалось; это отдельная миграция (SQLDelight 2, moko, SKIE).
4. Следующий осмысленный шаг по AGP/SDK: ревью человека / merge по явной просьбе. Тело PR #33 в GitHub **по-прежнему пустое**.
5. **2026-09-05:** по явной команде «делай» в тот же `update` добавлена семантика версий в `deploy-android-play`. Сам workflow не запускать без просьбы.

---

## Действия

Хронология того, что **уже сделано** (не интерпретация).

### Git / PR

- Просмотрен PR https://github.com/olegivo/RepeaTodo/pull/33 (`update` → `master`).
- Ветка `update` была отсталой относительно `master`; выполнен rebase на тогдашний `master` (версия приложения 7 / `0.6` с `master` сохранена, не затёрта черновиком PR).
- Работа велась на существующей ветке `update`, отдельную `cursor/…-6665` не создавали.
- **2026-08-31:** повторный rebase `update` на актуальный `origin/master` (`0782e8f`). 8 коммитов PR перенеслись без конфликтов. Новые коммиты только на `master` (инструкции агентам по Bitrise) вошли в историю `update`.
- `git push --force-with-lease origin update` для опубликованного rebase — сделан 2026-08-31 вместе с первым handoff-коммитом `c8bd991`.

**2026-09-05** (сверка + актуализация этого файла, без изменений toolchain):

- `git fetch origin master update`: HEAD / `origin/update` = `c8bd991`, `origin/master` = `0782e8f`, merge-base тот же, коммитов `master` не в `update` нет.
- Bitrise `primary` на `c8bd991`: `status=1` success, build 97, ~3m28s (`12:12:25Z`–`12:15:53Z`).
  URL: https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/c1d88ae1-e8b8-473f-98e9-4c9c480f5afa
- GitHub PR #33: OPEN, MERGEABLE / CLEAN, check Bitrise SUCCESS на том же slug, `headRefOid` = `c8bd991`, title без изменений, body пустое.
- Попытка записать body: `ManagePullRequest` `update_pr` — отказ («current description is empty»). `gh pr edit` / `PATCH /repos/olegivo/RepeaTodo/pulls/33` — `403 Resource not accessible by integration`. Computer Use: GitHub UI без логина, кнопки Edit нет.
- Коммит актуализации: `54422e2` `docs: refresh PR #33 handoff after green Bitrise`, запушен в `origin/update`.
- Bitrise `primary` на `54422e2`: `status=1` success, build 98, `07:19:58Z`–`07:23:07Z`.
  URL: https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/9a4b9082-b4cc-426a-8980-6b8e6ab796b0
- Коммит `a5a00dd` `docs: record Bitrise success on PR #33 handoff refresh`. Bitrise `primary` build 99 SUCCESS.
  URL: https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/486a601c-c6bc-445e-8a14-e217d75998a9

**2026-09-05** (семантика версий Android Play, по команде «делай», без новой ветки):

- Ранее агент начал ту же правку без разрешения; откатили `reset --soft` + `force-with-lease` на `a5a00dd`.
- Пользователь утвердил: путь `./androidApp/build.gradle.kts`; `git describe` = ближайший предок; `versionCode` = `Major*1000000 + Minor*10000 + Patch*100 + (BITRISE_BUILD_NUMBER % 100)`; Minor/Patch ≥ 100 не поддерживаем.
- В `bitrise.yml` у `deploy-android-play`: триггер по тегу, `fetch_tags: "yes"`, `script@1` → `CALCULATED_VERSION_*`, шаг `change-android-versioncode-and-versionname@1`. Bundle/sign/Play не меняли.
- Ручной Start build 105 (`04f5ef0`, без тега): SUCCESS, но `git describe` дал фолбек `0.0.1` из‑за `git fetch --depth=1`. В Play internal ушло `versionCode=105`, `versionName=0.0.1.105`.
  URL: https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/4150f5fb-1beb-459c-9a14-27cc05908be7
- По команде пользователя: два `git-clone@8` в `deploy-android-play` — `BITRISE_TRIGGER_METHOD=manual` → `clone_depth: "100"`; иначе дефолтный shallow. Deploy повторно не запускали.

Коммиты PR **после** rebase 2026-08-31 (от старых к новым):

1. `b554686` — `Bump target sdk`
2. `763b263` — `Update AGP, Kotlin`
3. `4d88faa` — `build: drop unused kotlin-gradle-plugin catalog entry`
4. `183ea92` — `build: install Android 16 SDK for compileSdk 36`
5. `a4ff1ea` — `build: make AGP 8.13 and compileSdk 36 actually compile`
6. `3ab79df` — `build: keep Kotlin 1.9.10 with AGP 8.13 for iOS/Bitrise`
7. `a8b40e2` — `fix(ios): pin atomicfu 0.17.3 for Kotlin 1.9.10 Native link`
8. `2b59553` — `fix(build): order SQLDelight schema and verify tasks for Gradle 8`

9. `c8bd991` — `docs: handoff for PR #33 (AGP 8.13 / compileSdk 36)`
10. `54422e2` — `docs: refresh PR #33 handoff after green Bitrise`
11. `a5a00dd` — `docs: record Bitrise success on PR #33 handoff refresh`
12. этот коммит — версионирование Play + запись в handoff

Инструмент `ManagePullRequest` **не смог** записать тело PR ни 2026-08-31, ни 2026-09-05: текущее description пустое, апдейт body отвергается. Title: `build: AGP 8.13, compileSdk/targetSdk 36`. `gh` в этой среде только read-only (`403` на `updatePullRequest`).

### Toolchain и модули

- Gradle wrapper: `8.13`.
- AGP: `8.13.0` через version catalog и корневой `plugins { … apply false }` (не `buildscript` classpath).
- Kotlin оставлен `1.9.10`.
- `androidApp`: `compileSdk = 36`, `targetSdk = 36`, `versionCode = 7`, `versionName = "0.6"`, Java 17, Compose compiler `1.5.3`.
- `shared`: `compileSdk = 36`; `targetSdk` у library убран.
- `.cursor/install.sh`: пакеты SDK `platforms;android-36`, `build-tools;36.0.0`.
- `gradle.properties`: `kotlin.mpp.androidGradlePluginCompatibility.nowarn=true`, `kotlin.apple.xcodeCompatibility.nowarn=true`.
- `allprojects` resolutionStrategy: `atomicfu*` → `0.17.3`.
- Bitrise cache keys переименованы в `gradle-deps-agp813-` и `konan-k1910-`, чтобы не подтягивать кэш от прогона с Kotlin 2.0.21.
- SQLDelight: `verify*DbMigration` `mustRunAfter` `generate*DbSchema` в `shared/build.gradle.kts`.

### Проверки

Локально (Linux Cloud Agent VM):

- `:androidApp:assembleDebug` — успех; в APK `compileSdkVersion=36`, `targetSdkVersion=36`, version `0.6` (7).
- `:shared:jvmTest` — 270 тестов, 0 failures.
- `:shared:generateCommonMainRepeaTodoDbSchema :shared:verifySqlDelightMigration --stacktrace` — сначала падение Gradle 8 implicit dependency; после `mustRunAfter` — `BUILD SUCCESSFUL`, в том числе с `--rerun-tasks`.
- iOS / XCFramework **не** собирались: хост linux_x64.

Bitrise `primary` (macOS, `osx-xcode-latest-stable`, Xcode 26.6):

- до rebase, коммит `77cd332db7c2dacad4625656226c5c7ac512cc39`: pass, ~4m42s
  https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/677cdb66-b2ce-412c-9fc1-82535370cb86
- после rebase + первый handoff, коммит `c8bd9911f051da69b163d435144d2f777d86e8f4`: pass, ~3m28s (build 97, 2026-08-31)
  https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/c1d88ae1-e8b8-473f-98e9-4c9c480f5afa
- актуализация handoff, коммит `54422e28b763635efad3a418e788ac2fc4a09a1f`: pass, ~3m09s (build 98, 2026-09-05)
  https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/9a4b9082-b4cc-426a-8980-6b8e6ab796b0

### Что сознательно не делали

- Не удаляли плагин `dev.icerock.moko.kswift` (пользователь запретил, пока не утвердит вариант).
- Не коммитили перегенерированный `shared/src/commonMain/sqldelight/databases/3.db` (тот же размер, бинарный шум от `generate*Schema`).
- Не мержили PR и не включали auto-merge.
- Не запускали `deploy-android-play` / `POST /builds` после добавления версионирования.

---

## Факты

### Репозиторий

- Репозиторий: `olegivo/RepeaTodo` (KMP: `androidApp`, `shared`, `iosApp`).
- База PR: `master`. Рабочая ветка PR: `update`.
- На `master` (fetch 2026-08-31 и повтор 2026-09-05): `0782e8f` `docs: инструкции агентам по логам Bitrise (#39)`. Не уехал.
- На `master` **нет** AGP 8.13 и `compileSdk` 36 для компиляции: приложение `targetSdk = 36`, **`compileSdk = 34`**, AGP **7.4.2**, Gradle **7.5**, Kotlin **1.9.10**.
- Коммит на `master`, объясняющий щель SDK: `de332ff` — `build: keep compileSdk 34; AGP 7.4 aapt2 cannot read API 36` (aapt2: `RES_TABLE_TYPE_TYPE entry offsets overlap`). Play Console требовал только `targetSdk` 36.
- Также уже на `master` (не уникально для этого PR): kotest 5.8.0, moko-mvvm 0.16.1, `androidTarget()`, Bitrise CI, Google Play internal deploy, split Gradle/Konan caches.

### Текущий toolchain на `update` (файлы)

Из `gradle/libs.versions.toml` и модулей:

| Компонент | Версия |
| --- | --- |
| AGP | 8.13.0 |
| Gradle | 8.13 |
| Kotlin | 1.9.10 |
| kotlinx-coroutines | 1.6.4 |
| SQLDelight | 1.5.5 |
| moko-kswift | 0.6.1 |
| moko-mvvm | 0.16.1 |
| kotest | 5.8.0 |
| Compose BOM | 2023.03.00 |
| Compose compiler extension | 1.5.3 |
| atomicfu (forced) | 0.17.3 |

`shared` SQLDelight:

```kotlin
schemaOutputDirectory = file("src/commonMain/sqldelight/databases")
verifyMigrations = true
```

Bitrise `primary` гоняет в одном вызове:

```
:shared:generateCommonMainRepeaTodoDbSchema :shared:verifySqlDelightMigration
```

### kswift в iOS (факт использования)

Плагин: только `SealedToSwiftEnumFeature`. Сгенерированные `*Ks` в приложении:

- `NavigationDestinationKs` — `iosApp/RepeaTodo/ViewFactory.swift`
- `ToDoListKs` — `iosApp/RepeaTodo/Main/DrawerToDoListsView.swift`

Kotlin sealed-источники:

- `shared/.../NavigationDestination` (`MainNavigator.kt`)
- `shared/.../ToDoList` (`ToDoList.kt`)

`WorkState` в Swift используется как Kotlin-классы (`WorkStateCompleted` и т.п.), не как `*Ks`.

Ссылка в Xcode-проекте на сгенерированный файл:

- `iosApp/RepeaTodo.xcodeproj/project.pbxproj` → `shared/build/bin/iosSimulatorArm64/debugFramework/sharedSwift/RepeaTodo_shared.swift`

Транзитивный `kswift-runtime` из `moko-mvvm-flow` — это **не** Gradle-плагин kswift.

### Среда агента

- Linux Cloud Agent: iOS Native targets disabled (`can be built with one of the hosts: macos_x64, macos_arm64`).
- Bitrise stack: `osx-xcode-latest-stable`, в логах фигурировал Xcode **26.6**.
- KGP 1.9.10 официально тестировался до Xcode 16.0 и AGP 8.2; предупреждения глушатся свойствами выше. На `master` тот же KGP уже собирался на этом Bitrise stack.

### Известные красные прогоны Bitrise **этого** PR (до финального зелёного `77cd332`)

Порядок по сути, не по wall-clock:

1. Исходный PR (Kotlin 2.x / AGP 8 / устаревший merge-base) — conflict + красный CI.
2. Kotlin **2.0.21** (попытка после отката 2.1.20) — iOS/XCFramework (детали в логах того прогона).
3. Kotlin 1.9.10 + AGP 8.13, **до** пина atomicfu: `linkDebugFrameworkIosArm64` — klib `atomicfu-iosarm64/0.23.1` skipped (`unknown IR provider: kotlin.native.cinterop`), daemon disappeared.
4. После пина atomicfu 0.17.3: падение **не** на Native link, а на SQLDelight + Gradle 8.13 implicit dependency (`verifyCommonMainRepeaTodoDbMigration` vs `generateCommonMainRepeaTodoDbSchema`, путь `shared/src/commonMain/sqldelight`).

Локально SQLDelight-ошибка **не** воспроизводилась, если запускать только `:shared:verifySqlDelightMigration` без generate в том же графе.

### Локальный провал, который чинили в Gradle, не в CI YAML

Сообщение Gradle 8.13:

```
Task ':shared:verifyCommonMainRepeaTodoDbMigration' uses this output of task
':shared:generateCommonMainRepeaTodoDbSchema' without declaring an explicit or implicit dependency.
Location: '.../shared/src/commonMain/sqldelight'
```

Документация Gradle: https://docs.gradle.org/8.13/userguide/validation_problems.html#implicit_dependency

### Тег и Play-версии (2026-09-05)

- Annotated-тег `0.6.0` → `3e78817`. Предок `origin/master` и `origin/update`.
- В `androidApp/build.gradle.kts` зашито `versionCode = 7`, `versionName = "0.6"`; Play-сборка переписывает это шагом Bitrise.
- `trigger_map`: `tag: ":^[0-9]+\\.[0-9]+\\.[0-9]+$"` → `deploy-android-play`.
- `git-clone@8`: `fetch_tags: "yes"`. При `BITRISE_TRIGGER_METHOD=manual` ещё `clone_depth: "100"`; иначе дефолт шага (`--depth=1`).
- Build 105: `git fetch --depth=1 --tags` скачал ref `0.6.0`, но `GIT_CLONE_COMMIT_COUNT=1` → `describe` → `0.0.1`.
- Формула (утверждена): `versionCode = Major*1000000 + Minor*10000 + Patch*100 + (BITRISE_BUILD_NUMBER % 100)`; `versionName = <три компонента>.<BITRISE_BUILD_NUMBER>`.
- База: `$BITRISE_GIT_TAG`, иначе `git describe --tags --abbrev=0`, иначе `0.0.1`.

### Прочее

- Тело PR #33 в GitHub пустое (проверено `gh pr view` 2026-09-05).
- Пользователь общается на русском; в правилах агента: отвечать по-русски; не `git add -A`; не коммитить `.idea` / `.vscode`; не удалять kswift без явного ОК.

---

## Исследования

Что смотрели и пробовали. **Не** является выбранным планом, пока не попало в «Действия» или в явное решение пользователя.

### Почему `compileSdk` 36 не жил на `master`

- Play Console: нужен `targetSdk` 36.
- AGP 7.4.2 aapt2 не читает resources.arsc API 36 (`RES_TABLE_TYPE_TYPE entry offsets overlap`) — зафиксировано коммитом `de332ff`.
- Вывод из этого исследования (см. «Умозаключения»): нужен более новый AGP, не только bump `compileSdk`.

### Kotlin 2.x + AGP 8.13

Пробовали (и откатили в истории ветки):

- **Kotlin 2.1.20** — локально смешанные Kotlin Gradle Plugin (`GradleBuildTime` / `kotlin-compiler-embeddable`).
- **Kotlin 1.9.10 + AGP 8.13 через `buildscript` classpath** — `NoSuchMethodError` `AbstractTimeSource` (kotlinx-coroutines в двух classloader’ах при загрузке SDK). Лечится тем, что AGP тоже в `plugins {}` рядом с KGP (комментарий в корневом `build.gradle.kts`).
- **Kotlin 2.0.21** — Android локально собирался; Bitrise iOS нет. Откат к 1.9.10 (`3ab79df` / бывший `ea7f04a`), AGP 8.13 оставлен.

iOS-стек, который держит 1.9.10: moko-kswift **0.6.1**, moko-mvvm **0.16.1**, SQLDelight **1.5.5**, coroutines **1.6.4**.

### atomicfu 0.23.x vs 0.17.3

- Транзитивно с новым стеком Native тащился `atomicfu-iosarm64` **0.23.1**.
- Klib 0.23.x требует Kotlin **1.9.21+** (cinterop IR provider). На 1.9.10 линкер пропускает klib и падает.
- kotlinx-coroutines **1.6.4** штатно сидит на atomicfu **0.17.3**.
- Плагин kswift известен тем, что при линковке трогает/скипает cinterop klibs — это **наблюдение из разбора**, не доказанный единственный root cause 0.23.1.

### SQLDelight 1.5.5 × Gradle 8

- `GenerateSchemaTask` пишет в `src/commonMain/sqldelight/databases`.
- `VerifyMigrationTask` читает `src/commonMain/sqldelight` (родитель).
- Gradle 8.13 считает это implicit dependency, если обе задачи в одном task graph.
- В SQLDelight 2.x overlap чинят в плагине; апгрейд SQLDelight **не** делали (тянет Kotlin 2 / новую схему плагина).
- Рассматривали `dependsOn` vs `mustRunAfter`: `dependsOn` всегда гонял бы generate (перезапись `.db` в source tree). Выбран `mustRunAfter`.
- Альтернатива «разнести две gradle-команды в Bitrise» не понадобилась.

Известные upstream-обсуждения (ориентиры, не патчи в этом репо): cashapp/sqldelight issues про Gradle 8 implicit dependency (`#3975`, `#3282`, `#4684`).

### kswift: оценка замены (пользователь просил **только оценить**, не внедрять)

Плагин `moko-kswift` archived. Он даёт exhaustive `switch` по sealed в Swift (`NavigationDestinationKs`, `ToDoListKs`). ObjC-мост Kotlin **сам по себе** не даёт таких enum.

Варианты, которые излагали пользователю (решения нет):

1. **Ручные 2 Swift enum + дроп плагина** — минимальный объём для этого PR. Нужно переписать два call site и, вероятно, убрать plugin из Gradle/Xcode generated-file. Транзитивный runtime из moko-mvvm-flow может остаться.
2. **Позже: Kotlin 2.x + SKIE** — production sealed→Swift enum. SKIE целится в Kotlin **2.0–2.4**, не в 1.9.10. Потребует SQLDelight 2 и проверку moko. Отдельная миграция.
3. **Swift Export** (Kotlin 2.0.20+, experimental): sealed **по-прежнему не** exhaustive Swift enum; несовместим с SKIE; переписывание Swift-слоя moko-mvvm.
4. **Оставить kswift** + пин atomicfu (уже сделан). Плагин archived, но Bitrise на `77cd332` с пином прошёл.

Bump Kotlin/AGP/KMP **не заменяет** exhaustive sealed switches.

### Поле тега и формула versionCode

В промпте было `tag_pattern`; в `trigger_map` этого файла ключ — `tag`. Значение regex взято буквально.

`git describe --tags --abbrev=0` — ближайший предок HEAD (пользователь оставил так).

Формула товарища с `% 10` заменена на `% 100` и сдвиг множителей, чтобы до 100 ретраев одного `Major.Minor.Patch` не сталкивались в Play. Minor/Patch ≥ 100 интерферируют с соседним полем; пользователь это принял.

Первая незапрошенная попытка (коммиты `f34d25b` / `fdef2e9` и revert’ы) снята с истории `reset --soft` на `a5a00dd`.

### Тело PR

`ManagePullRequest` `update_pr` с непустым body падает, если текущее description пустое — воспроизведено 2026-08-31 и 2026-09-05.

2026-09-05 дополнительно пробовали:

- `gh pr edit 33 --body …` — GraphQL `Resource not accessible by integration (updatePullRequest)`
- `gh api -X PATCH repos/olegivo/RepeaTodo/pulls/33` — HTTP 403, то же сообщение
- аккаунт `gh`: `cursor`, write на PR недоступен (интеграция read-only)
- Computer Use на github.com: сессии нет (Sign in / Sign up), меню `⋯` только Copy link / Copy Markdown, карандаша Edit нет

Обход, который сработает без новых секретов: вставить description вручную в GitHub UI под аккаунтом с write. Готовый текст лежит в «Следующие шаги». Title уже корректный, менять не нужно.

---

## Умозаключения

Помечено отдельно, чтобы не воспринимать как факт.

1. **AGP 8.13 — минимально достаточный рычаг для `compileSdk` 36** при желании не трогать Kotlin 1.9.10 / iOS. Gradle 8.13 и JDK 17 — следствия AGP, не самоцель.
2. **Kotlin 2.x в этом PR — регрессия iOS**, пока не мигрированы SQLDelight и moko/kswift (или не заменён kswift). Имеет смысл вынести в отдельный PR.
3. **Пин atomicfu 0.17.3 — рабочий обход**, не лечение корневой рассинхронизации версий. Снятие пина без Kotlin ≥ 1.9.21 / 2.x снова сломает Native link.
4. **`mustRunAfter` для SQLDelight — правильный локальный фикс 1.5.5**; долгий путь — SQLDelight 2, где generate/verify не делят source tree как output/input.
5. **Удаление kswift уменьшит хрупкость Native-линковки**, но это продуктовое решение по Swift API, не «бесплатный рефакторинг». Без замены сломаются exhaustive `switch` в `ViewFactory.swift` и `DrawerToDoListsView.swift`.
6. **Предупреждение Xcode 26 vs KGP 1.9.10 само по себе не фатально**: `master` уже собирался на том же stack. Красные iOS-сборки этого PR имели более конкретные причины (atomicfu klib, затем SQLDelight).
7. **Риск CI после rebase 2026-08-31 закрыт фактом**: `primary` зелёный на `c8bd991` и на docs-актуализации `54422e2`. Ещё один коммит только в этот markdown снова сменит HEAD; для кода сборки это формальность.
8. **PR готов к ревью человека** по заявленной цели: SDK 36 компилируется, Bitrise на `54422e2` зелёный, `master` не уехал. Оставшийся продуктовый долг (Kotlin 2, SQLDelight 2, kswift→SKIE) не должен смешиваться в #33, пока пользователь не попросит.
9. **Пустое body — ограничение инструментов агента, не пробел в ревью-материале.** Текст description готов; без записи в GitHub UI карточка PR выглядит пустой.

---

## Ограничения пользователя (действующие)

- Отвечать по-русски.
- Не коммитить без нужды; `git add` только с явными путями; не `.idea` / `.vscode`.
- **Не дропать kswift**, пока пользователь не утвердит вариант.
- Не мержить PR и не auto-merge без явной просьбы.
- iOS на Linux-агенте не собирать — это ожидаемо; источник истины по iOS — Bitrise.
- Новую ветку `cursor/…` не создавать, пока пользователь не скажет иначе.
- Не запускать `deploy-android-play` без явной просьбы.

---

## Следующие шаги (ещё не сделаны)

Сделано к 2026-09-05:

- [x] Дождаться Bitrise `primary` на HEAD после rebase + первый handoff (`c8bd991`, build 97, SUCCESS).
- [x] Дождаться Bitrise `primary` на актуализации handoff (`54422e2`, build 98, SUCCESS).

Осталось с write-доступом в GitHub UI:

- [ ] Вставить body PR #33 (API и агентский браузер пустой body не пишут). Текст:

```markdown
## Цель

Поднять toolchain так, чтобы `compileSdk` 36 реально компилировался. На `master` приложение уже с `targetSdk` 36, но `compileSdk` 34: AGP 7.4.2 aapt2 не читает resources.arsc API 36 (`RES_TABLE_TYPE_TYPE entry offsets overlap`, коммит `de332ff`).

## Что вошло

- AGP 8.13.0 через `plugins {}` (не `buildscript` classpath), Gradle 8.13, JDK 17
- `androidApp`: `compileSdk` 36, `targetSdk` 36, `versionCode` 7 / `0.6`, Compose compiler 1.5.3
- `shared`: `compileSdk` 36; `targetSdk` у library убран
- Kotlin **оставлен 1.9.10** (не 2.x)
- pin `atomicfu*` 0.17.3 — Native link на Kotlin 1.9.10
- SQLDelight 1.5.5: `verify*DbMigration` `mustRunAfter` `generate*DbSchema`
- `.cursor/install.sh`: `platforms;android-36`, `build-tools;36.0.0`
- ключи кэша Bitrise: `gradle-deps-agp813-`, `konan-k1910-`

## Сознательно не делали

- не поднимали Kotlin 2.x, SQLDelight 2, SKIE
- не удаляли и не заменяли moko-kswift 0.6.1 (`NavigationDestinationKs`, `ToDoListKs`)
- не снимали pin atomicfu
- не мержили этот PR

## CI

Bitrise `primary` на `c8bd991` (build 97) и `54422e2` (build 98): **SUCCESS**
https://app.bitrise.io/app/443dc155-900f-4e54-9c79-aaea03df19d6/build/9a4b9082-b4cc-426a-8980-6b8e6ab796b0

Linux Cloud Agent iOS не собирает; источник истины по iOS — Bitrise.

## Контекст для ревью / следующего агента

`docs/pr-33-handoff.md` — действия / факты / исследования / умозаключения раздельно.

Оставшийся долг (отдельные PR, не этот): Kotlin 2.x + SQLDelight 2 + судьба kswift.
```

Только по явной просьбе пользователя:

- [ ] Выбрать судьбу kswift (ручные enum / SKIE позже / оставить как есть).
- [ ] Отдельный PR: Kotlin 2.x + SQLDelight 2 + проверка moko.
- [ ] Merge PR #33.

Не делать по собственной инициативе:

- откат AGP на 7.4;
- `compileSdk` обратно на 34;
- снятие atomicfu pin;
- удаление `mustRunAfter` SQLDelight;
- Kotlin 2.x «заодно»;
- запуск `deploy-android-play` / `POST /builds`.
