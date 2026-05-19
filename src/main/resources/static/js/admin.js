(function () {
  "use strict";

  const themeCreateForm = document.getElementById("theme-create-form");
  const themeName = document.getElementById("theme-name");
  const themeDesc = document.getElementById("theme-desc");
  const themeImage = document.getElementById("theme-image");
  const themeCreateMsg = document.getElementById("theme-create-msg");
  const themeDeleteSelect = document.getElementById("theme-delete-select");
  const themeDeleteBtn = document.getElementById("theme-delete-btn");
  const themeDeleteMsg = document.getElementById("theme-delete-msg");

  const timeCreateForm = document.getElementById("time-create-form");
  const timeStart = document.getElementById("time-start");
  const timeCreateMsg = document.getElementById("time-create-msg");
  const timeDeleteSelect = document.getElementById("time-delete-select");
  const timeDeleteBtn = document.getElementById("time-delete-btn");
  const timeDeleteMsg = document.getElementById("time-delete-msg");

  const reservationsBody = document.getElementById("reservations-body");
  const refreshReservations = document.getElementById("refresh-reservations");
  const reservationsMsg = document.getElementById("reservations-msg");

  // --- 관리자 인증 흐름 ---
  const navLogin = document.getElementById("nav-login");
  const navLogout = document.getElementById("nav-logout");
  const loginModal = document.getElementById("login-modal");
  const loginForm = document.getElementById("login-form");
  const loginCancel = document.getElementById("login-cancel");
  const loginMsg = document.getElementById("login-msg");

  function updateAuthUI() {
    const hasToken = !!localStorage.getItem("token");
    if (navLogin) navLogin.classList.toggle("is-hidden", hasToken);
    if (navLogout) navLogout.classList.toggle("is-hidden", !hasToken);
  }

  if (navLogin) {
    navLogin.addEventListener("click", (e) => {
      e.preventDefault();
      loginModal.classList.remove("is-hidden");
    });
  }

  if (loginCancel) {
    loginCancel.addEventListener("click", () => {
      loginModal.classList.add("is-hidden");
      loginMsg.textContent = "";
    });
  }

  if (navLogout) {
    navLogout.addEventListener("click", async (e) => {
      e.preventDefault();
      try { await fetch("/logout", { method: "POST" }); } catch (err) {}
      localStorage.removeItem("token");
      updateAuthUI();
      reservationsBody.innerHTML = '<tr><td colspan="5">예약 목록을 보려면 로그인하세요.</td></tr>';
      alert("로그아웃 되었습니다.");
    });
  }

  if (loginForm) {
    loginForm.addEventListener("submit", async (e) => {
      e.preventDefault();
      loginMsg.textContent = "";
      loginMsg.classList.remove("message--err");
      const id = document.getElementById("login-id").value;
      const pw = document.getElementById("login-pw").value;

      try {
        const res = await fetch("/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({ loginId: id, password: pw }),
        });
        if (!res.ok) throw new Error("로그인 실패");
        const data = await res.json();
        localStorage.setItem("token", data.accessToken || data.token);
        loginModal.classList.add("is-hidden");
        updateAuthUI();
        loadReservations(); // 로그인 완료 후 목록 재조회
      } catch (err) {
        loginMsg.textContent = "로그인 정보가 올바르지 않습니다.";
        loginMsg.classList.add("message--err");
      }
    });
  }
  updateAuthUI();

  // --- 유틸리티 함수 ---
  function setMsg(el, text, ok) {
    if (!el) return;
    el.textContent = text;
    el.classList.remove("message--ok", "message--err");
    if (text) el.classList.add(ok ? "message--ok" : "message--err");
  }

  async function fetchJson(url, options = {}) {
    const headers = options.headers || {};
    const token = localStorage.getItem("token");
    if (token) headers["Authorization"] = `Bearer ${token}`;

    const res = await fetch(url, { ...options, headers });
    if (!res.ok) {
      if (res.status === 401) throw new Error("UNAUTHORIZED");
      const t = await res.text();
      throw new Error(t || res.statusText);
    }
    if (res.status === 204) return null;
    const ct = res.headers.get("content-type") || "";
    if (ct.includes("application/json")) return res.json();
    return null;
  }

  function formatTime(t) {
    if (!t) return "—";
    const parts = String(t).split(":");
    return `${parts[0]}:${parts[1] || "00"}`;
  }

  // --- 테마 관리 로직 ---
  async function loadThemesIntoDeleteSelect() {
    themeDeleteSelect.innerHTML = "";
    try {
      const apiThemes = await fetchJson(`/themes`);
      const themes = apiThemes || [];
      const sorted = [...themes].sort((a, b) => a.name.localeCompare(b.name, "ko"));

      const empty = document.createElement("option");
      empty.value = "";
      empty.textContent = "테마를 선택하세요";
      themeDeleteSelect.appendChild(empty);

      sorted.forEach((t) => {
        const opt = document.createElement("option");
        opt.value = String(t.id);
        opt.textContent = t.name;
        themeDeleteSelect.appendChild(opt);
      });
    } catch (e) {
      setMsg(themeDeleteMsg, "테마 목록을 불러오지 못했습니다.", false);
    }
  }

  themeCreateForm.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    setMsg(themeCreateMsg, "", true);
    const file = themeImage.files && themeImage.files[0];
    if (!file) {
      setMsg(themeCreateMsg, "이미지를 선택해 주세요.", false);
      return;
    }
    try {
      const formData = new FormData();
      formData.append("name", themeName.value.trim());
      formData.append("description", themeDesc.value.trim());
      formData.append("file", file);

      // FormData 전송 시에도 토큰 필요 (Admin 컨트롤러는 설정에 따라 다를 수 있으나 범용 적용)
      const headers = {};
      const token = localStorage.getItem("token");
      if (token) headers["Authorization"] = `Bearer ${token}`;

      const res = await fetch("/admin/themes", { method: "POST", body: formData, headers });
      if (!res.ok) {
        if (res.status === 401) throw new Error("UNAUTHORIZED");
        throw new Error(await res.text() || "등록 실패");
      }

      setMsg(themeCreateMsg, "테마가 등록되었습니다.", true);
      themeCreateForm.reset();
      await loadThemesIntoDeleteSelect();
    } catch (e) {
      if (e.message === "UNAUTHORIZED") {
        setMsg(themeCreateMsg, "권한이 만료되었습니다. 다시 로그인 해주세요.", false);
        loginModal.classList.remove("is-hidden");
      } else {
        setMsg(themeCreateMsg, e.message, false);
      }
    }
  });

  themeDeleteBtn.addEventListener("click", async () => {
    const id = themeDeleteSelect.value;
    if (!id || !confirm("삭제하시겠습니까?")) return;
    try {
      await fetchJson(`/admin/themes/${id}`, { method: "DELETE" });
      setMsg(themeDeleteMsg, "삭제되었습니다.", true);
      await loadThemesIntoDeleteSelect();
    } catch (e) {
      setMsg(themeDeleteMsg, "삭제 실패", false);
    }
  });

  // --- 시간 및 예약 관리 로직 ---
  async function loadTimesIntoDeleteSelect() {
    if (!timeDeleteSelect) return;
    timeDeleteSelect.innerHTML = "";
    try {
      const times = await fetchJson("/times");
      const sorted = [...times].sort((a, b) => a.startAt.localeCompare(b.startAt));

      const empty = document.createElement("option");
      empty.value = "";
      empty.textContent = "시간을 선택하세요";
      timeDeleteSelect.appendChild(empty);

      sorted.forEach((t) => {
        const opt = document.createElement("option");
        opt.value = String(t.id);
        opt.textContent = formatTime(t.startAt);
        timeDeleteSelect.appendChild(opt);
      });
    } catch (e) {
      setMsg(timeDeleteMsg, "시간 목록 로드 실패", false);
    }
  }

  timeCreateForm.addEventListener("submit", async (ev) => {
    ev.preventDefault();
    setMsg(timeCreateMsg, "", true);
    let startAt = timeStart.value;
    if (startAt.split(":").length === 2) startAt += ":00";
    try {
      await fetchJson("/admin/times", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ startAt }),
      });
      setMsg(timeCreateMsg, "시간이 등록되었습니다.", true);
      timeCreateForm.reset();
      await loadTimesIntoDeleteSelect();
    } catch (e) {
      setMsg(timeCreateMsg, e.message, false);
    }
  });

  timeDeleteBtn.addEventListener("click", async () => {
    const id = timeDeleteSelect.value;
    if (!id || !confirm("삭제하시겠습니까?")) return;
    try {
      await fetchJson(`/admin/times/${id}`, { method: "DELETE" });
      setMsg(timeDeleteMsg, "삭제되었습니다.", true);
      await loadTimesIntoDeleteSelect();
    } catch (e) {
      setMsg(timeDeleteMsg, "삭제 실패", false);
    }
  });

  async function loadReservations() {
    setMsg(reservationsMsg, "", true);
    reservationsBody.innerHTML = "";
    try {
      const list = await fetchJson("/reservations");
      if (!list || !list.length) {
        reservationsBody.innerHTML = '<tr><td colspan="5">예약이 없습니다.</td></tr>';
        return;
      }
      list.forEach((r) => {
        const tr = document.createElement("tr");
        const timeVal = r.time && r.time.startAt ? r.time.startAt : r.time;
        const cells = [r.id, r.name, r.date, formatTime(timeVal), r.theme?.name || "—"];
        cells.forEach(text => {
          const td = document.createElement("td");
          td.textContent = text;
          tr.appendChild(td);
        });
        reservationsBody.appendChild(tr);
      });
    } catch (e) {
      if (e.message === "UNAUTHORIZED") {
        reservationsBody.innerHTML = '<tr><td colspan="5">예약 목록을 보려면 로그인하세요.</td></tr>';
      } else {
        setMsg(reservationsMsg, "로드 실패", false);
      }
    }
  }

  refreshReservations.addEventListener("click", loadReservations);

  loadThemesIntoDeleteSelect();
  loadTimesIntoDeleteSelect();
  loadReservations();
})();
