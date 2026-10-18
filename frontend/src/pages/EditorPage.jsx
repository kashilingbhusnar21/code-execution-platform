import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, Link, useSearchParams, useLocation } from 'react-router-dom';
import CodeEditor, { LANGUAGE_TEMPLATES } from '../components/CodeEditor';
import OutputConsole from '../components/OutputConsole';
import HistoryPanel from '../components/HistoryPanel';
import ProblemStatement from '../components/ProblemStatement';
import { getProblemStarterCode } from '../utils/starterCodeTemplates';
import { api } from '../services/api';

const STATUS_META = {
  ACCEPTED: { label: 'Accepted', tone: 'bg-emerald-500/15 text-emerald-300 border-emerald-500/30' },
  SUCCESS: { label: 'Success', tone: 'bg-emerald-500/15 text-emerald-300 border-emerald-500/30' },
  WRONG_ANSWER: { label: 'Wrong Answer', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
  COMPILE_ERROR: { label: 'Compile Error', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
  COMPILATION_ERROR: { label: 'Compilation Error', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
  RUNTIME_ERROR: { label: 'Runtime Error', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
  TIME_LIMIT_EXCEEDED: { label: 'Time Limit Exceeded', tone: 'bg-amber-500/15 text-amber-300 border-amber-500/30' },
  TIMEOUT: { label: 'Timeout', tone: 'bg-amber-500/15 text-amber-300 border-amber-500/30' },
  ERROR: { label: 'Error', tone: 'bg-rose-500/15 text-rose-300 border-rose-500/30' },
  PENDING: { label: 'Pending', tone: 'bg-sky-500/15 text-sky-300 border-sky-500/30' },
  RUNNING: { label: 'Running', tone: 'bg-sky-500/15 text-sky-300 border-sky-500/30' },
};

const LANGUAGE_LABELS = {
  java: 'Java',
  python: 'Python',
  cpp: 'C++',
};

const getInitial = (value) => (value ? value.trim().charAt(0).toUpperCase() : 'U');

function EditorPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const problemId = searchParams.get('problemId');
  const isProblemsActive = location.pathname.startsWith('/problems');

  const [language, setLanguage] = useState('java');
  const [code, setCode] = useState(LANGUAGE_TEMPLATES.java);
  const [input, setInput] = useState('');
  const [stdout, setStdout] = useState('');
  const [stderr, setStderr] = useState('');
  const [status, setStatus] = useState(null);
  const [executionTime, setExecutionTime] = useState(null);
  const [isRunning, setIsRunning] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState(null);
  const [selectedTestCaseIndex, setSelectedTestCaseIndex] = useState(0);
  const [user, setUser] = useState(() => api.getUser());
  const [streak, setStreak] = useState(0);
  const [showUserDropdown, setShowUserDropdown] = useState(false);

  // Problem-solving mode state
  const [problem, setProblem] = useState(null);
  const [isLoadingProblem, setIsLoadingProblem] = useState(Boolean(problemId));
  const [problemError, setProblemError] = useState(null);
  const [userCodeByLanguage, setUserCodeByLanguage] = useState({});
  const [activeConsoleTab, setActiveConsoleTab] = useState('output');
  const [isConsoleCollapsed, setIsConsoleCollapsed] = useState(false);

  // Responsive split-pane state (40% problem statement, 60% editor)
  const [splitPercent, setSplitPercent] = useState(40);
  const [isDragging, setIsDragging] = useState(false);
  const [isLgScreen, setIsLgScreen] = useState(() =>
    typeof window !== 'undefined' ? window.innerWidth >= 1024 : true
  );

  // Vertically resizable editor and results panel state (60% editor, 40% console drawer)
  const [verticalSplitPercent, setVerticalSplitPercent] = useState(60);
  const [isVerticalDragging, setIsVerticalDragging] = useState(false);

  const historyRef = useRef(null);
  const userMenuRef = useRef(null);
  const splitContainerRef = useRef(null);
  const rightPaneRef = useRef(null);

  // Window resize listener for responsive layout
  useEffect(() => {
    const handleResize = () => {
      setIsLgScreen(window.innerWidth >= 1024);
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  // Draggable vertical divider mouse/touch handlers (horizontal resizing between Problem and Editor)
  const handleMouseDown = (e) => {
    e.preventDefault();
    setIsDragging(true);
  };

  const handleTouchStart = () => {
    setIsDragging(true);
  };

  useEffect(() => {
    if (!isDragging) return;

    const originalCursor = document.body.style.cursor;
    const originalUserSelect = document.body.style.userSelect;
    document.body.style.cursor = 'col-resize';
    document.body.style.userSelect = 'none';

    const handleMouseMove = (e) => {
      if (!splitContainerRef.current) return;
      const rect = splitContainerRef.current.getBoundingClientRect();
      const offsetX = e.clientX - rect.left;
      const totalWidth = rect.width;
      if (totalWidth <= 0) return;
      const newPercent = (offsetX / totalWidth) * 100;
      const clamped = Math.min(65, Math.max(25, newPercent));
      setSplitPercent(clamped);
    };

    const handleTouchMove = (e) => {
      if (!splitContainerRef.current || !e.touches || e.touches.length === 0) return;
      const touch = e.touches[0];
      const rect = splitContainerRef.current.getBoundingClientRect();
      const offsetX = touch.clientX - rect.left;
      const totalWidth = rect.width;
      if (totalWidth <= 0) return;
      const newPercent = (offsetX / totalWidth) * 100;
      const clamped = Math.min(65, Math.max(25, newPercent));
      setSplitPercent(clamped);
    };

    const handleMouseUp = () => {
      setIsDragging(false);
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
    document.addEventListener('touchmove', handleTouchMove, { passive: true });
    document.addEventListener('touchend', handleMouseUp);
    return () => {
      document.body.style.cursor = originalCursor;
      document.body.style.userSelect = originalUserSelect;
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
      document.removeEventListener('touchmove', handleTouchMove);
      document.removeEventListener('touchend', handleMouseUp);
    };
  }, [isDragging]);

  // Draggable horizontal divider mouse/touch handlers (vertical resizing between Editor and Console)
  const handleVerticalMouseDown = (e) => {
    e.preventDefault();
    if (isConsoleCollapsed) {
      setIsConsoleCollapsed(false);
    }
    setIsVerticalDragging(true);
  };

  const handleVerticalTouchStart = () => {
    if (isConsoleCollapsed) {
      setIsConsoleCollapsed(false);
    }
    setIsVerticalDragging(true);
  };

  useEffect(() => {
    if (!isVerticalDragging) return;

    const originalCursor = document.body.style.cursor;
    const originalUserSelect = document.body.style.userSelect;
    document.body.style.cursor = 'ns-resize';
    document.body.style.userSelect = 'none';

    const handleMouseMove = (e) => {
      if (!rightPaneRef.current) return;
      const rect = rightPaneRef.current.getBoundingClientRect();
      const totalHeight = rect.height;
      if (totalHeight <= 0) return;
      const offsetY = e.clientY - rect.top;
      const newPercent = (offsetY / totalHeight) * 100;
      // Sensible min-height bounds: between 22% and 78%
      const clamped = Math.min(78, Math.max(22, newPercent));
      setVerticalSplitPercent(clamped);
    };

    const handleTouchMove = (e) => {
      if (!rightPaneRef.current || !e.touches || e.touches.length === 0) return;
      const touch = e.touches[0];
      const rect = rightPaneRef.current.getBoundingClientRect();
      const totalHeight = rect.height;
      if (totalHeight <= 0) return;
      const offsetY = touch.clientY - rect.top;
      const newPercent = (offsetY / totalHeight) * 100;
      const clamped = Math.min(78, Math.max(22, newPercent));
      setVerticalSplitPercent(clamped);
    };

    const handleMouseUp = () => {
      setIsVerticalDragging(false);
    };

    document.addEventListener('mousemove', handleMouseMove);
    document.addEventListener('mouseup', handleMouseUp);
    document.addEventListener('touchmove', handleTouchMove, { passive: true });
    document.addEventListener('touchend', handleMouseUp);
    return () => {
      document.body.style.cursor = originalCursor;
      document.body.style.userSelect = originalUserSelect;
      document.removeEventListener('mousemove', handleMouseMove);
      document.removeEventListener('mouseup', handleMouseUp);
      document.removeEventListener('touchmove', handleTouchMove);
      document.removeEventListener('touchend', handleMouseUp);
    };
  }, [isVerticalDragging, isConsoleCollapsed]);

  // Close user menu on outside click
  useEffect(() => {
    const handlePointerDown = (event) => {
      if (userMenuRef.current && !userMenuRef.current.contains(event.target)) {
        setShowUserDropdown(false);
      }
    };

    document.addEventListener('mousedown', handlePointerDown);
    return () => document.removeEventListener('mousedown', handlePointerDown);
  }, []);

  // Fetch streak / user info
  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const userInfo = await api.getUserInfo();
        setStreak(userInfo.streakCount || 0);
      } catch (error) {
        console.error('Failed to fetch user info:', error);
      }
    };
    fetchUserInfo();
  }, []);

  // Fetch problem when problemId changes
  useEffect(() => {
    if (!problemId) {
      setProblem(null);
      setProblemError(null);
      setIsLoadingProblem(false);
      setUserCodeByLanguage({});
      setCode(LANGUAGE_TEMPLATES[language] || '');
      return;
    }

    let isMounted = true;
    const fetchProblemData = async () => {
      try {
        setIsLoadingProblem(true);
        setProblemError(null);
        const data = await api.getProblemById(problemId);
        if (!isMounted) return;

        setProblem(data);
        setUserCodeByLanguage({});

        // Load starter code for current language
        const starter = getProblemStarterCode(data, language);
        setCode(starter);

        // Pre-fill input with first sample test case if available
        if (data.sampleTestCases && data.sampleTestCases.length > 0) {
          const firstSample = data.sampleTestCases[0];
          if (firstSample && firstSample.input) {
            setInput(firstSample.input);
          }
        }
      } catch (err) {
        if (!isMounted) return;
        console.error('Error fetching problem:', err);
        const errorMsg =
          err.response?.data?.message ||
          (typeof err.response?.data === 'string' && err.response.data) ||
          err.message ||
          'Failed to load problem';
        setProblemError(errorMsg);
        setProblem(null);
      } finally {
        if (isMounted) {
          setIsLoadingProblem(false);
        }
      }
    };

    fetchProblemData();

    return () => {
      isMounted = false;
    };
  }, [problemId]);

  const handleLanguageChange = (nextLang) => {
    if (nextLang === language) return;

    // Cache current code under current language
    const updatedCache = {
      ...userCodeByLanguage,
      [language]: code,
    };
    setUserCodeByLanguage(updatedCache);

    // Retrieve saved code or problem starter code or default template
    let nextCode = updatedCache[nextLang];
    if (nextCode === undefined || nextCode === null) {
      if (problem) {
        nextCode = getProblemStarterCode(problem, nextLang);
      } else {
        nextCode = LANGUAGE_TEMPLATES[nextLang] || '';
      }
      updatedCache[nextLang] = nextCode;
      setUserCodeByLanguage(updatedCache);
    }

    setLanguage(nextLang);
    setCode(nextCode);
  };

  const handleReset = () => {
    let resetCode = '';
    if (problem) {
      resetCode = getProblemStarterCode(problem, language);
    } else {
      resetCode = LANGUAGE_TEMPLATES[language] || '';
    }
    setCode(resetCode);
    setUserCodeByLanguage((prev) => ({
      ...prev,
      [language]: resetCode,
    }));
  };

  const handleLoadFromHistory = ({ code: loadedCode, language: loadedLanguage }) => {
    const nextLang = loadedLanguage ? String(loadedLanguage).toLowerCase() : language;
    if (loadedLanguage) {
      setLanguage(nextLang);
    }
    if (typeof loadedCode === 'string') {
      setCode(loadedCode);
      setUserCodeByLanguage((prev) => ({
        ...prev,
        [nextLang]: loadedCode,
      }));
    }
  };

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
    setStatus('PENDING');
    setExecutionTime(null);
    setIsConsoleCollapsed(false);
    setActiveConsoleTab('output'); // Switch to output tab when running

    try {
      const result = await api.runCode(code, language, input, problemId);

      // If backend returns submissionId, poll for status
      if (result.submissionId) {
        const pollInterval = setInterval(async () => {
          try {
            const statusResult = await api.getSubmissionStatus(result.submissionId);

            if (statusResult.status !== 'PENDING') {
              clearInterval(pollInterval);
              applyResult(statusResult);
              historyRef.current?.refresh?.();
              // Refresh user info to get updated streak
              const userInfo = await api.getUserInfo();
              setStreak(userInfo.streakCount || 0);
            }
          } catch (error) {
            clearInterval(pollInterval);
            const message = error.response?.data?.message || error.message || 'Error checking status';
            setStdout('');
            setStderr(message);
            setStatus('ERROR');
            setExecutionTime(0);
          }
        }, 2000); // Poll every 2 seconds

        // Set a timeout to stop polling after 60 seconds
        setTimeout(() => {
          clearInterval(pollInterval);
          if (status === 'PENDING') {
            setStderr('Execution timed out');
            setStatus('TIMEOUT');
            setExecutionTime(0);
          }
        }, 60000);
      } else {
        // Fallback for synchronous execution
        applyResult(result);
        historyRef.current?.refresh?.();
      }
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

  const handleSubmit = async () => {
    if (!problemId) return;

    setIsSubmitting(true);
    setSubmissionResult(null);
    setSelectedTestCaseIndex(0);
    setIsConsoleCollapsed(false);
    setActiveConsoleTab('evaluation');

    try {
      const result = await api.submitCode(code, language, problemId);

      // If backend returns submissionId, poll for status
      if (result.submissionId) {
        const pollInterval = setInterval(async () => {
          try {
            const statusResult = await api.getSubmissionStatus(result.submissionId);

            if (statusResult.status !== 'PENDING' && statusResult.status !== 'RUNNING') {
              clearInterval(pollInterval);
              setSubmissionResult(statusResult);
              setIsSubmitting(false);

              // Auto-select first failing test case if any
              if (statusResult.testCaseResults && statusResult.testCaseResults.length > 0) {
                const firstFailed = statusResult.testCaseResults.findIndex(
                  (tc) => tc.status !== 'PASSED'
                );
                setSelectedTestCaseIndex(firstFailed >= 0 ? firstFailed : 0);
              }

              historyRef.current?.refresh?.();

              // Refresh user info if accepted to update streak
              if (statusResult.status === 'ACCEPTED') {
                try {
                  const userInfo = await api.getUserInfo();
                  setStreak(userInfo.streakCount || 0);
                } catch (e) {
                  console.error('Failed to update streak after submission:', e);
                }
              }
            }
          } catch (error) {
            clearInterval(pollInterval);
            setIsSubmitting(false);
            const message = error.response?.data?.message || error.message || 'Error checking submission status';
            setSubmissionResult({
              status: 'ERROR',
              errorDetails: message,
            });
          }
        }, 1500);

        // Timeout polling after 60 seconds
        setTimeout(() => {
          clearInterval(pollInterval);
          setIsSubmitting((current) => {
            if (current) {
              setSubmissionResult({
                status: 'TIME_LIMIT_EXCEEDED',
                errorDetails: 'Evaluation timed out after 60 seconds.',
              });
              return false;
            }
            return false;
          });
        }, 60000);
      } else {
        // Fallback for synchronous execution
        setSubmissionResult(result);
        setIsSubmitting(false);
        if (result.testCaseResults && result.testCaseResults.length > 0) {
          const firstFailed = result.testCaseResults.findIndex((tc) => tc.status !== 'PASSED');
          setSelectedTestCaseIndex(firstFailed >= 0 ? firstFailed : 0);
        }
        historyRef.current?.refresh?.();
        if (result.status === 'ACCEPTED') {
          try {
            const userInfo = await api.getUserInfo();
            setStreak(userInfo.streakCount || 0);
          } catch (e) {
            console.error('Failed to update streak:', e);
          }
        }
      }
    } catch (error) {
      setIsSubmitting(false);
      const data = error.response?.data;
      const message =
        (typeof data === 'object' && (data.message || data.errorDetails)) ||
        (typeof data === 'string' && data) ||
        error.message ||
        'An error occurred while submitting the code';
      setSubmissionResult({
        status: 'ERROR',
        errorDetails: message,
      });
    }
  };

  const handleLogout = () => {
    api.logout();
    setUser(null);
    setShowUserDropdown(false);
    historyRef.current?.clearHistory?.();
    navigate('/login', { replace: true });
  };

  const meta = STATUS_META[status] || null;
  const isProblemMode = Boolean(problemId);

  return (
    <div className="h-screen overflow-hidden bg-[#0b1120] text-slate-100">
      <div className="absolute inset-0 bg-[radial-gradient(circle_at_top_left,rgba(59,130,246,0.16),transparent_30%),radial-gradient(circle_at_top_right,rgba(16,185,129,0.10),transparent_25%),linear-gradient(to_bottom,rgba(15,23,42,0.92),rgba(2,6,23,1))]" />

      <div className={`relative flex h-full flex-col ${(isDragging || isVerticalDragging) ? 'select-none' : ''}`}>
        <header className="sticky top-0 z-30 border-b border-slate-800/80 bg-slate-950/80 backdrop-blur-xl">
          <div className="mx-auto flex h-16 w-full max-w-[1600px] items-center justify-between gap-3 px-4 sm:px-6 lg:px-8">
            <div className="flex min-w-0 items-center gap-2 sm:gap-4">
              <Link to="/problems" className="flex items-center gap-3 group">
                <div className="flex h-10 w-10 items-center justify-center rounded-xl border border-sky-500/20 bg-sky-500/10 text-sky-300 shadow-lg shadow-sky-500/10 transition-transform group-hover:scale-105">
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
                      {isProblemMode ? 'Problem Solver' : 'Dev Tool'}
                    </span>
                  </div>
                  <p className="truncate text-xs font-medium text-cyan-400/90 tracking-wide">
                    Built by Kashiling Bhusnar
                  </p>
                </div>
              </Link>

              {/* Primary Navigation: DSA Problems */}
              <nav className="flex items-center gap-1.5 border-l border-slate-800 pl-2 sm:pl-3 ml-1 sm:ml-2">
                <Link
                  to="/problems"
                  className={`group flex items-center gap-1.5 sm:gap-2 rounded-xl border px-2.5 sm:px-3 py-1.5 text-xs sm:text-sm font-medium transition-all duration-200 focus:outline-none focus-visible:ring-2 focus-visible:ring-cyan-400 focus-visible:ring-offset-2 focus-visible:ring-offset-slate-950 ${
                    isProblemsActive
                      ? 'border-cyan-400/70 bg-cyan-500/25 text-white shadow-md shadow-cyan-500/20 ring-1 ring-cyan-400/30'
                      : 'border-cyan-500/30 bg-cyan-500/10 text-cyan-100/90 shadow-sm shadow-cyan-500/5 hover:border-cyan-400/50 hover:bg-cyan-500/20 hover:text-white hover:shadow-cyan-500/15'
                  }`}
                  title="Browse DSA Problems"
                >
                  <svg
                    className={`h-4 w-4 shrink-0 transition-colors duration-200 ${
                      isProblemsActive ? 'text-cyan-300' : 'text-cyan-400 group-hover:text-cyan-200'
                    }`}
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  >
                    <polyline points="16 18 22 12 16 6" />
                    <polyline points="8 6 2 12 8 18" />
                  </svg>
                  <span className="font-semibold tracking-tight">DSA Problems</span>
                </Link>
                {isProblemMode && (
                  <Link
                    to="/editor"
                    className="hidden md:flex items-center gap-1.5 rounded-xl border border-slate-800/60 bg-slate-900/40 px-2.5 py-1.5 text-xs font-medium text-slate-400 transition-all duration-200 hover:border-slate-700 hover:bg-slate-800/60 hover:text-slate-200"
                    title="Open Standalone Dev Tool Workspace"
                  >
                    <span>Dev Tool</span>
                  </Link>
                )}
              </nav>
            </div>

            <div className="flex items-center gap-3">
              <div className="flex items-center gap-1 rounded-full border border-slate-800 bg-slate-900/70 px-1.5 py-1.5">
                {['java', 'python', 'cpp'].map((lang) => (
                  <button
                    key={lang}
                    type="button"
                    onClick={() => handleLanguageChange(lang)}
                    disabled={isRunning}
                    className={`relative px-4 py-1.5 text-sm font-medium transition-all duration-200 rounded-lg ${
                      language === lang
                        ? 'bg-gradient-to-r from-sky-500 to-cyan-400 text-slate-950 shadow-lg shadow-sky-500/20'
                        : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'
                    } disabled:cursor-not-allowed disabled:opacity-50`}
                  >
                    {LANGUAGE_LABELS[lang] || lang}
                  </button>
                ))}
              </div>

              <div className="hidden items-center gap-2 rounded-full border border-orange-500/30 bg-orange-500/10 px-3 py-1.5 text-xs font-semibold text-orange-300 md:flex">
                <span className="text-lg">🔥</span>
                <span>{streak} day streak</span>
              </div>

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
          {isProblemMode ? (
            /* Split-pane layout with draggable divider when problemId is present */
            <div
              ref={splitContainerRef}
              className={`flex min-h-0 flex-1 flex-col gap-3 lg:flex-row lg:gap-0 ${
                isDragging ? 'select-none' : ''
              }`}
            >
              {/* Left Pane: Problem Statement (~40% on wide screens, scrollable) */}
              <section
                className="flex min-h-0 min-w-0 flex-col overflow-hidden"
                style={{
                  width: isLgScreen ? `${splitPercent}%` : '100%',
                  flexBasis: isLgScreen ? `${splitPercent}%` : 'auto',
                }}
              >
                <ProblemStatement
                  problem={problem}
                  isLoading={isLoadingProblem}
                  error={problemError}
                  onUseInput={(val) => {
                    setInput(val);
                    setActiveConsoleTab('input');
                    setIsConsoleCollapsed(false);
                  }}
                />
              </section>

              {/* Draggable Vertical Divider between Problem and Editor */}
              <div
                onMouseDown={handleMouseDown}
                className={`hidden lg:flex w-3 shrink-0 cursor-col-resize items-center justify-center select-none group z-10 transition-colors ${
                  isDragging ? 'bg-sky-500/20' : 'hover:bg-sky-500/10'
                }`}
                title="Drag to resize panels"
              >
                <div
                  className={`h-12 w-1 rounded-full transition-colors ${
                    isDragging ? 'bg-sky-400' : 'bg-slate-700 group-hover:bg-sky-400'
                  }`}
                />
              </div>

              {/* Right Pane: Code Editor + Bottom Console (~60% on wide screens) */}
              <section
                ref={rightPaneRef}
                className={`flex min-h-0 min-w-0 flex-1 flex-col gap-0 overflow-hidden ${
                  isVerticalDragging ? 'select-none' : ''
                }`}
                style={{
                  width: isLgScreen ? `${100 - splitPercent}%` : '100%',
                  flexBasis: isLgScreen ? `${100 - splitPercent}%` : 'auto',
                }}
              >
                {/* Monaco Editor: occupies the dynamic vertical height */}
                <div
                  className={`overflow-hidden ${
                    (isDragging || isVerticalDragging) ? 'pointer-events-none select-none' : ''
                  }`}
                  style={{
                    height: isConsoleCollapsed ? 'calc(100% - 46px)' : `${verticalSplitPercent}%`,
                    minHeight: '140px',
                  }}
                >
                  <CodeEditor
                    code={code}
                    setCode={setCode}
                    language={language}
                    setLanguage={handleLanguageChange}
                    input={input}
                    setInput={setInput}
                    onRun={handleRun}
                    isRunning={isRunning}
                    onSubmit={handleSubmit}
                    isSubmitting={isSubmitting}
                    statusMeta={meta}
                    languageLabel={LANGUAGE_LABELS[language] || language}
                    onReset={handleReset}
                    isProblemMode={true}
                    hideInput={true}
                  />
                </div>

                {/* Draggable Horizontal Divider between Editor and Console Drawer */}
                <div
                  onMouseDown={handleVerticalMouseDown}
                  onTouchStart={handleVerticalTouchStart}
                  style={{ cursor: 'ns-resize' }}
                  className={`my-0.5 flex h-2.5 w-full shrink-0 cursor-ns-resize items-center justify-center select-none group z-10 transition-colors ${
                    isVerticalDragging ? 'bg-sky-500/20' : 'hover:bg-sky-500/10'
                  }`}
                  title="Drag up or down to resize editor and console"
                >
                  <div
                    className={`h-1 w-12 rounded-full transition-colors ${
                      isVerticalDragging ? 'bg-sky-400' : 'bg-slate-700 group-hover:bg-sky-400'
                    }`}
                  />
                </div>

                {/* Bottom Console Drawer: Testcase (stdin), Result, History */}
                <div
                  className={`flex flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/90 shadow-2xl backdrop-blur ${
                    isConsoleCollapsed ? 'shrink-0' : 'flex-1 min-h-[120px]'
                  }`}
                >
                  <div className="flex shrink-0 items-center justify-between border-b border-slate-800 bg-slate-900/70 px-4 py-2 sm:px-5">
                    <div className="flex items-center gap-1.5">
                      <button
                        type="button"
                        onClick={() => {
                          setActiveConsoleTab('input');
                          setIsConsoleCollapsed(false);
                        }}
                        className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-medium transition-all ${
                          activeConsoleTab === 'input' && !isConsoleCollapsed
                            ? 'bg-slate-800 text-sky-300 shadow'
                            : 'text-slate-400 hover:text-slate-200'
                        }`}
                      >
                        <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                          <path fillRule="evenodd" d="M3 4.5A1.5 1.5 0 014.5 3h11A1.5 1.5 0 0117 4.5v11a1.5 1.5 0 01-1.5 1.5h-11A1.5 1.5 0 013 15.5v-11zm3.75 3a.75.75 0 000 1.5h6.5a.75.75 0 000-1.5h-6.5zm0 3a.75.75 0 000 1.5h6.5a.75.75 0 000-1.5h-6.5z" clipRule="evenodd" />
                        </svg>
                        <span>Testcase (stdin)</span>
                        {input && <span className="h-1.5 w-1.5 rounded-full bg-sky-400" />}
                      </button>

                      <button
                        type="button"
                        onClick={() => {
                          setActiveConsoleTab('output');
                          setIsConsoleCollapsed(false);
                        }}
                        className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-medium transition-all ${
                          activeConsoleTab === 'output' && !isConsoleCollapsed
                            ? 'bg-slate-800 text-sky-300 shadow'
                            : 'text-slate-400 hover:text-slate-200'
                        }`}
                      >
                        <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                          <path fillRule="evenodd" d="M2 4.25A2.25 2.25 0 014.25 2h11.5A2.25 2.25 0 0118 4.25v11.5A2.25 2.25 0 0115.75 18H4.25A2.25 2.25 0 012 15.75V4.25zm4.03 6.28a.75.75 0 00-1.06-1.06L3.44 11l1.53 1.53a.75.75 0 001.06-1.06L5.06 11l.97-.47zm3.72 1.47a.75.75 0 000 1.5h4.5a.75.75 0 000-1.5h-4.5z" clipRule="evenodd" />
                        </svg>
                        <span>Result / Console</span>
                        {status && (
                          <span
                            className={`h-1.5 w-1.5 rounded-full ${
                              status === 'SUCCESS' ? 'bg-emerald-400' : 'bg-rose-400'
                            }`}
                          />
                        )}
                      </button>

                      <button
                        type="button"
                        onClick={() => {
                          setActiveConsoleTab('evaluation');
                          setIsConsoleCollapsed(false);
                        }}
                        className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-medium transition-all ${
                          activeConsoleTab === 'evaluation' && !isConsoleCollapsed
                            ? 'bg-slate-800 text-sky-300 shadow'
                            : 'text-slate-400 hover:text-slate-200'
                        }`}
                      >
                        <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                          <path fillRule="evenodd" d="M16.704 4.153a.75.75 0 01.143 1.052l-8 10.5a.75.75 0 01-1.127.075l-4.5-4.5a.75.75 0 011.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 011.05-.143z" clipRule="evenodd" />
                        </svg>
                        <span>Submit Result</span>
                        {isSubmitting && (
                          <span className="relative flex h-2 w-2">
                            <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                            <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                          </span>
                        )}
                        {!isSubmitting && submissionResult?.status && (
                          <span
                            className={`h-1.5 w-1.5 rounded-full ${
                              submissionResult.status === 'ACCEPTED' ? 'bg-emerald-400' : 'bg-rose-400'
                            }`}
                          />
                        )}
                      </button>

                      <button
                        type="button"
                        onClick={() => {
                          setActiveConsoleTab('history');
                          setIsConsoleCollapsed(false);
                        }}
                        className={`flex items-center gap-1.5 rounded-lg px-3 py-1.5 text-xs font-medium transition-all ${
                          activeConsoleTab === 'history' && !isConsoleCollapsed
                            ? 'bg-slate-800 text-sky-300 shadow'
                            : 'text-slate-400 hover:text-slate-200'
                        }`}
                      >
                        <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
                          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm.75-13a.75.75 0 00-1.5 0v5c0 .414.336.75.75.75h4a.75.75 0 000-1.5h-3.25V5z" clipRule="evenodd" />
                        </svg>
                        <span>Submissions</span>
                      </button>
                    </div>

                    <div className="flex items-center gap-2">
                      {activeConsoleTab === 'output' && executionTime !== null && !isConsoleCollapsed && (
                        <span className="hidden sm:inline-block rounded-md border border-slate-800 bg-slate-900 px-2 py-0.5 text-[11px] text-slate-400">
                          {executionTime} ms
                        </span>
                      )}
                      {activeConsoleTab === 'evaluation' && submissionResult?.executionTimeMs !== undefined && submissionResult?.executionTimeMs !== null && !isConsoleCollapsed && (
                        <span className="hidden sm:inline-block rounded-md border border-slate-800 bg-slate-900 px-2 py-0.5 text-[11px] text-slate-400">
                          {submissionResult.executionTimeMs} ms
                        </span>
                      )}
                      <button
                        type="button"
                        onClick={() => setIsConsoleCollapsed(!isConsoleCollapsed)}
                        className="flex items-center gap-1.5 rounded-lg border border-slate-800 bg-slate-900/60 px-2.5 py-1 text-xs text-slate-400 hover:border-slate-700 hover:text-slate-200 transition-colors"
                        title={isConsoleCollapsed ? 'Expand console drawer' : 'Collapse console drawer'}
                      >
                        <span>{isConsoleCollapsed ? 'Expand' : 'Collapse'}</span>
                        <svg
                          className={`h-3.5 w-3.5 transition-transform duration-200 ${
                            isConsoleCollapsed ? '' : 'rotate-180'
                          }`}
                          viewBox="0 0 20 20"
                          fill="currentColor"
                        >
                          <path fillRule="evenodd" d="M5.23 7.21a.75.75 0 011.06.02L10 11.168l3.71-3.938a.75.75 0 111.08 1.04l-4.25 4.5a.75.75 0 01-1.08 0l-4.25-4.5a.75.75 0 01.02-1.06z" clipRule="evenodd" />
                        </svg>
                      </button>
                    </div>
                  </div>

                  {!isConsoleCollapsed && (
                    <div className="flex-1 min-h-0 overflow-hidden">
                      {activeConsoleTab === 'input' && (
                        <div className="flex h-full flex-col bg-slate-950 p-3 sm:p-4">
                          <div className="mb-2 flex items-center justify-between text-xs text-slate-400 shrink-0">
                            <span>Custom standard input for execution:</span>
                            <div className="flex items-center gap-2">
                              {problem?.sampleTestCases?.[0]?.input && (
                                <button
                                  type="button"
                                  onClick={() => setInput(problem.sampleTestCases[0].input)}
                                  className="text-xs text-sky-400 hover:text-sky-300 underline"
                                >
                                  Reset to Sample 1
                                </button>
                              )}
                              <span className="rounded-full border border-slate-800 bg-slate-900 px-2 py-0.5 text-[11px] text-slate-400">
                                {input ? `${input.length} chars` : 'Empty'}
                              </span>
                            </div>
                          </div>
                          <textarea
                            value={input}
                            onChange={(e) => setInput(e.target.value)}
                            className="flex-1 resize-none overflow-y-auto rounded-xl border border-slate-800 bg-slate-900/60 p-3 font-mono text-sm text-slate-100 outline-none focus:border-sky-500/50 placeholder:text-slate-600"
                            placeholder="e.g. nums = [3,2,4]\ntarget = 6"
                            spellCheck="false"
                            disabled={isRunning}
                          />
                        </div>
                      )}

                      {activeConsoleTab === 'output' && (
                        <div className="flex h-full min-h-0 flex-col overflow-hidden">
                          <OutputConsole
                            stdout={stdout}
                            stderr={stderr}
                            status={status}
                            executionTime={executionTime}
                            isRunning={isRunning}
                          />
                        </div>
                      )}

                      {activeConsoleTab === 'evaluation' && (
                        <div className="h-full overflow-y-auto bg-slate-950 p-3 sm:p-4 text-xs">
                          {isSubmitting ? (
                            <div className="flex h-full flex-col items-center justify-center gap-3 text-center">
                              <div className="relative flex h-9 w-9 items-center justify-center">
                                <div className="absolute h-9 w-9 animate-ping rounded-full bg-emerald-500/20" />
                                <div className="h-7 w-7 animate-spin rounded-full border-2 border-emerald-400 border-t-transparent" />
                              </div>
                              <div>
                                <div className="text-sm font-semibold text-slate-100">
                                  Evaluating Test Cases...
                                </div>
                                <div className="mt-1 text-xs text-slate-400">
                                  Executing in isolated sandbox against all test cases.
                                </div>
                              </div>
                            </div>
                          ) : !submissionResult ? (
                            <div className="flex h-full flex-col items-center justify-center gap-2 text-center text-slate-400">
                              <svg className="h-8 w-8 text-slate-600" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                                <path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" strokeLinecap="round" strokeLinejoin="round" />
                              </svg>
                              <div className="text-sm font-medium text-slate-300">No Submission Evaluated</div>
                              <div className="text-xs text-slate-500">
                                Click <span className="font-semibold text-emerald-400">Submit</span> to evaluate your code against all test cases.
                              </div>
                            </div>
                          ) : (
                            <div className="flex flex-col gap-3">
                              {/* Verdict Banner */}
                              <div className="flex flex-wrap items-center justify-between gap-2 rounded-xl border border-slate-800 bg-slate-900/80 p-3">
                                <div className="flex items-center gap-3">
                                  {submissionResult.status === 'ACCEPTED' ? (
                                    <div className="flex items-center gap-2 text-emerald-400">
                                      <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-emerald-500/20 text-emerald-400">
                                        <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                                          <path fillRule="evenodd" d="M16.704 4.153a.75.75 0 01.143 1.052l-8 10.5a.75.75 0 01-1.127.075l-4.5-4.5a.75.75 0 011.06-1.06l3.894 3.893 7.48-9.817a.75.75 0 011.05-.143z" clipRule="evenodd" />
                                        </svg>
                                      </div>
                                      <div>
                                        <div className="text-base font-bold text-emerald-400">Accepted</div>
                                        <div className="text-xs text-slate-400">All test cases passed</div>
                                      </div>
                                    </div>
                                  ) : submissionResult.status === 'WRONG_ANSWER' ? (
                                    <div className="flex items-center gap-2 text-rose-400">
                                      <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-rose-500/20 text-rose-400">
                                        <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                                          <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                                        </svg>
                                      </div>
                                      <div>
                                        <div className="text-base font-bold text-rose-400">Wrong Answer</div>
                                        <div className="text-xs text-slate-400">Output did not match expected output</div>
                                      </div>
                                    </div>
                                  ) : submissionResult.status === 'COMPILE_ERROR' || submissionResult.status === 'COMPILATION_ERROR' ? (
                                    <div className="flex items-center gap-2 text-rose-400">
                                      <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-rose-500/20 text-rose-400">
                                        <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                                          <path fillRule="evenodd" d="M8.257 3.099c.765-1.36 2.722-1.36 3.486 0l5.58 9.92c.75 1.334-.213 2.98-1.742 2.98H4.42c-1.53 0-2.493-1.646-1.743-2.98l5.58-9.92zM11 13a1 1 0 11-2 0 1 1 0 012 0zm-1-8a1 1 0 00-1 1v3a1 1 0 002 0V6a1 1 0 00-1-1z" clipRule="evenodd" />
                                        </svg>
                                      </div>
                                      <div>
                                        <div className="text-base font-bold text-rose-400">Compile Error</div>
                                        <div className="text-xs text-slate-400">Failed to compile solution</div>
                                      </div>
                                    </div>
                                  ) : submissionResult.status === 'TIME_LIMIT_EXCEEDED' || submissionResult.status === 'TIMEOUT' ? (
                                    <div className="flex items-center gap-2 text-amber-400">
                                      <div className="flex h-7 w-7 items-center justify-center rounded-lg bg-amber-500/20 text-amber-400">
                                        <svg className="h-4 w-4" viewBox="0 0 20 20" fill="currentColor">
                                          <path fillRule="evenodd" d="M10 18a8 8 0 100-16 8 8 0 000 16zm.75-13a.75.75 0 00-1.5 0v5c0 .414.336.75.75.75h4a.75.75 0 000-1.5h-3.25V5z" clipRule="evenodd" />
                                        </svg>
                                      </div>
                                      <div>
                                        <div className="text-base font-bold text-amber-400">Time Limit Exceeded</div>
                                        <div className="text-xs text-slate-400">Execution exceeded time limit</div>
                                      </div>
                                    </div>
                                  ) : (
                                    <div className="flex items-center gap-2 text-rose-400">
                                      <div className="text-base font-bold text-rose-400">
                                        {submissionResult.status?.replace(/_/g, ' ') || 'Evaluation Failed'}
                                      </div>
                                    </div>
                                  )}
                                </div>

                                <div className="flex items-center gap-3">
                                  {submissionResult.totalCount !== undefined && submissionResult.totalCount !== null && (
                                    <div className="rounded-lg border border-slate-700 bg-slate-800/80 px-2.5 py-1 text-xs">
                                      <span className="text-slate-400">Passed: </span>
                                      <span className={`font-semibold ${submissionResult.passedCount === submissionResult.totalCount ? 'text-emerald-400' : 'text-slate-200'}`}>
                                        {submissionResult.passedCount ?? 0} / {submissionResult.totalCount}
                                      </span>
                                    </div>
                                  )}
                                  {submissionResult.executionTimeMs !== undefined && submissionResult.executionTimeMs !== null && (
                                    <div className="rounded-lg border border-slate-700 bg-slate-800/80 px-2.5 py-1 text-xs text-slate-400">
                                      Runtime: <span className="font-semibold text-slate-200">{submissionResult.executionTimeMs} ms</span>
                                    </div>
                                  )}
                                </div>
                              </div>

                              {/* Error details (if any) */}
                              {submissionResult.errorDetails && (
                                <div className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-3">
                                  <div className="mb-1 text-xs font-semibold uppercase tracking-wider text-rose-400">
                                    Error Details
                                  </div>
                                  <pre className="font-mono text-xs text-rose-200 whitespace-pre-wrap overflow-x-auto max-h-32">
                                    {submissionResult.errorDetails}
                                  </pre>
                                </div>
                              )}

                              {/* Test Case Breakdown */}
                              {submissionResult.testCaseResults && submissionResult.testCaseResults.length > 0 && (
                                <div className="flex flex-col gap-2">
                                  {/* Test Case Selector Tabs */}
                                  <div className="flex flex-wrap items-center gap-1.5 border-b border-slate-800/80 pb-2">
                                    {submissionResult.testCaseResults.map((tc, idx) => {
                                      const isPassed = tc.status === 'PASSED';
                                      const isSelected = selectedTestCaseIndex === idx;
                                      return (
                                        <button
                                          key={tc.testCaseId || idx}
                                          type="button"
                                          onClick={() => setSelectedTestCaseIndex(idx)}
                                          className={`flex items-center gap-1.5 rounded-lg px-2.5 py-1 text-xs transition-colors ${
                                            isSelected
                                              ? 'bg-slate-800 text-slate-100 font-semibold ring-1 ring-slate-700'
                                              : 'text-slate-400 hover:text-slate-200 hover:bg-slate-900'
                                          }`}
                                        >
                                          <span
                                            className={`h-2 w-2 rounded-full ${
                                              isPassed ? 'bg-emerald-400' : 'bg-rose-400'
                                            }`}
                                          />
                                          <span>
                                            Case {idx + 1}
                                            {tc.isHidden ? ' (Hidden)' : ''}
                                          </span>
                                        </button>
                                      );
                                    })}
                                  </div>

                                  {/* Selected Test Case Detail */}
                                  {(() => {
                                    const tc = submissionResult.testCaseResults[selectedTestCaseIndex] || submissionResult.testCaseResults[0];
                                    if (!tc) return null;
                                    const isPassed = tc.status === 'PASSED';

                                    return (
                                      <div className="rounded-xl border border-slate-800/80 bg-slate-900/50 p-3 space-y-2.5">
                                        <div className="flex items-center justify-between text-xs">
                                          <div className="flex items-center gap-2">
                                            <span className="font-semibold text-slate-200">
                                              Case {selectedTestCaseIndex + 1}
                                            </span>
                                            <span
                                              className={`rounded-full border px-2 py-0.5 text-[10px] font-semibold uppercase tracking-wider ${
                                                isPassed
                                                  ? 'border-emerald-500/30 bg-emerald-500/10 text-emerald-400'
                                                  : 'border-rose-500/30 bg-rose-500/10 text-rose-400'
                                              }`}
                                            >
                                              {tc.status}
                                            </span>
                                            {tc.isHidden && (
                                              <span className="rounded-full border border-sky-500/30 bg-sky-500/10 px-2 py-0.5 text-[10px] font-medium text-sky-400">
                                                Hidden Test Case
                                              </span>
                                            )}
                                          </div>
                                          {tc.executionTimeMs !== undefined && tc.executionTimeMs !== null && (
                                            <span className="text-slate-400">
                                              {tc.executionTimeMs} ms
                                            </span>
                                          )}
                                        </div>

                                        {tc.isHidden ? (
                                          <div className="rounded-lg border border-slate-800 bg-slate-950/80 p-3 text-xs text-slate-400">
                                            <div className="flex items-center gap-2 text-slate-300 mb-1">
                                              <svg className="h-4 w-4 text-sky-400" viewBox="0 0 20 20" fill="currentColor">
                                                <path fillRule="evenodd" d="M10 2a4 4 0 00-4 4v2H5a2 2 0 00-2 2v6a2 2 0 002 2h10a2 2 0 002-2v-6a2 2 0 00-2-2h-1V6a4 4 0 00-4-4zm2 6V6a2 2 0 10-4 0v2h4z" clipRule="evenodd" />
                                              </svg>
                                              <span className="font-medium">Confidential Test Case</span>
                                            </div>
                                            <p className="text-[11px] text-slate-500">
                                              Inputs and expected outputs for hidden test cases are kept private on the server to prevent hardcoding.
                                            </p>
                                          </div>
                                        ) : (
                                          <div className="grid grid-cols-1 md:grid-cols-2 gap-2 text-xs">
                                            {tc.input !== undefined && tc.input !== null && (
                                              <div className="col-span-full">
                                                <div className="text-[11px] font-semibold text-slate-400 mb-1">Input</div>
                                                <pre className="font-mono text-xs rounded-lg border border-slate-800 bg-slate-950 p-2 text-slate-200 overflow-x-auto whitespace-pre-wrap">
                                                  {tc.input}
                                                </pre>
                                              </div>
                                            )}
                                            {tc.expectedOutput !== undefined && tc.expectedOutput !== null && (
                                              <div>
                                                <div className="text-[11px] font-semibold text-slate-400 mb-1">Expected Output</div>
                                                <pre className="font-mono text-xs rounded-lg border border-slate-800 bg-slate-950 p-2 text-emerald-300 overflow-x-auto whitespace-pre-wrap">
                                                  {tc.expectedOutput}
                                                </pre>
                                              </div>
                                            )}
                                            {tc.actualOutput !== undefined && tc.actualOutput !== null && (
                                              <div>
                                                <div className="text-[11px] font-semibold text-slate-400 mb-1">Your Output</div>
                                                <pre className={`font-mono text-xs rounded-lg border border-slate-800 bg-slate-950 p-2 overflow-x-auto whitespace-pre-wrap ${
                                                  isPassed ? 'text-emerald-300' : 'text-rose-300'
                                                }`}>
                                                  {tc.actualOutput || '<Empty Output>'}
                                                </pre>
                                              </div>
                                            )}
                                            {tc.errorMessage && (
                                              <div className="col-span-full">
                                                <div className="text-[11px] font-semibold text-rose-400 mb-1">Error</div>
                                                <pre className="font-mono text-xs rounded-lg border border-rose-500/20 bg-rose-500/10 p-2 text-rose-200 overflow-x-auto whitespace-pre-wrap">
                                                  {tc.errorMessage}
                                                </pre>
                                              </div>
                                            )}
                                          </div>
                                        )}
                                      </div>
                                    );
                                  })()}
                                </div>
                              )}
                            </div>
                          )}
                        </div>
                      )}

                      {activeConsoleTab === 'history' && (
                        <div className="flex h-full min-h-0 flex-col overflow-hidden">
                          <HistoryPanel ref={historyRef} onLoadCode={handleLoadFromHistory} problemId={problemId} />
                        </div>
                      )}
                    </div>
                  )}
                </div>
              </section>
            </div>
          ) : (
            /* Standalone layout when no problemId is present */
            <div className="flex min-h-0 flex-1 flex-col gap-4 lg:flex-row">
              <section className="min-h-0 min-w-0 lg:flex-[2.1]">
                <CodeEditor
                  code={code}
                  setCode={setCode}
                  language={language}
                  setLanguage={handleLanguageChange}
                  input={input}
                  setInput={setInput}
                  onRun={handleRun}
                  isRunning={isRunning}
                  statusMeta={meta}
                  languageLabel={LANGUAGE_LABELS[language] || language}
                  onReset={handleReset}
                  isProblemMode={false}
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
                  <HistoryPanel ref={historyRef} onLoadCode={handleLoadFromHistory} problemId={null} />
                </div>
              </aside>
            </div>
          )}
        </main>
      </div>
    </div>
  );
}

export default EditorPage;
