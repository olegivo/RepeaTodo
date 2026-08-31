# RepeaTodo — инструкции для агентов

## Cursor Cloud

Среда: персональная Cloud Agent environment `olegivo/RepeaTodo`
([57f60bc9-a479-11f1-a7d1-d6b4613131ce](https://cursor.com/dashboard/cloud-agents/environments/e/57f60bc9-a479-11f1-a7d1-d6b4613131ce)).
Linux VM: JVM/Android собираются здесь, iOS (`iosApp`, Kotlin/Native ios*) — нет (нужны macOS + Xcode). CI iOS идёт в Bitrise.

Секреты задаются в Dashboard → Cloud Agents → Secrets (или на странице environment), не в `environment.json` и не в репозитории.

### Bitrise CI

Доступ к логам сборок — через API. У новых агентов переменная появляется только при **старте** сессии.

| Переменная | Где | Назначение |
| --- | --- | --- |
| `BITRISE_TOKEN` | Runtime Secret | PAT или Workspace API token с ролью **Developer** на приложении RepeaTodo. Нужен для логов (`GET .../log`). Tester/QA недостаточно. |
| `BITRISE_APP_SLUG` | опционально, не секрет | Slug приложения. Если нет — найти через `GET /v0.1/apps`. |

Когда пользователь спрашивает про падение CI, Bitrise, `primary` / `deploy-ios` / `deploy-android-play` или логи сборки — следуй `.cursor/skills/bitrise-ci/SKILL.md`.

Не печатай значение токена, не клади его в коммиты, диффы и артефакты. В curl используй `"Authorization: $BITRISE_TOKEN"`. Если переменная пустая — попроси добавить Runtime Secret и **запустить нового агента**; текущая сессия секрет не подхватит.
