import React from 'react';
import { Link } from 'react-router-dom';

const getDifficultyClass = (difficulty) => {
  switch (difficulty?.toUpperCase()) {
    case 'EASY':
      return 'border-emerald-500/30 bg-emerald-500/10 text-emerald-300';
    case 'MEDIUM':
      return 'border-amber-500/30 bg-amber-500/10 text-amber-300';
    case 'HARD':
      return 'border-rose-500/30 bg-rose-500/10 text-rose-300';
    default:
      return 'border-slate-700 bg-slate-800 text-slate-300';
  }
};

const ProblemStatement = ({
  problem,
  isLoading,
  error,
  onUseInput,
}) => {
  if (isLoading) {
    return (
      <section className="flex h-full min-h-0 flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/80 p-6 shadow-2xl shadow-black/20 backdrop-blur">
        <div className="flex h-full flex-col items-center justify-center gap-3 text-slate-400">
          <svg className="h-8 w-8 animate-spin text-sky-400" viewBox="0 0 24 24" fill="none">
            <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4" />
            <path className="opacity-90" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z" />
          </svg>
          <p className="text-sm">Loading problem details...</p>
        </div>
      </section>
    );
  }

  if (error || !problem) {
    return (
      <section className="flex h-full min-h-0 flex-col justify-center items-center overflow-hidden rounded-3xl border border-rose-500/30 bg-slate-950/80 p-6 shadow-2xl shadow-black/20 backdrop-blur text-center">
        <div className="max-w-md">
          <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-2xl border border-rose-500/30 bg-rose-500/10 text-rose-300">
            <svg className="h-6 w-6" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <path strokeLinecap="round" strokeLinejoin="round" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z" />
            </svg>
          </div>
          <h2 className="text-lg font-semibold text-slate-100 mb-2">Problem Not Found</h2>
          <p className="text-sm text-slate-400 mb-6">
            {error || 'The requested problem could not be found or failed to load.'}
          </p>
          <div className="flex items-center justify-center gap-3">
            <Link
              to="/problems"
              className="rounded-xl bg-gradient-to-r from-sky-500 to-cyan-400 px-4 py-2 text-sm font-semibold text-slate-950 hover:brightness-110 transition-all shadow-lg shadow-sky-500/20"
            >
              Browse Problems
            </Link>
            <Link
              to="/editor"
              className="rounded-xl border border-slate-700 bg-slate-900 px-4 py-2 text-sm font-medium text-slate-300 hover:bg-slate-800 transition-all"
            >
              Open Standalone Editor
            </Link>
          </div>
        </div>
      </section>
    );
  }

  return (
    <section className="flex h-full min-h-0 flex-col overflow-hidden rounded-3xl border border-slate-800 bg-slate-950/80 shadow-2xl shadow-black/20 backdrop-blur">
      {/* Pane Header */}
      <div className="flex shrink-0 items-center justify-between border-b border-slate-800 bg-slate-900/70 px-4 py-3 sm:px-5">
        <div className="flex items-center gap-3 min-w-0">
          <Link
            to="/problems"
            className="flex items-center gap-1.5 rounded-lg border border-slate-800 bg-slate-900 px-2.5 py-1 text-xs font-medium text-slate-300 transition-colors hover:border-slate-700 hover:text-white"
            title="Back to Problem Library"
          >
            <svg className="h-3.5 w-3.5" viewBox="0 0 20 20" fill="currentColor">
              <path fillRule="evenodd" d="M17 10a.75.75 0 01-.75.75H5.612l4.158 3.96a.75.75 0 11-1.04 1.08l-5.5-5.25a.75.75 0 010-1.08l5.5-5.25a.75.75 0 111.04 1.08L5.612 9.25H16.25A.75.75 0 0117 10z" clipRule="evenodd" />
            </svg>
            <span>Problems</span>
          </Link>
          <div className="h-4 w-px bg-slate-800" />
          <h2 className="truncate text-sm font-semibold text-slate-100">
            {problem.id}. {problem.title}
          </h2>
        </div>

        <div className="flex items-center gap-2">
          <span
            className={`rounded-full border px-2.5 py-0.5 text-[11px] font-semibold uppercase tracking-[0.16em] ${getDifficultyClass(
              problem.difficulty
            )}`}
          >
            {problem.difficulty}
          </span>
        </div>
      </div>

      {/* Pane Scrollable Content */}
      <div className="min-h-0 flex-1 overflow-y-auto px-5 py-5 text-slate-200 space-y-6">
        {/* Title and Topics */}
        <div>
          <h1 className="text-xl font-bold tracking-tight text-white sm:text-2xl">
            {problem.title}
          </h1>

          {problem.topics && problem.topics.length > 0 && (
            <div className="mt-2.5 flex flex-wrap gap-1.5">
              {problem.topics.map((topic) => (
                <span
                  key={topic.id || topic.name}
                  className="rounded-lg border border-sky-500/20 bg-sky-500/10 px-2.5 py-0.5 text-xs font-medium text-sky-300"
                >
                  {topic.name}
                </span>
              ))}
            </div>
          )}
        </div>

        {/* Problem Description */}
        <div className="space-y-2">
          <h3 className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">
            Description
          </h3>
          <div className="whitespace-pre-line text-sm leading-relaxed text-slate-300">
            {problem.description}
          </div>
        </div>

        {/* Public Sample Test Cases */}
        {problem.sampleTestCases && problem.sampleTestCases.length > 0 && (
          <div className="space-y-4">
            <h3 className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">
              Sample Examples
            </h3>
            <div className="space-y-3.5">
              {problem.sampleTestCases.map((testCase, index) => (
                <div
                  key={testCase.id || index}
                  className="rounded-2xl border border-slate-800/90 bg-slate-900/50 p-4 transition-colors hover:border-slate-700/80"
                >
                  <div className="mb-2.5 flex items-center justify-between">
                    <span className="text-xs font-semibold text-slate-200">
                      Example {index + 1}
                    </span>
                    {onUseInput && testCase.input && (
                      <button
                        type="button"
                        onClick={() => onUseInput(testCase.input)}
                        className="inline-flex items-center gap-1.5 rounded-lg border border-slate-700/80 bg-slate-800/80 px-2.5 py-1 text-[11px] font-medium text-slate-300 transition-colors hover:border-sky-500/50 hover:bg-sky-500/10 hover:text-sky-300"
                        title="Copy this sample input into the editor stdin box"
                      >
                        <svg className="h-3 w-3" viewBox="0 0 20 20" fill="currentColor">
                          <path d="M7 3.5A1.5 1.5 0 018.5 2h3.879a1.5 1.5 0 011.06.44l3.122 3.12a1.5 1.5 0 01.439 1.061V16.5A1.5 1.5 0 0115.5 18h-7A1.5 1.5 0 017 16.5v-13z" />
                          <path d="M4.5 6A1.5 1.5 0 003 7.5v11A1.5 1.5 0 004.5 20h7a1.5 1.5 0 001.5-1.5v-1h-1.5a2 2 0 01-2-2v-9.5h-5z" />
                        </svg>
                        <span>Use as Input</span>
                      </button>
                    )}
                  </div>

                  {testCase.explanation && (
                    <p className="mb-2.5 text-xs text-slate-400 italic">
                      {testCase.explanation}
                    </p>
                  )}

                  <div className="space-y-2">
                    <div>
                      <span className="block text-[11px] font-medium uppercase tracking-wider text-slate-400 mb-1">
                        Input:
                      </span>
                      <pre className="rounded-xl border border-slate-800 bg-slate-950/80 p-2.5 font-mono text-xs text-sky-200 overflow-x-auto whitespace-pre-wrap">
                        {testCase.input}
                      </pre>
                    </div>
                    <div>
                      <span className="block text-[11px] font-medium uppercase tracking-wider text-slate-400 mb-1">
                        Expected Output:
                      </span>
                      <pre className="rounded-xl border border-slate-800 bg-slate-950/80 p-2.5 font-mono text-xs text-emerald-300 overflow-x-auto whitespace-pre-wrap">
                        {testCase.expectedOutput}
                      </pre>
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Constraints */}
        {problem.constraints && (
          <div className="space-y-2">
            <h3 className="text-xs font-semibold uppercase tracking-[0.18em] text-slate-400">
              Constraints
            </h3>
            <div className="rounded-2xl border border-slate-800/90 bg-slate-900/40 p-4 font-mono text-xs text-slate-300 whitespace-pre-line leading-relaxed">
              {problem.constraints}
            </div>
          </div>
        )}

        {/* Problem Specifications */}
        <div className="pt-2 border-t border-slate-800/80 flex flex-wrap items-center justify-between text-xs text-slate-400 gap-2">
          <div className="flex items-center gap-4">
            {problem.timeLimit !== undefined && (
              <span>
                Time Limit: <strong className="text-slate-200">{problem.timeLimit} ms</strong>
              </span>
            )}
            {problem.memoryLimit !== undefined && (
              <span>
                Memory Limit: <strong className="text-slate-200">{problem.memoryLimit} MB</strong>
              </span>
            )}
          </div>
          <Link
            to="/editor"
            className="text-xs text-slate-500 hover:text-slate-400 transition-colors"
          >
            Standalone scratchpad mode →
          </Link>
        </div>
      </div>
    </section>
  );
};

export default ProblemStatement;
