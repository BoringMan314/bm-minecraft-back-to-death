# [B.M] Minecraft 回到死亡地點

[![Paper](https://img.shields.io/badge/Paper-26.3-2D2D2D)](https://papermc.io/)
[![Java](https://img.shields.io/badge/Java-25-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![GitHub](https://img.shields.io/badge/GitHub-bm--minecraft--back--to--death-181717?logo=github)](https://github.com/BoringMan314/bm-minecraft-back-to-death)
[![GitHub all releases](https://img.shields.io/github/downloads/BoringMan314/bm-minecraft-back-to-death/total)](https://github.com/BoringMan314/bm-minecraft-back-to-death/releases)
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
- [本機開發與測試](#本機開發與測試)
- [技術概要](#技術概要)
- [專案結構](#專案結構)
- [版本與多語系](#版本與多語系)
- [資料與隱私說明](#資料與隱私說明)
- [維護者：更新 GitHub 與發行版本](#維護者更新-github-與發行版本)
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

### 從 GitHub Releases 安裝

若 [GitHub Releases](https://github.com/BoringMan314/bm-minecraft-back-to-death/releases) 已提供 JAR，請選擇所需語系下載；尚無發行檔時，可依下方步驟自行建置。

1. 停止伺服器，將所選 JAR 放入 `plugins/` 資料夾。
2. 啟動 **Paper 26.3** 伺服器，確認控制台顯示插件已啟用。
3. 在 `plugins/bm-minecraft-back-to-death/` 調整設定；指令與設定項目請見下方說明。

> 同一插件只安裝一份語系 JAR；更新時請移除舊版 JAR。

### 從原始碼建置

1. 點選本頁綠色 **Code** → **Download ZIP** 解壓，或執行 `git clone https://github.com/BoringMan314/bm-minecraft-back-to-death.git`。
2. 依 [本機開發與測試](#本機開發與測試) 準備 JDK 與 Maven，執行建置。
3. 從本機 `dist/` 選取 `bm-minecraft-back-to-death_26.3_0.0.1-<語系>.jar`，依上方步驟安裝。

---

## 指令與權限

| 指令 | 可使用者 | 說明 |
|------|------|------|
| `/back` | 遊戲內玩家 | 傳送回自己的上一個死亡地點。 |
| `/bm-minecraft-back-to-death` | 所有人 | 顯示指令說明。 |
| `/bm-minecraft-back-to-death 0\|1` | OP／控制台 | 關閉或開啟功能。 |
| `/bm-minecraft-back-to-death info\|status\|reload` | OP／控制台 | 顯示資訊、狀態或重新載入。 |

管理權限為 `bm-minecraft-back-to-death.admin`；控制台永遠可使用管理指令。

---

## 設定檔

| 設定 | 預設值 | 說明 |
|------|------:|------|
| `enabled` | `true` | 是否記錄死亡位置並允許 `/back`。 |
| `admin-require-op` | `true` | 是否限制遊戲內管理者必須是 OP。 |

玩家可見文字由 [`lang/`](src/main/resources/lang/) 的語系檔提供。修改後執行 `/bm-minecraft-back-to-death reload`。

---

## 本機開發與測試

**Windows / PowerShell：**

1. 準備 **JDK 25**：放在 `.tools/<JDK 資料夾>/`，或安裝至可由 Windows `JavaSoft\JDK\25` 登錄項目辨識的位置。
2. 準備 **Maven**：放在 `.tools/apache-maven-3.9.11/`，或讓 `mvn.cmd` 可從 `PATH` 執行。首次建置需連線下載依賴。
3. 在專案根目錄執行：

```powershell
.\build.bat --no-pause
```

建置完成後，`dist/` 會產生 `zh_TW`、`zh_CN`、`ja_JP`、`en_US` 四份語系 JAR。直接執行 `build.bat` 會在結束時暫停；`--no-pause` 適合終端與自動化使用。

修改 [`src/main/java/`](src/main/java/) 或 [`src/main/resources/`](src/main/resources/) 後，重新建置並更換測試伺服器的 JAR，重新啟動伺服器，驗證 [功能](#功能) 及 [指令與權限](#指令與權限) 中的操作。`dist/`、`target/` 與 `.tools/` 由 [`.gitignore`](.gitignore) 排除，不會隨原始碼上傳。

---

## 技術概要

- **核心實作**：透過玩家死亡事件記錄位置，以指令執行傳送，並將死亡資料保存於本機 YAML。
- **指令與權限**：由 [`plugin.yml`](src/main/resources/plugin.yml) 宣告，實際管理限制由指令處理程式與設定共同決定。
- **建置與語系**：使用 [`pom.xml`](pom.xml) 定義依賴，由 [`build.bat`](build.bat) 依語系逐次執行 Maven 建置。

---

## 專案結構

| 路徑 | 說明 |
|------|------|
| [`pom.xml`](pom.xml) | 插件版本、Java 版本、Paper API 依賴與 Maven 建置設定 |
| [`build.bat`](build.bat) | Windows 四語系 JAR 建置腳本 |
| [`src/main/java/bm.minecraft.back.to.death/`](src/main/java/bm.minecraft.back.to.death/) | 插件主類別與功能實作 |
| [`src/main/resources/plugin.yml`](src/main/resources/plugin.yml) | 插件資訊、指令與權限宣告 |
| [`src/main/resources/config.yml`](src/main/resources/config.yml) | 功能與管理設定 |
| [`src/main/resources/active-language.yml`](src/main/resources/active-language.yml) | 建置時套用的預設語系 |
| [`src/main/resources/lang/`](src/main/resources/lang/) | 四種語系的訊息 |
| [`.gitignore`](.gitignore) | 本機工具、暫存與建置產物的排除規則 |

---

## 版本與多語系

- **插件版本**：目前為 `26.3_0.0.1`，建置設定見 [`pom.xml`](pom.xml)。
- **目標 API**：Paper `26.3`；實際依賴版本見 `pom.xml` 的 `paper.version`。
- **預設語系**：`zh_TW`，由 Maven 的 `default.language` 設定。
- **內建語系**：`zh_TW`、`zh_CN`、`ja_JP`、`en_US`（路徑為 `src/main/resources/lang/<語系>.yml`）。
- **語系選擇**：建置腳本將不同預設語系分別打包為 JAR；執行時依 `active-language.yml` 載入對應語系。

---

## 資料與隱私說明

本插件只在伺服器本機的 `data.yml` 儲存 UUID 與死亡位置，不會上傳資料、不含分析或廣告追蹤。

---

## 維護者：更新 GitHub 與發行版本

### 更新至 GitHub

在專案根目錄執行：

```powershell
git add README.md
git commit -m "V26.3_0.0.1"
git push origin main
```

### 準備發行檔

1. 確認 [`pom.xml`](pom.xml)、[`build.bat`](build.bat) 與 [`plugin.yml`](src/main/resources/plugin.yml) 中的版本設定一致。
2. 執行 `build.bat --no-pause`，並在測試伺服器驗證功能及語系顯示。
3. 在 [GitHub Releases](https://github.com/BoringMan314/bm-minecraft-back-to-death/releases) 建立對應版本，附上本機 `dist/` 中的四份語系 JAR 與更新說明。

---

## 授權

本專案以 [MIT License](LICENSE) 授權。

---

## 問題與建議

歡迎透過 [GitHub Issues](https://github.com/BoringMan314/bm-minecraft-back-to-death/issues) 回報錯誤或提出改善建議。回報時請一併提供 Paper 版本、Java 版本、插件版本、**語系**及重現步驟；若有錯誤，請附上相關設定與錯誤日誌。
