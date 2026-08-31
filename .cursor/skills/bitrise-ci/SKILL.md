---
name: bitrise-ci
description: Читать логи и статусы сборок Bitrise через API. Использовать при падениях CI, вопросах про Bitrise, workflow primary / deploy-ios / deploy-android-play, логах билдов.
---

# Bitrise CI для RepeaTodo

Конфиг пайплайна: `bitrise.yml`. Workflows: `primary` (PR), `deploy-ios`, `deploy-android-play`.

## Доступ

Нужен `BITRISE_TOKEN` (Runtime Secret). Не `echo` / `printenv` токен. Если unset — остановись: пользователь должен добавить секрет в Cloud Agents и стартовать **новую** сессию.

Роль на приложении минимум Developer. Endpoint логов: `GET /apps/{app-slug}/builds/{build-slug}/log`.

```bash
set -euo pipefail
: "${BITRISE_TOKEN:?BITRISE_TOKEN is not set}"

auth=(-H "Authorization: ${BITRISE_TOKEN}" -H "accept: application/json")
base=https://api.bitrise.io/v0.1

if [ -z "${BITRISE_APP_SLUG:-}" ]; then
  BITRISE_APP_SLUG="$(
    curl -sS "${auth[@]}" "${base}/apps?limit=50" \
      | jq -r '.data[] | select((.title|test("RepeaTodo";"i")) or (.repo_url|test("RepeaTodo";"i"))) | .slug' \
      | head -n1
  )"
fi
: "${BITRISE_APP_SLUG:?could not resolve app slug}"
```

Код `401` — токен неверный. `403` на `/log` — роль ниже Developer.

## Найти билд

```bash
# последние билды; status: 0=not finished, 1=success, 2=failed, 3=aborted, 4=in-progress
curl -sS "${auth[@]}" \
  "${base}/apps/${BITRISE_APP_SLUG}/builds?limit=10&workflow=primary"

# по ветке / PR
curl -sS "${auth[@]}" \
  "${base}/apps/${BITRISE_APP_SLUG}/builds?branch=${BRANCH}&status=2&limit=5"
```

Slug билда — поле `slug` (или из URL `https://app.bitrise.io/build/<slug>`).

## Логи

```bash
log_json="$(curl -sS "${auth[@]}" "${base}/apps/${BITRISE_APP_SLUG}/builds/${BUILD_SLUG}/log")"
raw_url="$(printf '%s' "$log_json" | jq -r '.expiring_raw_log_url // empty')"
# raw URL живёт ~10 минут; скачай сразу. Не логируй сам signed URL в чат, если в нём есть подпись.
if [ -n "$raw_url" ]; then
  curl -sS "$raw_url" -o /tmp/bitrise-${BUILD_SLUG}.log
else
  printf '%s' "$log_json" | jq -r '.log_chunks[]?.chunk // empty'
fi
```

Дальше ищи failed step, Gradle/Xcode ошибку, нехватку секретов code signing. Не триггерь новые билды (`POST /builds`) без явной просьбы пользователя.

## Документация

- [Auth](https://docs.bitrise.io/en/bitrise-ci/api/authenticating-with-the-bitrise-api)
- [Builds and logs](https://docs.bitrise.io/en/bitrise-ci/api/managing-an-app-s-builds)
