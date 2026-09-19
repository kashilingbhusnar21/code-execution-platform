import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import CodeEditor, { LANGUAGE_TEMPLATES } from '../components/CodeEditor';
import OutputConsole from '../components/OutputConsole';
import HistoryPanel from '../components/HistoryPanel';
import { api } from '../services/api';

const STATUS_META = {
  SUCCESS: { label: 'Success', tone: 'bg-emerald-500/15 text-emerald-300 border-emerald-500/30' },
  COMPILATION_ERROR: { label: 'Compilation Error', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
  RUNTIME_ERROR: { label: 'Runtime Error', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
  TIMEOUT: { label: 'Timeout', tone: 'bg-amber-500/15 text-amber-300 border-amber-500/30' },
  ERROR: { label: 'Error', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
};

const LANGUAGE_LABELS = {
  java: 'Java',
  python: 'Python',
  cpp: 'C++',
};

const getInitial = (value) => (value ? value.trim().charAt(0).toUpperCase() : 'U');

function EditorPage() {
  const navigate = useNavigate();
  const [code, setCode] = useState(LANGUAGE_TEMPLATES.java);
  const [language, setLanguage] = useState('java');
  const [input, setInput] = useState('');
  const [stdout, setStdout] = useState('');
  const [stderr, setStderr] = useState('');
  const [status, setStatus] = useState(null);
  const [executionTime, setExecutionTime] = useState(null);
  const [isRunning, setIsRunning] = useState(false);
  const [user, setUser] = useState(() => api.getUser());
  const [showUserDropdown, setShowUserDropdown] = useState(false);
  const historyRef = useRef(null);
  const userMenuRef = useRef(null);

  useEffect(() => {
    const handlePointerDown = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setShowUserDropdown(false);
      }
    };

    document.addEventListener('mousedown', handlePointerDown);
    return () => document.removeEventListener('mousedown', handlePointerDown);
  }, []);

  const detectStatus = (text) => {
    if (!text) return 'SUCCESS';
    if (text.includes('Compilation Error')) return 'COMPILATION_ERROR';
    if (text.toLowerCase().includes('timeout')) return 'TIMEOUT';
    if (text.includes('Exception') || text.includes('Error:') || text.includes('Traceback')) {
      return 'RUNTIME_ERROR';
    }
    return 'SUCCESS';
  };

  const applyResult = (result) => {
    if (typeof result === 'string') {
      const detected = detectStatus(result);
      setStatus(detected);
      setStdout(detected === 'SUCCESS' ? result : '');
      setStderr(detected === 'SUCCESS' ? '' : result);
      return;
    }

    const nextStatus = result.status || detectStatus(result.output || '');
    const resolvedStdout = result.stdout ?? (nextStatus === 'SUCCESS' ? (result.output || '') : '');
    const resolvedStderr = result.stderr ?? (nextStatus === 'SUCCESS' ? '' : (result.output || ''));

    setStatus(nextStatus);
    setStdout(resolvedStdout);
    setStderr(resolvedStderr);
    setExecutionTime(result.executionTimeMs ?? result.executionTime ?? null);
  };

  const handleRun = async () => {
    setIsRunning(true);
    setStdout('');
    setStderr('');
    setStatus(null);
    setExecutionTime(null);

    try {
      const result = await api.runCode(code, language, input);
      applyResult(result);
      historyRef.current?.refresh?.();
    } catch (error) {
      const data = error.response?.data;
      if (data && typeof data === 'object') {
        applyResult(data);
      } else {
        const message =
          (typeof data === 'string' && data) ||
          error.message ||
          'An error occurred while running the code';
        setStdout('');
        setStderr(message);
        setStatus('ERROR');
        setExecutionTime(0);
      }
    } finally {
      setIsRunning(false);
    }
  };

  const handleLogout = () => {
    api.logout();
    setUser(null);
    setShowUserDropdown(false);
    historyRef.current?.clearHistory?.();
    navigate('/login', { replace: true });
  };

  const handleLoadFromHistory = ({ code: loadedCode, language: loadedLanguage }) => {
    if (loadedLanguage) {
      setLanguage(String(loadedLanguage).toLowerCase());
    }
    if (typeof loadedCode === 'string') {
      setCode(loadedCode);
    }
  };

  const meta = STATUS_META[status] || null;

  return (
    <div className="h-screen overflow-hidden bg-[#0b1120] text-slate-100">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(59,130,246,0.16),transparent_30%),radial-gradient(circle_at_top_right,rgba(16,185,129,0.10),transparent_25%),linear-gradient(to_bottom,rgba(15,23,42,0.92),rgba(2,6,23,1))]" />

      <div className="relative flex h-full flex-col">
        <header className="sticky top-0 z-30 border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-xl">
          <div className="mx-auto flex h-16 w-full max-w-[1600px] items-center justify-between gap-3 px-4 sm:px-6 lg:px-8">
            <div className="flex min-w-0 items-center gap-3">
              <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-sky-500/20 bg-sky-500/10 text-sky-300 shadow-lg shadow-sky-500/10">
                <svg className="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                  <path d="M16 18l6-6-6-6" strokeLinecap="round" strokeLinejoin="round" />
                  <path d="M8 6l-6 6 6 6" strokeLinecap="round" strokeLinejoin="round" />
                  <path d="M14 4L10 20" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              </div>
              <div className="min-w-0">
                <div className="flex items-center gap-2">
                  <h1 className="truncate text-lg font-semibold tracking-tight text-slate-50 sm:text-xl">
                    CodeArena
                  </h1>
                  <span className="rounded-full border border-slate-700 bg-slate-900 px-2 py-0.5 text-[11px] font-medium uppercase tracking-[0.18em] text-slate-300">
                    Dev Tool
                  </span>
                </div>
                <p className="truncate text-xs text-slate-400">
                  Fast execution, clean results, and submission history in one workspace
                </p>
              </div>
            </div>

            <div className="flex items-center gap-3">
              <div className="hidden items-center gap-2 rounded-full border border-slate-800 bg-slate-900/70 px-3 py-1.5 text-xs text-slate-300 md:flex">
                <span className="h-2 w-2 rounded-full bg-emerald-400" />
                {isRunning ? 'Running' : 'Ready'}
              </div>

              <div className="relative" ref={userMenuRef}>
                <button
                  type="button"
                  onClick={() => setShowUserDropdown((value) => !value)}
                  className="flex items-center gap-3 rounded-full border border-slate-800 bg-slate-900/80 px-2.5 py-2 text-left transition-all duration-200 hover:border-slate-700 hover:bg-slate-800/90"
                >
                  <div className="flex h-9 w-9 items-center justify-center rounded-full bg-gradient-to-br from-sky-500 to-cyan-400 text-sm font-semibold text-slate-950">
                    {getInitial(user?.username)}
                  </div>
                  <div className="hidden min-w-0 sm:block">
                    <div className="truncate text-sm font-medium text-slate-100">
                      {user?.username || 'User'}
                    </div>
                    <div className="truncate text-xs text-slate-400">
                      {user?.email || 'Signed in'}
                    </div>
                  </div>
                  <svg
                    className={`h-4 w-4 text-slate-400 transition-transform duration-200 ${showUserDropdown ? 'rotate-180' : ''}`}
                    viewBox="0 0 20 20"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="1.8"
                  >
                    <path d="M5 7l5 6 5-6" strokeLinecap="round" strokeLinejoin="round" />
                  </svg>
                </button>

                {showUserDropdown && (
                  <div className="absolute right-0 mt-2 w-64 overflow-hidden rounded-2xl border border-slate-800 bg-slate-950/95 shadow-2xl shadow-black/40 backdrop-blur-xl">
                    <div className="border-b border-slate-800 px-4 py-4">
                      <div className="text-sm font-medium text-slate-100">{user?.username}</div>
                      <div className="mt-1 text-xs text-slate-400">{user?.email}</div>
                    </div>
                    <button
                      type="button"
                      onClick={handleLogout}
                      className="flex w-full items-center gap-3 px-4 py-3 text-left text-sm text-rose-300 transition-colors duration-200 hover:bg-rose-500/10"
                    >
                      <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                        <path d="M15 3h4a2 2 0 012 2v14a2 2 0 01-2 2h-4" strokeLinecap="round" strokeLinejoin="round" />
                        <path d="M10 17l5-5-5-5" strokeLinecap="round" strokeLinejoin="round" />
                        <path d="M15 12H3" strokeLinecap="round" strokeLinejoin="round" />
                      </svg>
                      Logout
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </header>

        <main className="relative mx-auto flex w-full max-w-[1600px] flex-1 min-h-0 flex-col gap-3 overflow-hidden p-3 sm:p-4 lg:p-5">
          <div className="flex min-h-0 flex-1 flex-col gap-4 lg:flex-row">
            <section className="min-h-0 min-w-0 lg:flex-[2.1]">
              <CodeEditor
                code={code}
                setCode={setCode}
                language={language}
                setLanguage={setLanguage}
                input={input}
                setInput={setInput}
                onRun={handleRun}
                isRunning={isRunning}
                statusMeta={meta}
                languageLabel={LANGUAGE_LABELS[language] || language}
              />
            </section>

            <aside className="flex min-h-0 min-w-0 flex-col gap-3 lg:flex-[1]">
              <div className="min-h-0 flex-[0.9]">
                <OutputConsole
                  stdout={stdout}
                  stderr={stderr}
                  status={status}
                  executionTime={executionTime}
                  isRunning={isRunning}
                />
              </div>
              <div className="min-h-0 flex-1">
                <HistoryPanel ref={historyRef} onLoadCode={handleLoadFromHistory} />
              </div>
            </aside>
          </div>
        </main>
      </div>
    </div>
  );
}

export default EditorPage;
