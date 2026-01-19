# FileTools — Minimal PDF Processing Service

> 🇬🇧 English version below  
> 🇷🇺 Русская версия — ниже

---

## 🇬🇧 English

### Overview

FileTools is a small end-to-end web application for basic PDF processing tasks such as merging files, splitting documents, and deleting selected pages.

The project is inspired by services like *iLovePDF*, but implemented as a clean, minimal, and fully self-contained demo application.  
Its main goal is to demonstrate backend–frontend interaction, asynchronous job processing, and a clear API-driven architecture.

---

### Features

- **Merge PDFs**  
  Combine multiple PDF files into a single document.

- **Split PDF**  
  Split one PDF into two separate files at a given page number.

- **Delete Pages**  
  Remove selected pages from a PDF using a simple page specification (e.g. `1,3,5-7`).

- **Asynchronous processing**  
  All operations are executed as background jobs with polling-based status updates.

- **Single-page UI**  
  Upload, process, and download files without leaving the page.

---

### Tech Stack

#### Backend
- Java 21
- Spring Boot
- Spring Web (REST API)
- Asynchronous job execution (`@Async`)
- Apache PDFBox
- Multipart file upload
- Local file storage
- JPA / Hibernate (Job & JobFile entities)

#### Frontend
- Vanilla JavaScript
- HTML5
- Tailwind CSS
- Fetch API
- Polling-based job status tracking
- No frameworks, no build step

---

### Architecture

- Each user action creates a **Job**
- Uploaded files are stored as **JobFiles**
- Processing is handled by a **JobProcessor** based on `JobType`
- Jobs are executed asynchronously in the background
- The frontend polls job status until completion
- Result files are downloaded via a dedicated endpoint

The architecture is modular and extensible: new file operations can be added by implementing a new `JobProcessor`.

---

### API Flow (Simplified)

1. `POST /api/jobs` — create a job
2. `POST /api/jobs/{jobId}/files` — upload input files
3. `POST /api/jobs/{jobId}/start` — start processing
4. `GET /api/jobs/{jobId}` — poll job status
5. `GET /api/files/{fileId}/download` — download result

---

### Deployment

🚧 **Live demo:**  
*(link will be added later)*

The project is designed to be easily deployable on platforms such as Render, Railway, Fly.io, or any VPS with Java support.

---

### Purpose

This project was built as:
- a portfolio project,
- a demonstration of clean backend architecture,
- an example of asynchronous processing,
- a simple but thoughtful UI/UX showcase.

---

### Author

Developed by **Zaviriukha Artemii**  
Computer Science / Software Engineering

---

---

## 🇷🇺 Русская версия

### Описание

FileTools — это небольшой end-to-end веб-проект для базовой обработки PDF-файлов: объединения, разбиения и удаления выбранных страниц.

Проект вдохновлён сервисами вроде *iLovePDF*, но реализован как минималистичное и полностью самостоятельное demo-приложение.  
Основная цель — показать взаимодействие frontend и backend, асинхронную обработку задач и чистую API-архитектуру.

---

### Возможности

- **Объединение PDF**  
  Склеивание нескольких PDF-файлов в один.

- **Разделение PDF**  
  Разбиение одного PDF на два файла по номеру страницы.

- **Удаление страниц**  
  Удаление выбранных страниц по простой спецификации (например `1,3,5-7`).

- **Асинхронная обработка**  
  Все операции выполняются в фоне с опросом статуса задачи.

- **Одностраничный интерфейс**  
  Загрузка, обработка и скачивание файлов без переходов между страницами.

---

### Используемые технологии

#### Backend
- Java 21
- Spring Boot
- Spring Web (REST API)
- Асинхронные задачи (`@Async`)
- Apache PDFBox
- Загрузка файлов (multipart)
- Локальное файловое хранилище
- JPA / Hibernate (Job и JobFile)

#### Frontend
- Чистый JavaScript (Vanilla JS)
- HTML5
- Tailwind CSS
- Fetch API
- Polling для отслеживания статуса задач
- Без фреймворков и сборщиков

---

### Архитектура

- Каждое действие пользователя создаёт **Job**
- Загруженные файлы хранятся как **JobFiles**
- Обработка выполняется соответствующим **JobProcessor**
- Задачи запускаются асинхронно
- Frontend опрашивает статус до завершения
- Результаты скачиваются через отдельный endpoint

Архитектура легко расширяется — новые операции добавляются через новые `JobProcessor`.

---

### Поток API (упрощённо)

1. `POST /api/jobs` — создание задачи
2. `POST /api/jobs/{jobId}/files` — загрузка файлов
3. `POST /api/jobs/{jobId}/start` — запуск обработки
4. `GET /api/jobs/{jobId}` — получение статуса
5. `GET /api/files/{fileId}/download` — скачивание результата

---

### Деплой

🚧 **Демо-версия:**  
*(ссылка будет добавлена позже)*

Проект готов к деплою на Render, Railway, Fly.io или любой VPS с поддержкой Java.

---

### Назначение проекта

Проект создан как:
- портфолио-работа,
- демонстрация архитектурного мышления,
- пример асинхронной обработки,
- аккуратный и понятный UI/UX-пример.

---

### Автор

Разработчик: **Завирюха Артемий**  
Computer Science / Software Engineering
