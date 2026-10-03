# [B.M] Minecraft 回到死亡地點

[![Paper](https://img.shields.io/badge/Paper-26.3-2D2D2D)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![GitHub](https://img.shields.io/badge/GitHub-bm--minecraft--back--to--death-181717?logo=github)](https://github.com/BoringMan314/bm-minecraft-back-to-death)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

適用於 **Minecraft Paper 26.3** 的插件：記錄每位玩家最後一次死亡的位置，玩家可使用 **`/back`** 回到上一個死亡地點。

*适用于 **Minecraft Paper 26.3** 的插件：记录玩家最后一次死亡的位置，并可使用 **`/back`** 返回。*<br>
*Minecraft Paper 26.3 向け：プレイヤーの最後の死亡地点を記録し、**`/back`** で戻れます。*<br>
*A **Minecraft Paper 26.3** plugin that records a player's latest death location and restores it with **`/back`**.*

---

## 目錄

- [功能](#功能)
- [系統需求](#系統需求)
- [安裝方式](#安裝方式)
- [指令與權限](#指令與權限)
- [設定檔](#設定檔)
- [本機建置](#本機建置)
- [專案結構](#專案結構)
- [版本與多語系](#版本與多語系)
- [資料與隱私說明](#資料與隱私說明)
- [授權](#授權)
- [問題與建議](#問題與建議)

---

## 功能

- 死亡時記錄世界、座標與朝向。
- `/back` 只會傳送玩家本人，控制台不可使用。
- 死亡位置保存在 `data.yml`，重新啟動後仍保留。
- OP 或控制台可開關功能、查看狀態與重載設定。
- 內建四種語系 JAR：`zh_TW`、`zh_CN`、`ja_JP`、`en_US`。

---

## 系統需求

- **Paper 26.3** 伺服器。
- **Java 25**。

---

## 安裝方式

1. 從 [`dist/`](dist/) 選擇所需語系 JAR。
2. 將 JAR 放入 Paper 伺服器的 `plugins/` 資料夾。
3. 啟動伺服器；首次載入後設定檔位於 `plugins/bm-minecraft-back-to-death/`。

> 請勿同時安裝多個語系 JAR。

---

## 指令與權限

| 指令 | 可使用者 | 說明 |
|---|---|---|
| `/back` | 遊戲內玩家 | 傳送回自己的上一個死亡地點。 |
| `/bm-minecraft-back-to-death` | 所有人 | 顯示指令說明。 |
| `/bm-minecraft-back-to-death 0\|1` | OP／控制台 | 關閉或開啟功能。 |
| `/bm-minecraft-back-to-death info\|status\|reload` | OP／控制台 | 顯示資訊、狀態或重新載入。 |

管理權限為 `bm-minecraft-back-to-death.admin`；控制台永遠可使用管理指令。

---

## 設定檔

| 設定 | 預設值 | 說明 |
|---|---:|---|
| `enabled` | `true` | 是否記錄死亡位置並允許 `/back`。 |
| `admin-require-op` | `true` | 是否限制遊戲內管理者必須是 OP。 |

玩家可見文字由 [`lang/`](src/main/resources/lang/) 的語系檔提供。修改後執行 `/bm-minecraft-back-to-death reload`。

---

## 本機建置

```bat
build.bat
```

建置完成後會在 `dist/` 產生四份語系 JAR。

---

## 專案結構

| 路徑 | 說明 |
|---|---|
| [`src/main/java/bm.minecraft.back.to.death/`](src/main/java/bm.minecraft.back.to.death/) | 死亡位置、指令與語系載入程式。 |
| [`src/main/resources/`](src/main/resources/) | `plugin.yml`、設定與語系檔。 |
| `data.yml` | 伺服器首次使用後建立的死亡位置資料。 |

---

## 版本與多語系

- **插件版本**：`26.3_0.0.1`
- **目標 API**：Paper `26.3`
- **內建語系**：`zh_TW`、`zh_CN`、`ja_JP`、`en_US`

---

## 資料與隱私說明

本插件只在伺服器本機的 `data.yml` 儲存 UUID 與死亡位置，不會上傳資料、不含分析或廣告追蹤。

---

## 授權

本專案以 [MIT License](LICENSE) 授權。

---

## 問題與建議

歡迎透過 [GitHub Issues](https://github.com/BoringMan314/bm-minecraft-back-to-death/issues) 回報錯誤或提出改善建議。
