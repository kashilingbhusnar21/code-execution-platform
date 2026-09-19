import React from 'react';
import Editor from '@monaco-editor/react';

export const LANGUAGE_TEMPLATES = {
  java: `public class Main {
    public static void main(String[] args) {
        System.out.println("Hello World");
    }
}`,
  python: `print("Hello World")`,
  cpp: `#include <iostream>
using namespace std;

int main() {
    cout << "Hello World" << endl;
    return 0;
}`,
};

const MONACO_LANGUAGE_MAP = {
  java: 'java',
  python: 'python',
  cpp: 'cpp',
};

const statusPillClass = (statusMeta) =>
  statusMeta?.tone || 'bg-slate-500/15 text-slate-300 border-slate-500/30';

const CodeEditor = ({
  code,
  setCode,
  language,
  setLanguage,
  input,
  setInput,
  onRun,
  isRunning,
  statusMeta,
  languageLabel,
}) => {
  const handleLanguageChange = (nextLanguage) => {
    setLanguage(nextLanguage);
    setCode(LANGUAGE_TEMPLATES[nextLanguage] || '');
  };

  const handleReset = () => {
    setCode(LANGUAGE_TEMPLATES[language] || '');
  };

  const monacoLanguage = MONACO_LANGUAGE_MAP[language] || 'java';

  return (
    <div className="flex h-full max-h-[calc(100vh-7rem)] min-h-0 flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/80 shadow-2xl shadow-black/20 backdrop-blur">
      <div className="flex items-center justify-between border-b border-slate-800 bg-slate-900/70 px-4 py-2.5 sm:px-5">
        <div className="min-w-0">
          <div className="flex items-center gap-2">
            <h2 className="truncate text-sm font-semibold text-slate-100">Editor</h2>
            <span className={`rounded-full border px-2.5 py-1 text-[11px] font-medium uppercase tracking-[0.16em] ${statusPillClass(statusMeta)}`}>
              {languageLabel}
            </span>
          </div>
          <p className="mt-1 text-xs text-slate-400">
            Write code, choose a language, and run against the current input.
          </p>
        </div>

        <div className="flex items-center gap-2">
          <label className="sr-only" htmlFor="language-select">
            Language
          </label>
          <select
            id="language-select"
            value={language}
            onChange={(e) => handleLanguageChange(e.target.value)}
            className="rounded-xl border border-slate-700 bg-slate-900 px-3 py-2 text-sm text-slate-100 outline-none transition-all duration-200 hover:border-slate-600 focus:border-sky-500 disabled:cursor-not-allowed disabled:opacity-50"
            disabled={isRunning}
          >
            <option value="java">Java</option>
            <option value="python">Python</option>
            <option value="cpp">C++</option>
          </select>
        </div>
      </div>

      <div className="min-h-0 flex-[1_1_auto] overflow-hidden border-b border-slate-800">
        <Editor
          height="100%"
          theme="vs-dark"
          language={monacoLanguage}
          value={code}
          onChange={(value) => setCode(value ?? '')}
          options={{
            fontSize: 14,
            fontFamily: "'Fira Code', 'Consolas', 'Courier New', monospace",
            minimap: { enabled: false },
            automaticLayout: true,
            scrollBeyondLastLine: false,
            tabSize: 4,
            insertSpaces: true,
            autoIndent: 'full',
            formatOnType: true,
            formatOnPaste: true,
            readOnly: isRunning,
            wordWrap: 'on',
            padding: { top: 16, bottom: 16 },
            lineNumbers: 'on',
            renderLineHighlight: 'all',
            cursorBlinking: 'smooth',
            cursorSmoothCaretAnimation: 'on',
            fontLigatures: true,
            smoothScrolling: true,
          }}
          loading={
            <div className="flex h-full items-center justify-center text-sm text-slate-400">
              Loading editor...
            </div>
          }
        />
      </div>

      <div className="flex flex-wrap items-center justify-between gap-3 border-b border-slate-800 bg-slate-900/70 px-4 py-2.5 sm:px-5">
        <div className="flex items-center gap-2 text-xs text-slate-400">
          <span className="rounded-full border border-slate-700 bg-slate-950 px-2 py-1 font-medium text-slate-300">
            Monaco
          </span>
          <span className="hidden sm:inline">Editor is scoped for quick edits and immediate execution.</span>
        </div>
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={handleReset}
            disabled={isRunning}
            className="inline-flex items-center gap-2 rounded-xl border border-slate-700 bg-slate-900 px-4 py-2 text-sm font-medium text-slate-100 transition-all duration-200 hover:border-slate-600 hover:bg-slate-800 disabled:cursor-not-allowed disabled:opacity-50"
          >
            <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
              <path d="M4 4v6h6" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M20 20v-6h-6" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M20 8a8 8 0 00-13.66-5.66L4 4" strokeLinecap="round" strokeLinejoin="round" />
              <path d="M4 16a8 8 0 0013.66 5.66L20 20" strokeLinecap="round" strokeLinejoin="round" />
            </svg>
            Reset
          </button>

          <button
            type="button"
            onClick={onRun}
            disabled={isRunning}
            className="inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-sky-500 to-cyan-400 px-5 py-2.5 text-sm font-semibold text-slate-950 shadow-lg shadow-sky-500/20 transition-all duration-200 hover:brightness-110 hover:shadow-sky-500/30 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isRunning ? (
              <>
                <svg className="h-4 w-4 animate-spin" viewBox="0 0 24 24" fill="none">
                  <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
                  <path className="opacity-90" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
                </svg>
                Running
              </>
            ) : (
              <>
                <svg className="h-4 w-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.8">
                  <path d="M8 5l11 7-11 7V5z" strokeLinecap="round" strokeLinejoin="round" />
                  <path d="M3 5v14" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
                Run Code
              </>
            )}
          </button>
        </div>
      </div>

      <div className="flex shrink-0 max-h-40 min-h-0 flex-col border-t border-slate-800 bg-slate-950/90">
        <div className="border-b border-slate-800 px-4 py-2.5 sm:px-5">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-xs font-semibold uppercase tracking-[0.2em] text-slate-400">
                Input
              </h3>
              <p className="mt-1 text-xs text-slate-500">stdin</p>
            </div>
            <span className="rounded-full border border-slate-800 bg-slate-900 px-2 py-1 text-[11px] text-slate-400">
              {input ? `${input.length} chars` : 'Empty'}
            </span>
          </div>
        </div>

        <textarea
          value={input}
          onChange={(e) => setInput(e.target.value)}
          className="min-h-24 flex-1 resize-none overflow-y-auto bg-slate-950 px-4 py-3 font-mono text-sm text-slate-100 outline-none placeholder:text-slate-600 sm:px-5"
          placeholder={'Example:\n5\n10'}
          spellCheck="false"
          disabled={isRunning}
        />
      </div>
    </div>
  );
};

export default CodeEditor;
