const API_BASE = "";

const sleep = (ms) => new Promise(r => setTimeout(r, ms));

async function createJob(type, optionsObj) {
  const body = {
    type,
    optionsJson: optionsObj ? JSON.stringify(optionsObj) : ""
  };

  const res = await fetch(`${API_BASE}/api/jobs`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(body),
  });

  if (!res.ok) throw new Error(`createJob failed: ${res.status}`);
  return await res.json();
}

async function uploadFile(jobId, file) {
  const fd = new FormData();
  fd.append("file", file);

  const res = await fetch(`${API_BASE}/api/jobs/${jobId}/files`, {
    method: "POST",
    body: fd,
  });

  if (!res.ok) throw new Error(`uploadFile failed: ${res.status}`);
  return await res.json();
}

async function startJob(jobId) {
  const res = await fetch(`${API_BASE}/api/jobs/${jobId}/start`, { method: "POST" });
  if (!res.ok) throw new Error(`startJob failed: ${res.status}`);
  return await res.json();
}

async function getJob(jobId) {
  const res = await fetch(`${API_BASE}/api/jobs/${jobId}`);
  if (!res.ok) throw new Error(`getJob failed: ${res.status}`);
  return await res.json();
}

async function listFiles(jobId) {
  const res = await fetch(`${API_BASE}/api/jobs/${jobId}/files`);
  if (!res.ok) throw new Error(`listFiles failed: ${res.status}`);
  return await res.json();
}

async function pollUntilDone(jobId, onTick) {
  while (true) {
    const job = await getJob(jobId);
    onTick?.(job);

    if (job.status === "DONE") return job;
    if (job.status === "FAILED") {
      throw new Error(job.errorMessage || job.errorCode || "Job failed");
    }
    await sleep(600);
  }
}

function setStatus(el, msg) {
  el.textContent = msg || "";
}

function escapeHtml(s) {
  return String(s)
    .replaceAll("&", "&amp;")
    .replaceAll("<", "&lt;")
    .replaceAll(">", "&gt;")
    .replaceAll('"', "&quot;")
    .replaceAll("'", "&#039;");
}

function clearDownloads(containerEl) {
  if (containerEl) containerEl.innerHTML = "";
}

function addDownloadButton(containerEl, fileId, label) {
  if (!containerEl) return;

  const safe = escapeHtml(label || "Download result");
  const url = `${API_BASE}/api/files/${fileId}/download`;

  const a = document.createElement("a");
  a.href = url;
  a.className =
    "block w-full text-center rounded-xl border border-zinc-700 " +
    "bg-zinc-900/40 py-3 hover:bg-zinc-900 text-zinc-200";
  a.textContent = safe;

  containerEl.appendChild(a);
}

function parsePagesSpec(spec) {
  const s = (spec || "").replace(/\s+/g, "");
  if (!s) return [];

  const out = new Set();

  for (const part of s.split(",")) {
    const m = part.match(/^(\d+)(?:-(\d+))?$/);
    if (!m) throw new Error(`Invalid pages format: ${part}`);

    const a = Number(m[1]);
    const b = m[2] ? Number(m[2]) : a;

    if (a < 1 || b < 1) throw new Error("Pages must be >= 1");

    for (let i = Math.min(a, b); i <= Math.max(a, b); i++) out.add(i);
  }

  return Array.from(out).sort((x, y) => x - y);
}

function fileKey(f) {
  return `${f.name}::${f.size}::${f.lastModified}`;
}

function renderFileChips(containerEl, files, onRemove) {
  if (!containerEl) return;

  if (!files || files.length === 0) {
    containerEl.innerHTML = "";
    return;
  }

  const chips = files.map((f, idx) => {
    const name = escapeHtml(f.name);
    return `
      <div class="flex items-center gap-2 rounded-xl border border-zinc-700 bg-zinc-900 px-3 py-2 text-sm text-zinc-200">
        <span class="max-w-[220px] truncate" title="${name}">${name}</span>
        <button type="button"
                data-idx="${idx}"
                class="ml-1 inline-flex h-6 w-6 items-center justify-center rounded-lg hover:bg-zinc-800 text-zinc-300">
          ✕
        </button>
      </div>
    `;
  }).join("");

  containerEl.innerHTML = `<div class="flex flex-wrap gap-2">${chips}</div>`;

  containerEl.querySelectorAll("button[data-idx]").forEach(btn => {
    btn.addEventListener("click", () => {
      const idx = Number(btn.getAttribute("data-idx"));
      onRemove?.(idx);
    });
  });
}

/* ===================== MERGE ===================== */
const mergeInput = document.getElementById("mergeFiles");
const mergeBtn = document.getElementById("mergeBtn");
const mergeStatus = document.getElementById("mergeStatus");
const mergeDownloads = document.getElementById("mergeDownloads");
const mergeFilesList = document.getElementById("mergeFilesList");

let mergeFileMap = new Map(); // key -> File

function syncMergeFiles() {
  const files = Array.from(mergeFileMap.values());
  renderFileChips(mergeFilesList, files, (idx) => {
    const f = files[idx];
    if (!f) return;
    mergeFileMap.delete(fileKey(f));
    syncMergeFiles();
    setStatus(mergeStatus, files.length ? `Selected files: ${files.length}` : "");
  });
  return files;
}

mergeInput?.addEventListener("change", () => {
  clearDownloads(mergeDownloads);

  for (const f of mergeInput.files || []) {
    mergeFileMap.set(fileKey(f), f);
  }
  mergeInput.value = "";

  const files = syncMergeFiles();
  setStatus(mergeStatus, `Selected files: ${files.length}`);
});

mergeBtn?.addEventListener("click", async () => {
  clearDownloads(mergeDownloads);
  try {
    const files = syncMergeFiles();
    if (files.length < 2) {
      setStatus(mergeStatus, "Select at least 2 PDF files.");
      return;
    }

    setStatus(mergeStatus, "Creating job...");
    const job = await createJob("PDF_MERGE", null);

    setStatus(mergeStatus, "Uploading files...");
    for (const f of files) await uploadFile(job.id, f);

    setStatus(mergeStatus, "Starting job...");
    await startJob(job.id);

    setStatus(mergeStatus, "Processing...");
    await pollUntilDone(job.id, j => setStatus(mergeStatus, `Status: ${j.status}`));

    setStatus(mergeStatus, "Done.");

    const jobFiles = await listFiles(job.id);
    const outputs = jobFiles.filter(x => x.role === "OUTPUT")
      .sort((a, b) => String(a.originalName).localeCompare(String(b.originalName)));

    if (outputs.length === 0) throw new Error("OUTPUT file not found");

    addDownloadButton(mergeDownloads, outputs[0].id, "Download merged.pdf");

    mergeFileMap.clear();
    syncMergeFiles();
  } catch (e) {
    setStatus(mergeStatus, `Error: ${e.message}`);
  }
});

/* ===================== SPLIT ===================== */
const splitFile = document.getElementById("splitFile");
const splitSpec = document.getElementById("splitSpec");
const splitBtn = document.getElementById("splitBtn");
const splitStatus = document.getElementById("splitStatus");
const splitDownloads = document.getElementById("splitDownloads");
const splitFileChip = document.getElementById("splitFileChip");

let splitSelectedFile = null;

function renderSplitChip() {
  if (!splitFileChip) return;
  if (!splitSelectedFile) {
    splitFileChip.innerHTML = "";
    return;
  }
  renderFileChips(splitFileChip, [splitSelectedFile], () => {
    splitSelectedFile = null;
    if (splitFile) splitFile.value = "";
    renderSplitChip();
  });
}

splitFile?.addEventListener("change", () => {
  clearDownloads(splitDownloads);
  splitSelectedFile = splitFile.files?.[0] || null;
  renderSplitChip();
});

splitBtn?.addEventListener("click", async () => {
  clearDownloads(splitDownloads);
  try {
    const file = splitSelectedFile;
    const splitAt = Number(splitSpec.value);

    if (!file) {
      setStatus(splitStatus, "Select a PDF file.");
      return;
    }
    if (!Number.isInteger(splitAt) || splitAt < 1) {
      setStatus(splitStatus, "splitAt must be an integer >= 1.");
      return;
    }

    setStatus(splitStatus, "Creating job...");
    const job = await createJob("PDF_SPLIT", { splitAt });

    setStatus(splitStatus, "Uploading file...");
    await uploadFile(job.id, file);

    setStatus(splitStatus, "Starting job...");
    await startJob(job.id);

    setStatus(splitStatus, "Processing...");
    await pollUntilDone(job.id, j => setStatus(splitStatus, `Status: ${j.status}`));

    setStatus(splitStatus, "Done.");

    const jobFiles = await listFiles(job.id);
    const outputs = jobFiles
      .filter(x => x.role === "OUTPUT")
      .sort((a, b) => String(a.originalName).localeCompare(String(b.originalName)));

    if (outputs.length === 0) throw new Error("OUTPUT files not found");

    // two buttons (or more, if backend changes)
    outputs.forEach((f, i) => {
      const label = f.originalName ? `Download ${f.originalName}` : `Download part ${i + 1}`;
      addDownloadButton(splitDownloads, f.id, label);
    });

  } catch (e) {
    setStatus(splitStatus, `Error: ${e.message}`);
  }
});

/* ===================== DELETE ===================== */
const delFile = document.getElementById("delFile");
const delSpec = document.getElementById("delSpec");
const delBtn = document.getElementById("delBtn");
const delStatus = document.getElementById("delStatus");
const delDownloads = document.getElementById("delDownloads");
const delFileChip = document.getElementById("delFileChip");

let delSelectedFile = null;

function renderDelChip() {
  if (!delFileChip) return;
  if (!delSelectedFile) {
    delFileChip.innerHTML = "";
    return;
  }
  renderFileChips(delFileChip, [delSelectedFile], () => {
    delSelectedFile = null;
    if (delFile) delFile.value = "";
    renderDelChip();
  });
}

delFile?.addEventListener("change", () => {
  clearDownloads(delDownloads);
  delSelectedFile = delFile.files?.[0] || null;
  renderDelChip();
});

delBtn?.addEventListener("click", async () => {
  clearDownloads(delDownloads);
  try {
    const file = delSelectedFile;
    if (!file) {
      setStatus(delStatus, "Select a PDF file.");
      return;
    }

    const pages = parsePagesSpec(delSpec.value);
    if (pages.length === 0) {
      setStatus(delStatus, "Specify pages to delete (e.g. 1,3,5-7).");
      return;
    }

    setStatus(delStatus, "Creating job...");
    const job = await createJob("PDF_DELETE_PAGES", { pages });

    setStatus(delStatus, "Uploading file...");
    await uploadFile(job.id, file);

    setStatus(delStatus, "Starting job...");
    await startJob(job.id);

    setStatus(delStatus, "Processing...");
    await pollUntilDone(job.id, j => setStatus(delStatus, `Status: ${j.status}`));

    setStatus(delStatus, "Done.");

    const jobFiles = await listFiles(job.id);
    const outputs = jobFiles
      .filter(x => x.role === "OUTPUT")
      .sort((a, b) => String(a.originalName).localeCompare(String(b.originalName)));

    if (outputs.length === 0) throw new Error("OUTPUT file not found");

    addDownloadButton(delDownloads, outputs[0].id, "Download deleted-pages.pdf");
  } catch (e) {
    setStatus(delStatus, `Error: ${e.message}`);
  }
});
