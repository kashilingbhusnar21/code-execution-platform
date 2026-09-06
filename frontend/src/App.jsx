import React, { useRef, useState } from 'react';
import CodeEditor, { LANGUAGE_TEMPLATES } from './components/CodeEditor';
import OutputConsole from './components/OutputConsole';
import HistoryPanel from './components/HistoryPanel';
import { api } from './services/api';
import './App.css';

function App() {
  const [code, setCode] = useState(LANGUAGE_TEMPLATES.java);
  const [language, setLanguage] = useState('java');
  const [input, setInput] = useState('');
  const [stdout, setStdout] = useState('');
  const [stderr, setStderr] = useState('');
  const [status, setStatus] = useState(null);
  const [executionTime, setExecutionTime] = useState(null);
  const [isRunning, setIsRunning] = useState(false);
  const [userId] = useState('user1');
  const historyRef = useRef(null);

  const applyResult = (result) => {
    if (typeof result === 'string') {
      const detected = detectStatus(result);
      setStatus(detected);
      if (detected === 'SUCCESS') {
        setStdout(result);
        setStderr('');
      } else {
        setStdout('');
        setStderr(result);
      }
      return;
    }

    const nextStatus = result.status || detectStatus(result.output || '');
    setStatus(nextStatus);
    setStdout(result.stdout ?? (nextStatus === 'SUCCESS' ? (result.output || '') : ''));
    setStderr(result.stderr ?? (nextStatus === 'SUCCESS' ? '' : (result.output || '')));
    setExecutionTime(
      result.executionTimeMs ?? result.executionTime ?? null
    );
  };

  const detectStatus = (text) => {
    if (!text) return 'SUCCESS';
    if (text.includes('Compilation Error')) return 'COMPILATION_ERROR';
    if (text.toLowerCase().includes('timeout')) return 'TIMEOUT';
    if (
      text.includes('Exception') ||
      text.includes('Error:') ||
      text.includes('Traceback')
    ) {
      return 'RUNTIME_ERROR';
    }
    return 'SUCCESS';
  };

  const handleRun = async () => {
    setIsRunning(true);
    setStdout('');
    setStderr('');
    setStatus(null);
    setExecutionTime(null);

    try {
      const result = await api.runCode(code, language, input, userId);
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

  const handleLoadFromHistory = ({ code: loadedCode, language: loadedLanguage }) => {
    if (loadedLanguage) {
      setLanguage(String(loadedLanguage).toLowerCase());
    }
    if (typeof loadedCode === 'string') {
      setCode(loadedCode);
    }
  };

  return (
    <div className="app">
      <header className="app-header">
        <div>
          <h1>Code Execution Platform</h1>
          <p>Write, run, and analyze your code</p>
        </div>
      </header>

      <main className="app-main">
        <div className="left-panel">
          <CodeEditor
            code={code}
            setCode={setCode}
            language={language}
            setLanguage={setLanguage}
            input={input}
            setInput={setInput}
            onRun={handleRun}
            isRunning={isRunning}
          />
        </div>

        <div className="right-panel">
          <OutputConsole
            stdout={stdout}
            stderr={stderr}
            status={status}
            executionTime={executionTime}
            isRunning={isRunning}
          />
          <HistoryPanel
            ref={historyRef}
            userId={userId}
            onLoadCode={handleLoadFromHistory}
          />
        </div>
      </main>
    </div>
  );
}

export default App;
